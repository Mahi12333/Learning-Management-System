package com.maven.neuto.config;

import com.maven.neuto.emun.RoleName;
import com.maven.neuto.model.Role;
import com.maven.neuto.model.User;
import com.maven.neuto.repository.RoleRepository;
import com.maven.neuto.security.jwt.JwtUtils;
import com.maven.neuto.security.service.UserDetailsImpl;
import com.maven.neuto.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Value("${frontend.url}")
    private String frontendUrl;

    String username;
    String idAttributeKey;

    @Deprecated
    public void onAuthenticationSuccessss(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        log.info("OAuth2 Login Success for Client: {}", oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
        if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()) || "google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            log.info("OAuth2 User Attributes: {}", principal.getAttributes());
            Map<String, Object> attributes = principal.getAttributes();
//           String email = attributes.getOrDefault("email", "").toString();
//            String name = attributes.getOrDefault("name", "").toString();
            String email = attributes.get("email") != null ? attributes.get("email").toString() : null;
            String name = attributes.get("name") != null ? attributes.get("name").toString() : username;

            if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = attributes.getOrDefault("login", "").toString();
                idAttributeKey = "id";
            } else if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = email.split("@")[0];
                idAttributeKey = "sub";
            } else {
                username = "";
                idAttributeKey = "id";
            }
            //System.out.println("HELLO OAUTH: " + email + " : " + name + " : " + username);

            userService.findByEmail(email)
                    .ifPresentOrElse(user -> {
                        log.info("Existing user logged in: {}", user);
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(user.getRole().getName())),
                                attributes,
                                idAttributeKey
                        );
                        log.info("Existing user oauthUser: {}", oauthUser);
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(user.getRole().getName())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        log.info("Existing user securityAuth in: {}", securityAuth);
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    }, () -> {
                        User newUser = new User();
                        Optional<Role> userRole = roleRepository.findByName(RoleName.USER.toString()); // Fetch existing role
                        if (userRole.isPresent()) {
                            newUser.setRole(userRole.get()); // Set existing role
                        } else {
                            // Handle the case where the role is not found
                            throw new RuntimeException("Default role not found");
                        }
                        newUser.setEmail(email);
                        newUser.setUserName(username);
                        newUser.setSetSignUpMethod(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        log.info("Before new user-----{}", newUser);
                        userService.registerUser(newUser);
                        log.info("After new user-----{}", newUser);
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(newUser.getRole().getName())),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(newUser.getRole().getName())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    });

        }
        this.setAlwaysUseDefaultTargetUrl(true);

        // JWT TOKEN LOGIC
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Extract necessary attributes
        String email = (String) attributes.get("email");
        System.out.println("OAuth2LoginSuccessHandler: " + username + " : " + email);
        log.info("attributes: {}", attributes);
        User user = userService.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found"));
        Set<SimpleGrantedAuthority> authorities = new HashSet<>(oauth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList()));

        log.info("Authorities from OAuth2User (before DB role): {}", authorities);
        log.info("Final authorities for user {}", user.getEmail());
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        log.info("Final authorities after adding DB role: {}", authorities);
        // Create UserDetailsImpl instance
        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getId(),
                user.getEmail(), // used as username
                null, // password
                true,
                true,
                true,
                true,
                authorities
        );

        // Generate JWT token
        String accessToken = jwtUtils.generateAccessToken(userDetails.getId());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getId());
        log.info("frontendUrl------: {}", frontendUrl);
        log.info("accessToken------: {}", accessToken);
        log.info("refreshToken------: {}", refreshToken);

        // Redirect to the frontend with the JWT token
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .build()
                .toUriString();
        log.info("Redirecting tolink------: {}", targetUrl);
        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        log.info("OAuth2 Login Success for provider {}: {}", provider, attributes);

        // Extract username and email safely
        String email = attributes.get("email") != null ? attributes.get("email").toString() : null;
        switch (provider) {
            case "google":
            case "apple":
                idAttributeKey = "sub";
                username = email != null ? email.split("@")[0] : "user_" + UUID.randomUUID();
                break;
            case "github":
                idAttributeKey = "id";
                username = attributes.getOrDefault("login", "user_" + UUID.randomUUID()).toString();
                break;
            case "facebook":
                idAttributeKey = "id";
                username = attributes.getOrDefault("name", "fb_user_" + UUID.randomUUID()).toString();
                break;
            default:
                idAttributeKey = "id";
                username = "user_" + UUID.randomUUID();
        }

        log.info("Extracted username: {}, email: {}, provider id key: {}", username, email, idAttributeKey);

        // Fetch existing user or create new one
        User user = userService.findByEmail(email).orElseGet(() -> {
            Role role = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUserName(username);
            newUser.setRole(role);
            newUser.setSetSignUpMethod(provider);
            log.info("Registering new user: {}", newUser);
            userService.registerUser(newUser);
            return newUser;
        });

        // Set OAuth2 user in security context
        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRole().getName())),
                attributes,
                idAttributeKey
        );
        Authentication securityAuth = new OAuth2AuthenticationToken(
                oauthUser,
                List.of(new SimpleGrantedAuthority(user.getRole().getName())),
                provider
        );
        SecurityContextHolder.getContext().setAuthentication(securityAuth);

        // Generate JWT tokens
        Set<SimpleGrantedAuthority> authorities = new HashSet<>(oauthUser.getAuthorities().stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                .collect(Collectors.toList()));
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));

        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                null,
                true, true, true, true,
                authorities
        );

        String accessToken = jwtUtils.generateAccessToken(userDetails.getId());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getId());

        log.info("Redirecting to frontend with tokens...");
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .build()
                .toUriString();

        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
