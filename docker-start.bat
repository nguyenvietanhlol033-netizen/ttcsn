@echo off
chcp 65001 > nul
title Melia Hotel Hanoi - Docker Starter (TTCSN)

echo =========================================================================
echo    HỆ THỐNG ĐẶT PHÒNG VÀ QUẢN LÝ KHÁCH SẠN MELIÁ HÀ NỘI
echo    ĐỒ ÁN THỰC TẬP CƠ SỞ NGÀNH - NHÓM 11
echo    KHỞI CHẠY TOÀN BỘ HỆ THỐNG QUA DOCKER (BACKEND + FRONTEND)
echo =========================================================================
echo.

cd /d "%~dp0"

echo [1/3] Đang kiểm tra trạng thái Docker Daemon...
docker info >nul 2>nul
if %errorlevel% neq 0 (
    echo [THÔNG BÁO] Docker Desktop chưa chạy, đang khởi động Docker Desktop...
    start "" "C:\Users\PC\AppData\Local\Programs\DockerDesktop\Docker Desktop.exe"
    echo Đang chờ Docker Daemon sẵn sàng (khoảng 15-20 giây)...
    :wait_docker
    timeout /t 3 /nobreak >nul
    docker info >nul 2>nul
    if %errorlevel% neq 0 goto wait_docker
    echo [OK] Docker Daemon đã sẵn sàng!
)

echo [2/3] Đang khởi chạy hệ thống qua Docker Compose...
docker compose up -d --build

echo.
echo [3/3] Đang mở giao diện trên trình duyệt...
timeout /t 3 /nobreak >nul
start "" http://localhost:3000

echo =========================================================================
echo    HỆ THỐNG ĐÃ SẴN SÀNG HOẠT ĐỘNG!
echo    - Cổng Khách hàng (Frontend):   http://localhost:3000
echo    - Cổng Quản trị Lễ tân/Admin:  http://localhost:3000/admin.html
echo    - API Backend & Swagger UI:     http://localhost:8080/swagger-ui.html
echo =========================================================================
echo Để dừng hệ thống, chạy file: docker-stop.bat
echo =========================================================================
pause
