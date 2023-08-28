package com.assignment.aggregationflux.service;

import com.assignment.aggregationflux.utils.AppClient;
import com.assignment.aggregationflux.utils.AppConstant;
import com.assignment.aggregationflux.utils.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class ShipmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);

    private final ConcurrentLinkedQueue<Map<String, List<String>>> incomingQueue = new ConcurrentLinkedQueue<>();

    private final Map<String, List<String>> shipmentQueue = new HashMap<>();

    private final Map<String, Mono<Map>> outgoingMap = new HashMap<>();

    AppClient appClient;

    public ShipmentService(AppClient appClient) {
        this.appClient = appClient;
    }

    public Mono<Map> process(Optional<List<String>> ids) {

        if (ids.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }

        log.debug("Start Shipment processing");

        //Unique key to identify request and filter from bulk payload response
        String key = UUID.randomUUID().toString().replace("-", "" );

        AppUtil.addToIncomingQueueMap(incomingQueue, key, ids.get());

        shipmentQueue.put(key, ids.get());

        Mono<Map<String, List<String>>> mono = Mono.fromCallable(() -> {
            if (shipmentQueue.size() >= 5) {
                log.debug("Invoking API because Queue size reaches maximum " + shipmentQueue.size());
                appClient.invokeApi(incomingQueue, outgoingMap, AppConstant.TYP_SHIPMENT_STR);
                log.debug("Complete API because Queue size reaches maximum " + shipmentQueue.size());
                return shipmentQueue;
            }
            //Wait for 5 seconds
            Thread.sleep(5000);
            log.debug("Invoking API because of timer expiry " + shipmentQueue.size());
            appClient.invokeApi(incomingQueue, outgoingMap, AppConstant.TYP_SHIPMENT_STR);
            log.debug("Complete API because Queue size reaches maximum " + shipmentQueue.size());
            return shipmentQueue;
        });

        log.debug("End Shipment processing");

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
                .flatMap(id -> AppUtil.getResponse(key, shipmentQueue, outgoingMap))
                .map(m -> m);

    }
}
