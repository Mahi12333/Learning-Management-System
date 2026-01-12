package com.maven.neuto.payload.request.log;

import lombok.*;

import java.time.Instant;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuditEvent {
    private String action;
    private String entity;
    private Long entityId;
    private Long userId;
    private Object oldValue;
    private Object newValue;
    private Instant timestamp;
}
