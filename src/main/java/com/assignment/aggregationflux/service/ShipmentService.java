package com.assignment.aggregationflux.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@SuppressWarnings({ "deprecation", "rawtypes" })
public class ShipmentService {	
	
	private static final Logger log = LoggerFactory.getLogger(AggregateService.class);

    private WebClientUtil webClientUtil;
    
    public ShipmentService(WebClientUtil webClientUtil) {
    	this.webClientUtil = webClientUtil;
    }

    private final ConcurrentLinkedQueue<Mono<List<String>>> queue = new ConcurrentLinkedQueue<Mono<List<String>>>();
    
    private final AtomicBoolean apiInvoked = new AtomicBoolean(Boolean.FALSE);

    private final CountDownLatch latch = new CountDownLatch(1);
    
    private String key = "";
    
    Map<String, Signal<? extends Map>> mapStringSignalCache = new HashMap<>();
    
    public Mono<Map> process(List<String> ids) throws InterruptedException {

        if (ids == null) {
            return Mono.just(Collections.emptyMap());
        }

        Mono<List<String>> data = Mono.just(ids);
        
        queue.add(data);
        
        Mono<List<String>> mono = Mono.fromCallable(() -> {
            if (queue.size() >= 2) {
                log.info("Queue size is " + queue.size());
                if (apiInvoked.compareAndSet(false, true)) {
                	key = UUID.randomUUID().toString().replace("-", "");
                    return getMergedData();
                }
            }
            Thread.sleep(5000);
            log.info("Time limit reached " + key);
            apiInvoked.compareAndSet(false, true);
            key = UUID.randomUUID().toString().replace("-", "");
            return getMergedData();
        });
        
        return invokeApi(mono, key);

    }
    
    private List<String> getMergedData() {
    	List<String> mergedData = new ArrayList<String>();
    	while(!queue.isEmpty()) {
    		mergedData.addAll(queue.poll().block());
    	}
    	return mergedData;
    }
    
	private Mono<Map> invokeApi(Mono<List<String>> mono, String key) {
    	log.info("Key " + key);
    	return CacheMono
                .lookup(mapStringSignalCache, key)
                .onCacheMissResume(this.fetchApiResponse(mono)).cache(Duration.ofSeconds(12));
    	
    }
    
    private Mono<Map> fetchApiResponse(Mono<List<String>> mono) {
    	
    	log.info("Cache miss ");
    	
    	return mono.flatMap(id -> {
            if(apiInvoked.get()) {
                Mono<Map> output = webClientUtil.callApi(id, "shipment-path")
                        .log()
                        .doFinally(op -> {
                            apiInvoked.set(false);
                            latch.countDown();
                        });
                return output;
            }
            return Mono.empty();
        });
    	
    }
}
