package com.assignment.aggregationflux.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@AllArgsConstructor
public class AggregateService {
    private static final Logger log =
            LoggerFactory.getLogger(AggregateService.class);

    ShipmentService shipmentService;

    private List<String> shipmentQueue = Collections.emptyList();

    private final AtomicBoolean apiInvoked = new AtomicBoolean(Boolean.FALSE);

    private final CountDownLatch latch = new CountDownLatch(1);

    public Mono<Map> process(List<String> ids) throws InterruptedException {

        if (ids == null) {
            return Mono.just(Collections.emptyMap());
        }

        shipmentQueue.addAll(ids);

        Mono<List<String>> mono = Mono.fromCallable(() -> {
            if (shipmentQueue.size() >= 5) {
                log.info("Queue size is " + shipmentQueue.size());
                if (apiInvoked.compareAndSet(false, true))
                    return shipmentQueue;
            }
            Thread.sleep(5000);
            log.info("Time limit reached" );
            apiInvoked.compareAndSet(false, true);
            return shipmentQueue;
        });

        return mono.flatMap(id -> {
            if(apiInvoked.get()) {
                Mono<Map> output = shipmentService.shipments(id)
                        .log()
                        .doFinally(op -> {
                            shipmentQueue.clear();
                            apiInvoked.set(false);
                            latch.countDown();
                        });
                return output;
            }
            return Mono.empty();
        });

    }
}
