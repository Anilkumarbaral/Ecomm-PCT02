package com.ecomm.userservice.service;

import com.ecomm.userservice.dto.request.AddressRequest;
import com.ecomm.userservice.dto.response.AddressResponse;
import com.ecomm.userservice.enums.AddressType;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    // Address Management
    public List<AddressResponse> getUserAddresses(UUID userId);
    public AddressResponse createAddress(UUID userId, AddressRequest request);
    public AddressResponse updateAddress(UUID addressId, AddressRequest request);
    public void deleteAddress(UUID addressId);
    public AddressResponse setDefaultAddress(UUID addressId, AddressType type);

}
