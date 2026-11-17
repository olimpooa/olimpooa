package br.ucsal.achados.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.service.ItemForm;
import br.ucsal.achados.service.ItemService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WebTest {

	@Autowired MockMvc mvc;
	@Autowired ItemService itens;

	private Item cadastrar(String titulo, Categoria categoria, String descricao) {
		ItemForm form = new ItemForm();
		form.setTitulo(titulo);
		form.setCategoria(categoria);
		form.setDescricao(descricao);
		form.setLocalEncontrado("Cantina");
		form.setDataEncontrado(LocalDate.now());
		return itens.cadastrar(form, "teste");
	}

	@Test
	void alunoConsultaSemLoginEItemSensivelFicaOculto() throws Exception {
		cadastrar("Garrafa visível", Categoria.GARRAFA_MARMITA, "descricao-publica");
		cadastrar("iPhone secreto", Categoria.CELULAR, "detalhe-privado");

		String html = mvc.perform(get("/")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		assertThat(html).contains("Garrafa visível").contains("Celular");
		assertThat(html).doesNotContain("iPhone secreto").doesNotContain("detalhe-privado");
	}

	@Test
	void detalheDeItemSensivelNaoVazaDescricao() throws Exception {
		Item celular = cadastrar("iPhone secreto", Categoria.CELULAR, "detalhe-privado");

		mvc.perform(get("/itens/" + celular.getId())).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("detalhe-privado"))));
	}

	@Test
	void areaDoResponsavelExigeLogin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/login"));
		mvc.perform(get("/admin/emails")).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(roles = "RESPONSAVEL")
	void responsavelAcessaPainel() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk()).andExpect(view().name("admin/painel"));
	}

	@Test
	void pedidoComEmailForaDoDominioEhRejeitado() throws Exception {
		Item item = cadastrar("Livro", Categoria.MATERIAL_ESCOLAR, "Livro");

		mvc.perform(post("/itens/" + item.getId() + "/interesse").with(csrf())
				.param("nome", "Fulano").param("email", "fulano@gmail.com").param("matricula", "123456"))
				.andExpect(status().isOk()).andExpect(view().name("item"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("@ucsal.edu.br")));
	}

	@Test
	void pedidoValidoRedirecionaParaAcompanhamento() throws Exception {
		Item item = cadastrar("Livro", Categoria.MATERIAL_ESCOLAR, "Livro");

		mvc.perform(post("/itens/" + item.getId() + "/interesse").with(csrf())
				.param("nome", "Fulano").param("email", "fulano@ucsal.edu.br").param("matricula", "123456"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("/interesse/*"));
	}

	@Test
	void recarregarAposErroDeValidacaoVoltaAoItem() throws Exception {
		mvc.perform(get("/itens/7/interesse")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/itens/7"));
	}

	@Test
	void itemInexistenteRetorna404() throws Exception {
		mvc.perform(get("/itens/999999")).andExpect(status().isNotFound());
	}
}
