package com.maven.neuto.controller;

import com.maven.neuto.model.ChatMessage;
import com.maven.neuto.payload.request.socket.ChatMessageDTO;
import com.maven.neuto.payload.request.socket.SendMessageDTO;
import com.maven.neuto.payload.request.socket.TypingDTO;
import com.maven.neuto.serviceImplement.ChatService;
import com.maven.neuto.serviceImplement.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;

    // Client sends to /app/chat.send
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageDTO dto, @Header("simpSessionAttributes") Map<String,Object> sessionAttrs) {
        Long senderId = (Long) sessionAttrs.get("userId");
        ChatMessageDTO saved = chatService.saveMessage(senderId, dto.getReceiverId(), dto.getMessage());
        // send to receiver queue
        messagingTemplate.convertAndSendToUser(dto.getReceiverId().toString(), "/queue/messages", saved);
        // 2️⃣ Send to receiver queue (new message)
        messagingTemplate.convertAndSendToUser(
                dto.getReceiverId().toString(),
                "/queue/messages",
                saved
        );

        // 3️⃣ Send back to sender (to reflect instantly)
        messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/messages",
                saved
        );

        // 4️⃣ Send unseen count for this conversation (sender → receiver)
        Long unseenCount = chatService.countUnreadMessagesBetweenUsers(dto.getReceiverId(), senderId);
        messagingTemplate.convertAndSendToUser(
                dto.getReceiverId().toString(),
                "/queue/unread-count",
                Map.of(
                        "fromUserId", senderId,
                        "unseenCount", unseenCount
                )
        );


        /*// 5️⃣ Optional: notify if receiver is online
        if (presenceService.isOnline(dto.getReceiverId())) {
            messagingTemplate.convertAndSendToUser(
                    dto.getReceiverId().toString(),
                    "/queue/notifications",
                    Map.of("type", "NEW_MESSAGE", "fromUserId", senderId)
            );
        }*/


    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingDTO dto, @Header("simpSessionAttributes") java.util.Map<String,Object> sessionAttrs) {
        Long from = (Long) sessionAttrs.get("userId");
        messagingTemplate.convertAndSendToUser(dto.getTo().toString(), "/queue/typing", new TypingDTO(from, dto.getTo()));
    }

    @MessageMapping("/chat.stopTyping")
    public void stopTyping(@Payload TypingDTO dto, @Header("simpSessionAttributes") java.util.Map<String,Object> sessionAttrs) {
        Long from = (Long) sessionAttrs.get("userId");
        messagingTemplate.convertAndSendToUser(dto.getTo().toString(), "/queue/stopTyping", new TypingDTO(from, dto.getTo()));
    }

}
