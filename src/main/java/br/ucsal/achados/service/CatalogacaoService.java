package br.ucsal.achados.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.ucsal.achados.config.AchadosProperties;
import br.ucsal.achados.ia.CatalogadorIA;
import br.ucsal.achados.ia.GeminiCatalogador;
import br.ucsal.achados.ia.CatalogadorSimulado;
import br.ucsal.achados.ia.SugestaoItem;

/**
 * Escolhe a estratégia de catalogação conforme a configuração e garante o fallback: se a IA falhar, o
 * responsável recebe um formulário vazio com um aviso, e o fluxo de cadastro continua manualmente.
 */
@Service
public class CatalogacaoService {

	private static final Logger log = LoggerFactory.getLogger(CatalogacaoService.class);

	private final AchadosProperties props;
	private final GeminiCatalogador gemini;
	private final CatalogadorSimulado simulado;

	public CatalogacaoService(AchadosProperties props, GeminiCatalogador gemini, CatalogadorSimulado simulado) {
		this.props = props;
		this.gemini = gemini;
		this.simulado = simulado;
	}

	public SugestaoItem catalogar(ImagemEntrada imagem, String informacoesExtras) {
		CatalogadorIA escolhido = escolher();
		try {
			return escolhido.analisar(imagem, informacoesExtras);
		} catch (RuntimeException e) {
			log.warn("Catalogação por {} falhou: {}", escolhido.nome(), e.getMessage());
			return SugestaoItem.vazia(escolhido.nome(),
					"A IA não está disponível agora (" + e.getMessage() + ") Preencha os campos manualmente.");
		}
	}

	public String provedorAtivo() {
		return escolher().nome();
	}

	private CatalogadorIA escolher() {
		String provedor = props.ia().provedor();
		boolean usarGemini = "gemini".equalsIgnoreCase(provedor)
				|| ("auto".equalsIgnoreCase(provedor) && props.ia().temChave());
		return usarGemini ? gemini : simulado;
	}
}
