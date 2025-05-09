package com.project.snackpick.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WellKnownController {

    @RequestMapping("/.well-known/**")
    public ResponseEntity<Void> ignoreWellKnownRequests() {
        return ResponseEntity.noContent().build();
    }
}
