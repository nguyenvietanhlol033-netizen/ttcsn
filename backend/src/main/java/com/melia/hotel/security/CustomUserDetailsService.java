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
import java.util.List;
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
        if (usernameOrPhoneOrEmail == null || usernameOrPhoneOrEmail.isBlank()) {
            throw new UsernameNotFoundException("Thông tin đăng nhập không được để trống");
        }
        String input = usernameOrPhoneOrEmail.trim();

        // 1. Kiểm tra tài khoản Nhân viên (ưu tiên theo tên đăng nhập hoặc mã nhân viên)
        Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTenDangNhap(input);
        if (nhanVienOpt.isEmpty()) {
            nhanVienOpt = nhanVienRepo.findById(input);
        }
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

        // 2. Kiểm tra tài khoản Khách hàng
        // 2.1 Theo tên đăng nhập
        Optional<KhachHang> khOpt = khachHangRepo.findByTenDangNhap(input);

        // 2.2 Theo Email
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepo.findByEmail(input);
        }

        // 2.3 Theo Số điện thoại
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepo.findBySoDienThoai(input);
        }

        // 2.4 Theo Số CCCD
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepo.findBySoCccd(input);
        }

        // 2.5 Theo Mã khách hàng (VD: KH001, KH...)
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepo.findById(input);
        }

        // 2.6 Khớp tiền tố email (VD: nhập "nva123" khớp với "nva123@gmail.com")
        if (khOpt.isEmpty() && !input.contains("@")) {
            List<KhachHang> matches = khachHangRepo.findByEmailStartingWith(input + "@");
            if (!matches.isEmpty()) {
                khOpt = Optional.of(matches.get(0));
            }
        }

        // 2.7 Khớp theo họ tên chính xác nếu chỉ có duy nhất 1 khách hàng trùng tên
        if (khOpt.isEmpty()) {
            List<KhachHang> byName = khachHangRepo.findByHoTenIgnoreCase(input);
            if (byName.size() == 1) {
                khOpt = Optional.of(byName.get(0));
            }
        }

        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            String principalUsername = (kh.getTenDangNhap() != null && !kh.getTenDangNhap().isBlank())
                    ? kh.getTenDangNhap()
                    : kh.getEmail();

            return new UserPrincipal(
                    kh.getMaKhachHang(),
                    principalUsername,
                    kh.getMatKhau(),
                    kh.getHoTen(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
            );
        }

        throw new UsernameNotFoundException("Không tìm thấy người dùng với thông tin: " + usernameOrPhoneOrEmail);
    }
}
