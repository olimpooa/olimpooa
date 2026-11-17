package br.ucsal.achados.notificacao;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import br.ucsal.achados.config.AchadosProperties;

public class SmtpNotificador implements Notificador {

	private final ObjectProvider<JavaMailSender> mailSender;
	private final String remetente;

	public SmtpNotificador(ObjectProvider<JavaMailSender> mailSender, AchadosProperties props) {
		this.mailSender = mailSender;
		this.remetente = props.notificacao().remetente();
	}

	@Override
	public String modo() {
		return "SMTP";
	}

	@Override
	public void enviar(String destinatario, String assunto, String corpo) {
		JavaMailSender sender = mailSender.getIfAvailable();
		if (sender == null) {
			throw new IllegalStateException("SMTP não configurado (defina spring.mail.host).");
		}
		SimpleMailMessage mensagem = new SimpleMailMessage();
		mensagem.setFrom(remetente);
		mensagem.setTo(destinatario);
		mensagem.setSubject(assunto);
		mensagem.setText(corpo);
		sender.send(mensagem);
	}
}
