//package com.maven.neuto.config;
//
//import com.maven.neuto.security.jwt.JwtUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtHandshakeInterceptor implements HandshakeInterceptor {
//    private final JwtUtils jwtUtils;
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//        List<String> authHeaders = request.getHeaders().get("Authorization");
//        log.info("Attempting WebSocket handshake with Authorization headers: {}", authHeaders);
//
//        String token = null;
//
//        // 1️⃣ Try Authorization header
//        if (authHeaders != null && !authHeaders.isEmpty() && authHeaders.get(0).startsWith("Bearer ")) {
//            token = authHeaders.get(0).substring(7);
//        }
//        // 2️⃣ Try query parameter fallback
//        else if (request instanceof ServletServerHttpRequest servletRequest) {
//            HttpServletRequest httpRequest = servletRequest.getServletRequest();
//            token = httpRequest.getParameter("token");
//        }
//
//        log.info("Attempting WebSocket handshake, token found={}", token != null);
//
//        // 3️⃣ Validate JWT
//        if (token != null && jwtUtils.validateJwtToken(token, false)) {
//            Long userId = jwtUtils.getUserIdFromJwtToken(token, false);
//            log.info("✅ WebSocket Auth Success. userId={}", userId);
//            attributes.put("userId", userId);
//            return true;
//        }
//
//        log.error("❌ WebSocket handshake failed. Invalid or missing token.");
//        return false;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
//
//
//    }
//}
