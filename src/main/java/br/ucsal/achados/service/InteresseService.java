package br.ucsal.achados.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.achados.domain.Interesse;
import br.ucsal.achados.domain.Interesse.Situacao;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;
import br.ucsal.achados.notificacao.EventosAchados.Destinatario;
import br.ucsal.achados.notificacao.EventosAchados.InteresseRegistrado;
import br.ucsal.achados.repository.InteresseRepository;
import br.ucsal.achados.repository.ItemRepository;

@Service
@Transactional
public class InteresseService {

	private static final String AUTOR_SISTEMA = "sistema";

	private final InteresseRepository interesses;
	private final ItemRepository itens;
	private final StatusService statusService;
	private final ApplicationEventPublisher eventos;

	public InteresseService(InteresseRepository interesses, ItemRepository itens, StatusService statusService,
			ApplicationEventPublisher eventos) {
		this.interesses = interesses;
		this.itens = itens;
		this.statusService = statusService;
		this.eventos = eventos;
	}

	public Interesse registrar(Long itemId, InteresseForm form) {
		Item item = itens.findById(itemId).orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado."));
		if (!item.getStatus().aceitaInteresse()) {
			throw new RegraNegocioException("Este item já foi devolvido e não aceita novos pedidos.");
		}
		String matricula = form.getMatricula().trim();
		if (interesses.existsByItemIdAndMatriculaAndSituacao(itemId, matricula, Situacao.ATIVO)) {
			throw new RegraNegocioException("Você já tem um pedido ativo para este item.");
		}
		Interesse interesse = interesses.save(new Interesse(item, form.getNome().trim(),
				form.getEmail().trim().toLowerCase(), matricula, blankToNull(form.getMensagem())));
		if (item.getStatus() == ItemStatus.DISPONIVEL) {
			statusService.mudar(item, ItemStatus.EM_ANALISE, AUTOR_SISTEMA, "Primeiro pedido de aluno");
		}
		eventos.publishEvent(new InteresseRegistrado(itemId, ItemPublico.de(item).titulo(),
				new Destinatario(interesse.getNome(), interesse.getEmail(), interesse.getToken())));
		return interesse;
	}

	@Transactional(readOnly = true)
	public Interesse obterPorToken(String token) {
		return interesses.findByToken(token).orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
	}

	public Interesse cancelar(String token) {
		Interesse interesse = obterPorToken(token);
		if (!interesse.isAtivo()) {
			throw new RegraNegocioException("Este pedido já não está ativo.");
		}
		interesse.cancelar();
		Item item = interesse.getItem();
		boolean semPedidosAtivos = interesses.countByItemIdAndSituacao(item.getId(), Situacao.ATIVO) == 0;
		if (item.getStatus() == ItemStatus.EM_ANALISE && semPedidosAtivos) {
			statusService.mudar(item, ItemStatus.DISPONIVEL, AUTOR_SISTEMA, "Todos os pedidos foram cancelados");
		}
		return interesse;
	}

	@Transactional(readOnly = true)
	public List<Interesse> listarPorItem(Long itemId) {
		return interesses.findByItemIdOrderByCriadoEmAsc(itemId);
	}

	private static String blankToNull(String texto) {
		return texto == null || texto.isBlank() ? null : texto.trim();
	}
}
