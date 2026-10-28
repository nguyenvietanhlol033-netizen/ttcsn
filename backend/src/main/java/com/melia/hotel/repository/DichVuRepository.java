package com.melia.hotel.repository;

import com.melia.hotel.entity.DichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DichVuRepository extends JpaRepository<DichVu, String> {
    List<DichVu> findByTenDvContainingIgnoreCase(String keyword);
}