package com.assignment.aggregationflux.service;


import java.util.List;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ThirdPartyApiService {

	public Mono<String> makeBulkRequest(String apiName, List<String> requests) {
        // Simulate making a bulk request to the third-party API
        String bulkRequestData = String.join(",", requests);
        return Mono.just("Bulk response for " + apiName + ": " + bulkRequestData);
    }
}
