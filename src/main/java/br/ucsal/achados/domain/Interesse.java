package br.ucsal.achados.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "interesse")
public class Interesse {

	public enum Situacao {
		ATIVO("Ativo"), CANCELADO("Cancelado"), ENCERRADO("Encerrado");

		private final String rotulo;

		Situacao(String rotulo) {
			this.rotulo = rotulo;
		}

		public String getRotulo() {
			return rotulo;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "item_id")
	private Item item;

	@Column(nullable = false, length = 120)
	private String nome;

	@Column(nullable = false, length = 120)
	private String email;

	@Column(nullable = false, length = 20)
	private String matricula;

	@Column(length = 300)
	private String mensagem;

	/** Identifica o pedido para que o aluno, sem login, possa consultá-lo e cancelá-lo. */
	@Column(nullable = false, unique = true, length = 40)
	private String token = UUID.randomUUID().toString();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Situacao situacao = Situacao.ATIVO;

	@Column(nullable = false)
	private LocalDateTime criadoEm = LocalDateTime.now();

	protected Interesse() {
	}

	public Interesse(Item item, String nome, String email, String matricula, String mensagem) {
		this.item = item;
		this.nome = nome;
		this.email = email;
		this.matricula = matricula;
		this.mensagem = mensagem;
	}

	public boolean isAtivo() {
		return situacao == Situacao.ATIVO;
	}

	public void cancelar() {
		if (isAtivo()) {
			this.situacao = Situacao.CANCELADO;
		}
	}

	public void encerrar() {
		if (isAtivo()) {
			this.situacao = Situacao.ENCERRADO;
		}
	}

	public Long getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getMatricula() {
		return matricula;
	}

	public String getMensagem() {
		return mensagem;
	}

	public String getToken() {
		return token;
	}

	public Situacao getSituacao() {
		return situacao;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}
}
