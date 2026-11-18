# ConectaSocial

<img width="244" height="207" alt="image" src="https://github.com/user-attachments/assets/abbe86ca-cf44-42e0-8cda-bf371b294afd" />


Plataforma web desenvolvida para aproximar ONGs, voluntários e beneficiários, facilitando a divulgação de campanhas sociais, a participação de pessoas interessadas e a conexão entre iniciativas e quem precisa de ajuda.

**Aplicação publicada:** https://global-solution-java-rm561497.azurewebsites.net

**Repositório:** https://github.com/isismodd/Global-Solution-Java

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.4.1
- Maven
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- JDBC Template
- Thymeleaf
- Bootstrap
- Oracle Database
- Flyway
- Bean Validation
- Spring AI com Google Gemini
- RabbitMQ com CloudAMQP
- Springdoc OpenAPI / Swagger
- Azure App Service
- GitHub Actions (CI/CD)

## Funcionalidades implementadas

- Cadastro de ONGs, voluntários e beneficiários.
- Autenticação por e-mail e senha.
- Senhas armazenadas com BCrypt.
- Aceite da política de uso durante o cadastro.
- Controle de acesso por perfil: `ONG`, `VOLUNTARIO` e `BENEFICIARIO`.
- Cadastro e gerenciamento de campanhas sociais.
- Listagem e visualização de campanhas.
- Manifestação de interesse em campanhas.
- Prevenção de registros duplicados de interesse.
- Notificação por e-mail à ONG quando alguém demonstra interesse.
- Recomendações inteligentes de campanhas utilizando Spring AI e Google Gemini.
- API REST com paginação.
- Validação de dados e tratamento global de exceções.
- Proteção de endpoints com JWT.

## Executar localmente

### Pré-requisitos

- JDK 21.
- Maven (ou Maven Wrapper incluído no projeto).
- Acesso a um banco de dados Oracle.
- Credenciais e serviços externos configurados.

### Variáveis de ambiente

Configure as seguintes variáveis antes de iniciar a aplicação:

| Variável | Finalidade |
|---|---|
| `DB_HOST` | Host do banco Oracle |
| `DB_PORT` | Porta do banco |
| `DB_SERVICE` | Service Name do Oracle |
| `DB_USERNAME` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave de assinatura dos tokens JWT |
| `GEMINI_API_KEY` | Chave de acesso ao Google Gemini |
| `RABBITMQ_URL` | URL de conexão com o CloudAMQP |
| `SPRING_MAIL_USERNAME` | Conta utilizada para envio de e-mails |
| `SPRING_MAIL_PASSWORD` | Senha ou credencial de envio de e-mails |

As variáveis devem corresponder aos nomes utilizados na configuração da aplicação. Não versione senhas, tokens ou chaves de API no repositório.

### Iniciar a aplicação

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```cmd
mvnw.cmd spring-boot:run
```

A aplicação utiliza a porta `8082` por padrão.

## Banco de dados

O projeto utiliza Oracle Database para persistência dos dados e Flyway para controle de migrações.

As migrações são executadas durante a inicialização da aplicação, conforme a configuração do ambiente.

É necessário configurar corretamente as credenciais e o acesso ao banco antes de executar o projeto.

## Usuários de teste

O sistema possui três perfis de acesso:

| Perfil | Permissões principais |
|---|---|
| ONG | Cadastro e gerenciamento de campanhas e acompanhamento de interessados |
| VOLUNTARIO | Visualização de campanhas e manifestação de interesse em voluntariar |
| BENEFICIARIO | Visualização de campanhas e solicitação de ajuda |

Para testar, realize o cadastro de usuários pelos formulários da aplicação, selecionando o perfil correspondente.

As credenciais devem ser criadas no próprio ambiente de teste. Não existem senhas de demonstração fixas documentadas neste repositório.

## Endpoints principais

A API REST disponibiliza operações relacionadas a campanhas, interesses, autenticação e recomendações inteligentes.

| Recurso | Operação | Descrição |
|---|---|---|
| Autenticação | Login | Autenticação e obtenção de token JWT |
| Campanhas | CRUD | Cadastro, consulta, atualização e exclusão de campanhas |
| Campanhas | Paginação | Consulta paginada de campanhas |
| Interesses | Registro | Registro de interesse em campanhas |
| IA | `POST /api/ia/recomendar-campanhas` | Recomendação inteligente de campanhas |

### Documentação Swagger

A documentação interativa da API está disponível em:

`/swagger-ui/index.html`

A documentação apresenta os endpoints disponíveis, parâmetros, modelos de requisição e respostas.

## Inteligência Artificial — Spring AI

O ConectaSocial utiliza Spring AI integrado ao Google Gemini para oferecer recomendações inteligentes de campanhas sociais.

A funcionalidade analisa as necessidades informadas pelo usuário e as campanhas ativas disponíveis, retornando recomendações compatíveis acompanhadas de justificativas.

A IA é utilizada como recurso de apoio à descoberta de campanhas, não como um chatbot genérico.

## Arquitetura distribuída — Requisito 5

**Opção escolhida: A — Mensageria assíncrona com RabbitMQ.**

O projeto utiliza RabbitMQ, disponibilizado pelo CloudAMQP, para processar notificações de interesse em campanhas.

Fluxo de funcionamento:

1. Um voluntário ou beneficiário demonstra interesse em uma campanha.
2. O sistema registra o interesse no banco de dados.
3. Após a confirmação da transação, um evento é publicado no RabbitMQ.
4. Um consumidor assíncrono recebe o evento.
5. O sistema envia um e-mail de notificação à ONG responsável pela campanha.

Essa arquitetura desacopla o registro do interesse do processamento da notificação, permitindo que o envio de e-mails aconteça de forma assíncrona.

## Deploy e integração contínua

A aplicação está publicada no Microsoft Azure App Service.

**URL:** https://global-solution-java-rm561497.azurewebsites.net

O projeto utiliza GitHub Actions para automatizar o processo de build e publicação.

O fluxo de CI/CD executa a compilação do projeto Maven e, após a validação, realiza a publicação da aplicação na Azure.

## Declaração de uso de Inteligência Artificial

Durante o desenvolvimento do ConectaSocial, foram utilizadas ferramentas de Inteligência Artificial, como o ChatGPT, para auxiliar na estruturação do projeto, organização do código, esclarecimento de dúvidas técnicas, correção de erros e apoio na documentação.

A IA foi utilizada como ferramenta de suporte ao desenvolvimento, enquanto as decisões de implementação, adaptações e validações foram realizadas pela equipe.

## Equipe

Ana Clara de Oliveira Nascimento | RM 561957 - 2TDSR ADS
Augusto Bonomo Junior | RM 565155 - 2TDSR ADS
Isis Macedo | RM 561497 - 2TDSR ADS
Henrique Pereira | RM 565608 - 2TDSR ADS
Rafael Carvalho Meireles | RM 563413 - 2TDSR ADS

Projeto acadêmico desenvolvido para a Global Solution da FIAP.

