package com.melia.hotel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "phong")
public class Phong {

    @Id
    @Column(name = "ma_phong", length = 20)
    private String maPhong;

    @Column(name = "so_phong", nullable = false, unique = true, length = 10)
    private String soPhong;

    @Column(name = "tang", nullable = false)
    private Integer tang;

    @Column(name = "trang_thai_phong", nullable = false, length = 30)
    private String trangThaiPhong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_loai_phong", nullable = false)
    private LoaiPhong loaiPhong;

    public Phong() {}

    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }
    public String getSoPhong() { return soPhong; }
    public void setSoPhong(String soPhong) { this.soPhong = soPhong; }
    public Integer getTang() { return tang; }
    public void setTang(Integer tang) { this.tang = tang; }
    public String getTrangThaiPhong() { return trangThaiPhong; }
    public void setTrangThaiPhong(String trangThaiPhong) { this.trangThaiPhong = trangThaiPhong; }
    public LoaiPhong getLoaiPhong() { return loaiPhong; }
    public void setLoaiPhong(LoaiPhong loaiPhong) { this.loaiPhong = loaiPhong; }
}
