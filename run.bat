@echo off
chcp 65001 >nul
set ISP_UTF8=1
if not exist out mkdir out
javac -encoding UTF-8 -d out src\isp\*.java
if errorlevel 1 (
  echo Compilation failed.
  pause
  exit /b 1
)
java -cp out isp.Main
pause
