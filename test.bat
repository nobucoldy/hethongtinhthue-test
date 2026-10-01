@echo off
chcp 65001 > nul
echo ==================================================================
echo   CHAY KIEM THU DON VI JUNIT 5 & XUAT BAO CAO JACOO COVERAGE
echo ==================================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-23"
set "MVN=C:\Program Files\NetBeans-25\netbeans\java\maven\bin\mvn.cmd"

echo Dang chay 98 test cases JUnit 5...
call "%MVN%" test jacoco:report

echo.
echo ==================================================================
echo   HOAN TAT!
echo   Bao cao do phu JaCoCo da duoc xuat tai:
echo   target\site\jacoco\index.html
echo ==================================================================
pause
