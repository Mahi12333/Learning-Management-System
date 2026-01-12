package com.maven.neuto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.neuto.model.AuditLog;
import com.maven.neuto.payload.request.log.AuditEvent;
import com.maven.neuto.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditEventListener {
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    //! Listen for AuditEvent and save to AuditLog after transaction commit using @TransactionalEventListener

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditEvent(AuditEvent event) {

        AuditLog log = new AuditLog();
        log.setAction(event.getAction());
        log.setEntity(event.getEntity());
        log.setEntityId(Long.valueOf(event.getEntityId())); // string to Long conversion may be needed
        log.setUserId(event.getUserId());

        log.setOldValue(toJson(event.getOldValue()));
        log.setNewValue(toJson(event.getNewValue()));

        auditLogRepository.save(log);
    }

    private String toJson(Object obj) {
        try {
            return obj == null ? null : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
