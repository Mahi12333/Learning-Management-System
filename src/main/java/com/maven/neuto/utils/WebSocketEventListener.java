package com.maven.neuto.utils;

import com.maven.neuto.serviceImplement.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final PresenceService presenceService;


    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        log.info("✅ User connected with event {}", event);
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        // ✅ Extract userId from session attributes (not from headers)
        Object userIdObj = accessor.getSessionAttributes() != null
                ? accessor.getSessionAttributes().get("userId")
                : null;

        log.info("✅ User connected with sessionId={} and userId={}", sessionId, userIdObj);

        if (userIdObj != null) {
            Long userId = Long.parseLong(userIdObj.toString());
            presenceService.registerConnection(userId, sessionId);
            log.info("✅ Registered user {} as online (session={})", userId, sessionId);
        } else {
            log.warn("⚠️ No userId found in session attributes for sessionId={}", sessionId);
        }
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        presenceService.unregisterConnectionBySession(sessionId);
        log.info("❌ Disconnected session {}", sessionId);
    }

}
