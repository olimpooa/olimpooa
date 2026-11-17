package br.ucsal.achados.ia;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.service.ImagemEntrada;
import br.ucsal.achados.util.Texto;

/**
 * Catalogador sem IA real, usado quando não há chave da API: deduz categoria e cor por palavras-chave do texto
 * informado. Serve para demonstração offline e como contingência.
 */
@Component
public class CatalogadorSimulado implements CatalogadorIA {

	private static final Pattern LOCAL = Pattern.compile(
			"(?iu)\\b(sala|bloco|laborat[óo]rio|lab)\\s+[\\p{L}]?-?\\d*[\\p{L}]?\\d*\\b|\\b(biblioteca|cantina|audit[óo]rio|estacionamento|portaria)\\b");
	private static final Map<String, Categoria> PALAVRAS_CATEGORIA = new LinkedHashMap<>();
	private static final List<String> CORES = List.of("preto", "branco", "azul", "vermelho", "verde", "amarelo",
			"cinza", "rosa", "roxo", "laranja", "marrom", "prata", "dourado");

	static {
		for (String p : List.of("celular", "iphone", "smartphone", "telefone")) {
			PALAVRAS_CATEGORIA.put(p, Categoria.CELULAR);
		}
		for (String p : List.of("carteira", "documento", "rg", "cnh", "cartao", "identidade")) {
			PALAVRAS_CATEGORIA.put(p, Categoria.CARTEIRA_DOCUMENTOS);
		}
		PALAVRAS_CATEGORIA.put("chave", Categoria.CHAVES);
		PALAVRAS_CATEGORIA.put("chaves", Categoria.CHAVES);
		for (String p : List.of("fone", "carregador", "notebook", "mouse", "calculadora", "pendrive", "tablet")) {
			PALAVRAS_CATEGORIA.put(p, Categoria.ELETRONICO);
		}
		for (String p : List.of("mochila", "bolsa", "pochete")) {
			PALAVRAS_CATEGORIA.put(p, Categoria.MOCHILA_BOLSA);
		}
		for (String p : List.of("casaco", "blusa", "camisa", "tenis", "bone", "jaqueta", "moletom")) {
			PALAVRAS_CATEGORIA.put(p, Categoria.VESTUARIO);
		}
		for (String p : List.of("garrafa", "squeeze", "marmita", "copo", "caneca")) {
			PALAVRAS_CATEGORIA.put(p, Categoria.GARRAFA_MARMITA);
		}
		for (String p : List.of("livro", "caderno", "estojo", "caneta", "apostila")) {
			PALAVRAS_CATEGORIA.put(p, Categoria.MATERIAL_ESCOLAR);
		}
		for (String p : List.of("oculos", "relogio", "colar", "anel", "pulseira")) {
			PALAVRAS_CATEGORIA.put(p, Categoria.ACESSORIO);
		}
	}

	@Override
	public String nome() {
		return "Simulado";
	}

	@Override
	public SugestaoItem analisar(ImagemEntrada imagem, String informacoesExtras) {
		List<String> palavras = Texto.palavras(informacoesExtras);
		String objeto = palavras.stream().filter(PALAVRAS_CATEGORIA::containsKey).findFirst().orElse(null);
		String cor = palavras.stream().filter(CORES::contains).findFirst().orElse(null);
		String aviso = "Modo simulado (sem IA real): categoria e cor foram deduzidas do texto informado. Revise todos os campos.";
		String local = extrairLocal(informacoesExtras);
		if (objeto == null) {
			return new SugestaoItem(null, Categoria.OUTROS, cor, null, Texto.vazioParaNulo(informacoesExtras), null,
					local, nome(), aviso, false);
		}
		String titulo = Texto.juntar(capitalizar(objeto), cor);
		return new SugestaoItem(titulo, PALAVRAS_CATEGORIA.get(objeto), cor, null,
				Texto.vazioParaNulo(informacoesExtras), null, local, nome(), aviso, false);
	}

	/** Reconhece locais comuns do campus, como "sala b415", "bloco c" ou "biblioteca". */
	private static String extrairLocal(String texto) {
		if (texto == null) {
			return null;
		}
		Matcher m = LOCAL.matcher(texto);
		if (!m.find()) {
			return null;
		}
		return java.util.Arrays.stream(m.group().strip().split("\\s+"))
				.map(p -> p.length() <= 2 || p.chars().anyMatch(Character::isDigit) ? p.toUpperCase() : capitalizar(p))
				.collect(java.util.stream.Collectors.joining(" "));
	}

	private static String capitalizar(String palavra) {
		return Character.toUpperCase(palavra.charAt(0)) + palavra.substring(1);
	}
}
