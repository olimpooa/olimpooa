package br.ucsal.achados.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.Interesse;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;
import br.ucsal.achados.notificacao.NotificacaoService;

/** Fluxo principal do MVP: cadastro → consulta → pedidos de alunos → devolução presencial. */
@SpringBootTest
@Transactional
class FluxoPrincipalTest {

	@Autowired ItemService itens;
	@Autowired InteresseService interesses;
	@Autowired BuscaService busca;
	@Autowired NotificacaoService notificacoes;

	private Item cadastrar(String titulo, Categoria categoria, String descricao) {
		ItemForm form = new ItemForm();
		form.setTitulo(titulo);
		form.setCategoria(categoria);
		form.setDescricao(descricao);
		form.setCor("azul");
		form.setLocalEncontrado("Biblioteca");
		form.setDataEncontrado(LocalDate.now());
		form.setCaracteristicasIdentificadoras("segredo-do-dono");
		return itens.cadastrar(form, "teste");
	}

	private InteresseForm aluno(String matricula) {
		InteresseForm form = new InteresseForm();
		form.setNome("Aluno " + matricula);
		form.setEmail("aluno" + matricula + "@ucsal.edu.br");
		form.setMatricula(matricula);
		return form;
	}

	@Test
	void primeiroPedidoColocaItemEmAnaliseEGeraHistoricoEEmails() {
		Item item = cadastrar("Garrafa azul", Categoria.GARRAFA_MARMITA, "Garrafa térmica");
		assertThat(item.getStatus()).isEqualTo(ItemStatus.DISPONIVEL);

		interesses.registrar(item.getId(), aluno("111111"));

		assertThat(itens.obter(item.getId()).getStatus()).isEqualTo(ItemStatus.EM_ANALISE);
		assertThat(itens.historico(item.getId())).extracting(h -> h.getPara())
				.containsExactly(ItemStatus.EM_ANALISE, ItemStatus.DISPONIVEL);
		assertThat(notificacoes.recentes()).extracting(e -> e.getDestinatario())
				.contains("aluno111111@ucsal.edu.br", "responsavel@ucsal.edu.br");
	}

	@Test
	void doisAlunosPodemPedirOMesmoItemEOCancelamentoDevolveADisponivelSoQuandoNaoRestaNinguem() {
		Item item = cadastrar("Mochila", Categoria.MOCHILA_BOLSA, "Mochila preta");
		Interesse a = interesses.registrar(item.getId(), aluno("111111"));
		Interesse b = interesses.registrar(item.getId(), aluno("222222"));
		assertThat(itens.interesses(item.getId())).hasSize(2).allMatch(Interesse::isAtivo);

		interesses.cancelar(a.getToken());
		assertThat(itens.obter(item.getId()).getStatus()).isEqualTo(ItemStatus.EM_ANALISE);

		interesses.cancelar(b.getToken());
		assertThat(itens.obter(item.getId()).getStatus()).isEqualTo(ItemStatus.DISPONIVEL);
	}

	@Test
	void rejeitaPedidoDuplicadoDoMesmoAlunoParaOMesmoItem() {
		Item item = cadastrar("Caderno", Categoria.MATERIAL_ESCOLAR, "Caderno azul");
		interesses.registrar(item.getId(), aluno("111111"));
		assertThatThrownBy(() -> interesses.registrar(item.getId(), aluno("111111")))
				.isInstanceOf(RegraNegocioException.class).hasMessageContaining("já tem um pedido");
	}

	@Test
	void devolucaoEncerraPedidosEBloqueiaNovos() {
		Item item = cadastrar("Casaco", Categoria.VESTUARIO, "Casaco cinza");
		Interesse pedido = interesses.registrar(item.getId(), aluno("111111"));

		itens.devolver(item.getId(), "Aluno 111111", "conferido", "teste");

		assertThat(itens.obter(item.getId()).getStatus()).isEqualTo(ItemStatus.DEVOLVIDO);
		assertThat(interesses.obterPorToken(pedido.getToken()).isAtivo()).isFalse();
		assertThatThrownBy(() -> interesses.registrar(item.getId(), aluno("333333")))
				.isInstanceOf(RegraNegocioException.class);
		assertThatThrownBy(() -> itens.liberar(item.getId(), "teste")).isInstanceOf(RegraNegocioException.class);
	}

	@Test
	void liberarVoltaADisponivelEEncerraPedidos() {
		Item item = cadastrar("Fone", Categoria.ELETRONICO, "Fone branco");
		Interesse pedido = interesses.registrar(item.getId(), aluno("111111"));

		itens.liberar(item.getId(), "teste");

		assertThat(itens.obter(item.getId()).getStatus()).isEqualTo(ItemStatus.DISPONIVEL);
		assertThat(interesses.obterPorToken(pedido.getToken()).isAtivo()).isFalse();
	}

	@Test
	void consultaPublicaNaoExpoeDetalhesDeItemSensivel() {
		Item celular = cadastrar("iPhone com capinha rosa", Categoria.CELULAR, "Tela com foto da família");

		ItemPublico visao = ItemPublico.de(celular);

		assertThat(visao.sensivel()).isTrue();
		assertThat(visao.titulo()).isEqualTo("Celular");
		assertThat(visao.descricao()).isNull();
		assertThat(visao.cor()).isNull();
		assertThat(visao.temFoto()).isFalse();
		assertThat(celular.getTextoBusca()).doesNotContain("iphone", "rosa", "familia");
	}

	@Test
	void buscaPorPalavrasEncontraPorSinonimoEFiltraPorCategoria() {
		cadastrar("Garrafa térmica azul", Categoria.GARRAFA_MARMITA, "Squeeze de 500 ml");
		cadastrar("Calculadora científica", Categoria.ELETRONICO, "Calculadora preta");

		FiltroBusca porTexto = new FiltroBusca();
		porTexto.setQ("squeeze");
		assertThat(busca.buscar(porTexto)).extracting(ItemPublico::titulo).containsExactly("Garrafa térmica azul");

		FiltroBusca porCategoria = new FiltroBusca();
		porCategoria.setCategoria(Categoria.ELETRONICO);
		assertThat(busca.buscar(porCategoria)).extracting(ItemPublico::titulo).containsExactly("Calculadora científica");
	}

	@Test
	void buscaPorDataELocal() {
		Item item = cadastrar("Livro", Categoria.MATERIAL_ESCOLAR, "Livro de cálculo");
		FiltroBusca filtro = new FiltroBusca();
		filtro.setLocal("bibli");
		filtro.setDe(LocalDate.now().minusDays(1));
		filtro.setAte(LocalDate.now());
		List<ItemPublico> resultado = busca.buscar(filtro);
		assertThat(resultado).extracting(ItemPublico::id).contains(item.getId());

		filtro.setDe(LocalDate.now().plusDays(1));
		assertThat(busca.buscar(filtro)).isEmpty();
	}
}
