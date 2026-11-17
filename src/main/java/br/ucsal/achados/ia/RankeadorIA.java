package br.ucsal.achados.ia;

import java.util.List;

import br.ucsal.achados.service.ItemPublico;

/** Estratégia de busca inteligente: ordena os itens em aberto pela chance de corresponderem à descrição do aluno. */
public interface RankeadorIA {

	String nome();

	List<Classificacao> ranquear(String descricao, List<ItemPublico> candidatos);

	record Classificacao(Long itemId, int pontuacao, String justificativa) {
	}
}
