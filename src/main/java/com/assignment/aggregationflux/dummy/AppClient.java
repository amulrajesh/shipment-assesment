package com.assignment.aggregationflux.dummy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.assignment.aggregationflux.properties.ConfigProperties;
import com.assignment.aggregationflux.service.WebClientUtil;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AppClient {

    private WebClientUtil webClientUtil;

    private ConfigProperties configProperties;

    public Mono<Map> getPricing(List<String> ids) {
        if(ids == null || ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }
        String url = configProperties.getPricingPath() + "?q=" + String.join(",", ids);
        return webClientUtil.invokeApi(url);
    }

    public Mono<Map> getTrack(List<String> ids) {
        if(ids == null || ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }
        String url = configProperties.getTrackPath() + "?q=" + String.join(",", ids);
        return webClientUtil.invokeApi(url);
    }

    public Mono<Map> getShipment(List<String> ids) {
        if(ids == null || ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }
        String url = configProperties.getShipmentPath() + "?q=" + String.join(",", ids);
        return webClientUtil.invokeApi(url);
    }

}
