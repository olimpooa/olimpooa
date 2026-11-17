# Achados e Perdidos UCSal

Sistema web em que o responsável pelo achados e perdidos cataloga os itens (com ajuda de IA a partir de uma foto) e os
alunos consultam, sem login, se o que perderam está lá. O aluno sinaliza interesse e a retirada é sempre presencial.
Quando mais de um aluno pede o mesmo item, o sistema mantém todos os pedidos e a conferência acontece no balcão.
Projeto do POOALAB 2026.2 (Programação Orientada a Objetos Avançada, UCSal).

## Problema, público e proposta de valor

- **Público:** estudantes da UCSal que perdem objetos no campus e o funcionário que cuida do achados e perdidos.
- **Problema:** hoje o controle é todo manual e presencial: o aluno precisa ir até o setor só para descobrir se o objeto está lá,
  e o funcionário descreve e organiza cada item à mão.
- **Proposta:** consulta online com status de cada item, catalogação assistida por IA (foto → ficha sugerida) e tratamento
  organizado de pedidos concorrentes pelo mesmo objeto.

## Integrantes e turma

- Cauã de Oliveira Lopes
- Ryan Abade Oliveira
- Pedro Luiz de Barros Pereira
- Arivaldo Teixeira Moraes Neto

Turma: ADS matutina (quartas-feiras, 09:50–12:30).

Documentos da entrega (problema e validação, definição do MVP, roteiro do pitch e declaração de IA): pasta [`docs/`](docs/).

## Funcionalidades

| Quem | O que faz |
|------|-----------|
| Aluno (sem login) | Lista itens com filtros (texto, categoria, local, data, situação); busca inteligente por descrição; vê o status; sinaliza interesse com nome, e-mail `@ucsal.edu.br` e matrícula; acompanha ou cancela o pedido por um link próprio |
| Responsável (login) | Cadastra item com foto, local e data em que foi achado; pede sugestão de ficha à IA e revisa; edita; registra devolução; libera um item sem dono confirmado; vê painel (totais, pedidos, tempo médio até devolver, itens parados), histórico de status e caixa de saída de e-mails |

**Status do item:** `Disponível` → `Em análise` (há pedidos de alunos) → `Devolvido`. Cancelando todos os pedidos, ou com a ação
"Liberar", o item volta a `Disponível`. Toda mudança fica no histórico (quem, quando, de/para).

**Itens sensíveis** (celular, carteira e documentos): a consulta pública mostra só categoria, local e data. Foto, descrição, cor e
marca ficam visíveis apenas ao responsável, que usa as "características para conferência" na verificação presencial.

## Tecnologias

- Java 21, Spring Boot 4.1 (MVC, Data JPA, Security, Validation, Mail), Thymeleaf, banco H2 em arquivo, Maven Wrapper.
- Arquitetura em camadas: `web` (controllers) → `service` → `repository` → `domain`, com `ia` e `notificacao` como módulos de integração.
- IA: API do Gemini (plano gratuito) acessada por REST, sem SDK. Há modo simulado quando não há chave.
- Boas práticas e padrões aplicados (úteis para explicar na mostra):
  - **Strategy** — `CatalogadorIA` (Gemini / simulado) e `RankeadorIA` (Gemini / palavras-chave) são intercambiáveis.
  - **Factory** — `NotificacaoConfig` escolhe `SmtpNotificador` ou `SimuladoNotificador` pela configuração.
  - **Observer** — os serviços publicam eventos (`EventosAchados`) e `NotificacaoListener` envia os e-mails.
  - **State** — `ItemStatus` concentra as transições permitidas; `StatusService` é o único ponto que muda status.
  - **Fallback / Adapter** — `GeminiClient` isola a API externa; se ela falhar, o cadastro segue manualmente e a busca usa palavras-chave.
  - **SOLID** — serviços com uma responsabilidade cada; dependência de interfaces (`Notificador`, `CatalogadorIA`, `RankeadorIA`);
    entidades sem lógica de infraestrutura.
  - **Streams e Collections** — busca com sinônimos e pontuação, painel com `groupingBy`.

## Pré-requisitos

- **JDK 21** (o projeto usa o Maven Wrapper, não é preciso instalar o Maven).
- Porta `8080` livre (ou defina `SERVER_PORT`).

## Como executar

```bash
./mvnw spring-boot:run
```

No Windows (PowerShell): `.\mvnw.cmd spring-boot:run`. Abra <http://localhost:8080>.

Na primeira execução com o banco vazio são carregados **11 itens e 3 pedidos fictícios** (veja "Demonstração"). Para não
carregar: `CARREGAR_DEMO=false`. Para recomeçar do zero, apague as pastas `data/` e `uploads/` com a aplicação parada.

### Alternativa com Docker (opcional)

Para quem não tem o JDK, com Docker e Docker Compose instalados:

```bash
docker compose up --build
```

