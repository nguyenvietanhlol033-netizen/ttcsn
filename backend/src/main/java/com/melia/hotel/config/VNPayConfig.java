package com.melia.hotel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {

    @Value("${vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.api-url}")
    private String vnpApiUrl;

    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_ORDER_TYPE = "other";

    public String getVnpPayUrl() { return vnpPayUrl; }
    public String getVnpReturnUrl() { return vnpReturnUrl; }
    public String getVnpTmnCode() { return vnpTmnCode; }
    public String getVnpHashSecret() { return vnpHashSecret; }
    public String getVnpApiUrl() { return vnpApiUrl; }
}
