package com.maven.neuto.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.maven.neuto.config.FirebaseConfig;
import com.maven.neuto.emun.SourceType;
import com.maven.neuto.model.FcNotification;
import com.maven.neuto.model.User;
import com.maven.neuto.repository.NotificationRepository;
import com.maven.neuto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final Executor cachedThreadPool;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;

    public void sendRealTimeNotification(String message) {
        cachedThreadPool.execute(() -> {
            //TODO
            // push WebSocket message
        });
    }

    public void sendNotification(
            User senderId,
            User receiverId,
            String title,
            String message,
            SourceType type,
            Long referenceId
    ) {

        // 1️⃣ Save notification in DB
        FcNotification notification = new FcNotification();
        notification.setSenderNotification(senderId);
        notification.setReceiverNotification(receiverId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setSourceType(type);
        notification.setRedirectUrl(redirectPathByType(type, referenceId));

        notificationRepository.save(notification);

        // 2️⃣ Async push
        cachedThreadPool.execute(() -> {
            try {
                sendPushNotification(receiverId, title, message, type, referenceId);
                //sendWebSocketNotification(receiverId, notification);
            } catch (Exception e) {
                log.error("Notification failed for user {}", receiverId, e);
            }
        });
    }


    private void sendPushNotification(
            User receiverId,
            String title,
            String message,
            SourceType type,
            Long referenceId
    ) throws FirebaseMessagingException {

        // Fetch device token from DB
        String fcmToken = getUserFcmToken(receiverId);
        if (fcmToken == null) return;

        Message firebaseMessage = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build()
                )
                .putData("type", type.name())
                .putData("referenceId", String.valueOf(referenceId))
                .build();

        firebaseMessaging.send(firebaseMessage);
    }

    private String getUserFcmToken(User receiverId) {

        return userRepository.findUserById(receiverId.getId())
                .map(User::getFcmToken)
                .filter(token -> !token.isBlank())
                .orElseGet(() -> {
                    log.warn("No FCM token for user {}", receiverId);
                    return null;
                });
    }

    private String redirectPathByType(SourceType type, Long referenceId) {
        switch (type) {
            case FOLLOW:
                return "/messages/" + referenceId;
            case COURSE:
                return "/friends/requests";
            case EVENT:
                return "/events/" + referenceId;
            default:
                return "/";
        }
    }

}
