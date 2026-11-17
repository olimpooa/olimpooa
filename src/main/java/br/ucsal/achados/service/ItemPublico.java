package br.ucsal.achados.service;

import java.time.LocalDate;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;

/**
 * Visão do item exposta aos alunos. Para categorias sensíveis, foto, descrição, cor e marca são omitidas.
 */
public record ItemPublico(Long id, String titulo, Categoria categoria, String cor, String descricao,
		String localEncontrado, LocalDate dataEncontrado, ItemStatus status, boolean temFoto, boolean sensivel) {

	public static ItemPublico de(Item item) {
		if (item.isSensivel()) {
			return new ItemPublico(item.getId(), item.getCategoria().getRotulo(), item.getCategoria(), null, null,
					item.getLocalEncontrado(), item.getDataEncontrado(), item.getStatus(), false, true);
		}
		return new ItemPublico(item.getId(), item.getTitulo(), item.getCategoria(), item.getCor(), item.getDescricao(),
				item.getLocalEncontrado(), item.getDataEncontrado(), item.getStatus(), item.temFoto(), false);
	}
}
