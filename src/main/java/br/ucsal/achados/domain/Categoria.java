package br.ucsal.achados.domain;

import java.util.Arrays;
import java.util.Optional;

import br.ucsal.achados.util.Texto;

/**
 * Categorias de item. Itens de categoria sensível não expõem foto nem detalhes na consulta pública:
 * a conferência de propriedade é feita presencialmente pelo responsável.
 */
public enum Categoria {

	CELULAR("Celular", true),
	CARTEIRA_DOCUMENTOS("Carteira e documentos", true),
	CHAVES("Chaves", false),
	ELETRONICO("Eletrônico", false),
	MOCHILA_BOLSA("Mochila ou bolsa", false),
	VESTUARIO("Roupa ou calçado", false),
	GARRAFA_MARMITA("Garrafa ou marmita", false),
	MATERIAL_ESCOLAR("Livro ou material escolar", false),
	ACESSORIO("Acessório", false),
	OUTROS("Outros", false);

	private final String rotulo;
	private final boolean sensivel;

	Categoria(String rotulo, boolean sensivel) {
		this.rotulo = rotulo;
		this.sensivel = sensivel;
	}

	public String getRotulo() {
		return rotulo;
	}

	public boolean isSensivel() {
		return sensivel;
	}

	/** Interpreta um texto vindo da IA ou de um formulário; devolve OUTROS quando não reconhece. */
	public static Categoria interpretar(String texto) {
		if (texto == null || texto.isBlank()) {
			return OUTROS;
		}
		String alvo = Texto.normalizar(texto).replace(' ', '_');
		Optional<Categoria> achada = Arrays.stream(values())
				.filter(c -> c.name().equalsIgnoreCase(alvo) || Texto.normalizar(c.rotulo).replace(' ', '_').equals(alvo))
				.findFirst();
		return achada.orElse(OUTROS);
	}
}
