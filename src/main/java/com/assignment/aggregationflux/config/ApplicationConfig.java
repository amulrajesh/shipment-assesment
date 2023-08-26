package com.assignment.aggregationflux.config;

import com.assignment.aggregationflux.properties.ConfigProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
@Data
@AllArgsConstructor
public class ApplicationConfig {

    private ConfigProperties configProperties;

    @Bean
    WebClient webClient() {
        //setting timeout for http request
        ReactorClientHttpConnector clientHttpConnector =
                new ReactorClientHttpConnector(
                        HttpClient.create(connectionProvider())
                                .responseTimeout(Duration.ofSeconds(10)
                                )
                );
        return WebClient.builder()
                .baseUrl(configProperties.getBaseUrl())
                .clientConnector(clientHttpConnector)
                .build();
    }

    @Bean
    ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("myConnectionPool" )
                .maxConnections(5)
                .maxLifeTime(Duration.ofSeconds(5))
                .pendingAcquireMaxCount(5)
                .evictInBackground(Duration.ofSeconds(120))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .build();
    }
}
