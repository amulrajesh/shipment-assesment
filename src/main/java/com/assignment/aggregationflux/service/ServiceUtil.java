package com.assignment.aggregationflux.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ServiceUtil {

    public Mono<String> validate(String ids) {
        if(ids == null || ids.isEmpty()) {
            return Mono.empty();
        }
        return Mono.just(ids);
    }
}
