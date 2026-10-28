package com.melia.hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "loai_phong")
public class LoaiPhong {

    @Id
    @Column(name = "ma_loai_phong", length = 20)
    private String maLoaiPhong;

    @Column(name = "ten_loai_phong", nullable = false, length = 100)
    private String tenLoaiPhong;

    @Column(name = "gia_phong_co_ban", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaPhongCoBan;

    @Column(name = "suc_chua", nullable = false)
    private Integer sucChua;

    @Column(name = "thong_tin", columnDefinition = "TEXT")
    private String thongTin;

    @Column(name = "hinh_anh_minh_hoa")
    private String hinhAnhMinhHoa;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @JsonIgnore
    @OneToMany(mappedBy = "loaiPhong", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Phong> danhSachPhong;

    public LoaiPhong() {}

    public String getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(String maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }
    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }
    public BigDecimal getGiaPhongCoBan() { return giaPhongCoBan; }
    public void setGiaPhongCoBan(BigDecimal giaPhongCoBan) { this.giaPhongCoBan = giaPhongCoBan; }
    public Integer getSucChua() { return sucChua; }
    public void setSucChua(Integer sucChua) { this.sucChua = sucChua; }
    public String getThongTin() { return thongTin; }
    public void setThongTin(String thongTin) { this.thongTin = thongTin; }
    public String getHinhAnhMinhHoa() { return hinhAnhMinhHoa; }
    public void setHinhAnhMinhHoa(String hinhAnhMinhHoa) { this.hinhAnhMinhHoa = hinhAnhMinhHoa; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public List<Phong> getDanhSachPhong() { return danhSachPhong; }
    public void setDanhSachPhong(List<Phong> danhSachPhong) { this.danhSachPhong = danhSachPhong; }
}
