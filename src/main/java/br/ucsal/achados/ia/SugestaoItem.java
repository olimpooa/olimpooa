package br.ucsal.achados.ia;

import br.ucsal.achados.domain.Categoria;

/** Ficha sugerida pela IA; o responsável sempre revisa antes de salvar. */
public record SugestaoItem(String titulo, Categoria categoria, String cor, String marca, String descricao,
		String caracteristicasIdentificadoras, String origem, String aviso, boolean geradaPorIa) {

	public static SugestaoItem vazia(String origem, String aviso) {
		return new SugestaoItem(null, null, null, null, null, null, origem, aviso, false);
	}
}
