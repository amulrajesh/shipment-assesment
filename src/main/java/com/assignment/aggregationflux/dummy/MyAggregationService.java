package com.assignment.aggregationflux.dummy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import static java.util.function.Predicate.not;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignment.aggregationflux.model.Aggregate;
import com.assignment.aggregationflux.service.PricingService;
import com.assignment.aggregationflux.service.ShipmentService;
import com.assignment.aggregationflux.service.TrackService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class MyAggregationService {

    @Autowired
    private AppClient appClient;
    @Autowired
    private TrackService trackClient;
    @Autowired
    private ShipmentService shipmentsClient;

    private final Sinks.Many<List<String>> pricingSink = Sinks.many().replay().all();
    private final Sinks.Many<List<String>> trackSink = Sinks.many().replay().all();
    private final Sinks.Many<List<String>> shipmentsSink = Sinks.many().replay().all();

    private final Flux<Mono<Map>> pricingFlux;
    private final Flux<Mono<Map>> trackFlux;
    private final Flux<Mono<Map>> shipmentsFlux;

    public MyAggregationService() {
        pricingFlux = pricingSink.asFlux()
                .flatMapIterable(Function.identity())
                .bufferTimeout(5, Duration.ofSeconds(5))
                .doOnNext(t -> System.out.println("Register pricing" + t.size()))
                .map(pricingList -> appClient.getPricing(pricingList));

        trackFlux = trackSink.asFlux()
                .flatMapIterable(Function.identity())
                .bufferTimeout(5, Duration.ofSeconds(5))
                .doOnNext(t -> System.out.println("Register tracking"))
                .map(trackList -> appClient.getTrack(trackList));

        shipmentsFlux = shipmentsSink.asFlux()
                .flatMapIterable(Function.identity())
                .bufferTimeout(5, Duration.ofSeconds(5))
                .doOnNext(t -> System.out.println("Register shipment"))
                .map(shipmentsList -> appClient.getShipment(shipmentsList));
    }

    public Mono<Aggregate> aggregate(Optional<List<String>> pricing,
                                           Optional<List<String>> track,
                                           Optional<List<String>> shipments) {

        pricing.filter(not(List::isEmpty)).ifPresent(l -> pricingSink.tryEmitNext(l));
        track.filter(not(List::isEmpty)).ifPresent(l -> trackSink.tryEmitNext(l));
        shipments.filter(not(List::isEmpty)).ifPresent(l -> shipmentsSink.tryEmitNext(l));

        Flux output = Flux.zip(pricingFlux, trackFlux, shipmentsFlux)
        		.doOnNext(t -> {
        			System.out.println("agggregate" + t.getT1());
        		})
                .flatMap(tuple -> Mono.zip(tuple.getT2(), tuple.getT3(), tuple.getT1())
                        .map(t -> new Aggregate(t.getT2(), t.getT3(), t.getT1())));
        
        return Mono.from(output);
    }
}