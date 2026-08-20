package com.syncria.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, RateLimitEntry> attempts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if ("/api/v1/auth/login".equals(path) && "POST".equalsIgnoreCase(request.getMethod())) {
            String ip = getClientIP(request);
            RateLimitEntry entry = attempts.compute(ip, (key, val) -> {
                RateLimitEntry current = val;
                if (current == null || current.isExpired()) {
                    current = new RateLimitEntry();
                }
                current.increment();
                return current;
            });

            if (entry.getCount() > 5) {
                log.warn("Rate limit exceeded for IP: {} ({} attempts in 60s)", ip, entry.getCount());
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Too many login attempts. Try again in 1 minute.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateLimitEntry {
        private final long windowStart;
        private int count;

        RateLimitEntry() {
            this.windowStart = System.currentTimeMillis();
            this.count = 0;
        }

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - windowStart > 60_000;
        }
    }
}
