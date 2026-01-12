package com.maven.neuto.aspect;

import com.maven.neuto.model.AuditLog;
import com.maven.neuto.payload.request.log.AuditEvent;
import com.maven.neuto.repository.AuditLogRepository;
import com.maven.neuto.utils.AuthUtil;
import com.maven.neuto.utils.EntityFetcher;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditLogRepository auditLogRepository;
    private final EntityFetcher entityFetcher;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;


    //! Implementaation auditLog using AOP Concept--
    /*@Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        //log.info("result info---", joinPoint);
        Object oldEntity = null;
        Object result;

        // 🔹 Fetch OLD data for UPDATE / DELETE
        if (auditable.action().equalsIgnoreCase("UPDATE")
                || auditable.action().equalsIgnoreCase("DELETE")) {

            Object[] args = joinPoint.getArgs();
            //log.info("args info---", args);
            if (args.length > 0) {
                oldEntity = entityFetcher.fetchOldEntity(args[0]);
            }
        }

        // 🔹 Proceed method execution
        result = joinPoint.proceed();
        //log.info("result info---", result);
        // 🔹 Save audit log AFTER success
        AuditLog log = new AuditLog();
        log.setAction(auditable.action());
        log.setEntity(auditable.entity());
        log.setTimestamp(Instant.now());

        // Entity ID
        extractEntityId(result).ifPresent(id -> log.setEntityId((Long) id));

        // OLD & NEW values
        log.setOldValue(entityFetcher.toJson(oldEntity));
        log.setNewValue(entityFetcher.toJson(result));

        // USER ID (SecurityContext)
        log.setUserId(authUtil.loggedInUserIdForTesting());

        auditLogRepository.save(log);

        return result;
    }

    private Optional<Long> extractEntityId(Object entity) {
        if (entity == null) return Optional.empty();

        // DTO support
        try {
            Method method = entity.getClass().getMethod("getId");
            return Optional.ofNullable((Long) method.invoke(entity));
        } catch (Exception ignored) {}

        // Entity support
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return Optional.ofNullable((Long) field.get(entity));
                } catch (Exception ignored) {}
            }
        }
        return Optional.empty();
    }
     */

    @Around("@annotation(auditable)")
    public Object audit( ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {

        Object oldValue = null;
        Object result;

        // 🔹 Capture OLD value for UPDATE / DELETE
        if (auditable.action().equalsIgnoreCase("UPDATE")
                || auditable.action().equalsIgnoreCase("DELETE")) {

            Object[] args = joinPoint.getArgs();
            //log.info("args info---", args);
            if (args.length > 0) {
                oldValue = entityFetcher.fetchOldEntity(args[0]);
            }
        }

        // 🔹 Execute actual method
        result = joinPoint.proceed();
        Long entityId = extractEntityId(result).orElse(null);

        // 🔹 Publish event AFTER method success
        eventPublisher.publishEvent(
                AuditEvent.builder()
                        .action(auditable.action())
                        .entity(auditable.entity())
                        .entityId(entityId)
                        .userId(authUtil.loggedInUserIdForTesting())
                        .oldValue(oldValue)
                        .newValue(result)
                        .build()
        );

        return result;
    }

    private Optional<Long> extractEntityId(Object entity) {
        if (entity == null) return Optional.empty();

        // DTO support
        try {
            Method method = entity.getClass().getMethod("getId");
            return Optional.ofNullable((Long) method.invoke(entity));
        } catch (Exception ignored) {}

        // Entity support
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return Optional.ofNullable((Long) field.get(entity));
                } catch (Exception ignored) {}
            }
        }
        return Optional.empty();
    }

}
