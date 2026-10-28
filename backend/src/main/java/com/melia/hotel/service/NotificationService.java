package com.melia.hotel.service;

import com.melia.hotel.entity.DonDatPhong;

public interface NotificationService {
    void sendBookingSuccessEmail(DonDatPhong booking);
}