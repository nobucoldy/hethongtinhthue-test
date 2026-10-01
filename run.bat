@echo off
chcp 65001 > nul
echo ==================================================================
echo   KHOI DONG HE THONG TINH THUE THU NHAP CA NHAN (PIT SYSTEM)
echo   Ngon ngu: Java 23 / 17 - Spring Boot 3.2.1
echo ==================================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Dang khoi dong may chu web tai cong 8080...
echo Truy cap giao dien tai: http://localhost:8080
echo.
echo Tai khoan mau:
echo   - Nguoi nop thue : nguyenvana / User@123
echo   - Can bo thue    : admin / Admin@123
echo.
java -jar target\tax-system-1.0.0.jar
pause
