package com.assignment.aggregationflux.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigProperties {

    @Value("${shipment.base-url}" )
    String baseUrl;

    @Value("${shipment.track-path}")
    String trackPath;

    @Value("${shipment.shipment-path}")
    String shipmentPath;

    @Value("${shipment.pricing-path}")
    String pricingPath;
}
