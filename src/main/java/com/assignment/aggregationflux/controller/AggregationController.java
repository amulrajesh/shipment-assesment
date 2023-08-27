package com.assignment.aggregationflux.controller;

import com.assignment.aggregationflux.dummy.MyAggregationService;
import com.assignment.aggregationflux.model.Aggregate;
import com.assignment.aggregationflux.service.AggregateService;
import com.assignment.aggregationflux.service.PricingService;
import com.assignment.aggregationflux.service.ShipmentService;
import com.assignment.aggregationflux.service.TrackService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class AggregationController {

    AggregateService aggregateService;

    PricingService pricingService;

    TrackService trackService;

    ShipmentService shipmentService;
    
    MyAggregationService aggregationService;

    @GetMapping(value = "/aggregation" )
    @ResponseBody
    public Mono<Aggregate> aggregateData(@RequestParam(value = "track", required = false) Optional<List<String>> trackingIds
            , @RequestParam(value = "shipments", required = false) Optional<List<String>> shipments
            , @RequestParam(value = "pricing", required = false) Optional<List<String>> pricing) throws Exception {

        //Mono<Map> trackingMono = trackService.track(trackingIds);
        //Mono<Map> shipmentMono = shipmentService.process(shipments);
        //Mono<Map> shipmentMono = aggregateService.process(shipments);
        //Mono<Map> pricingMono = pricingService.pricings(pricing);
        
        return aggregationService.aggregate(pricing,  trackingIds, shipments);

        //return Mono
          //      .zip(trackingMono, shipmentMono, pricingMono)
            //    .map(this::combine);

    }

   // private Aggregate combine(Tuple3<Map, Map, Map> tuple) {
     //   return Aggregate.create(tuple.getT1(), tuple.getT2(), tuple.getT3());
   // }

}
