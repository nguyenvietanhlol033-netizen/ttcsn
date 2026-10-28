@echo off
chcp 65001 > nul
echo =========================================================================
echo KHÁCH SẠN MELIÁ HÀ NỘI - BACKEND SPRING BOOT
echo Đề tài: Thực tập cơ sở ngành - Nhóm 11
echo =========================================================================
echo Đang khởi chạy hệ thống trên cổng 8080...
echo Swagger UI sẽ khả dụng tại: http://localhost:8080/swagger-ui.html
echo =========================================================================

if exist "target\melia-hotel-backend-1.0.0.jar" (
    java -jar target\melia-hotel-backend-1.0.0.jar
) else (
    call mvnw.cmd spring-boot:run
)

pause
