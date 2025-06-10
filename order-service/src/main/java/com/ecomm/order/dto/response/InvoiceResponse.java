package com.ecomm.order.dto.response;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private String orderNumber;
    private LocalDateTime date;
    private BigDecimal totalAmount;
    private String status;
    private List<InvoiceItemResponse> items;
}

