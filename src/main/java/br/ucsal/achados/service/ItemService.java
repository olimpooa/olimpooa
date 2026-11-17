package br.ucsal.achados.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.achados.domain.HistoricoStatus;
import br.ucsal.achados.domain.Interesse;
import br.ucsal.achados.domain.Interesse.Situacao;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;
import br.ucsal.achados.notificacao.EventosAchados.Destinatario;
import br.ucsal.achados.notificacao.EventosAchados.ItemDevolvido;
import br.ucsal.achados.notificacao.EventosAchados.ItemLiberado;
import br.ucsal.achados.repository.HistoricoStatusRepository;
import br.ucsal.achados.repository.InteresseRepository;
import br.ucsal.achados.repository.ItemRepository;
import br.ucsal.achados.util.Texto;

@Service
@Transactional
public class ItemService {

	private final ItemRepository itens;
	private final InteresseRepository interesses;
	private final HistoricoStatusRepository historico;
	private final StatusService statusService;
	private final FotoStorage fotos;
	private final ApplicationEventPublisher eventos;

	public ItemService(ItemRepository itens, InteresseRepository interesses, HistoricoStatusRepository historico,
			StatusService statusService, FotoStorage fotos, ApplicationEventPublisher eventos) {
		this.itens = itens;
		this.interesses = interesses;
		this.historico = historico;
		this.statusService = statusService;
		this.fotos = fotos;
		this.eventos = eventos;
	}

	@Transactional(readOnly = true)
	public Item obter(Long id) {
		return itens.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado."));
	}

	@Transactional(readOnly = true)
	public List<Item> listarTodos() {
		return itens.findAllByOrderByCriadoEmDesc();
	}

	@Transactional(readOnly = true)
	public List<HistoricoStatus> historico(Long itemId) {
		return historico.findByItemIdOrderByEmDescIdDesc(itemId);
	}

	@Transactional(readOnly = true)
	public List<Interesse> interesses(Long itemId) {
		return interesses.findByItemIdOrderByCriadoEmAsc(itemId);
	}

	public Item cadastrar(ItemForm form, String autor) {
		Item item = new Item();
		aplicar(item, form);
		item.setFotoArquivo(resolverFoto(form, null));
		itens.save(item);
		statusService.registrarCriacao(item, autor);
		return item;
	}

	public Item atualizar(Long id, ItemForm form, String autor) {
		Item item = obter(id);
		if (item.getStatus() == ItemStatus.DEVOLVIDO) {
			throw new RegraNegocioException("Itens devolvidos não podem ser editados.");
		}
		String fotoAnterior = item.getFotoArquivo();
		aplicar(item, form);
		item.setFotoArquivo(resolverFoto(form, fotoAnterior));
		if (fotoAnterior != null && !fotoAnterior.equals(item.getFotoArquivo())) {
			fotos.remover(fotoAnterior);
		}
		return item;
	}

	public Item devolver(Long id, String retiradoPor, String observacao, String autor) {
		Item item = obter(id);
		List<Destinatario> afetados = destinatariosAtivos(id);
		statusService.mudar(item, ItemStatus.DEVOLVIDO, autor, Texto.vazioParaNulo(observacao));
		item.setDevolvidoPara(Texto.vazioParaNulo(retiradoPor));
		encerrarInteresses(id);
		eventos.publishEvent(new ItemDevolvido(id, ItemPublico.de(item).titulo(), afetados));
		return item;
	}

	/** Devolve o item à lista de disponíveis (ex.: ninguém confirmou a propriedade presencialmente). */
	public Item liberar(Long id, String autor) {
		Item item = obter(id);
		List<Destinatario> afetados = destinatariosAtivos(id);
		statusService.mudar(item, ItemStatus.DISPONIVEL, autor, "Liberado para novos pedidos");
		encerrarInteresses(id);
		eventos.publishEvent(new ItemLiberado(id, ItemPublico.de(item).titulo(), afetados));
		return item;
	}

	private List<Destinatario> destinatariosAtivos(Long itemId) {
		return interesses.findByItemIdAndSituacao(itemId, Situacao.ATIVO).stream()
				.map(i -> new Destinatario(i.getNome(), i.getEmail(), i.getToken())).toList();
	}

	private void encerrarInteresses(Long itemId) {
		interesses.findByItemIdAndSituacao(itemId, Situacao.ATIVO).forEach(Interesse::encerrar);
	}

	private void aplicar(Item item, ItemForm form) {
		item.setTitulo(form.getTitulo().trim());
		item.setCategoria(form.getCategoria());
		item.setCor(Texto.vazioParaNulo(form.getCor()));
		item.setMarca(Texto.vazioParaNulo(form.getMarca()));
		item.setDescricao(Texto.vazioParaNulo(form.getDescricao()));
		item.setCaracteristicasIdentificadoras(Texto.vazioParaNulo(form.getCaracteristicasIdentificadoras()));
		item.setLocalEncontrado(form.getLocalEncontrado().trim());
		item.setDataEncontrado(form.getDataEncontrado());
		item.setObservacoes(Texto.vazioParaNulo(form.getObservacoes()));
		item.setCatalogadoPorIa(form.isCatalogadoPorIa());
		item.atualizarTextoBusca();
	}

	/** Prioriza uma foto recém-enviada; senão mantém a já guardada (cadastro em duas etapas ou edição). */
	private String resolverFoto(ItemForm form, String fotoAtual) {
		if (form.arquivoEnviado() != null) {
			return fotos.salvar(fotos.validar(form.arquivoEnviado()));
		}
		String informada = Texto.vazioParaNulo(form.getFotoArquivo());
		if (informada == null) {
			return fotoAtual;
		}
		return informada.equals(fotoAtual) ? fotoAtual : fotos.confirmarExistente(informada);
	}
}
