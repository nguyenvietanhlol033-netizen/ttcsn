package com.melia.hotel.security;

import com.melia.hotel.entity.KhachHang;
import com.melia.hotel.entity.NhanVien;
import com.melia.hotel.repository.KhachHangRepository;
import com.melia.hotel.repository.NhanVienRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final NhanVienRepository nhanVienRepo;
    private final KhachHangRepository khachHangRepo;

    public CustomUserDetailsService(NhanVienRepository nhanVienRepo, KhachHangRepository khachHangRepo) {
        this.nhanVienRepo = nhanVienRepo;
        this.khachHangRepo = khachHangRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrPhoneOrEmail) throws UsernameNotFoundException {
        Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTenDangNhap(usernameOrPhoneOrEmail);
        if (nhanVienOpt.isPresent()) {
            NhanVien nv = nhanVienOpt.get();
            return new UserPrincipal(
                    nv.getMaNhanVien(),
                    nv.getTenDangNhap(),
                    nv.getMatKhau(),
                    nv.getHoTen(),
                    Collections.singletonList(new SimpleGrantedAuthority(nv.getQuyenHan()))
            );
        }

        Optional<KhachHang> khOpt = khachHangRepo.findBySoDienThoai(usernameOrPhoneOrEmail);
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepo.findByEmail(usernameOrPhoneOrEmail);
        }

        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            return new UserPrincipal(
                    kh.getMaKhachHang(),
                    kh.getEmail(),
                    kh.getMatKhau(),
                    kh.getHoTen(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
            );
        }

        throw new UsernameNotFoundException("Không tìm thấy người dùng với thông tin: " + usernameOrPhoneOrEmail);
    }
}
