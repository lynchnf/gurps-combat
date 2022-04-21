@echo off
if "%OS%" == "Windows_NT" setlocal
set CURRENT_DIR=%cd%
cd /d %0\..
start "${pom.name}" /B java -jar ..\lib\${pom.artifactId}-${pom.version}.jar
cd %CURRENT_DIR%
