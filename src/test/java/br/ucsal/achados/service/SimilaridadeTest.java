package br.ucsal.achados.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SimilaridadeTest {

	@Test
	void ignoraAcentosEMaiusculas() {
		var r = Similaridade.pontuar("Óculos de grau preto bloco c", "OCULOS preto");
		assertThat(r.termos()).containsExactlyInAnyOrder("oculos", "preto");
		assertThat(r.percentual()).isEqualTo(100);
	}

	@Test
	void usaSinonimos() {
		assertThat(Similaridade.pontuar("smartphone preto cantina", "perdi meu celular").pontos()).isPositive();
		assertThat(Similaridade.pontuar("squeeze azul", "garrafa").pontos()).isPositive();
	}

	@Test
	void naoCasaTextosSemRelacao() {
		assertThat(Similaridade.pontuar("casaco cinza auditorio", "calculadora cientifica").pontos()).isZero();
	}

	@Test
	void palavrasSoltasNaoContamComoTermo() {
		assertThat(Similaridade.pontuar("garrafa azul", "perdi uma garrafa").termos()).containsExactly("garrafa");
	}
}
