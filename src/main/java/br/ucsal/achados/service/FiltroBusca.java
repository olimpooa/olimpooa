package br.ucsal.achados.service;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.ItemStatus;

public class FiltroBusca {

	private String q;
	private Categoria categoria;
	private String local;
	private ItemStatus status;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate de;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate ate;

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public ItemStatus getStatus() {
		return status;
	}

	public void setStatus(ItemStatus status) {
		this.status = status;
	}

	public LocalDate getDe() {
		return de;
	}

	public void setDe(LocalDate de) {
		this.de = de;
	}

	public LocalDate getAte() {
		return ate;
	}

	public void setAte(LocalDate ate) {
		this.ate = ate;
	}
}
