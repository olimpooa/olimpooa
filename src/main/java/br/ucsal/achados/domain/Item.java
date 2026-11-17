package br.ucsal.achados.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.ucsal.achados.util.Texto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "item")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String titulo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Categoria categoria = Categoria.OUTROS;

	@Column(length = 60)
	private String cor;

	@Column(length = 80)
	private String marca;

	@Column(length = 1000)
	private String descricao;

	/** Detalhes que só o dono saberia informar; visíveis apenas ao responsável, para a conferência presencial. */
	@Column(length = 1000)
	private String caracteristicasIdentificadoras;

	@Column(nullable = false, length = 150)
	private String localEncontrado;

	@Column(nullable = false)
	private LocalDate dataEncontrado;

	@Column(length = 1000)
	private String observacoes;

	@Column(length = 60)
	private String fotoArquivo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ItemStatus status = ItemStatus.DISPONIVEL;

	@Column(nullable = false)
	private LocalDateTime criadoEm = LocalDateTime.now();

	private LocalDateTime devolvidoEm;

	@Column(length = 120)
	private String devolvidoPara;

	private boolean catalogadoPorIa;

	@Column(length = 2000)
	private String textoBusca;

	public boolean isSensivel() {
		return categoria.isSensivel();
	}

	public boolean temFoto() {
		return fotoArquivo != null && !fotoArquivo.isBlank();
	}

	/** Recalcula o texto pesquisável usando só o que é público: itens sensíveis não indexam detalhes. */
	public void atualizarTextoBusca() {
		if (isSensivel()) {
			this.textoBusca = Texto.normalizar(Texto.juntar(categoria.getRotulo(), localEncontrado));
		} else {
			this.textoBusca = Texto.normalizar(
					Texto.juntar(titulo, categoria.getRotulo(), cor, marca, descricao, localEncontrado));
		}
	}

	/** Valida e aplica a transição de status conforme as regras de {@link ItemStatus}. */
	public void mudarStatus(ItemStatus destino) {
		if (!status.podeIrPara(destino)) {
			throw new IllegalStateException("Transição inválida: " + status.getRotulo() + " → " + destino.getRotulo());
		}
		this.status = destino;
		if (destino == ItemStatus.DEVOLVIDO) {
			this.devolvidoEm = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCaracteristicasIdentificadoras() {
		return caracteristicasIdentificadoras;
	}

	public void setCaracteristicasIdentificadoras(String caracteristicasIdentificadoras) {
		this.caracteristicasIdentificadoras = caracteristicasIdentificadoras;
	}

	public String getLocalEncontrado() {
		return localEncontrado;
	}

	public void setLocalEncontrado(String localEncontrado) {
		this.localEncontrado = localEncontrado;
	}

	public LocalDate getDataEncontrado() {
		return dataEncontrado;
	}

	public void setDataEncontrado(LocalDate dataEncontrado) {
		this.dataEncontrado = dataEncontrado;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public String getFotoArquivo() {
		return fotoArquivo;
	}

	public void setFotoArquivo(String fotoArquivo) {
		this.fotoArquivo = fotoArquivo;
	}

	public ItemStatus getStatus() {
		return status;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public LocalDateTime getDevolvidoEm() {
		return devolvidoEm;
	}

	public String getDevolvidoPara() {
		return devolvidoPara;
	}

	public void setDevolvidoPara(String devolvidoPara) {
		this.devolvidoPara = devolvidoPara;
	}

	public boolean isCatalogadoPorIa() {
		return catalogadoPorIa;
	}

	public void setCatalogadoPorIa(boolean catalogadoPorIa) {
		this.catalogadoPorIa = catalogadoPorIa;
	}

	public String getTextoBusca() {
		return textoBusca;
	}

	public void setTextoBusca(String textoBusca) {
		this.textoBusca = textoBusca;
	}
}
