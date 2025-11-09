package com.maven.neuto.payload.request.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class TypingDTO {
    private Long from;
    private Long to;
}
