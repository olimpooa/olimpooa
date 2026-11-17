package br.ucsal.achados.notificacao;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import br.ucsal.achados.config.AchadosProperties;

@Configuration
public class NotificacaoConfig {

	/** Factory: escolhe o canal conforme achados.notificacao.modo. */
	@Bean
	Notificador notificador(AchadosProperties props, ObjectProvider<JavaMailSender> mailSender) {
		if ("smtp".equalsIgnoreCase(props.notificacao().modo())) {
			return new SmtpNotificador(mailSender, props);
		}
		return new SimuladoNotificador();
	}
}
