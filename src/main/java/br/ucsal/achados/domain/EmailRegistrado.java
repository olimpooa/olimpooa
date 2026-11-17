package br.ucsal.achados.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Caixa de saída: todo e-mail gerado pelo sistema é registrado aqui, enviado de fato ou apenas simulado. */
@Entity
@Table(name = "email_registrado")
public class EmailRegistrado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String destinatario;

	@Column(nullable = false, length = 200)
	private String assunto;

	@Column(nullable = false, length = 3000)
	private String corpo;

	@Column(nullable = false, length = 20)
	private String modo;

	private boolean sucesso;

	@Column(length = 300)
	private String erro;

	@Column(nullable = false)
	private LocalDateTime registradoEm = LocalDateTime.now();

	protected EmailRegistrado() {
	}

	public EmailRegistrado(String destinatario, String assunto, String corpo, String modo, boolean sucesso, String erro) {
		this.destinatario = destinatario;
		this.assunto = assunto;
		this.corpo = corpo;
		this.modo = modo;
		this.sucesso = sucesso;
		this.erro = erro;
	}

	public Long getId() {
		return id;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public String getAssunto() {
		return assunto;
	}

	public String getCorpo() {
		return corpo;
	}

	public String getModo() {
		return modo;
	}

	public boolean isSucesso() {
		return sucesso;
	}

	public String getErro() {
		return erro;
	}

	public LocalDateTime getRegistradoEm() {
		return registradoEm;
	}
}
