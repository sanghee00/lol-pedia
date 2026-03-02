package com.example.lolpedia.global.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiagnosticController {

    @GetMapping("/thread-check")
    public ResponseEntity<String> checkThread() {
        Thread current = Thread.currentThread();
        return ResponseEntity.ok(
            "isVirtual: " + current.isVirtual() + ", name: " + current.getName()
        );
    }

}
