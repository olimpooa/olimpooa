package br.ucsal.achados.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.ucsal.achados.util.Texto;

/** Pontuação de palavras-chave entre uma consulta e o texto de um item, com sinônimos comuns no campus. */
public final class Similaridade {

	private static final Set<String> IGNORADAS = Set.of("de", "da", "do", "das", "dos", "um", "uma", "os", "as", "em",
			"no", "na", "com", "meu", "minha", "perdi", "achei", "procuro", "procurando", "que", "para", "por");

	private static final List<Set<String>> SINONIMOS = List.of(
			Set.of("celular", "telefone", "smartphone", "iphone", "aparelho"),
			Set.of("fone", "headphone", "headset", "airpods", "auricular"),
			Set.of("garrafa", "squeeze", "garrafinha"),
			Set.of("marmita", "lancheira", "pote"),
			Set.of("mochila", "bolsa", "bag"),
			Set.of("carteira", "porta", "documento", "documentos", "cartao"),
			Set.of("chave", "chaves", "chaveiro"),
			Set.of("casaco", "jaqueta", "moletom", "blusa"),
			Set.of("oculos", "armacao"),
			Set.of("notebook", "laptop", "computador"),
			Set.of("caderno", "agenda", "apostila"),
			Set.of("carregador", "cabo", "fonte"),
			Set.of("preto", "escuro"),
			Set.of("branco", "claro"));

	private Similaridade() {
	}

	public record Resultado(int pontos, int maximo, List<String> termos) {

		public int percentual() {
			return maximo == 0 ? 0 : Math.min(100, pontos * 100 / maximo);
		}
	}

	public static Resultado pontuar(String textoItem, String consulta) {
		List<String> palavras = Texto.palavras(consulta).stream().filter(p -> !IGNORADAS.contains(p)).toList();
		Set<String> tokensItem = new HashSet<>(Arrays.asList(Texto.normalizar(textoItem).split(" ")));
		int pontos = 0;
		List<String> termos = new java.util.ArrayList<>();
		for (String palavra : palavras) {
			int ponto = pontuarPalavra(palavra, tokensItem);
			if (ponto > 0) {
				pontos += ponto;
				termos.add(palavra);
			}
		}
		return new Resultado(pontos, palavras.size() * 3, termos);
	}

	private static int pontuarPalavra(String palavra, Set<String> tokensItem) {
		if (tokensItem.contains(palavra)) {
			return 3;
		}
		Set<String> grupo = sinonimosDe(palavra);
		if (grupo.stream().anyMatch(tokensItem::contains)) {
			return 2;
		}
		boolean parcial = tokensItem.stream()
				.anyMatch(t -> palavra.length() >= 4 && (t.startsWith(palavra) || palavra.startsWith(t) && t.length() >= 4));
		return parcial ? 1 : 0;
	}

	private static Set<String> sinonimosDe(String palavra) {
		return SINONIMOS.stream().filter(g -> g.contains(palavra)).flatMap(Set::stream).collect(Collectors.toSet());
	}
}
