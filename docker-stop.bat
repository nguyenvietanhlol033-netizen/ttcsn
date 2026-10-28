@echo off
chcp 65001 > nul
title Melia Hotel Hanoi - Docker Stopper (TTCSN)

echo =========================================================================
echo    HỆ THỐNG ĐẶT PHÒNG VÀ QUẢN LÝ KHÁCH SẠN MELIÁ HÀ NỘI
echo    ĐỒ ÁN THỰC TẬP CƠ SỞ NGÀNH - NHÓM 11
echo    DỪNG TOÀN BỘ CÁC SERVICE TRÊN DOCKER
echo =========================================================================
echo.

cd /d "%~dp0"
docker compose down

echo.
echo =========================================================================
echo [OK] Đã dừng và giải phóng toàn bộ tài nguyên Docker thành công!
echo =========================================================================
pause
