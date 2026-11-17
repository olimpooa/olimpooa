package br.ucsal.achados.notificacao;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import br.ucsal.achados.config.AchadosProperties;
import br.ucsal.achados.notificacao.EventosAchados.Destinatario;
import br.ucsal.achados.notificacao.EventosAchados.InteresseRegistrado;
import br.ucsal.achados.notificacao.EventosAchados.ItemDevolvido;
import br.ucsal.achados.notificacao.EventosAchados.ItemLiberado;

/** Observer dos eventos de domínio: transforma cada evento nos e-mails correspondentes. */
@Component
public class NotificacaoListener {

	private final NotificacaoService notificacoes;
	private final AchadosProperties props;

	public NotificacaoListener(NotificacaoService notificacoes, AchadosProperties props) {
		this.notificacoes = notificacoes;
		this.props = props;
	}

	@EventListener
	void aoRegistrarInteresse(InteresseRegistrado evento) {
		Destinatario aluno = evento.aluno();
		notificacoes.enviar(aluno.email(), "Pedido registrado: " + evento.itemTitulo(), """
				Olá, %s!

				Recebemos seu pedido para o item "%s" no Achados e Perdidos da UCSal.

				Para retirar o item, compareça presencialmente ao setor responsável. A confirmação de que o item \
				é seu será feita no local, e itens pessoais (como celular) exigem essa verificação.

				Acompanhe ou cancele seu pedido: %s/interesse/%s
				""".formatted(aluno.nome(), evento.itemTitulo(), props.baseUrl(), aluno.token()));
		notificacoes.enviar(props.notificacao().emailResponsavel(), "Novo pedido de aluno: " + evento.itemTitulo(), """
				%s sinalizou interesse no item "%s" (código %d).

				Veja os pedidos em %s/admin/itens/%d
				""".formatted(aluno.nome(), evento.itemTitulo(), evento.itemId(), props.baseUrl(), evento.itemId()));
	}

	@EventListener
	void aoDevolverItem(ItemDevolvido evento) {
		for (Destinatario aluno : evento.interessados()) {
			notificacoes.enviar(aluno.email(), "Item devolvido: " + evento.itemTitulo(), """
					Olá, %s!

					O item "%s" foi marcado como devolvido ao dono. Se você não foi quem retirou, \
					procure o responsável pelo Achados e Perdidos.
					""".formatted(aluno.nome(), evento.itemTitulo()));
		}
	}

	@EventListener
	void aoLiberarItem(ItemLiberado evento) {
		for (Destinatario aluno : evento.interessados()) {
			notificacoes.enviar(aluno.email(), "Pedido encerrado: " + evento.itemTitulo(), """
					Olá, %s!

					O item "%s" voltou a ficar disponível e seu pedido foi encerrado. Se ainda precisar dele, \
					faça um novo pedido em %s/itens/%d.
					""".formatted(aluno.nome(), evento.itemTitulo(), props.baseUrl(), evento.itemId()));
		}
	}
}
