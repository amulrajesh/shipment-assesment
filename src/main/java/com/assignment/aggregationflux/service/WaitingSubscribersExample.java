package com.assignment.aggregationflux.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class WaitingSubscribersExample {

    public static void main(String[] args) throws InterruptedException {
        // Create a multicast sink
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        // Create a flux from the sink
        Flux<String> flux = sink.asFlux();

        // Simulate 5 users subscribing or waiting for 5 seconds
        Flux<Tuple2<Long, String>> timedFlux = Flux.zip(Flux.interval(Duration.ofSeconds(1)), flux);

        // Collect the results and print
        timedFlux.take(5).doOnNext(tuple -> System.out.println("User " + tuple.getT1() + " received: " + tuple.getT2()))
                .subscribe(); // Blocking for demonstration purposes
    }
}
