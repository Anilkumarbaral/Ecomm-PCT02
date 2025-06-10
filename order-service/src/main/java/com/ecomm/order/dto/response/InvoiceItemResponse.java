package com.ecomm.order.dto.response;


import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponse {
    private String name;
    private BigDecimal price;
    private int qty;
    private BigDecimal total;
}

