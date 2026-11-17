package br.ucsal.achados.notificacao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.achados.domain.EmailRegistrado;
import br.ucsal.achados.repository.EmailRegistradoRepository;

@Service
public class NotificacaoService {

	private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

	private final Notificador notificador;
	private final EmailRegistradoRepository emails;

	public NotificacaoService(Notificador notificador, EmailRegistradoRepository emails) {
		this.notificador = notificador;
		this.emails = emails;
	}

	/** Falha no envio nunca derruba o fluxo principal: o erro fica registrado na caixa de saída. */
	@Transactional
	public void enviar(String destinatario, String assunto, String corpo) {
		boolean sucesso = true;
		String erro = null;
		try {
			notificador.enviar(destinatario, assunto, corpo);
		} catch (RuntimeException e) {
			sucesso = false;
			erro = abreviar(e.getMessage());
			log.warn("Falha ao enviar e-mail para {}: {}", destinatario, erro);
		}
		emails.save(new EmailRegistrado(destinatario, assunto, corpo, notificador.modo(), sucesso, erro));
	}

	@Transactional(readOnly = true)
	public List<EmailRegistrado> recentes() {
		return emails.findTop50ByOrderByRegistradoEmDescIdDesc();
	}

	public String modo() {
		return notificador.modo();
	}

	private static String abreviar(String texto) {
		if (texto == null) {
			return "erro desconhecido";
		}
		return texto.length() > 290 ? texto.substring(0, 290) : texto;
	}
}
