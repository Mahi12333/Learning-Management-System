package com.maven.neuto.mapstruct;

import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.model.Community;
import com.maven.neuto.model.User;
import com.maven.neuto.repository.CommunityRepository;
import com.maven.neuto.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class MapperContext {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public MapperContext(CommunityRepository communityRepository, UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    public Community getCommunity(Long communityId) {
        return communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
