package com.trail_race.race_application.controller;


import com.trail_race.race_application.dto.response.ApplicationResponse;
import com.trail_race.race_application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping("/application/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Integer id) {
        return ResponseEntity.ok(applicationService.getApplication(id));
    }

    @GetMapping("/applications")
    public List<ApplicationResponse> getAllApplications() {
        return applicationService.getAllApplications();
    }

}
