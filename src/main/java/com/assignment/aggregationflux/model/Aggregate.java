package com.assignment.aggregationflux.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@ToString
//@AllArgsConstructor(staticName = "create")
public class Aggregate {
    private Map<String, String> track;
    private Map<String, ArrayList<String>> shipments;
    private Map<String, Double> pricing;
    
    public Aggregate(Map<String, String> track, Map<String, ArrayList<String>> shipments, Map<String, Double> pricing) {
    	System.out.println("Callig aggregate");
    	track.forEach((k, v) -> System.out.println(k + " :: " + v));
    	this.track = track;
    	this.shipments = shipments;
    	this.pricing = pricing;
    	System.out.println("return aggregate");
    }
}
