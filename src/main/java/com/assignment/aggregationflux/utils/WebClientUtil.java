package com.assignment.aggregationflux.utils;

import com.assignment.aggregationflux.properties.ConfigProperties;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class WebClientUtil {

    private static final Logger log = LoggerFactory.getLogger(WebClientUtil.class);

    private WebClient webClient;

    private ConfigProperties configProperties;
    public Mono<Map> processRequest(List<String> ids, String typ) {
        if(ids == null || ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }
        String url = this.getUrl(typ) + "?q=" + String.join(",", ids);
        return this.invokeApi(url);
    }

    private String getUrl(String typ) {
        String output = "";
        switch (typ) {
            case "shipment-data":
                output = configProperties.getShipmentPath();
                break;
            case "tracking-data":
                output = configProperties.getTrackPath();
                break;
            case "pricing-data":
                output = configProperties.getPricingPath();
                break;
        }
        return output;
    }
    public Mono<Map> invokeApi(String url){
        log.debug("Calling API :: " + url);
        return this.webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorReturn(Collections.emptyMap()); // if no result, it returns 404, so return empty map
    }

}
