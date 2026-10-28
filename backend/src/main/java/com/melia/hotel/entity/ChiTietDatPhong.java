package com.melia.hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_dat_phong")
public class ChiTietDatPhong {

    @EmbeddedId
    private ChiTietDatPhongId id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maDatPhong")
    @JoinColumn(name = "ma_dat_phong")
    private DonDatPhong donDatPhong;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("maPhong")
    @JoinColumn(name = "ma_phong")
    private Phong phong;

    @Column(name = "so_phong", length = 10)
    private String soPhong;

    @Column(name = "gia_tai_thoi_diem_dat", precision = 12, scale = 2)
    private BigDecimal giaTaiThoiDiemDat;

    public ChiTietDatPhong() {}

    public ChiTietDatPhong(DonDatPhong donDatPhong, Phong phong, String soPhong) {
        this.id = new ChiTietDatPhongId(donDatPhong.getMaDatPhong(), phong.getMaPhong());
        this.donDatPhong = donDatPhong;
        this.phong = phong;
        this.soPhong = soPhong;
        this.giaTaiThoiDiemDat = (phong.getLoaiPhong() != null && phong.getLoaiPhong().getGiaPhongCoBan() != null)
                ? phong.getLoaiPhong().getGiaPhongCoBan() : BigDecimal.ZERO;
    }

    public ChiTietDatPhongId getId() { return id; }
    public void setId(ChiTietDatPhongId id) { this.id = id; }
    public DonDatPhong getDonDatPhong() { return donDatPhong; }
    public void setDonDatPhong(DonDatPhong donDatPhong) { this.donDatPhong = donDatPhong; }
    public Phong getPhong() { return phong; }
    public void setPhong(Phong phong) { this.phong = phong; }
    public String getSoPhong() { return soPhong; }
    public void setSoPhong(String soPhong) { this.soPhong = soPhong; }
    public BigDecimal getGiaTaiThoiDiemDat() { return giaTaiThoiDiemDat; }
    public void setGiaTaiThoiDiemDat(BigDecimal giaTaiThoiDiemDat) { this.giaTaiThoiDiemDat = giaTaiThoiDiemDat; }
}
