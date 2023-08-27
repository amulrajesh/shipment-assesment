package com.assignment.aggregationflux.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

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

    Cache cache;

    @Getter
    @Setter
	public static class Cache {
        private boolean enabled;
        private String host;
        private String port;
        private Duration expiration;
    }
}
