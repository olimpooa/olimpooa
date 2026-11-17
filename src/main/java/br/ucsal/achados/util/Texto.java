package br.ucsal.achados.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class Texto {

	private Texto() {
	}

	/** Minúsculas, sem acentos e sem pontuação, com espaços simples. */
	public static String normalizar(String texto) {
		if (texto == null) {
			return "";
		}
		String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return semAcento.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
	}

	public static List<String> palavras(String texto) {
		String normalizado = normalizar(texto);
		if (normalizado.isEmpty()) {
			return List.of();
		}
		return Arrays.stream(normalizado.split(" ")).filter(p -> p.length() > 1).distinct().collect(Collectors.toList());
	}

	public static String juntar(String... partes) {
		return Arrays.stream(partes).filter(p -> p != null && !p.isBlank()).collect(Collectors.joining(" "));
	}

	public static String vazioParaNulo(String texto) {
		return texto == null || texto.isBlank() ? null : texto.trim();
	}
}
