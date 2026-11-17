# Declaração de uso de inteligência artificial e de componentes de terceiros

**Solução:** Achados e Perdidos UCSal
**Turma:** ADS matutina · **Equipe:** [nome da equipe, se houver]
**Integrantes:** Cauã de Oliveira Lopes; Ryan Abade Oliveira; Pedro Luiz de Barros Pereira; Arivaldo Teixeira Moraes Neto

## Anexo E

### 1. Uso de inteligência artificial

**Houve uso de IA?** ( X ) Sim  (   ) Não

| Ferramenta ou modelo | Finalidade ou tarefa | Parte afetada | Revisão e validação realizadas |
|----------------------|----------------------|---------------|--------------------------------|
| Claude (Anthropic), via Claude Code | Escrita do código-fonte, dos testes automatizados, dos arquivos Docker, do README e destes documentos, a partir das decisões de escopo da equipe | Todo o repositório (`src/`, `docs/`, `README.md`, `Dockerfile`, `docker-compose.yml`) | Execução da suíte de 28 testes automatizados e uso manual do sistema com dados de demonstração. [A equipe deve acrescentar aqui como revisou e compreendeu o código.] |
| Google Gemini (API, modelo `gemini-3.1-flash-lite`) | Funcionalidade do produto, em tempo de execução: sugerir a ficha do item a partir da foto e ranquear itens na busca inteligente | `ia/GeminiCatalogador`, `ia/GeminiRankeador`, `ia/GeminiClient` | O responsável revisa e corrige toda sugestão antes de salvar; há fallback manual e por palavras-chave; testes automatizados cobrem a falha da IA. |

**Observações sobre o uso de IA:** a saída da IA não foi apresentada como evidência de validação com usuários reais. A validação
está registrada em `docs/problema-validacao.md` e vem de fontes humanas. Nenhuma chave de API é versionada no repositório; ela é
fornecida por variável de ambiente. Fotos de itens podem ser enviadas à API do Gemini; os dados de demonstração são fictícios.

### 2. Componentes e recursos de terceiros

**Houve uso de componentes de terceiros?** ( X ) Sim  (   ) Não

| Componente ou recurso | Origem ou link | Versão e licença | Função e alterações realizadas |
|-----------------------|----------------|------------------|--------------------------------|
| Spring Boot (Web MVC, Data JPA, Security, Validation, Mail) | https://spring.io/projects/spring-boot | 4.1.2 · Apache 2.0 | Framework da aplicação; sem alterações |
| Thymeleaf | https://www.thymeleaf.org | versão da BOM do Spring Boot · Apache 2.0 | Templates das páginas |
| Hibernate ORM | https://hibernate.org/orm | versão da BOM do Spring Boot · LGPL 2.1 | Persistência JPA |
| H2 Database | https://h2database.com | versão da BOM do Spring Boot · MPL 2.0 / EPL 1.0 | Banco de dados local |
| Jackson | https://github.com/FasterXML/jackson | versão da BOM do Spring Boot · Apache 2.0 | Leitura do JSON retornado pela IA |
| JUnit 5, AssertJ, Spring Test | https://junit.org · https://assertj.github.io | versões da BOM · EPL 2.0 / Apache 2.0 | Testes automatizados |
| Spring Initializr | https://start.spring.io | — | Geração do esqueleto inicial do projeto |
| Eclipse Temurin (JDK/JRE 21) | https://adoptium.net | 21 · GPLv2 com Classpath Exception | Execução e imagens Docker |
| API Google Gemini | https://ai.google.dev | serviço online · termos do Google AI Studio | Catalogação e busca por IA |

### 3. Materiais ou código preexistentes

**Houve material produzido antes do PooaLAB?** ( X ) Sim  (   ) Não

**Identificação e origem:** não há código preexistente: o sistema foi desenvolvido durante o PooaLAB. Como **material de pesquisa**, o
diagnóstico do problema (atendimento presencial, registro manual e itens parados) vem de uma entrevista com o funcionário do setor de Achados e
Perdidos, feita no Projeto de Extensão "Inteligência Artificial para Inclusão e Cidadania" (disciplina Tecnologias e Inteligências Artificiais,
2026.2, Grupo AF), que previa um chatbot. O chatbot e demais materiais desse projeto **não** foram reaproveitados aqui.

**Contribuição nova realizada pela equipe:** todo o sistema web (catálogo, pedidos, status, painel, catalogação e busca com IA, e-mails,
Docker e testes), a definição de escopo do MVP e a documentação desta entrega. [Se houver teste de uso com colegas, citar aqui.]

### 4. Declaração da equipe

Declaramos que as informações acima são completas e verdadeiras; que revisamos e testamos as contribuições apoiadas por IA; que identificamos as
fontes, licenças e partes preexistentes relevantes; que não apresentamos como autoria própria material de terceiros; e que não compartilhamos
dados pessoais, credenciais ou informações restritas de forma indevida.

**Responsável pela submissão:** [nome completo]

| Ciência dos integrantes | Assinatura ou registro de ciência |
|-------------------------|-----------------------------------|
| Cauã de Oliveira Lopes | |
| Ryan Abade Oliveira | |
| Pedro Luiz de Barros Pereira | |
| Arivaldo Teixeira Moraes Neto | |

**Data:** ____/____/________
