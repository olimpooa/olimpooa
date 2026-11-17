package br.ucsal.achados.ia;

import java.util.List;
import java.util.Map;

import tools.jackson.databind.json.JsonMapper;

/** Leitura tolerante do JSON devolvido pelo modelo (que pode vir com cercas de código ou campos ausentes). */
final class JsonIa {

	private static final JsonMapper MAPPER = new JsonMapper();

	private JsonIa() {
	}

	static Object ler(String texto) {
		String limpo = texto.strip();
		if (limpo.startsWith("```")) {
			limpo = limpo.replaceFirst("^```[a-zA-Z]*\\s*", "").replaceFirst("\\s*```$", "");
		}
		try {
			return MAPPER.readValue(limpo, Object.class);
		} catch (RuntimeException e) {
			throw new IaIndisponivelException("A IA devolveu uma resposta que não é JSON válido.", e);
		}
	}

	@SuppressWarnings("unchecked")
	static Map<String, Object> objeto(Object valor) {
		if (valor instanceof Map<?, ?> mapa) {
			return (Map<String, Object>) mapa;
		}
		throw new IaIndisponivelException("A IA devolveu um formato inesperado.");
	}

	@SuppressWarnings("unchecked")
	static List<Object> lista(Object valor) {
		if (valor instanceof List<?> lista) {
			return (List<Object>) lista;
		}
		throw new IaIndisponivelException("A IA devolveu um formato inesperado.");
	}

	static String texto(Map<String, Object> mapa, String chave) {
		Object valor = mapa.get(chave);
		return valor == null ? null : valor.toString().strip();
	}

	static int inteiro(Map<String, Object> mapa, String chave, int padrao) {
		Object valor = mapa.get(chave);
		return valor instanceof Number numero ? numero.intValue() : padrao;
	}
}
