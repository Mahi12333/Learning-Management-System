package com.maven.neuto.serviceImplement;

import com.maven.neuto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceService {

    private final Map<Long, String> userSessionMap = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public void registerConnection(Long userId, String sessionId) {
        log.info("Registering connection for userId with sessionId: {}{}", userId, sessionId);
        userSessionMap.put(userId, sessionId);
        sessionUserMap.put(sessionId, userId);
        userRepository.findById(userId).ifPresent(u -> {
            u.setIsOnline(true);
            u.setLastSeen(Instant.now());
            userRepository.save(u);
        });
        // ✅ 1. Broadcast to everyone
        //messagingTemplate.convertAndSend("/topic/online-users", getOnlineUserIds());

        // ✅ 2. Also send to the newly connected user (so they immediately get the list)
        // ⏳ Then broadcast global list (after 500ms delay)
//        CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS).execute(() -> {
//            messagingTemplate.convertAndSend("/topic/online-users", getOnlineUserIds());
//        });
        // 1) Send immediate personal snapshot (so new user never misses the list)
        List<Long> snapshot = getOnlineUserIds();
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/online-users", snapshot);

        // 2) Delay then broadcast global list to all subscribers
        CompletableFuture.delayedExecutor(250, TimeUnit.MILLISECONDS).execute(() -> {
            List<Long> all = getOnlineUserIds();
            log.info("Broadcasting online users: {}", all);
            messagingTemplate.convertAndSend("/topic/online-users", all);
        });
    }

    public void unregisterConnectionBySession(String sessionId) {
        log.info("unregisterConnectionBySession connection for sessionId: {}", sessionId);
        Long userId = sessionUserMap.remove(sessionId);
        if (userId != null) {
            userSessionMap.remove(userId);
            userRepository.findById(userId).ifPresent(u -> {
                u.setIsOnline(false);
                u.setLastSeen(Instant.now());
                userRepository.save(u);
            });
            log.info("unregisterConnectionBySession connection for userId: {}", userId);
            messagingTemplate.convertAndSend("/topic/online-users", getOnlineUserIds());
        }
    }

    public boolean isOnline(Long userId) {
        return userSessionMap.containsKey(userId);
    }

    public List<Long> getOnlineUserIds() {
        return new ArrayList<>(userSessionMap.keySet());
    }

    public Optional<String> getSessionIdForUser(Long userId) {
        return Optional.ofNullable(userSessionMap.get(userId));
    }
}


