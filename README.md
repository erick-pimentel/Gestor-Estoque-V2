# Gestor de Estoques API

Backend Spring Boot para o sistema de gerenciamento de estoques.

## Requisitos
- Java 17+
- Maven 3.9+
- MySQL com schema `gestor_estoque` e tabelas existentes

## Configuracao
Edite `src/main/resources/application.properties` e preencha:
- `spring.datasource.username`
- `spring.datasource.password`
- `app.jwt.secret`

## Executar
```bash
mvn spring-boot:run
```

Se estiver usando o alias PowerShell criado anteriormente:
```powershell
boot
```

## Testes
```bash
mvn test
```

## Endpoints principais
- `POST /auth/login`
- `GET/POST/PUT/DELETE /api/produtos`
- `GET/POST/PUT/DELETE /api/fornecedores`
- `GET/POST/DELETE /api/movimentacoes`
- `GET /api/relatorios/movimentacoes`
- `GET/POST/PUT/DELETE /api/usuarios`

## Frontend integrado
- Página inicial: `http://localhost:8080/HTML/Main.html`
- Login: `http://localhost:8080/HTML/Login.html`
- Após autenticar, as telas usam JWT no header `Authorization: Bearer <token>` e consomem dados reais do backend.

