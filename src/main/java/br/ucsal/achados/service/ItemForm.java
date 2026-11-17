package br.ucsal.achados.service;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/** Dados do formulário de cadastro/edição de item pelo responsável. */
public class ItemForm {

	@NotBlank(message = "Informe um título")
	@Size(max = 120, message = "Máximo de 120 caracteres")
	private String titulo;

	@NotNull(message = "Escolha uma categoria")
	private Categoria categoria;

	@Size(max = 60, message = "Máximo de 60 caracteres")
	private String cor;

	@Size(max = 80, message = "Máximo de 80 caracteres")
	private String marca;

	@Size(max = 1000, message = "Máximo de 1000 caracteres")
	private String descricao;

	@Size(max = 1000, message = "Máximo de 1000 caracteres")
	private String caracteristicasIdentificadoras;

	@NotBlank(message = "Informe onde o item foi encontrado")
	@Size(max = 150, message = "Máximo de 150 caracteres")
	private String localEncontrado;

	@NotNull(message = "Informe a data em que o item foi encontrado")
	@PastOrPresent(message = "A data não pode estar no futuro")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dataEncontrado = LocalDate.now();

	@Size(max = 1000, message = "Máximo de 1000 caracteres")
	private String observacoes;

	/** Informações extras do responsável para ajudar a IA (não são gravadas). */
	@Size(max = 500, message = "Máximo de 500 caracteres")
	private String informacoesExtras;

	private MultipartFile foto;
	private String fotoArquivo;
	private boolean catalogadoPorIa;

	public static ItemForm de(Item item) {
		ItemForm form = new ItemForm();
		form.titulo = item.getTitulo();
		form.categoria = item.getCategoria();
		form.cor = item.getCor();
		form.marca = item.getMarca();
		form.descricao = item.getDescricao();
		form.caracteristicasIdentificadoras = item.getCaracteristicasIdentificadoras();
		form.localEncontrado = item.getLocalEncontrado();
		form.dataEncontrado = item.getDataEncontrado();
		form.observacoes = item.getObservacoes();
		form.fotoArquivo = item.getFotoArquivo();
		form.catalogadoPorIa = item.isCatalogadoPorIa();
		return form;
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

	public String getInformacoesExtras() {
		return informacoesExtras;
	}

	public void setInformacoesExtras(String informacoesExtras) {
		this.informacoesExtras = informacoesExtras;
	}

	public MultipartFile getFoto() {
		return foto;
	}

	public void setFoto(MultipartFile foto) {
		this.foto = foto;
	}

	public String getFotoArquivo() {
		return fotoArquivo;
	}

	public void setFotoArquivo(String fotoArquivo) {
		this.fotoArquivo = fotoArquivo;
	}

	public boolean isCatalogadoPorIa() {
		return catalogadoPorIa;
	}

	public void setCatalogadoPorIa(boolean catalogadoPorIa) {
		this.catalogadoPorIa = catalogadoPorIa;
	}
}
