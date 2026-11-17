package br.ucsal.achados.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;
import br.ucsal.achados.repository.ItemRepository;
import br.ucsal.achados.util.Texto;
import jakarta.persistence.criteria.Predicate;

/** Consulta pública: filtros estruturados no banco e busca por palavras-chave (com sinônimos) via Streams. */
@Service
@Transactional(readOnly = true)
public class BuscaService {

	private static final Sort ORDEM = Sort.by(Sort.Direction.DESC, "dataEncontrado").and(Sort.by(Sort.Direction.DESC, "id"));

	private final ItemRepository itens;

	public BuscaService(ItemRepository itens) {
		this.itens = itens;
	}

	public List<ItemPublico> buscar(FiltroBusca filtro) {
		List<Item> candidatos = itens.findAll(especificacao(filtro), ORDEM);
		String consulta = Texto.vazioParaNulo(filtro.getQ());
		if (consulta == null) {
			return candidatos.stream().map(ItemPublico::de).toList();
		}
		return candidatos.stream()
				.map(item -> new Pontuado(item, Similaridade.pontuar(item.getTextoBusca(), consulta).pontos()))
				.filter(p -> p.pontos() > 0)
				.sorted(Comparator.comparingInt(Pontuado::pontos).reversed())
				.map(p -> ItemPublico.de(p.item()))
				.toList();
	}

	/** Itens ainda em aberto, usados pela busca inteligente e pelo painel público. */
	public List<ItemPublico> emAberto() {
		return itens.findByStatusInOrderByDataEncontradoDesc(List.of(ItemStatus.DISPONIVEL, ItemStatus.EM_ANALISE))
				.stream().map(ItemPublico::de).toList();
	}

	public ItemPublico obterPublico(Long id) {
		return itens.findById(id).map(ItemPublico::de)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado."));
	}

	private record Pontuado(Item item, int pontos) {
	}

	private static Specification<Item> especificacao(FiltroBusca f) {
		return (root, query, cb) -> {
			List<Predicate> condicoes = new ArrayList<>();
			if (f.getCategoria() != null) {
				condicoes.add(cb.equal(root.get("categoria"), f.getCategoria()));
			}
			if (f.getStatus() != null) {
				condicoes.add(cb.equal(root.get("status"), f.getStatus()));
			} else {
				condicoes.add(cb.notEqual(root.get("status"), ItemStatus.DEVOLVIDO));
			}
			String local = Texto.vazioParaNulo(f.getLocal());
			if (local != null) {
				condicoes.add(cb.like(cb.lower(root.get("localEncontrado")), "%" + local.toLowerCase(Locale.ROOT) + "%"));
			}
			if (f.getDe() != null) {
				condicoes.add(cb.greaterThanOrEqualTo(root.get("dataEncontrado"), f.getDe()));
			}
			if (f.getAte() != null) {
				condicoes.add(cb.lessThanOrEqualTo(root.get("dataEncontrado"), f.getAte()));
			}
			return cb.and(condicoes.toArray(new Predicate[0]));
		};
	}
}
