package com.melia.hotel.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dich_vu")
public class DichVu {

    @Id
    @Column(name = "ma_dich_vu", length = 20)
    private String maDichVu;

    @Column(name = "ten_dv", nullable = false, length = 100)
    private String tenDv;

    @Column(name = "gia_dv", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaDv;

    @Column(name = "mo_ta_dv", columnDefinition = "TEXT")
    private String moTaDv;

    public DichVu() {}

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }
    public String getTenDv() { return tenDv; }
    public void setTenDv(String tenDv) { this.tenDv = tenDv; }
    public BigDecimal getGiaDv() { return giaDv; }
    public void setGiaDv(BigDecimal giaDv) { this.giaDv = giaDv; }
    public String getMoTaDv() { return moTaDv; }
    public void setMoTaDv(String moTaDv) { this.moTaDv = moTaDv; }
}
