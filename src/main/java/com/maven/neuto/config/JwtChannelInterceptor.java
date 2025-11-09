package com.maven.neuto.config;

import com.maven.neuto.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.debug("STOMP CONNECT headers: {}", accessor.toNativeHeaderMap());

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    if (jwtUtils.validateJwtToken(token, false)) {
                        Long userId = jwtUtils.getUserIdFromJwtToken(token, false);
                        // set principal
                        UsernamePasswordAuthenticationToken user =
                                new UsernamePasswordAuthenticationToken(userId, null, List.of());
                        accessor.setUser(user);
                        // store userId in session attributes for event listeners
                        if (accessor.getSessionAttributes() != null) {
                            accessor.getSessionAttributes().put("userId", userId);
                        }
                        log.info("STOMP CONNECT authenticated userId={}", userId);
                        return message;
                    } else {
                        log.warn("Invalid JWT token in STOMP CONNECT");
                    }
                } catch (Exception e) {
                    log.warn("JWT validation error on STOMP CONNECT: {}", e.getMessage());
                }
            } else {
                log.warn("No Authorization header present in STOMP CONNECT");
            }

            // Reject: throw or return message causing error — choose IllegalArgument for now
            throw new IllegalArgumentException("Invalid or missing JWT token in STOMP CONNECT");
        }
        return message;
    }
}
