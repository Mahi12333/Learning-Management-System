package com.maven.neuto.utils;

import com.maven.neuto.exception.APIException;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.model.Community;
import com.maven.neuto.model.User;
import com.maven.neuto.repository.CommunityRepository;
import com.maven.neuto.repository.UserRepository;
import com.maven.neuto.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;


    @Transactional(readOnly = true)
    public Long loggedInUserIdForTesting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new APIException("no.authenticated.user.found", HttpStatus.BAD_REQUEST);
        }

        Long userId = userDetails.getId();
        return userId;
    }

    @Transactional(readOnly = true)
    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new APIException("no.authenticated.user.found", HttpStatus.BAD_REQUEST);
        }
        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new APIException("User Not Found with username", HttpStatus.BAD_REQUEST));

        log.info("currentUser info name--- {}", user.getFirstName());
        log.info("currentUser email--- {}", user.getEmail());

        return user;
    }

    @Transactional
    public String loggedInEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getEmail();
    }

    @Transactional
    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication---------", authentication.getName(), authentication);
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getId();
    }

    /*@Transactional
    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
        return user;
    }*/

    @Transactional(readOnly = true)
    public Long communityId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new APIException("no.authenticated.user.found", HttpStatus.BAD_REQUEST);
        }
        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with username: " + authentication.getName()));
        Long communityId = user.getUserCommunity().getId();
        Community community = communityRepository.findById(user.getUserCommunity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("community.not.found"));

        return community.getId();
    }

}
