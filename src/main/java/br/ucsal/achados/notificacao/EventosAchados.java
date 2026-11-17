package br.ucsal.achados.notificacao;

import java.util.List;

/** Eventos de domínio publicados pelos serviços; os ouvintes (padrão Observer) decidem quem é avisado. */
public final class EventosAchados {

	private EventosAchados() {
	}

	public record Destinatario(String nome, String email, String token) {
	}

	public record InteresseRegistrado(Long itemId, String itemTitulo, Destinatario aluno) {
	}

	public record ItemDevolvido(Long itemId, String itemTitulo, List<Destinatario> interessados) {
	}

	public record ItemLiberado(Long itemId, String itemTitulo, List<Destinatario> interessados) {
	}
}
