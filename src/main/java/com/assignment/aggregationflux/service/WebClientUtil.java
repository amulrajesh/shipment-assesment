package com.assignment.aggregationflux.service;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Service
@AllArgsConstructor
public class WebClientUtil {
    private static final Logger log =
            LoggerFactory.getLogger(WebClientUtil.class);

    private WebClient webClient;

    public Mono<Map> invokeApi(String url){
    	
    	log.info("Calling API " + url);
    	
        return this.webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorReturn(Collections.emptyMap()); // if no result, it returns 404, so return empty map
    }

}
