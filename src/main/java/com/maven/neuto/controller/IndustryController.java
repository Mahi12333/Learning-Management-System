package com.maven.neuto.controller;


import com.maven.neuto.config.MessageConfig;
import com.maven.neuto.payload.request.Industry.IndustryCreateDTO;
import com.maven.neuto.service.IndustryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;


@RestController
@RequestMapping("/api/v1/industry")
@RequiredArgsConstructor
public class IndustryController {
    private final IndustryService industryService;
    private final MessageConfig messageConfig;

    @PostMapping("/create-industry")
    public ResponseEntity<?> createIndustry(@Valid @RequestBody IndustryCreateDTO request, Locale locale){
        String response = industryService.createIndustry(request);
        String localizedMessage = messageConfig.getMessage(response, null, locale);
        return new ResponseEntity<>(localizedMessage, HttpStatus.CREATED);
    }
}
