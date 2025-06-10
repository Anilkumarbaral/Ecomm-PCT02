package com.ecomm.order.dto.response;

import com.ecomm.order.model.OrderAddress;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddressResponse {
    private Long id;
    private Long orderId;
    private String addressType;
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

    public static OrderAddressResponse fromEntity(OrderAddress address) {
        return OrderAddressResponse.builder()
                .id(address.getId())
                .orderId(address.getOrderId())
                .addressType(address.getAddressType())
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .company(address.getCompany())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .build();
    }
}
