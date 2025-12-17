package com.maven.neuto.payload.request.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestion {
    private String question;
    private String answer;
    private Map<String, String> options;
}
