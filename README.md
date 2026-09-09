# Controle de Despesas Pessoais

API REST desenvolvida para praticar Java e Spring Boot, com cadastro de despesas, organização por categorias e acompanhamento de pagamentos. Projeto de aprendizado e portfólio.

## Tecnologias

Java 25 · Spring Boot 4.1.1 · Spring Web MVC · Spring Data JPA / Hibernate · Jakarta Validation · H2 · Maven

## Funcionalidades

- Cadastro, consulta, atualização e exclusão de despesas e categorias.
- Associação opcional de despesas a categorias.
- Filtros por situação de pagamento e categoria, inclusive combinados.
- Consulta de despesas vencidas e por período de vencimento.
- Marcação de despesas como pagas.
- Resumo por período com valores totais, pagos e pendentes.
- Validação dos dados e respostas padronizadas de erro.
- Bloqueio da exclusão de categorias que possuem despesas associadas.

## Como executar

Com o JDK 25 instalado, clone ou baixe o repositório e abra um terminal na pasta que contém o `pom.xml`.

No Windows, usando PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

O Maven Wrapper incluído no projeto baixa a versão necessária do Maven.

A API fica disponível em [http://localhost:8080](http://localhost:8080). As requisições podem ser feitas pelo Postman.

O banco H2 é configurado em `src/main/resources/application.properties`. O console pode ser acessado em [http://localhost:8080/h2-console](http://localhost:8080/h2-console), utilizando a URL JDBC e as credenciais definidas nesse arquivo.

## Endpoints

| Método | Rota | Operação |
| --- | --- | --- |
| GET | `/despesas` | Listar despesas; aceita os filtros opcionais `paga` e `categoriaId` |
| GET | `/despesas/{id}` | Buscar despesa por ID |
| GET | `/despesas/vencidas` | Listar despesas não pagas com vencimento anterior ao dia atual |
| GET | `/despesas/periodo?inicio=AAAA-MM-DD&fim=AAAA-MM-DD` | Consultar por período de vencimento |
| GET | `/despesas/resumo?inicio=AAAA-MM-DD&fim=AAAA-MM-DD` | Consultar resumo de valores por período |
| POST | `/despesas` | Cadastrar despesa |
| PUT | `/despesas/{id}` | Atualizar despesa |
| PATCH | `/despesas/{id}/pagar` | Marcar despesa como paga |
| DELETE | `/despesas/{id}` | Excluir despesa |
| GET | `/categorias` | Listar categorias |
| GET | `/categorias/{id}` | Buscar categoria por ID |
| POST | `/categorias` | Cadastrar categoria |
| PUT | `/categorias/{id}` | Atualizar categoria |
| DELETE | `/categorias/{id}` | Excluir categoria sem despesas associadas |

Exemplos de consultas:

```http
GET /despesas?paga=false&categoriaId=1
GET /despesas/periodo?inicio=2026-09-01&fim=2026-09-30
GET /despesas/resumo?inicio=2026-09-01&fim=2026-09-30
```

## Exemplo de cadastro

Envie os corpos das requisições como JSON, com o cabeçalho `Content-Type: application/json`.

Primeiro, cadastre uma categoria com `POST /categorias`:

```json
{
  "nome": "Lazer"
}
```

Depois, cadastre uma despesa com `POST /despesas`:

```json
{
  "descricao": "Ingresso de cinema",
  "valor": 45.00,
  "dataVencimento": "2026-09-15",
  "paga": false,
  "categoria": {
    "id": 1
  }
}
```

Substitua `1` pelo ID retornado no cadastro da categoria. Para cadastrar uma despesa sem categoria, omita o campo `categoria`. O ID da nova despesa é gerado automaticamente.

## Organização

O código é organizado em camadas: `resources` recebe as requisições HTTP, `services` concentra as regras de negócio e `repositories` realiza o acesso aos dados. As entidades ficam em `entities`.

## Autor

[Pedro Henrique dos Santos Silva](https://github.com/pedrohsantosdev)
