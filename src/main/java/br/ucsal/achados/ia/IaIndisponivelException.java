package br.ucsal.achados.ia;

public class IaIndisponivelException extends RuntimeException {

	public IaIndisponivelException(String mensagem) {
		super(mensagem);
	}

	public IaIndisponivelException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
