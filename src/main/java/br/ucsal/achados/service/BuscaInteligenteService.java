package br.ucsal.achados.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.ucsal.achados.config.AchadosProperties;
import br.ucsal.achados.ia.GeminiRankeador;
import br.ucsal.achados.ia.PalavrasChaveRankeador;
import br.ucsal.achados.ia.RankeadorIA;
import br.ucsal.achados.ia.RankeadorIA.Classificacao;

/** "Perdi um fone preto": a IA ordena os itens em aberto; se falhar, usa palavras-chave e avisa o aluno. */
@Service
public class BuscaInteligenteService {

	private static final Logger log = LoggerFactory.getLogger(BuscaInteligenteService.class);

	public record Sugestao(ItemPublico item, int pontuacao, String justificativa) {
	}

	public record Resultado(List<Sugestao> sugestoes, String origem, String aviso) {
	}

	private final AchadosProperties props;
	private final BuscaService busca;
	private final GeminiRankeador gemini;
	private final PalavrasChaveRankeador palavrasChave;

	public BuscaInteligenteService(AchadosProperties props, BuscaService busca, GeminiRankeador gemini,
			PalavrasChaveRankeador palavrasChave) {
		this.props = props;
		this.busca = busca;
		this.gemini = gemini;
		this.palavrasChave = palavrasChave;
	}

	public Resultado buscar(String descricao) {
		List<ItemPublico> candidatos = busca.emAberto();
		RankeadorIA escolhido = escolher();
		String aviso = null;
		List<Classificacao> ranking;
		try {
			ranking = escolhido.ranquear(descricao, candidatos);
		} catch (RuntimeException e) {
			log.warn("Busca inteligente por {} falhou: {}", escolhido.nome(), e.getMessage());
			escolhido = palavrasChave;
			aviso = "A IA não está disponível agora; mostramos uma busca por palavras-chave.";
			ranking = palavrasChave.ranquear(descricao, candidatos);
		}
		Map<Long, ItemPublico> porId = candidatos.stream().collect(Collectors.toMap(ItemPublico::id, Function.identity()));
		List<Sugestao> sugestoes = ranking.stream()
				.filter(c -> porId.containsKey(c.itemId()))
				.map(c -> new Sugestao(porId.get(c.itemId()), c.pontuacao(), c.justificativa()))
				.toList();
		return new Resultado(sugestoes, escolhido.nome(), aviso);
	}

	private RankeadorIA escolher() {
		String provedor = props.ia().provedor();
		boolean usarGemini = "gemini".equalsIgnoreCase(provedor)
				|| ("auto".equalsIgnoreCase(provedor) && props.ia().temChave());
		return usarGemini ? gemini : palavrasChave;
	}
}
