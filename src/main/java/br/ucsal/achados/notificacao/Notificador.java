package br.ucsal.achados.notificacao;

/** Canal de saída de e-mails. Implementações: envio SMTP real ou modo simulado. */
public interface Notificador {

	String modo();

	void enviar(String destinatario, String assunto, String corpo);
}
