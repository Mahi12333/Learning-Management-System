package com.maven.neuto.service;

import com.maven.neuto.payload.request.Industry.IndustryCreateDTO;
import jakarta.validation.Valid;

public interface IndustryService {
    String createIndustry(IndustryCreateDTO request);
}
