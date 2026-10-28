package com.melia.hotel.service.impl;

import com.melia.hotel.config.VNPayConfig;
import com.melia.hotel.entity.DonDatPhong;
import com.melia.hotel.entity.HoaDon;
import com.melia.hotel.entity.PhieuGiamGia;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.BookingRepository;
import com.melia.hotel.repository.CouponRepository;
import com.melia.hotel.repository.HoaDonRepository;
import com.melia.hotel.service.NotificationService;
import com.melia.hotel.service.PaymentService;
import com.melia.hotel.util.QRCodeUtil;
import com.melia.hotel.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final VNPayConfig vnPayConfig;
    private final BookingRepository bookingRepo;
    private final HoaDonRepository hoaDonRepo;
    private final CouponRepository couponRepo;
    private final QRCodeUtil qrCodeUtil;
    private final NotificationService notificationService;

    public PaymentServiceImpl(VNPayConfig vnPayConfig,
                              BookingRepository bookingRepo,
                              HoaDonRepository hoaDonRepo,
                              CouponRepository couponRepo,
                              QRCodeUtil qrCodeUtil,
                              NotificationService notificationService) {
        this.vnPayConfig = vnPayConfig;
        this.bookingRepo = bookingRepo;
        this.hoaDonRepo = hoaDonRepo;
        this.couponRepo = couponRepo;
        this.qrCodeUtil = qrCodeUtil;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public String createVNPayPaymentUrl(String maDatPhong, HttpServletRequest request) {
        DonDatPhong booking = bookingRepo.findById(maDatPhong)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng: " + maDatPhong));

        long amount = booking.getTongTien().longValue() * 100L;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.VNP_VERSION);
        vnp_Params.put("vnp_Command", VNPayConfig.VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", booking.getMaDatPhong());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don dat phong Melia Hanoi #" + booking.getMaDatPhong());
        vnp_Params.put("vnp_OrderType", VNPayConfig.VNP_ORDER_TYPE);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnPayConfig.getVnpPayUrl() + "?" + queryUrl;
    }

    @Override
    @Transactional
    public String handleVNPayCallback(Map<String, String> vnpayParams) {
        String vnp_SecureHash = vnpayParams.get("vnp_SecureHash");
        if (!VNPayUtil.verifySignature(vnpayParams, vnp_SecureHash, vnPayConfig.getVnpHashSecret())) {
            throw new BusinessException("Chữ ký số VNPay không hợp lệ (Checksum Failed)");
        }

        String maDatPhong = vnpayParams.get("vnp_TxnRef");
        String responseCode = vnpayParams.get("vnp_ResponseCode");

        DonDatPhong booking = bookingRepo.findById(maDatPhong)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng: " + maDatPhong));

        HoaDon hoaDon = hoaDonRepo.findByDonDatPhong(booking)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hóa đơn cho đơn đặt phòng: " + maDatPhong));

        // 1. Chống xử lý lặp (Idempotency): Nếu đơn đã CONFIRMED hoặc hóa đơn đã PAID, bỏ qua không gửi email lần 2
        if ("CONFIRMED".equalsIgnoreCase(booking.getTrangThaiDon()) || "PAID".equalsIgnoreCase(hoaDon.getTrangThaiThanhToan())) {
            log.info("Đơn đặt phòng {} đã được xử lý xác nhận trước đó, bỏ qua giao dịch lặp.", maDatPhong);
            return "Đơn đặt phòng đã được xác nhận thanh toán trước đó";
        }

        // 2. Đối chiếu số tiền vnp_Amount với tổng tiền trên hóa đơn
        String amountStr = vnpayParams.get("vnp_Amount");
        if (amountStr != null && !amountStr.isBlank()) {
            long vnpAmount = Long.parseLong(amountStr) / 100L;
            if (hoaDon.getTongTienThanhToan().longValue() != vnpAmount) {
                log.error("Số tiền VNPay phản hồi ({}) không khớp với tổng tiền đơn phòng ({})", vnpAmount, hoaDon.getTongTienThanhToan());
                throw new BusinessException("Số tiền thanh toán VNPay (" + vnpAmount + ") không khớp với số tiền trên hóa đơn (" + hoaDon.getTongTienThanhToan() + ")!");
            }
        }

        // 3. Xử lý kết quả giao dịch
        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            booking.setTrangThaiDon("CONFIRMED");
            String qrBase64 = qrCodeUtil.generateQRCodeImageBase64("MELIA-CHECKIN-" + booking.getMaDatPhong());
            booking.setQrCode(qrBase64);
            bookingRepo.save(booking);

            hoaDon.setDaThanhToan(hoaDon.getTongTienThanhToan());
            hoaDon.setTrangThaiThanhToan("PAID");
            hoaDon.setPhuongThucThanhToan("VNPAY");
            hoaDonRepo.save(hoaDon);

            // Gửi email xác nhận kèm mã QR Check-in bất đồng bộ
            notificationService.sendBookingSuccessEmail(booking);
            log.info("Xử lý thanh toán VNPay thành công cho đơn đặt phòng: {}", maDatPhong);
            return "Thanh toán thành công đơn đặt phòng " + maDatPhong;
        } else {
            // Thanh toán thất bại hoặc khách hủy giao dịch: Gán CANCELLED, hóa đơn FAILED
            booking.setTrangThaiDon("CANCELLED");

            // Hoàn lại số lượng voucher nếu đơn có dùng mã giảm giá
            if (booking.getPhieuGiamGia() != null) {
                PhieuGiamGia voucher = booking.getPhieuGiamGia();
                voucher.setSoLuongPhieu(voucher.getSoLuongPhieu() + 1);
                couponRepo.save(voucher);
                log.info("Đã hoàn lại lượt sử dụng cho voucher {} do đơn đặt phòng {} thanh toán thất bại",
                        voucher.getMaPhieuGg(), maDatPhong);
            }

            bookingRepo.save(booking);

            hoaDon.setTrangThaiThanhToan("FAILED");
            hoaDonRepo.save(hoaDon);

            log.warn("Thanh toán VNPay không thành công cho đơn {}. Mã phản hồi VNPay: {}", maDatPhong, responseCode);
            return "Giao dịch thanh toán VNPay không thành công hoặc bị hủy (Mã phản hồi: " + responseCode + ")";
        }
    }
}
