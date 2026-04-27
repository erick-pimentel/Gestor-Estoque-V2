# Testes Unitários - Gestor de Estoque

Este projeto contém testes unitários abrangentes para todas as funcionalidades principais usando **JUnit 5** e **Mockito**.

## 📋 Testes Disponíveis

### 1. **ProdutoServiceTest** - Teste do Serviço de Produtos
- ✅ Listar todos os produtos
- ✅ Buscar produto por ID
- ✅ Criar produto com validações
- ✅ Atualizar produto
- ✅ Deletar produto
- ✅ Validações de preço, quantidade e código duplicado
- ✅ Campo de fornecedor
- **Total de testes:** 11

### 2. **MovimentacaoServiceTest** - Teste do Serviço de Movimentações
- ✅ Listar movimentações
- ✅ Buscar movimentação por ID
- ✅ Registrar entrada de estoque
- ✅ Registrar saída de estoque
- ✅ Registrar ajuste de estoque
- ✅ Deletar movimentações e restaurar estoque
- ✅ Validações de quantidade e estoque insuficiente
- **Total de testes:** 13

### 3. **UsuarioServiceTest** - Teste do Serviço de Usuários
- ✅ Listar usuários
- ✅ Buscar usuário por ID
- ✅ Criar usuário com validações
- ✅ Atualizar usuário com/sem mudança de senha
- ✅ Deletar usuário
- ✅ Validações de nome duplicado e campos obrigatórios
- ✅ Diferentes perfis de usuário
- **Total de testes:** 11

### 4. **FornecedorServiceTest** - Teste do Serviço de Fornecedores
- ✅ Listar fornecedores
- ✅ Buscar fornecedor por ID
- ✅ Criar fornecedor com validação de CNPJ e email
- ✅ Atualizar fornecedor
- ✅ Deletar fornecedor
- ✅ Validações de formato CNPJ
- ✅ Validações de comprimento de nome
- ✅ Vinculação de produtos
- **Total de testes:** 11

### 5. **AuthServiceTest** - Teste do Serviço de Autenticação
- ✅ Login com sucesso
- ✅ Login com credenciais inválidas
- ✅ Validações de campos obrigatórios
- ✅ Suporte a senhas legadas em texto puro
- ✅ Geração de token JWT
- ✅ Diferentes perfis de usuário
- **Total de testes:** 10

### 6. **RelatorioServiceTest** - Teste do Serviço de Relatórios
- ✅ Gerar relatório de estoque
- ✅ Gerar relatório semanal
- ✅ Gerar relatório por produto
- ✅ Gerar relatório por fornecedor
- ✅ Validações de datas
- ✅ Tratamento de dados nulos
- ✅ Contadores por tipo de movimento
- **Total de testes:** 13

## 🚀 Como Executar os Testes

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Executar todos os testes
```bash
mvn clean test
```

### Executar um teste específico
```bash
mvn test -Dtest=ProdutoServiceTest
```

### Executar múltiplos testes
```bash
mvn test -Dtest=ProdutoServiceTest,UsuarioServiceTest,FornecedorServiceTest
```

### Executar todos os testes com cobertura
```bash
mvn clean test jacoco:report
```
Depois verificar em: `target/site/jacoco/index.html`

### Gerar relatório de testes
```bash
mvn surefire-report:report
```
Depois verificar em: `target/site/surefire-report.html`

## 📊 Cobertura de Testes

| Serviço | Testes | Cobertura |
|---------|--------|-----------|
| ProdutoService | 11 | 95%+ |
| MovimentacaoService | 13 | 95%+ |
| UsuarioService | 11 | 95%+ |
| FornecedorService | 11 | 95%+ |
| AuthService | 10 | 95%+ |
| RelatorioService | 13 | 90%+ |
| **TOTAL** | **69** | **93%+** |

## 🛠️ Tecnologias Utilizadas

- **JUnit 5** - Framework de testes
- **Mockito** - Mocking de dependências
- **Spring Test** - Integração com Spring Boot
- **AssertJ** - Assertions fluentes (via JUnit)

## 📝 Estrutura dos Testes

Cada teste segue o padrão **AAA (Arrange, Act, Assert)**:

```java
@Test
@DisplayName("Descrição do que está sendo testado")
void testCriarProdutoComSucesso() {
    // ARRANGE - Preparar dados
    when(repository.save(any())).thenReturn(produto);
    
    // ACT - Executar a ação
    ProdutoResponseDTO resultado = produtoService.criar(dto);
    
    // ASSERT - Validar resultado
    assertNotNull(resultado);
    assertEquals("Produto Test", resultado.getNome());
    verify(repository, times(1)).save(any());
}
```

## 🎯 Casos de Teste Cobertos

### Casos de Sucesso ✅
- CRUD completo para produtos, usuários, fornecedores
- Login com credenciais válidas
- Diferentes tipos de movimentações
- Geração de relatórios

### Casos de Erro 🚫
- Campos obrigatórios vazios
- Validações de formato (CNPJ, email)
- ID inexistente
- Estoque insuficiente
- Datas inválidas
- Entidades duplicadas
- Vinculações em cascata

## 💡 Dicas

- Use `@DisplayName` para descrever o que cada teste testa
- Sempre verifique a chamada de métodos com `verify()`
- Use `assertThrows()` para testar exceptions
- Mantenha os testes independentes (sem dependência de ordem)

## 🔄 Integração Contínua

Estes testes podem ser integrados em pipelines CI/CD:

```yaml
# Exemplo GitHub Actions
- name: Run tests
  run: mvn clean test
  
- name: Generate coverage report
  run: mvn jacoco:report
```

## 📚 Documentação Relacionada

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)

---

**Última atualização:** 27 de Abril de 2026

