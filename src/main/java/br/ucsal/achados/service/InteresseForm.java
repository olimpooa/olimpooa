package br.ucsal.achados.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dados informados pelo aluno ao sinalizar que um item é seu. */
public class InteresseForm {

	@NotBlank(message = "Informe seu nome")
	@Size(max = 120, message = "Máximo de 120 caracteres")
	private String nome;

	@NotBlank(message = "Informe seu e-mail institucional")
	@Pattern(regexp = "(?i)^[A-Za-z0-9._%+-]+@ucsal\\.edu\\.br$", message = "Use seu e-mail institucional (@ucsal.edu.br)")
	@Size(max = 120, message = "Máximo de 120 caracteres")
	private String email;

	@NotBlank(message = "Informe sua matrícula")
	@Pattern(regexp = "^[0-9]{6,12}$", message = "A matrícula deve ter de 6 a 12 números")
	private String matricula;

	@Size(max = 300, message = "Máximo de 300 caracteres")
	private String mensagem;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
