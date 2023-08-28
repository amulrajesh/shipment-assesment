package com.assignment.aggregationflux.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;

@Data
@ToString
//@AllArgsConstructor(staticName = "create")
public class Aggregate {
    private Map<String, Double> pricing;
    private Map<String, String> track;
    private Map<String, ArrayList<String>> shipments;
    public Aggregate() {

    }

    public Aggregate(Map<String, Double> pricing, Map<String, String> track,
                     Map<String, ArrayList<String>> shipments) {
        this.pricing = pricing;
        this.track = track;
        this.shipments = shipments;
    }
}
