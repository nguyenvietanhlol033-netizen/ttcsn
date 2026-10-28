@echo off
chcp 65001 > nul
title Melia Hotel Hanoi - Backend Spring Boot

echo =========================================================================
echo KHACH SAN MELIA HA NOI - BACKEND SPRING BOOT
echo De tai: Thuc tap co so nganh - Nhom 11
echo =========================================================================

echo [1/2] Kiem tra trang thai cong 8080...
netstat -ano | findstr :8080 | findstr LISTENING > nul
if %errorlevel% equ 0 (
    echo.
    echo =========================================================================
    echo [THONG BAO] Cong 8080 hien dang duoc su dung!
    echo Nguyen nhan: Docker container 'melia-hotel-backend' dang chay tren cong 8080.
    echo.
    echo Ban co the chon 1 trong 2 cach sau:
    echo.
    echo  [CACH 1 - DUNG DOCKER]:
    echo    He thong da san sang chay, ban KHONG CAN chay file run.bat nay nua!
    echo    - Frontend:   http://localhost:3000
    echo    - Swagger UI: http://localhost:8080/swagger-ui.html
    echo.
    echo  [CACH 2 - CHAY TRUC TIEP TREN MAY BANG RUN.BAT]:
    echo    1. Tat Docker bang cach chay file: docker-stop.bat
    echo       (hoac go lenh: docker stop melia-hotel-backend)
    echo    2. Sau do click dup lai file run.bat nay.
    echo =========================================================================
    echo.
    pause
    exit /b 1
)

echo [2/2] Dang khoi chay he thong tren cong 8080...
echo Swagger UI se kha dung tai: http://localhost:8080/swagger-ui.html
echo =========================================================================

if exist "target\melia-hotel-backend-1.0.0.jar" (
    java -jar target\melia-hotel-backend-1.0.0.jar
) else (
    call mvnw.cmd spring-boot:run
)

pause