A aplicação sobe em <http://localhost:8080>. Para usar a IA real, copie `.env.example` para `.env` e preencha `GEMINI_API_KEY`
(o `.env` não é versionado). Os dados e as fotos ficam em volumes Docker e persistem entre execuções; `docker compose down -v` apaga tudo.

### Acesso do responsável (credenciais fictícias de demonstração)

- Endereço: <http://localhost:8080/admin>
- Usuário: `responsavel` · Senha: `ucsal2026`
- Altere com as variáveis `ADMIN_USUARIO` e `ADMIN_SENHA`.

### Configuração por variáveis de ambiente

| Variável | Padrão | Função |
|----------|--------|--------|
| `GEMINI_API_KEY` | vazio | Chave gratuita do Google AI Studio. Sem ela, a catalogação usa o modo simulado |
| `GEMINI_MODELO` | `gemini-3.1-flash-lite` | Modelo do Gemini usado (troque se a cota/modelo mudar) |
| `IA_PROVEDOR` | `auto` | `auto`, `gemini` ou `simulado` |
| `NOTIFICACAO_MODO` | `simulado` | `smtp` para enviar e-mails de verdade (defina também `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`) |
| `EMAIL_RESPONSAVEL` | `responsavel@ucsal.edu.br` | Quem recebe o aviso de novos pedidos |
| `APP_BASE_URL` | `http://localhost:8080` | Usada nos links enviados por e-mail |
| `SERVER_PORT` | `8080` | Porta HTTP |

**Nunca versione chaves ou senhas.** Exemplo (PowerShell) com IA real:

```powershell
$env:GEMINI_API_KEY = "sua-chave"
.\mvnw.cmd spring-boot:run
```

## Como executar os testes

```bash
./mvnw test
```

Os testes (32) cobrem as transições de status, o fluxo cadastro → pedido → devolução, pedidos concorrentes e cancelamento, a ocultação de
itens sensíveis, a busca com sinônimos, a validação do e-mail `@ucsal.edu.br`, o acesso restrito do responsável e o fallback da IA.
Eles não usam rede nem chave de API.

## Roteiro para reproduzir o fluxo principal (demonstração)

1. Abra a página inicial e mostre a lista; observe que o celular e a carteira aparecem sem detalhes (itens sensíveis).
2. Entre em `/busca-inteligente` e descreva "perdi meu headphone branco": o fone de ouvido aparece (sinônimo).
3. Abra "Garrafa térmica azul", preencha nome, e-mail `qualquer@ucsal.edu.br` e matrícula com 6 a 12 números e envie. O item passa a `Em análise`.
4. Entre em `/admin` com as credenciais acima, abra o item e veja o pedido, o histórico e (em "Caixa de saída de e-mails") os avisos gerados.
5. Em "Cadastrar item", envie uma foto, escreva "mochila vermelha" em informações extras e clique em **Analisar com IA**; revise a ficha e salve.
6. No item com pedido, use **Marcar como devolvido**. O painel mostra os novos totais.

## Limitações conhecidas e próximos passos

- A IA depende de internet e da cota gratuita do Gemini; sem chave, a catalogação é só uma dedução por palavras-chave. A sugestão
  da IA pode errar, por isso o responsável sempre revisa antes de salvar.
- O e-mail e a matrícula do aluno não são conferidos em nenhuma base da UCSal (proibido pelo escopo do projeto); só o formato é validado.
  Quem prova que o item é do aluno é a conferência presencial.
- O aluno acompanha o pedido apenas pelo link recebido (não há conta de aluno). Quem perder o link pode fazer novo pedido.
- Só existe um usuário responsável, em memória, e não há bloqueio contra tentativas repetidas de login.
- Dados pessoais dos pedidos (nome, e-mail, matrícula) ficam no banco sem rotina de expiração; em uso real seria necessário definir
  prazo de retenção e termo de consentimento.
- Uma foto de item sensível é servida só ao responsável, mas fotos já enviadas à API do Gemini deixam o ambiente da universidade.
  Em uso real, avaliar política de dados antes de enviar fotos de documentos.
- Próximos passos: expiração/doação de itens antigos, múltiplos responsáveis, QR code para o balcão, notificação do aluno quando um item
  parecido for cadastrado.

## Fontes, licenças, componentes de terceiros e uso de IA

- **Bibliotecas:** Spring Boot, Spring Security, Spring Data JPA, Thymeleaf, Hibernate, H2 e Jackson (licenças Apache 2.0 / EPL / MPL),
  obtidas via Maven Central. O esqueleto do projeto foi gerado pelo Spring Initializr.
- **API de terceiros:** Google Gemini (REST), usada em tempo de execução para catalogar fotos e ranquear a busca.
- **Dados de demonstração:** fictícios, criados para este projeto. Nenhum dado real de alunos ou da instituição foi usado.
- **Uso de IA no desenvolvimento:** o código-fonte, os testes e este README foram gerados com apoio do Claude (Anthropic, via Claude Code)
  a partir das decisões de escopo da equipe. A equipe deve revisar e compreender o código e registrar esse uso na declaração do Anexo E.
