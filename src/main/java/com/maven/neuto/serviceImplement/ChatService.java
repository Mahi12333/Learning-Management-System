package com.maven.neuto.serviceImplement;

import com.maven.neuto.exception.APIException;
import com.maven.neuto.model.ChatMessage;
import com.maven.neuto.payload.request.socket.ChatMessageDTO;
import com.maven.neuto.repository.BlockedUserRepository;
import com.maven.neuto.repository.ChatMessageRepository;
import com.maven.neuto.repository.DeletedMessageRepository;
import com.maven.neuto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository messageRepo;
    private final DeletedMessageRepository deletedRepo;
    private final BlockedUserRepository blockedRepo;
    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageDTO saveMessage(Long senderId, Long receiverId, String text) {
        // check block
        if (blockedRepo.existsByBlockerIdAndBlockedId(receiverId, senderId)) {
            throw new APIException("You are blocked by the receiver", HttpStatus.BAD_REQUEST);
        }
        var conv = conversationService.findOrCreate(senderId, receiverId);
         ChatMessage saved = new ChatMessage();
         saved.setSender(userRepository.getReferenceById(senderId));
         saved.setReceiver(userRepository.getReferenceById(receiverId));
         saved.setMessage(text);
         saved.setConversation(conv);
         saved.setIsSeen(false);

         ChatMessage saveMessage = messageRepo.save(saved);

        ChatMessageDTO m = ChatMessageDTO.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .conversationId(conv.getId())
                .message(text)
                .isSeen(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return m;
    }


    public Long countUnreadMessagesBetweenUsers(Long receiverId, Long senderId) {
        return messageRepo.countUnreadMessagesBetweenUsers(receiverId, senderId);
    }
}
