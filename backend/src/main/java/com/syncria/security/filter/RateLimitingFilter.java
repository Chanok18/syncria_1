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

    private final Map<String, RateLimitEntry> ipAttempts = new ConcurrentHashMap<>();
    private final Map<String, RateLimitEntry> emailAttempts = new ConcurrentHashMap<>();

    private static final int AUTH_LIMIT = 5;
    private static final int CRUD_LIMIT = 60;
    private static final int DASHBOARD_LIMIT = 30;
    private static final long WINDOW_MS = 60_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String ip = getClientIP(request);

        if (isAuthEndpoint(path, method)) {
            if (isRateLimited(ip, AUTH_LIMIT, ipAttempts)) {
                log.warn("Rate limit exceeded for IP on auth endpoint: {} (path={})", ip, path);
                send429(response, "Too many authentication attempts. Try again in 1 minute.");
                return;
            }
        } else if (isCrudEndpoint(path, method)) {
            if (isRateLimited(ip, CRUD_LIMIT, ipAttempts)) {
                log.warn("Rate limit exceeded for IP on CRUD endpoint: {} (path={})", ip, path);
                send429(response, "Too many requests. Try again in 1 minute.");
                return;
            }
        } else if (isDashboardEndpoint(path, method)) {
            if (isRateLimited(ip, DASHBOARD_LIMIT, ipAttempts)) {
                log.warn("Rate limit exceeded for IP on dashboard: {} (path={})", ip, path);
                send429(response, "Too many requests. Try again in 1 minute.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public boolean isRateLimitedByLoginEmail(String email) {
        if (email == null || email.isBlank()) return false;
        String key = "login:" + email.toLowerCase().trim();
        return isRateLimited(key, AUTH_LIMIT, emailAttempts);
    }

    private boolean isAuthEndpoint(String path, String method) {
        return path.startsWith("/api/v1/auth/")
                && ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method));
    }

    private boolean isCrudEndpoint(String path, String method) {
        if (!path.startsWith("/api/v1/")) return false;
        if (path.startsWith("/api/v1/auth/") || path.startsWith("/api/v1/health")) return false;
        return "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);
    }

    private boolean isDashboardEndpoint(String path, String method) {
        return "/api/v1/dashboard".equals(path) && "GET".equalsIgnoreCase(method);
    }

    private boolean isRateLimited(String key, int limit, Map<String, RateLimitEntry> store) {
        RateLimitEntry entry = store.compute(key, (k, val) -> {
            RateLimitEntry current = val;
            if (current == null || current.isExpired()) {
                current = new RateLimitEntry();
            }
            current.increment();
            return current;
        });
        return entry.getCount() > limit;
    }

    private void send429(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
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
            return System.currentTimeMillis() - windowStart > WINDOW_MS;
        }
    }
}
