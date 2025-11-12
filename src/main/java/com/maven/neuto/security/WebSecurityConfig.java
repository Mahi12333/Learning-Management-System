package com.maven.neuto.security;

import com.maven.neuto.config.OAuth2LoginSuccessHandler;
import com.maven.neuto.security.jwt.AuthEntryPointJwt;
import com.maven.neuto.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;

    @Autowired
    @Lazy
    OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
       /* http.csrf(csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/auth/public/**")
        );*/
        http.cors(withDefaults());
        //! Disable the Csrf Token
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/csrf-token").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/favicon.ico").permitAll()

                //! User Part ----
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/image/file-upload").authenticated()
                .requestMatchers("/api/v1/image/multiple").authenticated()
                .requestMatchers("/api/v1/users/user-profile/{userName}").authenticated()
                .requestMatchers("/api/v1/users/create-user").permitAll()
                .requestMatchers("/api/v1/users/update-user/{userId}").authenticated()
                .requestMatchers("/api/v1/users/password-resect").permitAll()
                .requestMatchers("/api/v1/users/otp-verify-password").permitAll()
                .requestMatchers("/api/v1/users/reset-Password").permitAll()
                .requestMatchers("/api/v1/users/refreshToken").permitAll()
                .requestMatchers("/api/v1/users/teacher-member-invite").hasAnyAuthority("ADMIN", "TEACHER") // only admin can see all users
                .requestMatchers("/api/v1/users/logout").authenticated()
                .requestMatchers("/api/v1/users/user-from-community").authenticated()
                .requestMatchers("/api/v1/users/user-follow").authenticated()
                .requestMatchers("/api/v1/users/tags-for-member").authenticated()
                .requestMatchers("/api/v1/users/user-profile").authenticated()
                .requestMatchers("/api/v1/users/user-register").authenticated()
                .requestMatchers("/api/v1/users/profile-complete").authenticated()
                .requestMatchers("/api/v1/users/profile-visit").authenticated()
                .requestMatchers("/api/v1/users/profile-visit-tabs").authenticated()
                .requestMatchers("/api/v1/users/invite-member-teacher").authenticated()
                 //! Short Content Part---
                .requestMatchers("/api/v1/short-content/create-short-content").authenticated()
                .requestMatchers("/api/v1/short-content/updated-short-content").authenticated()
                .requestMatchers("/api/v1/short-content/delete-short-content").authenticated()
                .requestMatchers("/api/v1/short-content/like-dislike-short-content").authenticated()
                .requestMatchers("/api/v1/short-content/bookmark-short-content").authenticated()
                .requestMatchers("/api/v1/short-content/fetch-short-content").authenticated()

                //! Course Part---
                .requestMatchers("/api/v1/course/create-course").hasAnyAuthority("ADMIN", "TEACHER") // can do only admin and  Teacher
                .requestMatchers("/api/v1/course/updated-course").hasAnyAuthority("ADMIN", "TEACHER") // can do only admin and owner of course
                .requestMatchers("/api/v1/course/delete-course").hasAnyAuthority("ADMIN", "TEACHER") // can do only admin and owner of course
                .requestMatchers("/api/v1/course/public-course").authenticated()
                .requestMatchers("/api/v1/course/ongoing-course").hasAnyAuthority("STUDENT")
                .requestMatchers("/api/v1/course/recommend-course").hasAnyAuthority("STUDENT")
                //! Group Part---
                .requestMatchers("/api/v1/group/create-group").hasAnyAuthority("ADMIN", "TEACHER") // can do only admin and  Teacher
                .requestMatchers("/api/v1/group/updated-group").hasAnyAuthority("ADMIN", "TEACHER") // can do only admin and owner of course
                .requestMatchers("/api/v1/group/delete-group").hasAnyAuthority("ADMIN", "TEACHER") // can do only admin and owner of course
                .requestMatchers("/api/v1/group/public-group").authenticated()
                //! Event Part---
                .requestMatchers("/api/v1/event/create-event").hasAnyAuthority("STUDENT")
                .requestMatchers("/api/v1/event/updated-event").hasAnyAuthority("STUDENT")
                .requestMatchers("/api/v1/event/delete-event").hasAnyAuthority("STUDENT")
                .requestMatchers("/api/v1/event/public-event").authenticated()
                //! Conversation and Message Part---
                .requestMatchers("/api/v1/message/sidebar-users").authenticated()
                .requestMatchers("/api/v1/message/chats").authenticated()
                .requestMatchers("/ws/**").permitAll()

                .anyRequest().authenticated());

        http.oauth2Login(oauth2 ->
                oauth2.successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler((req, res, ex) -> {
                    log.error("OAuth2 login failed: ", ex);
                })
        );

        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler));

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        log.info("Security Configuration Loaded Successfully");

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Increased security strength
    }

}
