package com.melia.hotel.service.impl;

import com.melia.hotel.entity.DonDatPhong;
import com.melia.hotel.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final JavaMailSender mailSender;

    public NotificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendBookingSuccessEmail(DonDatPhong booking) {
        try {
            log.info("Bắt đầu tiến trình gửi Email xác nhận đặt phòng bất đồng bộ cho đơn: {}", booking.getMaDatPhong());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setTo(booking.getKhachHang().getEmail());
            helper.setSubject("Meliá Hanoi Hotel - Xác nhận đặt phòng thành công #" + booking.getMaDatPhong());

            String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <h2 style="color: #9C1D26; text-align: center;">KHÁCH SẠN MELIÁ HÀ NỘI</h2>
                    <p style="text-align: center; color: #666;">44B Lý Thường Kiệt, Hoàn Kiếm, Hà Nội</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;"/>
                    <p>Kính gửi Quý khách <b>%s</b>,</p>
                    <p>Meliá Hà Nội xin trân trọng cảm ơn Quý khách đã tin tưởng lựa chọn dịch vụ của chúng tôi. Yêu cầu đặt phòng của Quý khách đã được <b>XÁC NHẬN VÀ THANH TOÁN THÀNH CÔNG</b>.</p>
                    
                    <table style="width: 100%%; border-collapse: collapse; margin: 15px 0;">
                        <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Mã đặt phòng:</b></td><td style="padding: 8px; border-bottom: 1px solid #eee; color: #9C1D26; font-weight: bold;">%s</td></tr>
                        <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Hạng phòng:</b></td><td style="padding: 8px; border-bottom: 1px solid #eee; font-weight: bold;">%s</td></tr>
                        <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Ngày nhận phòng:</b></td><td style="padding: 8px; border-bottom: 1px solid #eee;">%s (Từ 14:00)</td></tr>
                        <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Ngày trả phòng:</b></td><td style="padding: 8px; border-bottom: 1px solid #eee;">%s (Trước 12:00)</td></tr>
                        <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Tổng thanh toán:</b></td><td style="padding: 8px; border-bottom: 1px solid #eee; font-weight: bold;">%,.0f VNĐ</td></tr>
                    </table>

                    <div style="text-align: center; margin: 25px 0;">
                        <p style="margin-bottom: 10px; font-weight: bold; color: #333;">MÃ QR CHECK-IN TỰ ĐỘNG TẠI QUẦY LỄ TÂN:</p>
                        <img src="%s" alt="QR Code Check-in" style="width: 200px; height: 200px; border: 2px solid #9C1D26; border-radius: 6px; padding: 5px;"/>
                        <p style="font-size: 12px; color: #777; margin-top: 5px;">Quý khách vui lòng xuất trình mã QR này tại quầy Lễ tân để nhận phòng nhanh chóng.</p>
                    </div>

                    <p style="font-size: 13px; color: #888; text-align: center;">Mọi thắc mắc xin vui lòng liên hệ hotline: (+84) 24 3934 3344 hoặc email info@meliahanoi.vn</p>
                </div>
            """,
                    booking.getKhachHang().getHoTen(),
                    booking.getMaDatPhong(),
                    booking.getLoaiPhong() != null ? booking.getLoaiPhong().getTenLoaiPhong() : "Tiêu chuẩn Meliá",
                    booking.getThoiGianNhanPhong(),
                    booking.getThoiGianTraPhong(),
                    booking.getTongTien().doubleValue(),
                    booking.getQrCode() != null ? booking.getQrCode() : ""
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Đã gửi thành công Email xác nhận đơn phòng {} tới {}", booking.getMaDatPhong(), booking.getKhachHang().getEmail());
        } catch (Exception e) {
            log.warn("Không thể gửi email xác nhận cho đơn {} (Có thể do chưa cấu hình SMTP Gmail mật khẩu ứng dụng thực tế): {}",
                    booking.getMaDatPhong(), e.getMessage());
        }
    }
}
