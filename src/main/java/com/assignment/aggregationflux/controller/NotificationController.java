package com.assignment.aggregationflux.controller;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.aggregationflux.service.MergeAndCallService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
public class NotificationController {

    private final MergeAndCallService mergeAndCallService;

    public NotificationController(MergeAndCallService mergeAndCallService) {
        this.mergeAndCallService = mergeAndCallService;
    }

    @GetMapping("/process")
    public Mono<String> processRequest(@RequestParam String request) {
        return mergeAndCallService.processApiRequest("api", request);
        //return Mono.just("Request received: " + request);
    }
    

}
