package com.melia.hotel.repository;

import com.melia.hotel.entity.ChiTietDatPhong;
import com.melia.hotel.entity.ChiTietDatPhongId;
import com.melia.hotel.entity.DonDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, ChiTietDatPhongId> {
    List<ChiTietDatPhong> findByDonDatPhong(DonDatPhong donDatPhong);
}