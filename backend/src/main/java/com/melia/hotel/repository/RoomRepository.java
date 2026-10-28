package com.melia.hotel.repository;

import com.melia.hotel.entity.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Phong, String> {

    @Query("SELECT COUNT(p) FROM Phong p WHERE p.loaiPhong.maLoaiPhong = :maLoaiPhong AND p.trangThaiPhong <> 'MAINTENANCE'")
    long countOperationalRoomsByLoaiPhong(@Param("maLoaiPhong") String maLoaiPhong);

    long countByTrangThaiPhongNot(String trangThaiPhong);

    long countByTrangThaiPhong(String trangThaiPhong);

    @Query("SELECT p FROM Phong p WHERE p.loaiPhong.maLoaiPhong = :maLoaiPhong AND p.trangThaiPhong = 'AVAILABLE'")
    List<Phong> findAvailableRoomsForCheckIn(@Param("maLoaiPhong") String maLoaiPhong);

    List<Phong> findByTrangThaiPhong(String trangThaiPhong);

    List<Phong> findByLoaiPhong_MaLoaiPhong(String maLoaiPhong);
}
