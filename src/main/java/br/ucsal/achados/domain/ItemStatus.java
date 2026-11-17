package br.ucsal.achados.domain;

/**
 * Estados do ciclo de vida de um item. As transições permitidas ficam no próprio enum (padrão State),
 * evitando condicionais espalhadas pelos serviços.
 */
public enum ItemStatus {

	DISPONIVEL("Disponível", "Aguardando o dono"),
	EM_ANALISE("Em análise", "Há alunos com interesse; a confirmação é presencial"),
	DEVOLVIDO("Devolvido", "Entregue ao dono");

	private final String rotulo;
	private final String descricao;

	ItemStatus(String rotulo, String descricao) {
		this.rotulo = rotulo;
		this.descricao = descricao;
	}

	public String getRotulo() {
		return rotulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public boolean podeIrPara(ItemStatus destino) {
		return switch (this) {
			case DISPONIVEL -> destino == EM_ANALISE || destino == DEVOLVIDO;
			case EM_ANALISE -> destino == DISPONIVEL || destino == DEVOLVIDO;
			case DEVOLVIDO -> false;
		};
	}

	public boolean aceitaInteresse() {
		return this != DEVOLVIDO;
	}
}
