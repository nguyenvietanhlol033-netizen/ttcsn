package com.melia.hotel.controller;

import com.melia.hotel.dto.request.LoginRequest;
import com.melia.hotel.dto.request.RegisterRequest;
import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.dto.response.JwtAuthResponse;
import com.melia.hotel.entity.KhachHang;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.KhachHangRepository;
import com.melia.hotel.security.JwtTokenProvider;
import com.melia.hotel.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Các API đăng nhập và đăng ký tài khoản")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final KhachHangRepository khachHangRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          KhachHangRepository khachHangRepo,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.khachHangRepo = khachHangRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống cấp Access Token JWT")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        JwtAuthResponse authResponse = JwtAuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresInMs(86400000L)
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .fullName(userPrincipal.getFullName())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", authResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản Khách hàng mới")
    public ResponseEntity<ApiResponse<String>> registerCustomer(@Valid @RequestBody RegisterRequest request) {
        if (khachHangRepo.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email này đã được sử dụng trên hệ thống!");
        }
        if (khachHangRepo.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new BusinessException("Số điện thoại này đã được đăng ký trên hệ thống!");
        }
        if (request.getSoCccd() != null && !request.getSoCccd().isBlank() && khachHangRepo.existsBySoCccd(request.getSoCccd())) {
            throw new BusinessException("Số CCCD/Định danh cá nhân này đã tồn tại trên hệ thống!");
        }

        String maKhachHang = "KH" + System.currentTimeMillis();
        KhachHang khachHang = KhachHang.builder()
                .maKhachHang(maKhachHang)
                .hoTen(request.getHoTen())
                .ngaySinh(request.getNgaySinh())
                .soDienThoai(request.getSoDienThoai())
                .email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getPassword()))
                .soCccd(request.getSoCccd())
                .trangThaiTk("ACTIVE")
                .build();

        khachHangRepo.save(khachHang);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký tài khoản khách hàng thành công", maKhachHang));
    }
}
