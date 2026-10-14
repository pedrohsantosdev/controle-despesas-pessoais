# Controle de Despesas Pessoais

API REST para cadastrar despesas, organizá-las por categorias e acompanhar pagamentos. Projeto de aprendizado e portfólio desenvolvido com Java e Spring Boot, com persistência em MySQL e execução por Docker Compose.

## Funcionalidades

- Cadastro, consulta, atualização e exclusão de despesas e categorias.
- Associação opcional de despesas a categorias.
- Filtros por categoria e situação de pagamento, separados ou combinados.
- Consulta de despesas vencidas e por período de vencimento.
- Marcação de despesas como pagas.
- Resumo por período com valores totais, pagos e pendentes.
- Validação dos dados e tratamento de erros HTTP.
- Bloqueio da exclusão de categorias que possuem despesas associadas.

## Tecnologias

| Tecnologia | Uso |
| --- | --- |
| Java 25 e Spring Boot 4.1.1 | Desenvolvimento e execução da aplicação |
| Spring Web MVC | Endpoints REST |
| Spring Data JPA e Hibernate | Persistência e mapeamento das entidades |
| Jakarta Validation | Validação dos dados de entrada |
| MySQL 8.4.7 | Banco no Docker Compose |
| H2 | Banco em memória utilizado somente nos testes |
| JUnit, Mockito e MockMvc | Testes automatizados |
| Maven Wrapper | Compilação e execução local |
| Docker e Docker Compose | Construção da imagem e execução dos serviços |

## Executar com Docker

É necessário ter Docker Desktop em execução, com suporte a contêineres Linux e Docker Compose. Java e Maven são fornecidos pelas imagens do Docker; não precisam estar instalados no computador para esta opção.

Os comandos abaixo são para PowerShell, executados na raiz do projeto.

### 1. Obter o projeto

```powershell
git clone https://github.com/pedrohsantosdev/controle-despesas-pessoais.git
cd controle-despesas-pessoais
```

### 2. Configurar as senhas

Na primeira execução, copie o arquivo de exemplo. Se já houver um `.env`, mantenha o arquivo existente.

```powershell
Copy-Item .env.example .env
```

No Linux ou macOS, utilize `cp .env.example .env`.

Edite o `.env`, substituindo os exemplos por senhas de sua escolha:

```dotenv
MYSQL_PASSWORD=defina_uma_senha_local
MYSQL_ROOT_PASSWORD=defina_outra_senha_para_root
```

`MYSQL_PASSWORD` é a senha do usuário da aplicação, `dev_pedro`. `MYSQL_ROOT_PASSWORD` é a senha do administrador do MySQL. O Compose exige as duas variáveis e fornece ao backend apenas as credenciais do usuário da aplicação.

O `.env` está excluído do versionamento e do contexto de construção da imagem. O `.env.example` contém apenas exemplos.

### 3. Iniciar e consultar a API

```powershell
docker compose up -d --build
```

O Compose inicia os serviços `mysql` e `backend`, aguardando a verificação de saúde do banco antes de iniciar a API. Na primeira execução, o download das imagens e dependências pode levar alguns minutos.

Confira o estado e acompanhe a inicialização:

```powershell
docker compose ps
docker compose logs -f backend
```

Aguarde a mensagem `Started DespesasApplication`. Nesse comando de acompanhamento de logs, `Ctrl+C` retorna ao terminal sem parar os serviços.

