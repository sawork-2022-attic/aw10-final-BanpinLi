package com.micropos.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeliveryDto {

    @JsonProperty("order")
    private OrderDto order;
    @JsonProperty("status")
    private String status;

}
