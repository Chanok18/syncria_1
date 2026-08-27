package com.syncria.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RateLimitingFilterTest {

    private RateLimitingFilter filter;
    private FilterChain filterChain;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new RateLimitingFilter();
        filterChain = mock(FilterChain.class);
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldAllowAuthRequestsUnderLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 5; i++) {
            filter.doFilterInternal(request, response, filterChain);
        }

        assertThat(response.getStatus()).isEqualTo(200);
        verify(filterChain, org.mockito.Mockito.times(5)).doFilter(request, response);
    }

    @Test
    void shouldBlockAuthRequestsOverLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 6; i++) {
            response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, filterChain);
        }

        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    void shouldAllowCrudRequestsUnderLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/contacts");
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 60; i++) {
            filter.doFilterInternal(request, response, filterChain);
        }

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldBlockCrudRequestsOverLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/contacts");
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 61; i++) {
            response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, filterChain);
        }

        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    void shouldAllowDashboardRequestsUnderLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/dashboard");
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 30; i++) {
            filter.doFilterInternal(request, response, filterChain);
        }

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldBlockDashboardRequestsOverLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/dashboard");
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 31; i++) {
            response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, filterChain);
        }

        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    void shouldNotRateLimitGetRequests() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/contacts");
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 100; i++) {
            filter.doFilterInternal(request, response, filterChain);
        }

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldRateLimitByLoginEmail() {
        String email = "test@example.com";

        boolean limited1 = filter.isRateLimitedByLoginEmail(email);
        assertThat(limited1).isFalse();

        for (int i = 0; i < 5; i++) {
            filter.isRateLimitedByLoginEmail(email);
        }

        boolean limited2 = filter.isRateLimitedByLoginEmail(email);
        assertThat(limited2).isTrue();
    }

    @Test
    void shouldRateLimitDifferentIPsSeparately() throws ServletException, IOException {
        MockHttpServletRequest request1 = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request1.setRemoteAddr("192.168.1.1");

        MockHttpServletRequest request2 = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request2.setRemoteAddr("192.168.1.2");

        for (int i = 0; i < 5; i++) {
            filter.doFilterInternal(request1, response, filterChain);
        }

        response = new MockHttpServletResponse();
        filter.doFilterInternal(request2, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldReturn429WithJsonMessage() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 6; i++) {
            response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, filterChain);
        }

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentAsString()).contains("Too many authentication attempts");
    }
}
