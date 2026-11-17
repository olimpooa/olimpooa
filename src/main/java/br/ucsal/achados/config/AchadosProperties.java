package br.ucsal.achados.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "achados")
public record AchadosProperties(
		@DefaultValue("http://localhost:8080") String baseUrl,
		@DefaultValue("./uploads") String diretorioUploads,
		@DefaultValue Admin admin,
		@DefaultValue Ia ia,
		@DefaultValue Notificacao notificacao,
		@DefaultValue Demo demo) {

	public record Admin(@DefaultValue("responsavel") String usuario, @DefaultValue("ucsal2026") String senha) {
	}

	/** provedor: "auto" (Gemini se houver chave, senão simulado), "gemini" ou "simulado". */
	public record Ia(@DefaultValue("auto") String provedor, String apiKey,
			@DefaultValue("gemini-3.1-flash-lite") String modelo, @DefaultValue("30") int timeoutSegundos) {

		public boolean temChave() {
			return apiKey != null && !apiKey.isBlank();
		}
	}

	/** modo: "simulado" (só registra na caixa de saída) ou "smtp" (envia de verdade). */
	public record Notificacao(@DefaultValue("simulado") String modo,
			@DefaultValue("responsavel@ucsal.edu.br") String emailResponsavel,
			@DefaultValue("achados-perdidos@ucsal.edu.br") String remetente) {
	}

	public record Demo(@DefaultValue("true") boolean carregar) {
	}
}
