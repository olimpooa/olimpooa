package br.ucsal.achados.service;

import org.springframework.stereotype.Service;

import br.ucsal.achados.domain.HistoricoStatus;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;
import br.ucsal.achados.repository.HistoricoStatusRepository;

/** Único ponto de mudança de status: aplica a transição e registra o histórico. */
@Service
public class StatusService {

	private final HistoricoStatusRepository historico;

	public StatusService(HistoricoStatusRepository historico) {
		this.historico = historico;
	}

	public void registrarCriacao(Item item, String autor) {
		historico.save(new HistoricoStatus(item, null, item.getStatus(), autor, "Item cadastrado"));
	}

	public void mudar(Item item, ItemStatus destino, String autor, String observacao) {
		ItemStatus origem = item.getStatus();
		try {
			item.mudarStatus(destino);
		} catch (IllegalStateException e) {
			throw new RegraNegocioException(e.getMessage());
		}
		historico.save(new HistoricoStatus(item, origem, destino, autor, observacao));
	}
}
