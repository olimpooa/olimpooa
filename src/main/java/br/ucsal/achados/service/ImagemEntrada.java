package br.ucsal.achados.service;

/** Imagem já validada (tipo conferido pelo conteúdo, não pelo nome do arquivo). */
public record ImagemEntrada(byte[] bytes, String tipo) {

	public String extensao() {
		return switch (tipo) {
			case "image/png" -> "png";
			case "image/webp" -> "webp";
			default -> "jpg";
		};
	}
}
