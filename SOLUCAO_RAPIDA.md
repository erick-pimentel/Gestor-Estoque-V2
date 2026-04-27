# 🚀 SOLUÇÃO RÁPIDA - EXECUTE OS TESTES EM 3 MINUTOS

## ⚠️ Seu Erro
```
'mvn' não é reconhecido como nome de cmdlet
```

## ✅ Solução em 3 Passos

### **Passo 1: Instalar Maven (Automático - 2 min)**

Copie e execute COMO ADMINISTRADOR no PowerShell:

```powershell
# Clique com botão direito no PowerShell > Executar como administrador

# Cole este comando:
powershell -NoProfile -ExecutionPolicy Bypass -Command "& 'D:\projects\intelliji\GestorEstoquesWeb\install-maven.ps1'"
```

**Ou manualmente:**

1. Clique: Win + X
2. Selecione: Windows PowerShell (Admin)
3. Execute:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```
4. Execute:
```powershell
& 'D:\projects\intelliji\GestorEstoquesWeb\install-maven.ps1'
```

---

### **Passo 2: Feche e Reabra o PowerShell**

- Feche completamente
- Abra novamente (não precisa ser admin)

---

### **Passo 3: Rode os Testes**

```powershell
cd D:\projects\intelliji\GestorEstoquesWeb

# Todos os 69 testes
mvn clean test

# Ou apenas um serviço
mvn test -Dtest=ProdutoServiceTest
```

---

## 📊 Resultado Esperado

Se funcionar, você verá:
```
[INFO] Running com.gestorestoques.api.service.ProdutoServiceTest
[INFO] Tests run: 11, Failures: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

---

## ❓ Se não funcionar...

### **Erro: "Execution Policy"**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### **Erro: "Permission Denied"**
Abra PowerShell como Admin (Win + X > PowerShell Admin)

### **Erro: "Maven still not found"**
```powershell
# Feche e reabra PowerShell completamente
# Verifique:
mvn --version
```

### **Erro: "Java not found"**
```powershell
java -version
```
Se não funcionar, instale Java 21+:
https://www.oracle.com/java/technologies/javase-jdk21-downloads.html

---

## 💡 Dica Rápida

Se tudo falhar, use a IDE:
- IntelliJ IDEA (File > Open > Selecione projeto)
- VS Code (Extensions: Maven for Java)
- Eclipse (Import > Existing Maven Project)

A IDE resolvará Maven automaticamente!

---

## ✨ Depois de Instalar Maven

Comandos úteis:

```bash
# Todos os testes
mvn clean test

# Testes específicos
mvn test -Dtest=ProdutoServiceTest
mvn test -Dtest=MovimentacaoServiceTest
mvn test -Dtest=UsuarioServiceTest

# Gerar relatório de cobertura
mvn clean test jacoco:report

# Gerar relatório de testes
mvn test surefire-report:report

# Apenas compilar (não executar)
mvn compile test-compile
```

---

**🎯 Seu objetivo:** Executar `mvn clean test` com sucesso!

Tente agora e me informe se funciona! 🚀

