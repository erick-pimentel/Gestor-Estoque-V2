@echo off
REM ============================================
REM SCRIPT DE EXECUÇÃO DOS TESTES UNITÁRIOS
REM Gestor de Estoque - Testes JUnit 5 + Mockito
REM Windows Batch Version
REM ============================================

setlocal enabledelayedexpansion

cls
echo ==========================================
echo   GESTOR DE ESTOQUE - TESTES UNITARIOS
echo   JUnit 5 + Mockito
echo ==========================================
echo.

REM Define variáveis
set OPTION=%1
set PROJECT_DIR=%CD%

REM ============================================
REM VALIDAR MAVEN
REM ============================================
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Maven nao encontrado no PATH
    echo.
    echo Instale Maven ou adicione ao PATH do sistema
    echo https://maven.apache.org/download.cgi
    exit /b 1
)

echo [INFO] Maven encontrado:
mvn --version | findstr "Apache"
echo.

REM ============================================
REM PARSED OPTIONS
REM ============================================

if "%OPTION%"=="" goto SHOW_MENU
if /i "%OPTION%"=="all" goto RUN_ALL
if /i "%OPTION%"=="produto" goto TEST_PRODUTO
if /i "%OPTION%"=="movimentacao" goto TEST_MOVIMENTACAO
if /i "%OPTION%"=="usuario" goto TEST_USUARIO
if /i "%OPTION%"=="fornecedor" goto TEST_FORNECEDOR
if /i "%OPTION%"=="auth" goto TEST_AUTH
if /i "%OPTION%"=="relatorio" goto TEST_RELATORIO
if /i "%OPTION%"=="coverage" goto COVERAGE
if /i "%OPTION%"=="report" goto SUREFIRE_REPORT
if /i "%OPTION%"=="compile" goto COMPILE_ONLY
if /i "%OPTION%"=="verbose" goto VERBOSE_MODE
if /i "%OPTION%"=="help" goto SHOW_MENU
if /i "%OPTION%"=="?" goto SHOW_MENU
goto SHOW_MENU

REM ============================================
REM EXECUTAR TODOS OS TESTES
REM ============================================
:RUN_ALL
echo [OPCAO] Todos os Testes
echo.
echo Executando 69 testes unitarios...
echo.
call mvn clean test
if %ERRORLEVEL% equ 0 (
    echo.
    echo [OK] Todos os testes foram executados com sucesso!
) else (
    echo.
    echo [ERRO] Alguns testes falharam!
)
goto END

REM ============================================
REM TESTE: PRODUTO
REM ============================================
:TEST_PRODUTO
echo [OPCAO] Testes de Produto Service
echo.
echo Executando 11 testes...
echo.
call mvn test -Dtest=ProdutoServiceTest
goto END

REM ============================================
REM TESTE: MOVIMENTACAO
REM ============================================
:TEST_MOVIMENTACAO
echo [OPCAO] Testes de Movimentacao Service
echo.
echo Executando 13 testes...
echo.
call mvn test -Dtest=MovimentacaoServiceTest
goto END

REM ============================================
REM TESTE: USUARIO
REM ============================================
:TEST_USUARIO
echo [OPCAO] Testes de Usuario Service
echo.
echo Executando 11 testes...
echo.
call mvn test -Dtest=UsuarioServiceTest
goto END

REM ============================================
REM TESTE: FORNECEDOR
REM ============================================
:TEST_FORNECEDOR
echo [OPCAO] Testes de Fornecedor Service
echo.
echo Executando 11 testes...
echo.
call mvn test -Dtest=FornecedorServiceTest
goto END

REM ============================================
REM TESTE: AUTH
REM ============================================
:TEST_AUTH
echo [OPCAO] Testes de Auth Service
echo.
echo Executando 10 testes...
echo.
call mvn test -Dtest=AuthServiceTest
goto END

REM ============================================
REM TESTE: RELATORIO
REM ============================================
:TEST_RELATORIO
echo [OPCAO] Testes de Relatorio Service
echo.
echo Executando 13 testes...
echo.
call mvn test -Dtest=RelatorioServiceTest
goto END

REM ============================================
REM COBERTURA DE CODIGO
REM ============================================
:COVERAGE
echo [OPCAO] Cobertura de Codigo (JaCoCo)
echo.
echo Gerando relatorio de cobertura...
echo.
call mvn clean test jacoco:report
echo.
if exist "target\site\jacoco\index.html" (
    echo [OK] Relatorio gerado: target\site\jacoco\index.html
    echo.
    echo Abrindo relatorio...
    start target\site\jacoco\index.html
) else (
    echo [ERRO] Falha ao gerar relatorio
)
goto END

REM ============================================
REM RELATORIO SUREFIRE
REM ============================================
:SUREFIRE_REPORT
echo [OPCAO] Relatorio Surefire
echo.
echo Gerando relatorio de testes...
echo.
call mvn test surefire-report:report
echo.
if exist "target\site\surefire-report.html" (
    echo [OK] Relatorio gerado: target\site\surefire-report.html
    echo.
    echo Abrindo relatorio...
    start target\site\surefire-report.html
) else (
    echo [ERRO] Falha ao gerar relatorio
)
goto END

REM ============================================
REM COMPILAR APENAS
REM ============================================
:COMPILE_ONLY
echo [OPCAO] Compilar Apenas
echo.
echo Compilando testes sem executar...
echo.
call mvn test-compile
echo.
if %ERRORLEVEL% equ 0 (
    echo [OK] Testes compilados com sucesso!
) else (
    echo [ERRO] Erro na compilacao dos testes!
)
goto END

REM ============================================
REM MODO VERBOSE
REM ============================================
:VERBOSE_MODE
echo [OPCAO] Modo Verbose (Detalhado)
echo.
echo Executando testes com logs detalhados...
echo.
call mvn test -X
goto END

REM ============================================
REM MENU DE AJUDA
REM ============================================
:SHOW_MENU
echo.
echo ===================================================
echo   OPCOES DISPONÍVEIS
echo ===================================================
echo.
echo   all              - Executar todos os 69 testes
echo   produto          - Testes ProdutoService (11)
echo   movimentacao     - Testes MovimentacaoService (13)
echo   usuario          - Testes UsuarioService (11)
echo   fornecedor       - Testes FornecedorService (11)
echo   auth             - Testes AuthService (10)
echo   relatorio        - Testes RelatorioService (13)
echo   coverage         - Gerar cobertura (JaCoCo)
echo   report           - Gerar relatorio (Surefire)
echo   compile          - Apenas compilar testes
echo   verbose          - Modo verbose/detalhado
echo   help             - Mostrar este menu
echo.
echo ===================================================
echo   EXEMPLOS DE USO
echo ===================================================
echo.
echo   run-tests.bat all
echo   run-tests.bat produto
echo   run-tests.bat coverage
echo.
echo ===================================================
echo   INFORMACOES
echo ===================================================
echo.
echo   Total de Testes:     69
echo   Classes Testadas:    6 (Services)
echo   Cobertura Esperada:  ~90%%
echo   Framework:           JUnit 5 + Mockito
echo.
goto END

:END
endlocal

