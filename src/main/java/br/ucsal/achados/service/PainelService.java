package br.ucsal.achados.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.Interesse;
import br.ucsal.achados.domain.Interesse.Situacao;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;
import br.ucsal.achados.repository.InteresseRepository;
import br.ucsal.achados.repository.ItemRepository;

@Service
@Transactional(readOnly = true)
public class PainelService {

	private static final int DIAS_PARA_CONSIDERAR_PARADO = 30;

	public record Painel(long total, Map<ItemStatus, Long> porStatus, long pedidosAtivos, Double diasMedioAteDevolucao,
			long semPedidoHaMuitoTempo, Map<Categoria, Long> porCategoria) {
	}

	private final ItemRepository itens;
	private final InteresseRepository interesses;

	public PainelService(ItemRepository itens, InteresseRepository interesses) {
		this.itens = itens;
		this.interesses = interesses;
	}

	public Painel gerar() {
		List<Item> todos = itens.findAll();
		Map<ItemStatus, Long> porStatus = new LinkedHashMap<>();
		for (ItemStatus status : ItemStatus.values()) {
			porStatus.put(status, todos.stream().filter(i -> i.getStatus() == status).count());
		}
		Double mediaDias = todos.stream().filter(i -> i.getDevolvidoEm() != null)
				.mapToLong(i -> Math.max(0, Duration.between(i.getCriadoEm(), i.getDevolvidoEm()).toDays()))
				.average().stream().boxed().findFirst().orElse(null);
		LocalDate limite = LocalDate.now().minusDays(DIAS_PARA_CONSIDERAR_PARADO);
		long parados = todos.stream()
				.filter(i -> i.getStatus() == ItemStatus.DISPONIVEL && i.getDataEncontrado().isBefore(limite)).count();
		Map<Categoria, Long> porCategoria = todos.stream()
				.collect(Collectors.groupingBy(Item::getCategoria, Collectors.counting()))
				.entrySet().stream()
				.sorted(Map.Entry.<Categoria, Long>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		return new Painel(todos.size(), porStatus, interesses.countBySituacao(Situacao.ATIVO), mediaDias, parados,
				porCategoria);
	}

	public Map<Long, Long> pedidosAtivosPorItem() {
		return interesses.findAll().stream().filter(Interesse::isAtivo)
				.collect(Collectors.groupingBy(i -> i.getItem().getId(), Collectors.counting()));
	}

	public static int diasParado() {
		return DIAS_PARA_CONSIDERAR_PARADO;
	}
}
