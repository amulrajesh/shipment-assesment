package com.assignment.aggregationflux.service;

import com.assignment.aggregationflux.properties.ConfigProperties;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Service
@AllArgsConstructor
public class PricingService {

    private WebClientUtil webClientUtil;

    private ConfigProperties configProperties;

    public Mono<Map> pricings(String ids) {
        if(ids == null || ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }
        String url = configProperties.getPricingPath() + "?q=" + ids;
        return webClientUtil.invokeApi(url);
    }

}
