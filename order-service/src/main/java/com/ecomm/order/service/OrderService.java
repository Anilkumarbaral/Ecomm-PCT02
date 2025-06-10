package com.ecomm.order.service;


import com.ecomm.order.dto.request.OrderAddressRequest;
import com.ecomm.order.dto.request.OrderRequest;
import com.ecomm.order.dto.response.InvoiceResponse;
import com.ecomm.order.dto.response.OrderAddressResponse;
import com.ecomm.order.dto.response.OrderResponse;
import com.ecomm.order.dto.response.OrderStatusHistoryResponse;


import java.util.List;

public interface OrderService {

    // Checkout
    OrderResponse createOrder(OrderRequest request); // Full checkout
    OrderResponse reviewOrder(Long orderId);
    OrderResponse confirmOrder(Long orderId);

    // Address management
    OrderAddressResponse addAddressToOrder(Long orderId, OrderAddressRequest request);

    // Payment method selection (for now, store payment method name)
    OrderResponse selectPaymentMethod(Long orderId, String paymentMethod);

    // Tracking
    List<OrderResponse> getOrderHistoryByUser(Long userId);
    OrderStatusHistoryResponse updateOrderStatus(Long orderId, String newStatus, Long updatedBy);
    InvoiceResponse generateInvoice(Long orderId);
}

