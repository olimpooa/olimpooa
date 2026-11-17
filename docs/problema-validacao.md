# Problema e validação

**Solução:** Achados e Perdidos UCSal
**Equipe e turma:** ADS matutina (quartas-feiras, 09:50–12:30)
**Integrantes:** Cauã de Oliveira Lopes · Ryan Abade Oliveira · Pedro Luiz de Barros Pereira · Arivaldo Teixeira Moraes Neto

> Itens entre `[colchetes]` são lacunas que a equipe precisa preencher com fatos reais. Este documento não contém dados inventados.

---

## Anexo A — Ficha do problema

**Formulação:** Para *estudantes e demais membros da comunidade da UCSal que perdem objetos no campus*, que enfrentam
*a dificuldade de saber se o objeto foi encontrado* durante *o dia a dia nas aulas e nos espaços do campus*, a consequência
é *objetos que ficam meses guardados sem que os donos saibam que foram achados, e deslocamentos até o setor apenas para
descobrir se o item está lá*. Hoje, esse público tenta resolver a situação por meio de *ir presencialmente ao setor de Achados e
Perdidos, onde o atendimento e o registro dos objetos são manuais*.

| Campo | Preenchimento |
|-------|---------------|
| **Público** | Alunos, professores, técnicos e visitantes da UCSal que perdem objetos; e o funcionário responsável pelo setor de Achados e Perdidos. |
| **Situação** | Quando alguém perde um objeto no campus e quer saber se ele foi encontrado. O setor fica no terceiro andar de um bloco do campus e o atendimento é exclusivamente presencial. |
| **Problema** | A pessoa quer saber se o objeto está no setor, mas só consegue descobrir indo até lá. O funcionário, por sua vez, cataloga e descreve cada item à mão, sem um sistema. |
| **Impacto** | Itens permanecem meses guardados sem que os donos saibam que foram encontrados; deslocamentos desnecessários (inclusive para quem tem dificuldade de locomoção); trabalho manual de registro e de atendimento. |
| **Alternativa atual** | Ida presencial ao setor; registro manual dos itens em ficha de controle; não há sistema de consulta. |
| **Hipótese crítica** | (1) Os donos não sabem que o objeto foi encontrado e consultariam um catálogo online se ele existisse. (2) A catalogação a partir de uma foto reduz o esforço do funcionário. (3) A entrega precisa continuar presencial, com conferência humana. |
| **Evidência** | Entrevista com o funcionário do setor (ver Anexo B, registro 1). Pendente: teste do MVP com colegas e com o funcionário (registros 2 em diante). |
| **Recorte** | Catálogo online com status do item, pedido de interesse sem login e tratamento de pedidos concorrentes; catalogação assistida por IA. A confirmação do dono e a entrega ficam presenciais e fora do sistema. |

---

## Anexo B — Registro de validação

Cada linha é uma entrevista, observação, teste ou fonte. Não registrar nomes nem dados pessoais.

| Data e perfil | Hipótese | Evidência observada | Decisão |
|---------------|----------|---------------------|---------|
| **1. [dd/mm/2026]** — funcionário do setor de Achados e Perdidos da UCSal, com cerca de onze anos de atuação na instituição. Fonte: entrevista feita no Projeto de Extensão "Inteligência Artificial para Inclusão e Cidadania" (disciplina Tecnologias e Inteligências Artificiais, 2026.2, Grupo AF), do qual um integrante da equipe participou. | O setor não possui sistema e tem dificuldade de comunicar aos donos que os objetos foram achados. | O atendimento é exclusivamente presencial; o registro dos objetos é manual; muitos itens ficam meses guardados sem que os donos saibam que foram encontrados; o setor fica no terceiro andar de um bloco do campus. | **Manter** o problema. **Alterar** a solução em relação ao projeto de extensão (que previa um chatbot): o MVP é um catálogo web com status e catalogação por IA. **Manter** a entrega presencial. |
| **2. [dd/mm/2026]** — [perfil, ex.: estudante de ADS, sem identificação] | A pessoa encontra, sem ajuda, o item de interesse e consegue sinalizar o pedido. | [O que foi observado: tempo, dúvidas, erros] | [Manter / alterar / abandonar] |
| **3. [dd/mm/2026]** — [perfil] | [...] | [...] | [...] |
| **4. [dd/mm/2026]** — [perfil] | [...] | [...] | [...] |
| **5. [dd/mm/2026]** — [perfil: funcionário do setor, se for possível mostrar o MVP] | O cadastro com IA é útil e a ficha sugerida está correta o suficiente para revisar. | [...] | [...] |

### Como fazer o teste do MVP (registros 2 a 5)

Faça com 3 a 5 pessoas, uma por vez, usando a demonstração (`README.md`). Não ajude e anote o que acontecer.

**Tarefas para o participante:**
1. "Você perdeu um fone de ouvido branco. Descubra se ele está no achados e perdidos." (consulta e busca)
2. "Sinalize que o item é seu." (pedido de interesse; use dados fictícios)
3. Para quem for o funcionário: "Cadastre este objeto usando a foto e a IA" e "marque o item como devolvido".

**Registre:** se concluiu sem ajuda (sim/não), onde hesitou ou errou, quanto tempo levou, o que achou do aviso de que a retirada é presencial e uma frase livre do participante (sem nome).

**Ética:** explique que é um trabalho acadêmico, peça concordância verbal, não colete nome nem dados pessoais e registre só o perfil (ex.: "estudante de ADS").

### Síntese dos aprendizados

- O problema tem origem em uma escuta direta do funcionário que opera o setor: o gargalo é a informação não chegar ao dono do objeto.
- A entrega precisa continuar presencial, com conferência humana, principalmente para itens pessoais como celular.
- **Limitação declarada:** até este momento a evidência vem de uma única entrevista, do projeto de extensão. Não há pesquisa com estudantes
  nem teste de uso do MVP. [Atualizar esta frase após os registros 2 a 5.]

### Mudanças realizadas no problema ou na solução

- A solução passou de um chatbot (projeto de extensão) para um **sistema web de catálogo**, porque o objetivo do POOALAB exige um MVP
  executável com fluxo completo (cadastro → consulta → pedido → devolução), e a consulta visual com filtros e status atende melhor à necessidade
  de "saber se o objeto está lá".
- Foi incluída, a partir de uma decisão da equipe, a regra de **ocultar detalhes de itens sensíveis** (celular, carteira, documentos) na consulta pública, para reduzir
  reivindicações falsas e proteger privacidade.
