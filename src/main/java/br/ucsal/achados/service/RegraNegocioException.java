package br.ucsal.achados.service;

/** Violação de uma regra de negócio que deve ser mostrada ao usuário. */
public class RegraNegocioException extends RuntimeException {

	public RegraNegocioException(String mensagem) {
		super(mensagem);
	}
}
