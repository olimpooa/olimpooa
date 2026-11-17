package br.ucsal.achados.domain;

import java.time.LocalDateTime;

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
@Table(name = "historico_status")
public class HistoricoStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "item_id")
	private Item item;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ItemStatus de;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ItemStatus para;

	@Column(nullable = false, length = 80)
	private String autor;

	@Column(length = 300)
	private String observacao;

	@Column(nullable = false)
	private LocalDateTime em = LocalDateTime.now();

	protected HistoricoStatus() {
	}

	public HistoricoStatus(Item item, ItemStatus de, ItemStatus para, String autor, String observacao) {
		this.item = item;
		this.de = de;
		this.para = para;
		this.autor = autor;
		this.observacao = observacao;
	}

	public Long getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public ItemStatus getDe() {
		return de;
	}

	public ItemStatus getPara() {
		return para;
	}

	public String getAutor() {
		return autor;
	}

	public String getObservacao() {
		return observacao;
	}

	public LocalDateTime getEm() {
		return em;
	}
}
