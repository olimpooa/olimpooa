package br.ucsal.achados.ia;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import br.ucsal.achados.config.AchadosProperties;
import br.ucsal.achados.service.ImagemEntrada;

/** Cliente mínimo da API REST do Gemini (plano gratuito). A chave segue no cabeçalho e nunca é registrada em log. */
@Component
public class GeminiClient {

	private static final String URL_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";

	private final AchadosProperties.Ia config;
	private final RestClient http;

	public GeminiClient(AchadosProperties props) {
		this.config = props.ia();
		SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
		fabrica.setConnectTimeout(Duration.ofSeconds(5));
		fabrica.setReadTimeout(Duration.ofSeconds(config.timeoutSegundos()));
		this.http = RestClient.builder().requestFactory(fabrica).build();
	}

	public boolean configurado() {
		return config.temChave();
	}

	/** Envia o prompt (e a imagem, se houver) e devolve o texto JSON gerado pelo modelo. */
	public String gerarJson(String prompt, ImagemEntrada imagem) {
		if (!configurado()) {
			throw new IaIndisponivelException("Chave da API Gemini não configurada.");
		}
		List<Object> partes = new ArrayList<>();
		partes.add(Map.of("text", prompt));
		if (imagem != null) {
			partes.add(Map.of("inline_data", Map.of("mime_type", imagem.tipo(),
					"data", Base64.getEncoder().encodeToString(imagem.bytes()))));
		}
		Map<String, Object> corpo = new LinkedHashMap<>();
		corpo.put("contents", List.of(Map.of("parts", partes)));
		corpo.put("generationConfig", Map.of("responseMimeType", "application/json", "temperature", 0.2));

		try {
			Map<?, ?> resposta = http.post()
					.uri(URL_BASE + config.modelo() + ":generateContent")
					.header("x-goog-api-key", config.apiKey())
					.contentType(MediaType.APPLICATION_JSON)
					.body(corpo)
					.retrieve()
					.body(Map.class);
			return extrairTexto(resposta);
		} catch (RestClientResponseException e) {
			throw new IaIndisponivelException("O Gemini respondeu HTTP " + e.getStatusCode().value()
					+ " (cota esgotada, modelo ou chave sem permissão).", e);
		} catch (RestClientException e) {
			throw new IaIndisponivelException("Falha ao consultar o Gemini (cota, rede ou chave inválida).", e);
		}
	}

	private static String extrairTexto(Map<?, ?> resposta) {
		try {
			Map<?, ?> candidato = (Map<?, ?>) ((List<?>) resposta.get("candidates")).get(0);
			Map<?, ?> conteudo = (Map<?, ?>) candidato.get("content");
			String texto = ((List<?>) conteudo.get("parts")).stream()
					.map(p -> (Map<?, ?>) p)
					.filter(p -> p.get("text") != null && !Boolean.TRUE.equals(p.get("thought")))
					.map(p -> p.get("text").toString())
					.collect(java.util.stream.Collectors.joining());
			if (texto.isBlank()) {
				throw new IaIndisponivelException("O Gemini devolveu uma resposta vazia.");
			}
			return texto;
		} catch (ClassCastException | NullPointerException | IndexOutOfBoundsException e) {
			throw new IaIndisponivelException("O Gemini não devolveu conteúdo utilizável (possível bloqueio de segurança).", e);
		}
	}
}
