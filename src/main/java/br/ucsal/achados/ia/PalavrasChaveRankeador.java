package br.ucsal.achados.ia;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import br.ucsal.achados.service.ItemPublico;
import br.ucsal.achados.service.Similaridade;
import br.ucsal.achados.util.Texto;

/** Ranqueamento local por palavras-chave e sinônimos: contingência quando a IA está indisponível. */
@Component
public class PalavrasChaveRankeador implements RankeadorIA {

	@Override
	public String nome() {
		return "Palavras-chave";
	}

	@Override
	public List<Classificacao> ranquear(String descricao, List<ItemPublico> candidatos) {
		return candidatos.stream()
				.map(item -> classificar(item, Similaridade.pontuar(textoPublico(item), descricao)))
				.filter(c -> c.pontuacao() > 0)
				.sorted(Comparator.comparingInt(Classificacao::pontuacao).reversed())
				.toList();
	}

	private static Classificacao classificar(ItemPublico item, Similaridade.Resultado r) {
		String justificativa = r.termos().isEmpty() ? null : "Coincide com: " + String.join(", ", r.termos());
		return new Classificacao(item.id(), r.percentual(), justificativa);
	}

	private static String textoPublico(ItemPublico item) {
		return Texto.juntar(item.titulo(), item.categoria().getRotulo(), item.cor(), item.descricao(),
				item.localEncontrado());
	}
}
