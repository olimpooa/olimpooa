package br.ucsal.achados.ia;

import br.ucsal.achados.service.ImagemEntrada;

/** Estratégia de catalogação automática: gera uma ficha sugerida a partir da foto e de informações extras. */
public interface CatalogadorIA {

	String nome();

	SugestaoItem analisar(ImagemEntrada imagem, String informacoesExtras);
}
