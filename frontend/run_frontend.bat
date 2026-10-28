@echo off
chcp 65001 > nul
title Melia Hotel Hanoi - Frontend Launcher

echo =========================================================================
echo    HỆ THỐNG ĐẶT PHÒNG VÀ QUẢN LÝ KHÁCH SẠN MELIÁ HÀ NỘI
echo    KHỞI CHẠY GIAO DIỆN FRONTEND (UI/UX PRO MAX STANDARD)
echo =========================================================================
echo.

cd /d "%~dp0"

:: 1. Kiểm tra Java jwebserver (Tích hợp sẵn trong JDK 18+)
where jwebserver >nul 2>nul
if %errorlevel% equ 0 (
    echo [OK] Tìm thấy Java jwebserver. Đang khởi chạy máy chủ web tại http://localhost:3000 ...
    start "" http://localhost:3000/index.html
    jwebserver -p 3000 -d . -b 127.0.0.1
    goto end
)

:: Kiểm tra đường dẫn JDK Eclipse Adoptium
if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot\bin\jwebserver.exe" (
    echo [OK] Tìm thấy JDK 25 jwebserver. Đang khởi chạy máy chủ web tại http://localhost:3000 ...
    start "" http://localhost:3000/index.html
    "C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot\bin\jwebserver.exe" -p 3000 -d . -b 127.0.0.1
    goto end
)

:: 2. Kiểm tra Python
where python >nul 2>nul
if %errorlevel% equ 0 (
    echo [OK] Tìm thấy Python. Đang khởi chạy máy chủ web tại http://localhost:3000 ...
    start "" http://localhost:3000/index.html
    python -m http.server 3000
    goto end
)

:: 3. Kiểm tra Node / npx
where npx >nul 2>nul
if %errorlevel% equ 0 (
    echo [OK] Tìm thấy Node.js. Đang khởi chạy web server tại http://localhost:3000 ...
    start "" http://localhost:3000/index.html
    npx -y serve -l 3000 .
    goto end
)

:: 4. Mở trực tiếp bằng trình duyệt mặc định
echo [OK] Đang mở giao diện trực tiếp trên trình duyệt mặc định...
start "" "%~dp0index.html"

:end
