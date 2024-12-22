package com.tcs.profileservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig
{
    @Value("${gateway.host}")
    private String gateway_host;

    @Value("${gateway.port}")
    private String gateway_port;

    @Bean
    public WebClient webClient_1(WebClient.Builder webClientBuilder)
    {
        return webClientBuilder
                .baseUrl("http://"+gateway_host+":"+gateway_port+"/auth-service/api/v1/validate")
                .filter(new LoggingWebClientFilter())
                .build();
    }
}
