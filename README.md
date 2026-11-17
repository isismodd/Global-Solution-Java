# ConectaSocial

Plataforma web para aproximar ONGs, voluntários e beneficiários, permitindo o cadastro de perfis e, nas próximas etapas, a divulgação e descoberta de campanhas sociais.

link: https://global-solution-java-rm561497.azurewebsites.net

## Tecnologias

- Java 21
- Spring Boot 3.4.1
- Maven
- Spring Security
- Thymeleaf e Bootstrap
- Oracle Database
- Flyway
- JWT para endpoints da API

## Funcionalidades implementadas

- Cadastro de ONGs, voluntários e beneficiários
- Autenticação por e-mail e senha
- Senhas armazenadas com BCrypt
- Aceite da política de uso durante o cadastro
- Perfis de acesso: ONG, VOLUNTARIO e BENEFICIARIO

## Executar localmente

Configure as variáveis de ambiente antes de iniciar a aplicação:

- `DB_HOST`
- `DB_PORT`
- `DB_SERVICE`
- `DB_USERNAME`
- `DB_PASSWORD`

Não coloque credenciais diretamente no `application.properties` nem as versione no Git.

Execute:

```bash
./mvnw spring-boot:run
```

No Windows:

```cmd
mvnw.cmd spring-boot:run
```

A aplicação utiliza a porta `8082` por padrão.

## Banco de dados e migrações

As migrações Flyway existentes foram preservadas para manter o histórico do banco utilizado durante o desenvolvimento. As migrações iniciais pertencem ao sistema-base e ainda contêm estruturas antigas. Não execute alterações destrutivas nem remova tabelas sem antes configurar e validar um schema exclusivo do ConectaSocial.

## Próximas etapas

- Cadastro e gerenciamento de campanhas pelas ONGs
- Feed de campanhas
- Página de detalhes das campanhas
- Manifestação de interesse em campanhas
