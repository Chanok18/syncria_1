package com.syncria.module.dashboard.controller;

import com.syncria.module.dashboard.dto.DashboardResponseDTO;
import com.syncria.module.dashboard.service.DashboardService;
import com.syncria.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        DashboardResponseDTO dashboard = dashboardService.getDashboard(companyId);
        return ResponseEntity.ok(dashboard);
    }
}
