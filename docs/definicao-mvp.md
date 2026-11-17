# Definição do MVP

**Solução:** Achados e Perdidos UCSal

## Anexo C

| Campo | Definição |
|-------|-----------|
| **Usuário principal** | O funcionário responsável pelo setor (cadastra e entrega) e o aluno que perdeu um objeto (consulta e pede). |
| **Resultado principal** | O aluno descobre online se o objeto perdido está no setor e sinaliza que é dele; o responsável cataloga itens rapidamente com ajuda de IA e conduz a devolução presencial. |
| **Fluxo de ponta a ponta** | **Entrada:** foto + local e data em que o item foi achado → **Processamento:** a IA sugere a ficha, o responsável revisa e salva; o aluno consulta, busca e envia o pedido (nome, e-mail `@ucsal.edu.br`, matrícula); o status muda para *Em análise* → **Resultado:** o responsável confere o dono presencialmente e marca *Devolvido*, e o histórico e os e-mails ficam registrados. |
| **Critérios de pronto** | 1. O responsável faz login, cadastra um item com foto e recebe uma ficha sugerida que pode editar. 2. Um aluno sem login encontra o item por filtro ou busca (inclusive por sinônimo) e envia o pedido. 3. O item muda de *Disponível* para *Em análise* e o pedido aparece para o responsável. 4. Dois alunos podem pedir o mesmo item; o mesmo aluno não pode pedir duas vezes. 5. O responsável marca *Devolvido* e os pedidos são encerrados. 6. Itens sensíveis não mostram foto nem detalhes ao público. 7. Se a IA falhar, o cadastro continua manualmente e a busca usa palavras-chave. 8. A aplicação roda pelo README em outro computador e os testes automatizados passam (`./mvnw test`). |
| **Fora do escopo** | Contas de aluno; integração com sistemas ou bases da UCSal (e checagem real de matrícula); aplicativo móvel; chatbot; notificações push; confirmação do dono pelo sistema (é presencial); múltiplos responsáveis; expiração e doação automática de itens antigos. |
| **Maior risco** | A IA depender de internet e de cota gratuita durante a mostra, e a ficha sugerida vir errada. Mitigação: fallback manual, modo simulado, itens pré-cadastrados e revisão obrigatória pelo responsável. |
| **Teste principal** | Rodar o roteiro do README com dados de demonstração; executar a suíte automatizada (32 testes); e observar 3 a 5 pessoas usando o fluxo (Anexo B). |
| **Limitações conhecidas** | Uma única entrevista como evidência; sem conta de aluno (o acompanhamento é pelo link do pedido); e-mail e matrícula só têm o formato validado; um único responsável, com login em memória e sem bloqueio de tentativas; dados dos pedidos sem prazo de retenção; fotos enviadas ao Gemini saem do ambiente da universidade. |

## Arquitetura resumida

- Spring Boot 4.1 + Thymeleaf + H2, em camadas `web → service → repository → domain`.
- Módulos de integração: `ia` (Gemini, com fallback) e `notificacao` (SMTP real ou simulado).
- Padrões aplicados: Strategy (catalogador e ranqueador de IA), Factory (notificador), Observer (eventos de domínio),
  State (transições de `ItemStatus`) e Fallback (resiliência da IA).
