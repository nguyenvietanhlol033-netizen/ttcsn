package com.melia.hotel.service.impl;

import com.melia.hotel.dto.response.RoomAvailabilityResponse;
import com.melia.hotel.entity.LoaiPhong;
import com.melia.hotel.entity.Phong;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.BookingRepository;
import com.melia.hotel.repository.LoaiPhongRepository;
import com.melia.hotel.repository.RoomRepository;
import com.melia.hotel.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final LoaiPhongRepository loaiPhongRepository;
    private final BookingRepository bookingRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
                           LoaiPhongRepository loaiPhongRepository,
                           BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.loaiPhongRepository = loaiPhongRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAvailabilityResponse> findAvailableRoomTypes(LocalDate checkIn, LocalDate checkOut, Integer guests) {
        if (checkIn == null || checkOut == null) {
            throw new BusinessException("Ngày nhận phòng và ngày trả phòng không được để trống!");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessException("Ngày trả phòng phải sau ngày nhận phòng ít nhất 1 ngày!");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BusinessException("Ngày nhận phòng không thể nằm trong quá khứ!");
        }

        int guestCount = (guests != null && guests > 0) ? guests : 1;
        List<LoaiPhong> allTypes = loaiPhongRepository.findAll();
        List<RoomAvailabilityResponse> responseList = new ArrayList<>();
        LocalDateTime holdDeadline = LocalDateTime.now().minusMinutes(15);

        for (LoaiPhong lp : allTypes) {
            if (lp.getSucChua() >= guestCount) {
                long operationalRooms = roomRepository.countOperationalRoomsByLoaiPhong(lp.getMaLoaiPhong());
                long bookedRooms = bookingRepository.countActiveOverlappingBookings(
                        lp.getMaLoaiPhong(), checkIn, checkOut, holdDeadline);
                long availableRoomsCount = operationalRooms - bookedRooms;

                if (availableRoomsCount > 0) {
                    List<Phong> physicalRooms = roomRepository.findAvailableRoomsForCheckIn(lp.getMaLoaiPhong());
                    List<String> roomCodes = physicalRooms.stream()
                            .map(Phong::getSoPhong)
                            .collect(Collectors.toList());

                    responseList.add(RoomAvailabilityResponse.builder()
                            .roomTypeId(lp.getMaLoaiPhong())
                            .roomTypeName(lp.getTenLoaiPhong())
                            .basePrice(lp.getGiaPhongCoBan())
                            .capacity(lp.getSucChua())
                            .description(lp.getMoTa())
                            .imageUrl(lp.getHinhAnhMinhHoa())
                            .availableRoomsCount((int) availableRoomsCount)
                            .availableRoomCodes(roomCodes)
                            .build());
                }
            }
        }
        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoaiPhong> getAllRoomTypes() {
        return loaiPhongRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public LoaiPhong getRoomTypeById(String maLoaiPhong) {
        return loaiPhongRepository.findById(maLoaiPhong)
                .orElseThrow(() -> new BusinessException("Không tìm thấy loại phòng với mã: " + maLoaiPhong));
    }

    @Override
    @Transactional
    public LoaiPhong createRoomType(LoaiPhong loaiPhong) {
        if (loaiPhongRepository.existsById(loaiPhong.getMaLoaiPhong())) {
            throw new BusinessException("Mã loại phòng đã tồn tại: " + loaiPhong.getMaLoaiPhong());
        }
        return loaiPhongRepository.save(loaiPhong);
    }

    @Override
    @Transactional(readOnly = true)
    public Phong getRoomById(String maPhong) {
        return roomRepository.findById(maPhong)
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin phòng: " + maPhong));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phong> getAvailableRoomsByLoaiPhong(String maLoaiPhong, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRoomsForCheckIn(maLoaiPhong);
    }
}
