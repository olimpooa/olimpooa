package br.ucsal.achados.notificacao;

/** Não envia nada: o e-mail fica apenas na caixa de saída do sistema (usado na demonstração). */
public class SimuladoNotificador implements Notificador {

	@Override
	public String modo() {
		return "SIMULADO";
	}

	@Override
	public void enviar(String destinatario, String assunto, String corpo) {
		// Intencionalmente vazio: NotificacaoService já registra o e-mail.
	}
}
