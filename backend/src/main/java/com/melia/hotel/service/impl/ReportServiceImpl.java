package com.melia.hotel.service.impl;

import com.melia.hotel.dto.response.OccupancyReportResponse;
import com.melia.hotel.dto.response.RevenueReportResponse;
import com.melia.hotel.entity.DonDatPhong;
import com.melia.hotel.entity.HoaDon;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.BookingRepository;
import com.melia.hotel.repository.HoaDonRepository;
import com.melia.hotel.repository.RoomRepository;
import com.melia.hotel.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final RoomRepository roomRepo;
    private final BookingRepository bookingRepo;
    private final HoaDonRepository hoaDonRepo;

    public ReportServiceImpl(RoomRepository roomRepo,
                             BookingRepository bookingRepo,
                             HoaDonRepository hoaDonRepo) {
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
        this.hoaDonRepo = hoaDonRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public OccupancyReportResponse calculateOccupancyRate(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new BusinessException("Từ ngày (fromDate) và đến ngày (toDate) không được để trống!");
        }
        if (toDate.isBefore(fromDate)) {
            throw new BusinessException("Đến ngày (toDate) phải lớn hơn hoặc bằng từ ngày (fromDate)!");
        }

        long totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        long totalOperationalRooms = roomRepo.countByTrangThaiPhongNot("MAINTENANCE");
        if (totalOperationalRooms == 0) {
            totalOperationalRooms = roomRepo.count();
        }

        // Tổng công suất phòng khả dụng trong kỳ (room-nights capacity)
        long totalCapacity = totalOperationalRooms * totalDays;

        // Lấy tất cả đơn đặt phòng hợp lệ (CONFIRMED, CHECKED_IN, CHECKED_OUT) giao nhau với khoảng thời gian [fromDate, toDate]
        List<DonDatPhong> overlappingBookings = bookingRepo.findOverlappingActiveBookings(fromDate, toDate);

        long totalOccupiedNights = 0;
        for (DonDatPhong b : overlappingBookings) {
            LocalDate checkIn = b.getThoiGianNhanPhong();
            LocalDate checkOut = b.getThoiGianTraPhong();

            // Tính điểm bắt đầu và kết thúc giao thoa
            LocalDate start = checkIn.isBefore(fromDate) ? fromDate : checkIn;
            LocalDate end = checkOut.isAfter(toDate.plusDays(1)) ? toDate.plusDays(1) : checkOut;

            long nights = ChronoUnit.DAYS.between(start, end);
            if (nights > 0) {
                totalOccupiedNights += nights;
            }
        }

        double occupancyRate = totalCapacity > 0 ? ((double) totalOccupiedNights / totalCapacity) * 100.0 : 0.0;
        if (occupancyRate > 100.0) {
            occupancyRate = 100.0;
        }

        return OccupancyReportResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalRooms(totalOperationalRooms)
                .occupiedRooms(totalOccupiedNights)
                .occupancyRate(BigDecimal.valueOf(occupancyRate).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse calculateRevenueReport(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new BusinessException("Từ ngày (fromDate) và đến ngày (toDate) không được để trống!");
        }
        if (toDate.isBefore(fromDate)) {
            throw new BusinessException("Đến ngày (toDate) phải lớn hơn hoặc bằng từ ngày (fromDate)!");
        }

        long totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        long totalOperationalRooms = roomRepo.countByTrangThaiPhongNot("MAINTENANCE");
        if (totalOperationalRooms == 0) {
            totalOperationalRooms = roomRepo.count();
        }

        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(23, 59, 59);

        List<HoaDon> invoices = hoaDonRepo.findByNgayLapHoaDonBetweenAndTrangThaiThanhToan(start, end, "PAID");

        BigDecimal roomRevenue = invoices.stream()
                .map(HoaDon::getTienPhong)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal serviceRevenue = invoices.stream()
                .map(h -> h.getTienDichVu() != null ? h.getTienDichVu() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue = invoices.stream()
                .map(HoaDon::getTongTienThanhToan)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Công thức chuẩn RevPAR = Doanh thu phòng / (Tổng số phòng khả dụng x Số ngày)
        long totalAvailableRoomNights = totalOperationalRooms * totalDays;
        BigDecimal revPar = BigDecimal.ZERO;
        if (totalAvailableRoomNights > 0 && roomRevenue.compareTo(BigDecimal.ZERO) > 0) {
            revPar = roomRevenue.divide(BigDecimal.valueOf(totalAvailableRoomNights), 2, RoundingMode.HALF_UP);
        }

        return RevenueReportResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalInvoices(invoices.size())
                .roomRevenue(roomRevenue)
                .serviceRevenue(serviceRevenue)
                .totalRevenue(totalRevenue)
                .revPar(revPar)
                .build();
    }
}
