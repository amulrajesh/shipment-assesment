package com.assignment.aggregationflux.service;

import com.assignment.aggregationflux.properties.ConfigProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ShipmentService {

    private WebClientUtil webClientUtil;

    private ConfigProperties configProperties;

    public Mono<Map> shipments(List<String> ids) {
        if(ids == null || ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }
        System.out.println("Calling shipment API " + ids);
        String val = String.join(",", ids);
        String url = configProperties.getShipmentPath() + "?q=" + val;
        return webClientUtil.invokeApi(url);
    }
}
