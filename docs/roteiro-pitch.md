# Roteiro do pitch

**Solução:** Achados e Perdidos UCSal · **Mostra ADS:** 01/12/2026 · **Pitch:** até 5 minutos + 3 de perguntas

## Anexo D

| Parte | Conteúdo esperado | Sugestão para a equipe (ajustem às suas palavras) |
|-------|-------------------|---------------------------------------------------|
| **Abertura** (0:00–0:20) | Nome da solução e uma frase concreta sobre o problema. | "Achados e Perdidos UCSal: hoje, para saber se o seu fone foi encontrado, você precisa subir até o setor e perguntar." |
| **Público e contexto** (0:20–0:40) | Quem enfrenta o problema e em qual situação. | Alunos e demais membros da UCSal que perdem objetos; o setor atende só presencialmente e registra tudo à mão. |
| **Evidências** (0:40–1:15) | O que foi descoberto e qual hipótese precisou mudar. | Entrevista com o funcionário (11 anos de casa): sem sistema, itens meses parados. A ideia inicial era um chatbot; viramos catálogo web com status. [Acrescentar o resultado dos testes com colegas, se feitos.] |
| **Proposta** (1:15–1:50) | Qual resultado o MVP permite alcançar. | O aluno vê online se o objeto está lá e sinaliza que é dele; o funcionário cataloga por foto com IA. A entrega continua presencial. |
| **Demonstração** (1:50–3:30) | Execução do fluxo principal com dados preparados. | 1) Cadastro com foto + IA no `/admin`. 2) Aluno busca "headphone branco" e pede o item. 3) Status vira *Em análise*; dois pedidos no mesmo item. 4) Responsável marca *Devolvido*; histórico e e-mails. |
| **Decisões e limites** (3:30–4:20) | Justificativa do escopo e limitações conhecidas. | Itens sensíveis ocultos; conferência presencial; IA com fallback; limitações: uma entrevista, sem conta de aluno, matrícula só validada no formato. |
| **Aprendizado** (4:20–5:00) | Resultado alcançado e próximo passo mais importante. | Fluxo completo funcionando e testado (28 testes). Próximo passo: testar com o setor real e definir retenção de dados. |

## Divisão de falas (sugestão: 4 integrantes)

- Abertura, público e evidências: [integrante 1]
- Proposta e decisões técnicas (padrões, IA): [integrante 2]
- Demonstração ao vivo (cadastro e devolução): [integrante 3]
- Demonstração (lado do aluno), limites e encerramento: [integrante 4]

Todos devem saber explicar qualquer parte do sistema; o professor pode perguntar a qualquer integrante.

## Preparação e contingência

- Ensaiar com cronômetro. Abrir antes: `/admin` logado, a página inicial e uma foto de objeto pronta para o cadastro.
- Conferir a chave `GEMINI_API_KEY` e a internet; se a IA falhar, mostrar o fallback (é uma decisão de projeto, não um erro).
- Levar a aplicação rodando localmente (ou `docker compose up`) e uma gravação curta do fluxo como apoio.
- Pitch final em **PDF** no Classroom (`pitch.pdf`).

## Perguntas prováveis

- *Por que a confirmação do dono é presencial?* Para impedir reivindicação falsa; a IA e o sistema não conseguem comprovar propriedade.
- *Como a IA é usada e quais os riscos?* Sugere a ficha a partir da foto; o responsável sempre revisa; há fallback; fotos vão ao Gemini (ver limitações).
- *O que os padrões de projeto resolvem?* Trocar IA e canal de e-mail sem mexer nos serviços (Strategy/Factory), desacoplar notificações (Observer), centralizar transições (State).
- *Quais dados pessoais são coletados?* Nome, e-mail institucional e matrícula, só no pedido; sem consulta a bases da UCSal.
