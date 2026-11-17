package br.ucsal.achados.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ItemStatusTest {

	@Test
	void transicoesPermitidas() {
		assertThat(ItemStatus.DISPONIVEL.podeIrPara(ItemStatus.EM_ANALISE)).isTrue();
		assertThat(ItemStatus.EM_ANALISE.podeIrPara(ItemStatus.DISPONIVEL)).isTrue();
		assertThat(ItemStatus.EM_ANALISE.podeIrPara(ItemStatus.DEVOLVIDO)).isTrue();
		assertThat(ItemStatus.DISPONIVEL.podeIrPara(ItemStatus.DEVOLVIDO)).isTrue();
	}

	@Test
	void devolvidoEhEstadoFinal() {
		for (ItemStatus destino : ItemStatus.values()) {
			assertThat(ItemStatus.DEVOLVIDO.podeIrPara(destino)).isFalse();
		}
		assertThat(ItemStatus.DEVOLVIDO.aceitaInteresse()).isFalse();
	}

	@Test
	void itemRejeitaTransicaoInvalida() {
		Item item = new Item();
		item.mudarStatus(ItemStatus.DEVOLVIDO);
		assertThat(item.getDevolvidoEm()).isNotNull();
		assertThatThrownBy(() -> item.mudarStatus(ItemStatus.DISPONIVEL)).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void categoriaInterpretaTextoDaIa() {
		assertThat(Categoria.interpretar("celular")).isEqualTo(Categoria.CELULAR);
		assertThat(Categoria.interpretar("Carteira e documentos")).isEqualTo(Categoria.CARTEIRA_DOCUMENTOS);
		assertThat(Categoria.interpretar("coisa desconhecida")).isEqualTo(Categoria.OUTROS);
		assertThat(Categoria.interpretar(null)).isEqualTo(Categoria.OUTROS);
	}
}
