@echo off
setlocal EnableExtensions

rem A tiny Maven bootstrapper so Maven does not need to be installed globally.
set "MAVEN_VERSION=3.9.12"
set "WRAPPER_ROOT=%~dp0"
set "MAVEN_HOME=%WRAPPER_ROOT%.mvn\apache-maven-%MAVEN_VERSION%"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Downloading Apache Maven %MAVEN_VERSION% for this project...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference = 'Stop'; $root = $env:WRAPPER_ROOT; $mavenDirectory = Join-Path $root '.mvn'; $archive = Join-Path ([System.IO.Path]::GetTempPath()) 'apache-maven-3.9.12-bin.zip'; New-Item -ItemType Directory -Path $mavenDirectory -Force | Out-Null; Invoke-WebRequest -UseBasicParsing -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.12/apache-maven-3.9.12-bin.zip' -OutFile $archive; Expand-Archive -Path $archive -DestinationPath $mavenDirectory -Force"
    if errorlevel 1 exit /b %errorlevel%
)

call "%MAVEN_HOME%\bin\mvn.cmd" %*