Endereço base: **[http://localhost:8081](http://localhost:8081)**.

No Postman, envie `GET http://localhost:8081/despesas`. Pelo PowerShell:

```powershell
Invoke-RestMethod -Uri http://localhost:8081/despesas
```

A resposta esperada é `200 OK`, com uma lista de despesas, que pode estar vazia. A API não possui uma página inicial na rota `/`; utilize os endpoints documentados abaixo.

### Portas e conexão

| Acesso | Endereço |
| --- | --- |
| API pelo computador | `http://localhost:8081` |
| API dentro do contêiner | Porta `8080` |
| MySQL pelo computador, inclusive Workbench | `127.0.0.1:3307` |
| MySQL a partir do backend no Compose | `mysql:3306` |
| Banco e usuário | `despesas` / `dev_pedro` |

As portas publicadas pelo Compose ficam vinculadas a `127.0.0.1`. As variáveis `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` configuram a conexão do backend dentro do contêiner.

### Parar, retomar e preservar os dados

Para parar a API e o MySQL:

```powershell
docker compose stop
```

Para iniciar novamente:

```powershell
docker compose up -d
```

Se executar `docker compose up --build` sem `-d`, os logs ficam no terminal e `Ctrl+C` encerra os serviços. Após alterar o código, execute novamente `docker compose up -d --build` para reconstruir a imagem.

O volume `mysql_data` armazena os dados do banco e os preserva ao parar ou recriar os contêineres. Remover esse volume apaga os dados. As credenciais do MySQL são configuradas na primeira inicialização do volume; mudar o `.env` depois disso não altera automaticamente a senha já cadastrada no banco.

O Dockerfile compila o projeto em uma etapa com Maven e executa o JAR em outra com Java. Ele utiliza `-DskipTests`: os testes não são executados na construção da imagem.

## Executar pelo Maven ou IntelliJ

Esta opção exige JDK 25 e um MySQL disponível. Para utilizar apenas o banco do Compose, configure o `.env` conforme a seção anterior e execute:

```powershell
docker compose up -d mysql
```

Aguarde o serviço `mysql` aparecer como `healthy` em `docker compose ps`. Configure a senha e inicie a aplicação no mesmo terminal:

```powershell
$env:DB_PASSWORD = "substitua_pela_mesma_senha_de_MYSQL_PASSWORD"
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
export DB_PASSWORD='substitua_pela_mesma_senha_de_MYSQL_PASSWORD'
./mvnw spring-boot:run
```

Se necessário, conceda permissão de execução ao wrapper com `chmod +x mvnw`.

No IntelliJ, configure `DB_PASSWORD` nas variáveis de ambiente da configuração de execução e execute `DespesasApplication`.

**Nesta opção, a API fica em [http://localhost:8080](http://localhost:8080).** O arquivo `application.properties` usa os seguintes valores:

| Variável | Valor padrão ou finalidade |
| --- | --- |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3307`, porta publicada pelo Compose |
| `DB_PASSWORD` | Obrigatória; senha do usuário `dev_pedro` |

O nome do banco é `despesas` e o usuário padrão é `dev_pedro`. Para outro MySQL, ajuste a conexão e as credenciais conforme o seu ambiente. O `.env` usado pelo Compose não é carregado automaticamente pela execução local desta aplicação.

A configuração atual utiliza `spring.jpa.hibernate.ddl-auto=update` para atualizar a estrutura das tabelas no ambiente de desenvolvimento. O projeto não possui migrações versionadas de banco.

## Endpoints

### Despesas

| Método | Rota | Operação |
| --- | --- | --- |
| GET | `/despesas` | Listar; aceita os filtros opcionais `paga` e `categoriaId` |
| GET | `/despesas/{id}` | Buscar por ID |
| GET | `/despesas/vencidas` | Listar despesas não pagas com vencimento anterior ao dia atual |
| GET | `/despesas/periodo?inicio=AAAA-MM-DD&fim=AAAA-MM-DD` | Consultar por período de vencimento |
| GET | `/despesas/resumo?inicio=AAAA-MM-DD&fim=AAAA-MM-DD` | Resumir valores por período de vencimento |
| POST | `/despesas` | Cadastrar |
| PUT | `/despesas/{id}` | Atualizar dados, preservando a situação de pagamento |
| PATCH | `/despesas/{id}/pagar` | Marcar como paga; não requer corpo |
| DELETE | `/despesas/{id}` | Excluir |

### Categorias

| Método | Rota | Operação |
| --- | --- | --- |
| GET | `/categorias` | Listar |
| GET | `/categorias/{id}` | Buscar por ID |
| POST | `/categorias` | Cadastrar |
| PUT | `/categorias/{id}` | Atualizar |
| DELETE | `/categorias/{id}` | Excluir, se não houver despesas associadas |

## Exemplos de uso

Use o endereço base correspondente à execução: `http://localhost:8081` no Docker ou `http://localhost:8080` pelo Maven/IntelliJ. Envie `Content-Type: application/json` nas requisições com corpo. Os IDs dos exemplos são ilustrativos; substitua-os pelos retornados pela API.

### Cadastrar categoria e despesa

Primeiro, envie `POST /categorias`:

```json
{
  "nome": "Lazer"
}
```

Utilize o ID da categoria em `POST /despesas`:

```json
{
  "descricao": "Ingresso de cinema",
  "valor": 45.00,
  "dataVencimento": "2026-10-20",
  "paga": false,
  "categoriaId": 1
}
```

Os cadastros retornam `201 Created`, o objeto criado e o cabeçalho `Location`. Para cadastrar sem categoria, omita `categoriaId` ou envie `null`. Quando informado, o ID deve corresponder a uma categoria existente. O ID da despesa é gerado automaticamente.

A resposta de despesa contém `id`, `descricao`, `valor`, `dataVencimento` e `paga`; atualmente, não inclui dados da categoria.

### Atualizar e pagar

Envie `PUT /despesas/{id}` com os dados completos de atualização:

```json
{
  "descricao": "Ingresso de cinema atualizado",
  "valor": 50.00,
  "dataVencimento": "2026-10-22",
  "categoriaId": 1
}
```

A atualização mantém a situação de pagamento. Enviar `categoriaId: null` ou omitir esse campo remove a associação existente. Descrição, valor e data de vencimento continuam obrigatórios.

Para registrar o pagamento, envie `PATCH /despesas/{id}/pagar` sem corpo. A resposta contém a despesa com `paga: true`.

### Filtrar e consultar o resumo

```http
GET /despesas?paga=false
GET /despesas?categoriaId=1
GET /despesas?paga=false&categoriaId=1
GET /despesas/vencidas
GET /despesas/periodo?inicio=2026-10-01&fim=2026-10-31
GET /despesas/resumo?inicio=2026-10-01&fim=2026-10-31
```

O período considera a data de vencimento e inclui as datas inicial e final. `inicio` não pode ser posterior a `fim`. A consulta de vencidas considera a data atual do ambiente que executa a API e não inclui despesas que vencem hoje.

Exemplo de resumo para um período com R$ 45 em despesas pagas e R$ 50 em pendentes:

```json
{
  "total": 95.00,
  "totalPago": 45.00,
  "totalPendente": 50.00
}
```

## Validações e respostas

- Nome de categoria e descrição de despesa não podem ser nulos, vazios ou conter apenas espaços.
- Valor e data de vencimento são obrigatórios; o valor deve ser maior que zero.
- Uma categoria com despesas associadas não pode ser excluída. Remova as associações ou as despesas antes de excluir a categoria.

| Status | Situação |
| --- | --- |
| `200 OK` | Consulta, atualização ou pagamento concluído |
| `201 Created` | Cadastro concluído |
| `204 No Content` | Exclusão concluída, sem corpo |
| `400 Bad Request` | Dados inválidos ou período com início posterior ao fim |
| `404 Not Found` | Despesa ou categoria não encontrada |
| `409 Conflict` | Tentativa de excluir categoria com despesas associadas |

As exceções tratadas pelo `ResourceExceptionHandler` seguem esta estrutura. Exemplo para uma despesa inexistente:

```json
{
  "timestamp": "2026-10-14T12:00:00Z",
  "status": 404,
  "error": "Recurso não encontrado",
  "message": "Recurso não encontrado, Id: 99",
  "path": "/despesas/99"
}
```

## Testes

Com o JDK 25 configurado, execute na raiz do projeto:

```powershell
.\mvnw.cmd test
```

No Linux ou macOS, utilize `./mvnw test`. O Maven Wrapper baixa a versão necessária do Maven e as dependências.

| Camada | Verificação |
| --- | --- |
| Serviços | Regras de negócio com JUnit e Mockito |
| Endpoints | Rotas, validações e respostas com MockMvc e serviços simulados |
| Repositórios | Consultas JPA com banco H2 |
| Integração | Fluxos da API com MockMvc, serviços e repositórios reais sobre H2 |

Os cenários de integração incluem cadastro, consulta, pagamento, atualização, exclusão, filtros, resumo por período e bloqueio da exclusão de categoria associada.

O perfil `test`, definido em `src/test/resources/application-test.properties`, utiliza H2 em memória. **A suíte não exige Docker nem credenciais do MySQL**, mas não verifica particularidades desse banco. Os relatórios ficam em `target/surefire-reports`.

Para executar somente os testes de integração da API:

```powershell
.\mvnw.cmd "-Dtest=DespesasApiIntegrationTest" test
```

## Organização do código

| Pacote | Responsabilidade |
| --- | --- |
| `resources` | Receber requisições HTTP e produzir respostas |
| `dtos` | Representar os dados de entrada e saída |
| `services` | Aplicar as regras de negócio |
| `repositories` | Consultar e persistir dados |
| `entities` | Mapear entidades e relacionamentos |
| `resources.exceptions` | Tratar exceções na camada HTTP |
| `services.exceptions` | Definir exceções das regras de negócio |

## Autor

[Pedro Henrique dos Santos Silva](https://github.com/pedrohsantosdev)

