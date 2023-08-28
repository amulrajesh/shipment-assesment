package com.assignment.aggregationflux.service;

import com.assignment.aggregationflux.properties.ConfigProperties;
import com.assignment.aggregationflux.utils.AppClient;
import com.assignment.aggregationflux.utils.AppConstant;
import com.assignment.aggregationflux.utils.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class PricingService {

    private static final Logger log = LoggerFactory.getLogger(PricingService.class);

    private final ConcurrentLinkedQueue<Map<String, List<String>>> incomingQueue = new ConcurrentLinkedQueue<>();

    private final Map<String, List<String>> pricingQueue = new HashMap<>();

    private final Map<String, Mono<Map>> outgoingMap = new HashMap<>();

    AppClient appClient;
    
    ConfigProperties configProperties;

    public PricingService(AppClient appClient, ConfigProperties configProperties) {
        this.appClient = appClient;
        this.configProperties = configProperties;
    }

    public Mono<Map> process(Optional<List<String>> ids) {

        if (ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }

        log.debug("Start Pricing processing");

        //Unique key to identify request and filter from bulk payload response
        String key = UUID.randomUUID().toString().replace("-", "" );

        AppUtil.addToIncomingQueueMap(incomingQueue, key, ids.get());

        pricingQueue.put(key, ids.get());

        Mono<Map<String, List<String>>> mono = Mono.fromCallable(() -> {
            if (pricingQueue.size() >= configProperties.getQueueSize()) {
                log.debug("Invoking API because Queue size reaches maximum " + pricingQueue.size());
                appClient.invokeApi(incomingQueue, outgoingMap, AppConstant.TYP_PRICING_STR);
                log.debug("Complete API because Queue size reaches maximum " + pricingQueue.size());
                return pricingQueue;
            }
            //Wait for 5 seconds
            Thread.sleep(configProperties.getApiQueueTimeLimit());
            log.debug("Invoking API because of timer expiry " + pricingQueue.size());
            appClient.invokeApi(incomingQueue, outgoingMap, AppConstant.TYP_PRICING_STR);
            log.debug("Complete API because Queue size reaches maximum " + pricingQueue.size());
            return pricingQueue;
        });

        log.debug("End Pricing processing");

        return getOutput(mono, key);

    }

    /**
     * Iterate Mono
     * 1. Filter using key
     * 2. Get output from the OutgoingMap using key
     * 3. Repeat the flow when the result is empty
     */
    private Mono<Map> getOutput(Mono<Map<String, List<String>>> mono,
                                String key) {

        return mono
                .filter(id -> id.containsKey(key))
                .flatMap(id -> AppUtil.getResponse(key, pricingQueue, outgoingMap))
                .map(m -> m);

    }
}
