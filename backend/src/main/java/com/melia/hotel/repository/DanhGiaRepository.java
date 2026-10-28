package com.melia.hotel.repository;

import com.melia.hotel.entity.DanhGia;
import com.melia.hotel.entity.DonDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, String> {
    Optional<DanhGia> findByDonDatPhong(DonDatPhong donDatPhong);
    List<DanhGia> findBySoSaoDanhGiaGreaterThanEqual(Integer minStars);

    @Query("SELECT d FROM DanhGia d WHERE d.soSaoDanhGia >= 4 ORDER BY d.thoiGianDanhGia DESC")
    List<DanhGia> findTopHighRatingReviews();
}
