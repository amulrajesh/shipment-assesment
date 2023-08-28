package com.assignment.aggregationflux.properties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("aggregation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigProperties {

    String baseUrl;

    String trackPath;

    String shipmentPath;

    String pricingPath;
    
    Integer queueSize;
    
    Integer apiQueueTimeLimit;
}