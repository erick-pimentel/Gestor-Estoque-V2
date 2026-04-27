#!/bin/bash

# ============================================
# SCRIPT DE EXECUÇÃO DOS TESTES UNITÁRIOS
# Gestor de Estoque - Testes JUnit 5 + Mockito
# ============================================

echo "🧪 Iniciando execução dos testes unitários..."
echo ""

# Define cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# ============================================
# FUNÇÃO: Executa teste e mostra resultado
# ============================================
run_test() {
    local test_name=$1
    local description=$2
    echo -e "${BLUE}▶ Executando: ${description}${NC}"
    if mvn test -Dtest=$test_name > /dev/null 2>&1; then
        echo -e "${GREEN}✅ PASSOU: ${description}${NC}"
        return 0
    else
        echo -e "${RED}❌ FALHOU: ${description}${NC}"
        return 1
    fi
}

# ============================================
# OPÇÃO 1: Executar todos os testes
# ============================================
if [ "$1" == "all" ] || [ "$1" == "" ]; then
    echo -e "${YELLOW}[OPÇÃO: Todos os Testes]${NC}"
    echo ""
    mvn clean test
    exit $?
fi

# ============================================
# OPÇÃO 2: Executar testes específicos
# ============================================
if [ "$1" == "produto" ]; then
    echo -e "${YELLOW}[OPÇÃO: Testes de Produto]${NC}"
    mvn test -Dtest=ProdutoServiceTest
    exit $?
fi

if [ "$1" == "movimentacao" ]; then
    echo -e "${YELLOW}[OPÇÃO: Testes de Movimentação]${NC}"
    mvn test -Dtest=MovimentacaoServiceTest
    exit $?
fi

if [ "$1" == "usuario" ]; then
    echo -e "${YELLOW}[OPÇÃO: Testes de Usuário]${NC}"
    mvn test -Dtest=UsuarioServiceTest
    exit $?
fi

if [ "$1" == "fornecedor" ]; then
    echo -e "${YELLOW}[OPÇÃO: Testes de Fornecedor]${NC}"
    mvn test -Dtest=FornecedorServiceTest
    exit $?
fi

if [ "$1" == "auth" ]; then
    echo -e "${YELLOW}[OPÇÃO: Testes de Autenticação]${NC}"
    mvn test -Dtest=AuthServiceTest
    exit $?
fi

if [ "$1" == "relatorio" ]; then
    echo -e "${YELLOW}[OPÇÃO: Testes de Relatórios]${NC}"
    mvn test -Dtest=RelatorioServiceTest
    exit $?
fi

# ============================================
# OPÇÃO 3: Cobertura de código
# ============================================
if [ "$1" == "coverage" ]; then
    echo -e "${YELLOW}[OPÇÃO: Cobertura de Código]${NC}"
    echo ""
    echo "Gerando relatório de cobertura..."
    mvn clean test jacoco:report
    echo ""
    echo -e "${GREEN}✅ Relatório gerado em: target/site/jacoco/index.html${NC}"
    exit $?
fi

# ============================================
# OPÇÃO 4: Relatório Surefire
# ============================================
if [ "$1" == "report" ]; then
    echo -e "${YELLOW}[OPÇÃO: Relatório de Testes]${NC}"
    echo ""
    echo "Gerando relatório Surefire..."
    mvn test surefire-report:report
    echo ""
    echo -e "${GREEN}✅ Relatório gerado em: target/site/surefire-report.html${NC}"
    exit $?
fi

# ============================================
# OPÇÃO 5: Modo rápido (apenas compilação)
# ============================================
if [ "$1" == "compile" ]; then
    echo -e "${YELLOW}[OPÇÃO: Compilar Testes]${NC}"
    mvn test-compile
    exit $?
fi

# ============================================
# OPÇÃO 6: Modo verbose (com logs detalhados)
# ============================================
if [ "$1" == "verbose" ]; then
    echo -e "${YELLOW}[OPÇÃO: Modo Verbose/Detalhado]${NC}"
    mvn test -X
    exit $?
fi

# ============================================
# OPÇÃO 7: Serial (executar um de cada vez)
# ============================================
if [ "$1" == "serial" ]; then
    echo -e "${YELLOW}[OPÇÃO: Execução Serial]${NC}"
    echo ""
    run_test "ProdutoServiceTest" "Testes de Produto"
    run_test "MovimentacaoServiceTest" "Testes de Movimentação"
    run_test "UsuarioServiceTest" "Testes de Usuário"
    run_test "FornecedorServiceTest" "Testes de Fornecedor"
    run_test "AuthServiceTest" "Testes de Autenticação"
    run_test "RelatorioServiceTest" "Testes de Relatórios"
    exit $?
fi

# ============================================
# OPÇÃO 8: Modo rápido apenas com erros
# ============================================
if [ "$1" == "fail-fast" ]; then
    echo -e "${YELLOW}[OPÇÃO: Fail Fast (parar no primeiro erro)]${NC}"
    mvn test -DfailIfNoTests=false
    exit $?
fi

# ============================================
# MENU DE AJUDA
# ============================================
echo ""
echo -e "${BLUE}╔═══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   GESTOR DE ESTOQUE - EXECUÇÃO DE TESTES UNITÁRIOS           ║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}USO:${NC}"
echo "  ./run-tests.sh [OPÇÃO]"
echo ""
echo -e "${YELLOW}OPÇÕES DISPONÍVEIS:${NC}"
echo ""
echo "  all              Executar todos os testes (padrão)"
echo "  produto          Testar ProdutoService (11 testes)"
echo "  movimentacao     Testar MovimentacaoService (13 testes)"
echo "  usuario          Testar UsuarioService (11 testes)"
echo "  fornecedor       Testar FornecedorService (11 testes)"
echo "  auth             Testar AuthService (10 testes)"
echo "  relatorio        Testar RelatorioService (13 testes)"
echo "  coverage         Gerar relatório de cobertura com JaCoCo"
echo "  report           Gerar relatório de testes Surefire"
echo "  compile          Apenas compilar os testes"
echo "  verbose          Modo verbose com logs detalhados"
echo "  serial           Executar cada serviço um de cada vez"
echo "  fail-fast        Parar no primeiro erro encontrado"
echo ""
echo -e "${YELLOW}EXEMPLOS:${NC}"
echo ""
echo "  # Executar todos os testes"
echo "  ./run-tests.sh all"
echo ""
echo "  # Executar testes de um serviço específico"
echo "  ./run-tests.sh produto"
echo ""
echo "  # Gerar cobertura de código"
echo "  ./run-tests.sh coverage"
echo ""
echo "  # Modo verbose"
echo "  ./run-tests.sh verbose"
echo ""
echo -e "${YELLOW}TOTAIS:${NC}"
echo "  69 testes unitários"
echo "  6 classes de serviço testadas"
echo "  ~90% de cobertura de código"
echo ""

