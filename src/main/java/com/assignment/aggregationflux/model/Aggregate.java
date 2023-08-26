package com.assignment.aggregationflux.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor(staticName = "create")
public class Aggregate {
    private Map<String, String> track;
    private Map<String, ArrayList<String>> shipments;
    private Map<String, String> pricing;
}
