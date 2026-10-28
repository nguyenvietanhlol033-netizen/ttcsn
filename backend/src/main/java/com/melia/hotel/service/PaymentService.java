package com.melia.hotel.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {
    String createVNPayPaymentUrl(String maDatPhong, HttpServletRequest request);
    String handleVNPayCallback(Map<String, String> vnpayParams);
}
