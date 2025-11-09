package com.maven.neuto.serviceImplement;

import com.maven.neuto.emun.ProfileComplete;
import com.maven.neuto.emun.Status;
import com.maven.neuto.exception.APIException;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.model.*;
import com.maven.neuto.payload.request.socket.ChatMessageResponseDTO;
import com.maven.neuto.payload.request.socket.ReportUserDTO;
import com.maven.neuto.payload.request.socket.SidebarUserDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.repository.*;
import com.maven.neuto.service.MessageService;
import com.maven.neuto.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImple implements MessageService {
    private final AuthUtil authUtil;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final DeletedMessageRepository deletedMessageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final BlockedUserRepository blockedUserRepository;
    private final ReportUserRepository reportUserRepository;
    private final ConversationService conversationService;


    @Override
    public PaginatedResponse<SidebarUserDTO> getSideBarUsers(String search, Integer pageNumber, Integer pageSize, String sortOrder) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        log.info("currentUserId---{}", currentUserId);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
        log.info("currentUser---{}", currentUser);
        Long communityId = currentUser.getUserCommunity() != null ? currentUser.getUserCommunity().getId() : null;
        if (communityId == null) {
            return PaginatedResponse.<SidebarUserDTO>builder()
                    .content(Collections.emptyList())
                    .pageNumber(0)
                    .pageSize(0)
                    .totalElements(0)
                    .totalPages(0)
                    .build();
        }
        log.info("communityId---{}", communityId);


        // 1️⃣ Find all users in the same community (except current)
        List<User> users = userRepository.findAllByCommunityIdAndStatusAndProfileCompleteAndIdNotAndRoleIdNot(
                communityId, Status.ACTIVE, ProfileComplete.COMPLETE, currentUserId, 1L);
        log.info("users---{}", users);
        // Optional search filter
        if (search != null && !search.isEmpty()) {
            String s = search.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getFirstName().toLowerCase().contains(s)
                            || u.getLastName().toLowerCase().contains(s))
                    .toList();
        }

        // 2️⃣ Get deleted messages (so we skip them)
        List<Long> deletedIds = deletedMessageRepository.findMessageIdsByUserId(currentUserId);
        if (deletedIds == null || deletedIds.isEmpty()) {
            deletedIds = List.of(-1L); // dummy value
        }
        log.info("deletedIds---{}", deletedIds);

        // 3️⃣ Fetch last messages between currentUser and all community users
        List<ChatMessage> lastMessages = chatMessageRepository.findLatestMessagesForUsers(currentUserId, deletedIds);
        log.info("lastMessages---{}", lastMessages.size());
        // 4️⃣ Unread counts
        List<Object[]> unreadCounts = chatMessageRepository.countUnreadMessagesForUser(currentUserId);
        Map<Long, Long> unreadMap = unreadCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // senderId
                        row -> (Long) row[1]  // count
                ));
        log.info("unreadMap---{}", unreadMap);

        // 5️⃣ Map last messages per user
        Map<Long, ChatMessage> lastMessageMap = new HashMap<>();
        for (ChatMessage msg : lastMessages) {
            Long partnerId = msg.getSender().getId().equals(currentUserId)
                    ? msg.getReceiver().getId()
                    : msg.getSender().getId();
            lastMessageMap.putIfAbsent(partnerId, msg);
        }

        // 6️⃣ Build DTO list
        List<SidebarUserDTO> sidebarUsers = users.stream()
                .map(u -> {
                    ChatMessage lastMsg = lastMessageMap.get(u.getId());
                    return SidebarUserDTO.builder()
                            .id(u.getId())
                            .userName(u.getUserName())
                            .fullName(u.getFirstName() + " " + u.getLastName())
                            .profileImageUrl(u.getProfilePicture())
                            .online(Boolean.TRUE.equals(u.getIsOnline()))
                            .lastActive(u.getLastSeen())
                            .message(lastMsg != null ? lastMsg.getMessage() : null)
                            .lastMessageTime(lastMsg != null ? lastMsg.getCreatedAt() : null)
                            .unReadMessageCount(unreadMap.getOrDefault(u.getId(), 0L))
                            .build();
                })
                .sorted(Comparator.comparing(
                        (SidebarUserDTO dto) -> Optional.ofNullable(dto.getLastMessageTime()).orElse(Instant.now()),
                        Comparator.reverseOrder()
                ))
                .toList();

        // 7️⃣ Paginate manually
        int start = Math.min(pageNumber * pageSize, sidebarUsers.size());
        int end = Math.min(start + pageSize, sidebarUsers.size());
        List<SidebarUserDTO> pageContent = sidebarUsers.subList(start, end);

        return PaginatedResponse.<SidebarUserDTO>builder()
                .content(pageContent)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements((long) sidebarUsers.size())
                .totalPages((int) Math.ceil((double) sidebarUsers.size() / pageSize))
                .build();
    }

    @Override
    public PaginatedResponse<ChatMessageResponseDTO> getChats(Long receiverId, Integer pageNumber, Integer pageSize, String sortOrder) {
        Long senderId = authUtil.loggedInUserIdForTesting();
        Conversation conversation = conversationService.findOrCreate(senderId, receiverId);

        // 2️⃣ Fetch deleted messages
        List<Long> deletedIds = deletedMessageRepository.findMessageIdsByUserId(senderId);
        if (deletedIds.isEmpty()) deletedIds = List.of(-1L);

        // 3️⃣ Mark as seen
        chatMessageRepository.markMessagesAsSeen(conversation.getId(), senderId, receiverId);

        // 4️⃣ Fetch messages
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by("createdAt").ascending()
                : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<ChatMessage> page = chatMessageRepository.findVisibleMessages(conversation.getId(), deletedIds, pageable);
        Long total = chatMessageRepository.countVisibleMessages(conversation.getId(), deletedIds);

        // 5️⃣ Convert to DTO
        List<ChatMessageResponseDTO> content = page.getContent().stream()
                .map(m -> new ChatMessageResponseDTO(
                        m.getId(),
                        m.getSender().getId(),
                        m.getReceiver().getId(),
                        m.getMessage(),
                        m.getCreatedAt(),
                        m.getUpdatedAt()
                ))
                .sorted(Comparator.comparing(ChatMessageResponseDTO::getCreatedAt))
                .toList();

        // 6️⃣ Send WebSocket updates
        messagingTemplate.convertAndSend("/topic/messagesSeen/" + receiverId,
                Map.of("senderId", senderId, "receiverId", receiverId));
        messagingTemplate.convertAndSend("/topic/sidebar/" + senderId,
                Map.of("event", "refreshSidebar"));

        // 7️⃣ Return paginated response
        return PaginatedResponse.<ChatMessageResponseDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(total)
                .totalPages(page.getTotalPages())
                .build();
    }


    public String clearChatsss(Long chatUserId) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();

        // 🧠 Step 1: Find or create conversation between the two users
        Conversation conversation = conversationRepository
                .findByParticipants(currentUserId, chatUserId)
                .orElseThrow(() -> new ResourceNotFoundException("conversation.notfound"));

        // 📨 Step 2: Get all messages from this conversation
        List<ChatMessage> messages = chatMessageRepository.findByConversationId(conversation.getId());
        if (messages.isEmpty()) {
            return "No messages to clear.";
        }

        // 🧩 Step 3: Prepare deleted message records for this user
        List<DeletedMessage> toInsert = messages.stream()
                .map(msg -> DeletedMessage.builder()
                        .user(userRepository.getReferenceById(currentUserId))
                        .message(msg)
                        .build())
                .toList();

        // 💾 Step 4: Save them (avoid duplicates)
        for (DeletedMessage del : toInsert) {
            deletedMessageRepository.findByUserIdAndMessageId(currentUserId, del.getMessage().getId())
                    .or(() -> Optional.of(deletedMessageRepository.save(del)));
        }

        // 🔔 Step 5: Optionally notify the WebSocket (sidebar refresh)
        messagingTemplate.convertAndSend("/topic/sidebar/" + currentUserId, Map.of(
                "event", "refreshSidebarForDeleteMessage"
        ));
        return "Chat cleared successfully.";
    }

    @Override
    public String clearChats(Long chatUserId) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        Conversation conversation = conversationService.findOrCreate(currentUserId, chatUserId);

        // 🧩 Step 2: Fetch all messages for that conversation
        List<ChatMessage> messages = chatMessageRepository.findByConversationId(conversation.getId());

        if (messages.isEmpty()) {
            return "no.messages.to.clear.";
        }

        // 🧾 Step 3: Prepare DeletedMessage records
        List<DeletedMessage> toInsert = messages.stream()
                .map(msg -> DeletedMessage.builder()
                        .user(userRepository.getReferenceById(currentUserId))
                        .message(msg)
                        .build())
                .toList();

        // 🧱 Step 4: Bulk insert (ignore duplicates)
        deletedMessageRepository.saveAll(toInsert);

        return "chat.cleared.successfully.";
    }

    @Override
    public String blockUser(Long blockedId) {
        Long blockerId = authUtil.loggedInUserIdForTesting();
        if(blockerId.equals(blockedId)){
            throw new APIException("cannot.block.yourself", HttpStatus.BAD_REQUEST);
        }
        boolean existsBlocked = blockedUserRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
        if(existsBlocked){
            blockedUserRepository.delete(blockedUserRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                    .orElseThrow(() -> new ResourceNotFoundException("blocked.user.notfound")));
            return "user.unblocked.successfully";
        }else{
            ChatBlockedUser blockedUser = ChatBlockedUser.builder()
                    .chatBlocker(userRepository.getReferenceById(blockerId))
                    .chatBlocked(userRepository.getReferenceById(blockedId))
                    .build();
            blockedUserRepository.save(blockedUser);
            return "user.blocked.successfully";
        }
    }

    @Override
    public String reportUser(ReportUserDTO request) {
        Long reporterId = authUtil.loggedInUserIdForTesting();
        ReportUser reportedUser = ReportUser.builder()
                .reporter(userRepository.getReferenceById(reporterId))
                .reported(userRepository.getReferenceById(request.getReportedUserId()))
                .reason(request.getReason())
                .build();
        reportUserRepository.save(reportedUser);
        return "user.reported.successfully";
    }

}
