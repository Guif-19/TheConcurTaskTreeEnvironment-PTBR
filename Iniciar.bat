@echo off
setlocal
pushd "%~dp0"
where java >nul 2>&1
if errorlevel 1 (
    echo Java nao encontrado. Instale o Java 8 ou superior.
    pause
    popd
    exit /b 1
)
if not exist "classes-ptbr\MainFrame.class" (
    echo Execute Compilar.ps1 para gerar esta versao.
    pause
    popd
    exit /b 1
)
java -Dfile.encoding=UTF-8 -cp "classes-ptbr" MainFrame
set "CTT_EXIT=%ERRORLEVEL%"
if not "%CTT_EXIT%"=="0" pause
popd
exit /b %CTT_EXIT%
