package com.maven.neuto.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @Pointcut("@annotation(com.example.project.annotation.Auditable)")
    public void auditableMethods() {}

    @After("auditableMethods()")
    public void auditAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("[AUDIT] User performed action in method: {}", methodName);

        // TODO: Save into Elasticsearch
        // auditRepository.save(new AuditLog(userId, methodName, LocalDateTime.now()));
    }
}
