package com.micropos.gateway.gateway;

import com.micropos.gateway.dto.DeliveryDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class HttpOutboundGateway {

    @Bean
    public IntegrationFlow outGate() {
        return IntegrationFlows.from("deliveryChannel")
                .handle(WebFlux.outboundGateway(
                        message -> UriComponentsBuilder
                                .fromUriString("http://localhost:1148/api/delivery/{orderId}")
                                .buildAndExpand(message.getPayload())
                                .toUri())
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(DeliveryDto.class))
                .get();
    }

}
