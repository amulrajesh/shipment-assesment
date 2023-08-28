package com.assignment.aggregationflux.controller;

import com.assignment.aggregationflux.model.Aggregate;
import com.assignment.aggregationflux.service.AggregationService;
import com.assignment.aggregationflux.service.ShipmentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class AggregationController {

    AggregationService aggregationService;

    ShipmentService shipmentService;

    @GetMapping(value = "/aggregation" )
    @ResponseBody
    public Mono<Aggregate> aggregateData(@RequestParam(value = "track", required = false) Optional<List<String>> trackingIds
            , @RequestParam(value = "shipments", required = false) Optional<List<String>> shipments
            , @RequestParam(value = "pricing", required = false) Optional<List<String>> pricing) throws Exception {

        return aggregationService.aggregate(pricing,  trackingIds, shipments);

    }

}
