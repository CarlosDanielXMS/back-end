# Teste Técnico – Desenvolvedor Backend Java (Júnior)

Este projeto implementa uma API REST em Java com Spring Boot para o gerenciamento de clientes, locações e reservas.  
A aplicação utiliza autenticação JWT, migração de banco de dados com Flyway e documentação via Swagger

---

## Tecnologias utilizadas

- Java 17  
- Spring Boot 3.5  
- Spring Data JPA  
- Spring Security (JWT)  
- Flyway  
- MapStruct  
- Springdoc OpenAPI (Swagger)  
- PostgreSQL  
- Docker e Docker Compose

---

## Requisitos de ambiente

- JDK 17 ou superior  
- Maven 3.9 ou superior  
- Docker (opcional, mas recomendado)  
- Porta 8080 livre no sistema  

---

## Como executar o projeto

### 1. Clonar o repositório
```bash
git clone https://github.com/CarlosDanielXMS/back-end.git
cd back-end
```

### 2. Gerar o arquivo .jar da aplicação
Execute o comando abaixo para compilar o projeto e gerar o arquivo .jar:
```bash
mvn clean package -DskipTests
```
O arquivo será criado dentro da pasta `target/`.

### 3. Executar o projeto com Docker (Recomendado)
Com o Docker e o Docker Compose instalados, execute:
```bash
docker compose up --build
```
Esse comando irá criar e iniciar os containers do PostgreSQL e da aplicação.  
A aplicação ficará disponível em:
```
http://localhost:8080/swagger-ui.html
```

### 4. Executar o projeto localmente (sem Docker)
Caso prefira executar sem o Docker, siga os passos abaixo.

1. Crie o banco de dados manualmente:
```sql
CREATE DATABASE testeTecnico;
```
2. Atualize as credenciais de acesso no arquivo:
```
src/main/resources/application.properties
```
3. Execute a aplicação localmente:
```bash
mvn spring-boot:run
```

O Flyway criará automaticamente as tabelas `clientes`, `locacoes` e `reservas`.  
A aplicação estará disponível em `http://localhost:8080/swagger-ui.html`.

---

## Autenticação e uso do JWT

A autenticação é feita por token JWT.

### Login
Endpoint público:
```
POST /auth/login
```

Exemplo de corpo da requisição:
```json
{
  "email": "admin@teste.com",
  "senha": "123456"
}
```

A resposta conterá o token JWT:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

No Swagger, clique em "Authorize" insira o token e clique em `Authorize`.

---

## Documentação da API

A documentação pode ser acessada em:

http://localhost:8080/swagger-ui.html

Ela contém os grupos:
- Autenticação
- Clientes
- Locações
- Reservas

---

## Banco de dados e migração

O banco é gerenciado pelo Flyway, que cria as tabelas principais e aplica validações de integridade.  
O script inicial (`V1__init.sql`) contém:
- Tabelas `clientes`, `locacoes` e `reservas`
- Restrições de integridade (check, foreign key e regras de período)

---

## Estrutura do projeto

```
src/main/java/com/example/back_end
 ├── auth/           -> autenticação e JWT
 ├── configs/        -> segurança e Swagger
 ├── controllers/    -> endpoints REST
 ├── dtos/           -> objetos de transferência de dados
 ├── entities/       -> entidades JPA
 ├── enums/          -> enums de domínio (SituaçãoReserva, TiposLocação)
 ├── exceptions/     -> tratamento global de erros
 ├── mappers/        -> conversores MapStruct
 ├── repositories/   -> interfaces de acesso ao banco
 └── services/       -> regras de negócio
```

---

## Testes automatizados

Os testes podem ser executados com:
```bash
mvn test
```

Incluem:
- Testes de autenticação (login válido e inválido)
- Testes de serviços (ClienteService, LocacaoService, ReservaService)
- Testes de integração de regras de negócio (sobreposição de reservas, exclusão com vínculo etc.)

---

## Uso do Docker

O projeto pode ser executado de forma completa usando Docker.

Dockerfile: cria uma imagem leve baseada em `eclipse-temurin:17-jre-alpine`.  
docker-compose.yml: sobe o banco PostgreSQL e a aplicação Spring Boot.  
Os dados do banco são persistidos em volume local.

---

## Sobre o campo “Tipo de Locação”

O DER fornecido com o teste não contém uma tabela separada para “tipo de locação”, além de que Locação não é mencionada em "Locações que devem ser implementadas". Por isso, subentende-se que "Tipos de Locações - CRUD completo" se refere a Locações.  
Por isso, o campo `tipo` foi implementado como um enum (`TiposLocacao`), representando valores fixos: RESIDENCIAL, NAO_RESIDENCIAL e TEMPORADA.

---

## Regras de negócio implementadas

- Impedir reservas com datas sobrepostas para a mesma locação.  
- Impedir exclusão de cliente ou locação com reservas vinculadas.  
- Validar que data_fim é posterior a data_inicio.  
- Verificar se o tempo de reserva respeita o intervalo mínimo e máximo da Locação.  
- Calcular o valor total automaticamente com base no tempo da Reserva e no valor por hora da Locação.  
- Aplicar paginação em todos os endpoints de listagem.  

---

## Diferenciais entregues

- Projeto totalmente conteinerizado (Dockerfile e docker-compose)  
- Testes automatizados 
- DTOs e Mappers implementados com MapStruct  
- Paginação configurada em todos os endpoints de listagem  
- Tratamento global de erros padronizado  
- Documentação via Swagger/OpenAPI  

---

## Credenciais padrão

| Campo | Valor |
|--------|--------|
| E-mail | admin@teste.com |
| Senha  | 123456 |

---

## Rastreamento dos requisitos do teste

| Requisito (documento do teste) | Implementação |
|--------------------------------|----------------|
| Clientes - CRUD completo | ClienteController + DTOs + validações |
| Locações - CRUD completo | LocacoesController + DTOs + validações |
| Tipos de Locações - CRUD completo | Implementado como enum (DER não possui tabela separada) |
| Reservas - CRUD completo + validações | ReservaController + DTOs + validações + verificação de sobreposição |
| Disponibilidade de locações | Endpoint /locacoes/disponiveis & /locacoes/disponiveis-entre |
| Regras de negócio | Validações em ReservaService, ClienteService, LocacaoService |
| Autenticação JWT + login público | AuthController, JwtService, JwtAuthFilter |
| Documentação Swagger | OpenApiConfig |
| Migração de banco | Flyway |
| Testes unitários | Testes de serviços e autenticação |
| Docker | Dockerfile e docker-compose |

---

## Autor

Carlos Daniel Xavier Moreira da Silva  
FATEC – São José do Rio Preto  
[LinkedIn](https://www.linkedin.com/in/carlos-daniel-x-m-silva-731487206/)
