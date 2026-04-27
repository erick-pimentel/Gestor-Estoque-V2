# 🧪 Guia Completo - Testes Unitários

## 📚 Índice
1. [Pré-requisitos](#pré-requisitos)
2. [Instalação](#instalação)
3. [Executando os Testes](#executando-os-testes)
4. [Entendendo os Testes](#entendendo-os-testes)
5. [Troubleshooting](#troubleshooting)
6. [Estrutura dos Testes](#estrutura-dos-testes)

---

## 🔧 Pré-requisitos

### Sistema Operacional
- Windows 10+, macOS 10.14+, ou Linux (Ubuntu 18.04+)

### Software Necessário
- **Java JDK 17 ou superior**
  - Verificar: `java -version`
  - Baixar: https://www.oracle.com/java/technologies/javase-jdk17-downloads.html

- **Maven 3.8+**
  - Verificar: `mvn --version`
  - Baixar: https://maven.apache.org/download.cgi

- **Git** (opcional, mas recomendado)
  - Verificar: `git --version`
  - Baixar: https://git-scm.com/

### Variáveis de Ambiente

#### Windows
1. Abra "Variáveis de Ambiente" (`Win + Pause/Break > Configurações Avançadas`)
2. Adicione/Verifique:
   - `JAVA_HOME`: `C:\Program Files\Java\jdk-17`
   - `M2_HOME`: `C:\apache-maven-3.9.0`
   - PATH: Inclua `%JAVA_HOME%\bin` e `%M2_HOME%\bin`

#### Linux/macOS
```bash
# Adicionar ao ~/.bashrc ou ~/.zshrc
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export M2_HOME=/opt/apache-maven-3.9.0
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH
```

---

## 📥 Instalação

### 1. Clonar o Repositório
```bash
git clone https://github.com/erick-pimentel/Gestor-Estoque-V2.git
cd Gestor-Estoque-V2
git checkout GestorEstoque-Spring
```

### 2. Verificar Ambiente
```bash
# Verificar Java
java -version

# Verificar Maven
mvn --version

# Verificar projeto
mvn clean compile
```

### 3. Dependências dos Testes
Os testes usam as seguintes dependências (já incluídas no `pom.xml`):

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JaCoCo (Cobertura) -->
<dependency>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 🚀 Executando os Testes

### ✅ 1. Executar Todos os 69 Testes

#### Windows
```bash
.\run-tests.bat all
```

#### Linux/macOS
```bash
bash run-tests.sh all
```

#### Manual (Todos os Sistemas)
```bash
mvn clean test
```

**Tempo esperado:** 30-60 segundos

---

### ✅ 2. Executar Testes de um Serviço Específico

#### ProdutoService (11 testes)
```bash
# Windows
.\run-tests.bat produto

# Linux/macOS
bash run-tests.sh produto

# Manual
mvn test -Dtest=ProdutoServiceTest
```

#### MovimentacaoService (13 testes)
```bash
mvn test -Dtest=MovimentacaoServiceTest
```

#### UsuarioService (11 testes)
```bash
mvn test -Dtest=UsuarioServiceTest
```

#### FornecedorService (11 testes)
```bash
mvn test -Dtest=FornecedorServiceTest
```

#### AuthService (10 testes)
```bash
mvn test -Dtest=AuthServiceTest
```

#### RelatorioService (13 testes)
```bash
mvn test -Dtest=RelatorioServiceTest
```

---

### ✅ 3. Gerar Relatório de Cobertura

```bash
# Windows
.\run-tests.bat coverage

# Linux/macOS
bash run-tests.sh coverage

# Manual
mvn clean test jacoco:report
```

**Resultado:** Abra `target/site/jacoco/index.html` no navegador

---

### ✅ 4. Gerar Relatório Surefire

```bash
# Windows
.\run-tests.bat report

# Linux/macOS
bash run-tests.sh report

# Manual
mvn test surefire-report:report
```

**Resultado:** Abra `target/site/surefire-report.html` no navegador

---

### ✅ 5. Modo Verbose (Debug)

```bash
# Windows
.\run-tests.bat verbose

# Linux/macOS
bash run-tests.sh verbose

# Manual
mvn test -X
```

---

## 📖 Entendendo os Testes

### Estrutura de um Teste

```java
@ExtendWith(MockitoExtension.class)  // Ativa Mockito
@DisplayName("Testes do ProdutoService")  // Descrição
class ProdutoServiceTest {
    
    @Mock  // Cria um mock
    private ProdutoRepository produtoRepository;
    
    @InjectMocks  // Injeta dependências mockadas
    private ProdutoService produtoService;
    
    @BeforeEach  // Executado antes de cada teste
    void setUp() {
        // Preparar dados comuns
    }
    
    @Test
    @DisplayName("Deve criar produto com sucesso")
    void testCriarSucesso() {
        // ARRANGE - Preparar
        when(produtoRepository.save(any())).thenReturn(produto);
        
        // ACT - Executar
        ProdutoResponseDTO resultado = produtoService.criar(dto);
        
        // ASSERT - Validar
        assertNotNull(resultado);
        assertEquals("Produto Test", resultado.getNome());
        
        // VERIFY - Verificar chamadas
        verify(produtoRepository, times(1)).save(any());
    }
}
```

### O Padrão AAA
1. **Arrange** - Preparar dados e mocks
2. **Act** - Executar a ação
3. **Assert** - Validar o resultado

### Anotações Importantes

| Anotação | Uso |
|----------|-----|
| `@Test` | Marca método como teste |
| `@DisplayName` | Descrição legível do teste |
| `@BeforeEach` | Executar antes de cada teste |
| `@AfterEach` | Executar depois de cada teste |
| `@Mock` | Criar mock de dependência |
| `@InjectMocks` | Injetar o serviço a testar |
| `@ExtendWith` | Ativar framework (Mockito) |

---

## 🔍 Entendendo os Mocks

### O que é um Mock?
Um **mock** é um objeto falso que simula o comportamento de um objeto real.

```java
// Sem mock (integração)
ProdutoRepository repo = new ProdutoRepository();
ProdutoService service = new ProdutoService(repo);

// Com mock (unitário)
@Mock
ProdutoRepository repo;  // Mock do repositório

@InjectMocks
ProdutoService service;  // Serviço com dependência mockada
```

### Configurar Comportamento
```java
// Quando save() é chamado, retornar produto
when(produtoRepository.save(any())).thenReturn(produto);

// Quando findById() for chamado com 1, retornar Optional
when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

// Quando findById() for chamado com 999, lançar exception
when(produtoRepository.findById(999)).thenThrow(new RuntimeException());
```

### Verificar Chamadas
```java
// Verificar se save foi chamado 1 vez
verify(produtoRepository, times(1)).save(any());

// Verificar se delete nunca foi chamado
verify(produtoRepository, never()).delete(any());
```

---

## 🛠️ Troubleshooting

### ❌ Erro: "Maven não encontrado"

**Solução:**
```bash
# Windows
set PATH=%PATH%;C:\apache-maven-3.9.0\bin

# Linux/macOS
export PATH=$PATH:/opt/apache-maven-3.9.0/bin

# Verificar
mvn --version
```

---

### ❌ Erro: "Java version not recognized"

**Solução:**
```bash
# Definir JAVA_HOME
# Windows (Painel de Controle > Variáveis de Ambiente)

# Linux/macOS
export JAVA_HOME=$(/usr/libexec/java_where -v 17)
java -version
```

---

### ❌ Erro: "Testes falhando com NullPointerException"

**Causa:** Mockito não está ativado

**Solução:**
```java
// Adicionar esta anotação na classe de teste
@ExtendWith(MockitoExtension.class)
```

---

### ❌ Erro: "Could not resolve all dependencies"

**Solução:**
```bash
# Limpar cache do Maven
mvn clean
mvn dependency:resolve

# Atualizar dependências
mvn dependency:tree
```

---

### ❌ Testes lentos

**Otimizações:**
```bash
# Executar testes em paralelo
mvn test -T 1C

# Apenas compilar (sem executar)
mvn test-compile

# Executar um teste
mvn test -Dtest=ProdutoServiceTest
```

---

## 📊 Relatórios

### Cobertura de Código (JaCoCo)

```bash
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

**O que verá:**
- % de linhas cobertas por testes
- % de branches cobertas
- Classes com menor cobertura

---

### Relatório de Testes (Surefire)

```bash
mvn test surefire-report:report
# Abrir: target/site/surefire-report.html
```

**O que verá:**
- Número de testes executados
- Número de sucessos/falhas
- Tempo de execução
- Lista de testes que falharam

---

## 📝 Boas Práticas

✅ **Faça:**
- Use nomes descritivos: `testCriarProdutoComSucesso()`
- Teste casos de sucesso E erro
- Use `@DisplayName` para clareza
- Mantenha testes simples e focados
- Use constantes para dados comuns

❌ **Não faça:**
- Testes dependentes de ordem de execução
- Usar `Thread.sleep()` para sincronizar
- Testar múltiplas coisas em um teste
- Usar dados aleatórios (hard-code valores)
- Ignorar testes falhando (marcar como `@Disabled`)

---

## 🔗 Recursos

- [JUnit 5 Docs](https://junit.org/junit5/)
- [Mockito Docs](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)

---

## 💬 Suporte

Dúvidas? Confira:
1. `TESTS_README.md` - Resumo dos testes
2. `TESTES_SUMARIO.md` - Detalhes técnicos
3. Arquivos de teste - Exemplos de código

---

**Última atualização:** 27 de Abril de 2026
**Versão:** 1.0

