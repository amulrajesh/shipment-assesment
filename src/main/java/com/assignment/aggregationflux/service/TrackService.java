package com.assignment.aggregationflux.service;

import com.assignment.aggregationflux.properties.ConfigProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Service
@AllArgsConstructor
public class TrackService {

    private WebClientUtil webClientUtil;

    private ConfigProperties configProperties;

    public Mono<Map> track(String ids) {
        if(ids == null || ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }
        String url = configProperties.getTrackPath() + "?ids=" + ids;
        return webClientUtil.invokeApi(url);
    }

}
