package com.ecomm.order.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String addressType; // SHIPPING or BILLING

    private String firstName;
    private String lastName;
    private String company;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
