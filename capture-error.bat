@echo off
REM Script para capturar erro exato dos testes
REM Execute no PowerShell: cd D:\projects\intelliji\GestorEstoquesWeb; .\capture-error.bat

echo ========================================
echo  Capturando erro dos testes...
echo ========================================
echo.

cd D:\projects\intelliji\GestorEstoquesWeb

REM Compilar testes
echo [1] Compilando testes...
mvn test-compile -q 2> error.log

if %ERRORLEVEL% neq 0 (
    echo [ERRO DE COMPILACAO]
    type error.log
    goto END
)

echo [OK] Compilacao sucedeu
echo.

REM Executar um teste específico
echo [2] Rodando teste individual...
mvn test -Dtest=ProdutoServiceTest -q 2> error.log

if %ERRORLEVEL% neq 0 (
    echo [ERRO DE EXECUCAO]
    type error.log
    goto END
)

echo [OK] Teste passou
echo.

echo [3] Rodando todos os testes...
mvn test -q 2> error.log

if %ERRORLEVEL% neq 0 (
    echo [ERRO]
    type error.log
    goto END
)

echo [OK] Todos os testes passaram!

:END
echo.
echo ========================================
type error.log
echo ========================================

