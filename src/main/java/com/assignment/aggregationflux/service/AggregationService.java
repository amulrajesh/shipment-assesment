package com.assignment.aggregationflux.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import com.assignment.aggregationflux.model.Aggregate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple3;

@Service
@AllArgsConstructor
public class AggregationService {

    ShipmentService shipmentService;

    PricingService pricingService;

    TrackingService trackingService;

    Scheduler scheduler;

    public Mono<Aggregate> aggregate(Optional<List<String>> pricing,
                                     Optional<List<String>> track,
                                     Optional<List<String>> shipments) {

        Mono<Map> trackingMono = trackingService.process(track).subscribeOn(scheduler);
        Mono<Map> shipmentMono = shipmentService.process(shipments).subscribeOn(scheduler);
        Mono<Map> pricingMono = pricingService.process(pricing).subscribeOn(scheduler);

        return Mono
                .zip(pricingMono, trackingMono, shipmentMono)
                .timeout(Duration.ofSeconds(10))
                .map(this::combine)
                .onErrorResume(this::emptyAggregate);

    }

    private Mono<Aggregate> emptyAggregate(Throwable throwable) {
        return Mono.just(new Aggregate());
    }

    private Aggregate combine(Tuple3<Map, Map, Map> tuple) {
        return new Aggregate(tuple.getT1(), tuple.getT2(), tuple.getT3());
    }
}
