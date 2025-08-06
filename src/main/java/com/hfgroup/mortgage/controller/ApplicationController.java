package com.hfgroup.mortgage.controller;

import com.hfgroup.mortgage.dto.request.ApplicationDTO;
import com.hfgroup.mortgage.model.Application;
import com.hfgroup.mortgage.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public Application createApplication(@RequestBody ApplicationDTO applicationDTO) {
        return applicationService.createApplication(applicationDTO);
    }

    @GetMapping
    public Application getApplication(@RequestParam String id){
        return new Application();
    }


}
