package com.assignment.aggregationflux.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MergeAndCallService {

	private final Map<String, Sinks.Many<String>> apiQueues = new HashMap();
	private final ThirdPartyApiService thirdPartyApiService;

	public MergeAndCallService(ThirdPartyApiService thirdPartyApiService) {
		this.thirdPartyApiService = thirdPartyApiService;
	}

	public Mono<String> processApiRequest(String apiName, String requestData) {
					return Mono.empty(); // Wait for more requests to accumulate
	}

	private Mono<String> flushQueueAndMakeBulkRequest(String apiName) {
		return apiQueues.get(apiName).asFlux().buffer(5)
				.flatMap(requests -> thirdPartyApiService.makeBulkRequest(apiName, requests)).then(Mono.defer(() -> {
					apiQueues.get(apiName).tryEmitComplete();
					return Mono.just("Bulk request completed for " + apiName);
				}));
	}
}