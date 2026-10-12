@echo off
chcp 65001 > nul
echo ==================================================================
echo   KHOI DONG HE THONG TINH THUE THU NHAP CA NHAN (PIT SYSTEM)
echo   Tu dong bien dich (build) code moi nhat truoc khi chay
echo ==================================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MVN=C:\Program Files\NetBeans-25\netbeans\java\maven\bin\mvn.cmd"

echo [1/2] Dang dong goi code moi nhat...
call "%MVN%" package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [LOI] Bien dich that bai! Vui long kiem tra lai loi trong code.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [2/2] Dang khoi dong may chu web tai cong 8080...
echo Truy cap giao dien tai: http://localhost:8080
echo.
echo Tai khoan mau:
echo   - Nguoi nop thue : nguyenvana / User@123
echo   - Can bo thue    : admin / Admin@123
echo.
java -jar target\tax-system-1.0.0.jar
pause
