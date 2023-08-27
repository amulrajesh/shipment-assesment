package com.assignment.aggregationflux.service;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.assignment.aggregationflux.properties.ConfigProperties;

import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class WebClientUtil {
    private static final Logger log =
            LoggerFactory.getLogger(WebClientUtil.class);

    private WebClient webClient;

    private ConfigProperties configProperties;

    public Mono<Map> invokeApi(String url){
    	
    	log.info("Calling API " + url);
    	
        return this.webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnSuccess(t -> System.out.println("Success"))
                .onErrorReturn(Collections.emptyMap()); // if no result, it returns 404, so return empty map
    }

    public Mono<Map> callApi(List<String> ids, String typ) {
        if(ids == null || ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }
        String val = String.join(",", ids);
        String url = this.getPath(typ) + "?q=" + val;
        return this.invokeApi(url);
    }
    
    private String getPath(String typ) {
    	String path = "";
    	switch(typ) {
    	case "shipment-path":
    		path = configProperties.getShipmentPath();
    		break;
    	case "track-path":
    		path = configProperties.getTrackPath();
    		break;

    	case "pricing-path":
    		path = configProperties.getPricingPath();
    		break;
    	}
    	return path;
    }

}
