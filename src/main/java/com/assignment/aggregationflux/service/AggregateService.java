package com.assignment.aggregationflux.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.cache.CacheMono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@SuppressWarnings({ "deprecation", "rawtypes" })
public class AggregateService {
	
	
	private static final Logger log = LoggerFactory.getLogger(AggregateService.class);

    ShipmentService shipmentService;
    
    public AggregateService(ShipmentService shipmentService) {
    	this.shipmentService = shipmentService;
    }

    private List<String> shipmentQueue = new ArrayList<String>();
    
    private final AtomicBoolean apiInvoked = new AtomicBoolean(Boolean.FALSE);

    private final CountDownLatch latch = new CountDownLatch(1);
    
    private String key = "";
    
    Map<String, Signal<? extends Map>> mapStringSignalCache = new HashMap<>();
    
    public Mono<Map> process(List<String> ids) throws InterruptedException {

        if (ids == null) {
            return Mono.just(Collections.emptyMap());
        }

        shipmentQueue.addAll(ids);   
        
        Mono<List<String>> mono = Mono.fromCallable(() -> {
            if (shipmentQueue.size() >= 5) {
                log.info("Queue size is " + shipmentQueue.size());
                if (apiInvoked.compareAndSet(false, true)) {
                	key = UUID.randomUUID().toString().replace("-", "");
                    return shipmentQueue;
                }
            }
            Thread.sleep(5000);
            log.info("Time limit reached" + key);
            apiInvoked.compareAndSet(false, true);
            key = UUID.randomUUID().toString().replace("-", "");
            return shipmentQueue;
        });
        
        return invokeApi(mono, key);

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
    		/*
            if(apiInvoked.get()) {
                Mono<Map> output = shipmentService.shipments(id, "shipment-path")
                        .log()
                        .doFinally(op -> {
                        	shipmentQueue.clear();
                            apiInvoked.set(false);
                            latch.countDown();
                        });
                return output;
            }
            */
            return Mono.empty();
        });
    	
    }

}
