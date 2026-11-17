package br.ucsal.achados.ia;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.service.BuscaInteligenteService;
import br.ucsal.achados.service.CatalogacaoService;
import br.ucsal.achados.service.ItemForm;
import br.ucsal.achados.service.ItemService;

/** Provedor "gemini" sem chave configurada: a IA falha e o sistema deve cair no modo manual/palavras-chave. */
@SpringBootTest(properties = { "achados.ia.provedor=gemini", "achados.ia.api-key=" })
@Transactional
class IaFallbackTest {

	@Autowired CatalogacaoService catalogacao;
	@Autowired BuscaInteligenteService buscaInteligente;
	@Autowired ItemService itens;

	@Test
	void catalogacaoComIaIndisponivelDevolveFormularioVazioComAviso() {
		SugestaoItem sugestao = catalogacao.catalogar(null, "garrafa azul");

		assertThat(sugestao.geradaPorIa()).isFalse();
		assertThat(sugestao.titulo()).isNull();
		assertThat(sugestao.aviso()).contains("Preencha os campos manualmente");
	}

	@Test
	void buscaInteligenteComIaIndisponivelUsaPalavrasChaveEAvisa() {
		ItemForm form = new ItemForm();
		form.setTitulo("Fone de ouvido branco");
		form.setCategoria(Categoria.ELETRONICO);
		form.setLocalEncontrado("Biblioteca");
		form.setDataEncontrado(LocalDate.now());
		itens.cadastrar(form, "teste");

		var resultado = buscaInteligente.buscar("perdi um headphone branco");

		assertThat(resultado.aviso()).contains("palavras-chave");
		assertThat(resultado.origem()).isEqualTo("Palavras-chave");
		assertThat(resultado.sugestoes()).extracting(s -> s.item().titulo()).contains("Fone de ouvido branco");
	}

	@Test
	void catalogadorSimuladoDeduzCategoriaECorDoTexto() {
		SugestaoItem sugestao = new CatalogadorSimulado().analisar(null, "Garrafa azul perto do bebedouro");

		assertThat(sugestao.categoria()).isEqualTo(Categoria.GARRAFA_MARMITA);
		assertThat(sugestao.cor()).isEqualTo("azul");
		assertThat(sugestao.titulo()).isEqualTo("Garrafa azul");
		assertThat(sugestao.geradaPorIa()).isFalse();
	}
}
