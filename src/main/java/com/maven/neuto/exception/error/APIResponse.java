package com.maven.neuto.exception.error;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse {
    public String message;
    public boolean status;
}
