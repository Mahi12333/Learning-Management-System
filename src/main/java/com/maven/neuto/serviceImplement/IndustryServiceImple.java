package com.maven.neuto.serviceImplement;

import com.maven.neuto.exception.APIException;
import com.maven.neuto.model.Industry;
import com.maven.neuto.payload.request.Industry.IndustryCreateDTO;
import com.maven.neuto.repository.IndustryRepository;
import com.maven.neuto.service.IndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class IndustryServiceImple implements IndustryService {
    private final IndustryRepository industryRepository;

    @Override
    public String createIndustry(IndustryCreateDTO request) {
       industryRepository.findByName(request.getName()).ifPresent(existing-> { throw new APIException("Industry.already.exists", HttpStatus.BAD_REQUEST); });
        Industry industry = new Industry();
        industry.setName(request.getName());
        industry.setCreatedAt(Instant.now());
        industry.setUpdatedAt(Instant.now());
        industryRepository.save(industry);
        return "Industry.created.successfully";
    }
}
