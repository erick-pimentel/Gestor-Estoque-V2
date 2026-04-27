# 🔧 SOLUÇÃO: Maven não Encontrado

## 🚨 Erro Recebido
```
'mvn' não é reconhecido como nome de cmdlet
```

## ✅ SOLUÇÃO RÁPIDA (Recomendado)

### **Opção 1: Instalar Maven (5 minutos)**

#### Windows:

1. **Baixar Maven**
   - Acesse: https://maven.apache.org/download.cgi
   - Baixe: `apache-maven-3.9.x-bin.zip` (versão mais recente)

2. **Descompactar**
   - Descompacte em: `C:\apache-maven-3.9.x`
   - Exemplo: `C:\apache-maven-3.9.8`

3. **Adicionar ao PATH**
   - Abra: Painel de Controle > Variáveis de Ambiente
   - Clique: "Editar variáveis de ambiente da conta"
   - Nova variável:
     - **Nome:** `M2_HOME`
     - **Valor:** `C:\apache-maven-3.9.8`
   
4. **Atualizar PATH**
   - Localize a variável `Path`
   - Clique: "Editar..."
   - Novo: `%M2_HOME%\bin`
   - Clique: OK > OK > OK

5. **Verificar (Restart PowerShell)**
   ```bash
   mvn --version
   ```

---

#### Linux/macOS:

```bash
# macOS com Homebrew
brew install maven

# Linux (Ubuntu/Debian)
sudo apt-get install maven

# Verificar
mvn --version
```

---

### **Opção 2: Usando Docker (Se Java/Maven não funcionar)**

```bash
# Clonar repo
git clone https://github.com/erick-pimentel/Gestor-Estoque-V2.git
cd Gestor-Estoque-V2
git checkout GestorEstoque-Spring

# Rodar testes com Docker
docker run -v %cd%:/app -w /app maven:3.9-eclipse-temurin mvn clean test
```

---

### **Opção 3: Usar IDE (IntelliJ IDEA)**

Se você já tem IntelliJ:

1. Abra o projeto
2. Clique em: File > Project Structure
3. Configure JDK 21+
4. Clique: Run > Edit Configurations
5. Novo: Maven
6. Name: "Test All"
7. Command line: `clean test`
8. Clique: Run

---

## 🧪 Executar Após Instalar Maven

```bash
# Todos os testes
mvn clean test

# Apenas ProdutoService
mvn test -Dtest=ProdutoServiceTest

# Gerar cobertura
mvn clean test jacoco:report
```

---

## ✨ Alternativas Rápidas

### Sem Maven Instalado:

1. **Usar IDE (recomendado)**
   - IntelliJ IDEA
   - Eclipse
   - VS Code com Extensions

2. **Usar Docker**
   - Sem instalar nada localmente
   - Tudo containerizado

3. **Usar GitHub Actions**
   - Fazer push para GitHub
   - Testes rodam automaticamente

---

## 🎯 Passos Recomendados

### **Solução Mais Rápida (5 min):**

**Windows PowerShell (como Admin):**
```powershell
# 1. Baixar Maven
Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.8/binaries/apache-maven-3.9.8-bin.zip" -OutFile C:\maven.zip

# 2. Descompactar
Expand-Archive -Path C:\maven.zip -DestinationPath C:\

# 3. Adicionar ao PATH (permanente)
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\apache-maven-3.9.8\bin", "User")

# 4. Fechar e Abrir PowerShell novamente

# 5. Verificar
mvn --version
```

---

## 🔍 Diagnóstico

Após instalar, verifique:

```bash
# Deve mostrar versão
mvn --version

# Deve estar no PATH
where mvn

# Deve estar em C:\apache-maven-3.9.x\bin\mvn.cmd
Get-Command mvn
```

---

## 💡 Dicas

- ✅ Use **PowerShell como admin** para instalar
- ✅ **Reinicie depois de adicionar ao PATH**
- ✅ Verifique com `mvn --version` antes de rodar testes
- ✅ Se erro persistir, limpe cache: `mvn clean`

---

## 🆘 Ainda com Problemas?

1. Verifique Java:
```bash
java -version    # Deve ser 17+, seu é 21 (OK!)
```

2. Limpe cache Maven:
```bash
mvn clean
```

3. Reinstale Maven:
   - Delete pasta C:\apache-maven-3.9.x
   - Refaça a instalação

4. Use IDE como alternativa

---

**Próximo passo:** Instale Maven e rode `mvn clean test` novamente! 🚀

