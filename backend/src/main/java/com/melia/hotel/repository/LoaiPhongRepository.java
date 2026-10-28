package com.melia.hotel.repository;

import com.melia.hotel.entity.LoaiPhong;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, String> {
    Optional<LoaiPhong> findByTenLoaiPhong(String tenLoaiPhong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lp FROM LoaiPhong lp WHERE lp.maLoaiPhong = :id")
    Optional<LoaiPhong> findByIdWithLock(@Param("id") String id);
}
