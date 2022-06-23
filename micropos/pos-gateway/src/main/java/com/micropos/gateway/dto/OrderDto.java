package com.micropos.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderDto {

    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("product")
    private ProductDto product;
    @JsonProperty("quantity")
    private Integer quantity;

}
