package com.melia.hotel.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietDatPhongId implements Serializable {
    private String maDatPhong;
    private String maPhong;

    public ChiTietDatPhongId() {}
    public ChiTietDatPhongId(String maDatPhong, String maPhong) {
        this.maDatPhong = maDatPhong;
        this.maPhong = maPhong;
    }

    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; }
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietDatPhongId that = (ChiTietDatPhongId) o;
        return Objects.equals(maDatPhong, that.maDatPhong) && Objects.equals(maPhong, that.maPhong);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDatPhong, maPhong);
    }
}
