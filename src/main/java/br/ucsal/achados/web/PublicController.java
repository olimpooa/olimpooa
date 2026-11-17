package br.ucsal.achados.web;

import java.nio.file.Path;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.Interesse;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;
import br.ucsal.achados.service.BuscaInteligenteService;
import br.ucsal.achados.service.BuscaService;
import br.ucsal.achados.service.FiltroBusca;
import br.ucsal.achados.service.FotoStorage;
import br.ucsal.achados.service.InteresseForm;
import br.ucsal.achados.service.InteresseService;
import br.ucsal.achados.service.ItemPublico;
import br.ucsal.achados.service.ItemService;
import br.ucsal.achados.service.RecursoNaoEncontradoException;
import br.ucsal.achados.service.RegraNegocioException;
import jakarta.validation.Valid;

/** Área pública: o aluno consulta itens e sinaliza interesse, sem login. */
@Controller
public class PublicController {

	private final BuscaService busca;
	private final BuscaInteligenteService buscaInteligente;
	private final InteresseService interesses;
	private final ItemService itens;
	private final FotoStorage fotos;

	public PublicController(BuscaService busca, BuscaInteligenteService buscaInteligente, InteresseService interesses,
			ItemService itens, FotoStorage fotos) {
		this.busca = busca;
		this.buscaInteligente = buscaInteligente;
		this.interesses = interesses;
		this.itens = itens;
		this.fotos = fotos;
	}

	@GetMapping("/")
	String inicio(@ModelAttribute("filtro") FiltroBusca filtro, Model model) {
		model.addAttribute("itens", busca.buscar(filtro));
		model.addAttribute("categorias", Categoria.values());
		model.addAttribute("statuses", ItemStatus.values());
		return "home";
	}

	@GetMapping("/busca-inteligente")
	String buscaInteligente(@RequestParam(required = false) String descricao, Model model) {
		model.addAttribute("descricao", descricao);
		if (descricao != null && !descricao.isBlank()) {
			model.addAttribute("resultado", buscaInteligente.buscar(descricao.strip()));
		}
		return "busca-inteligente";
	}

	@GetMapping("/itens/{id}")
	String detalhe(@PathVariable Long id, Model model) {
		model.addAttribute("item", busca.obterPublico(id));
		model.addAttribute("form", new InteresseForm());
		return "item";
	}

	/** Recarregar a página após um erro de validação cai aqui (GET): volta ao formulário do item. */
	@GetMapping("/itens/{id}/interesse")
	String voltarAoItem(@PathVariable Long id) {
		return "redirect:/itens/" + id;
	}

	@PostMapping("/itens/{id}/interesse")
	String registrarInteresse(@PathVariable Long id, @Valid @ModelAttribute("form") InteresseForm form,
			BindingResult resultado, Model model) {
		ItemPublico item = busca.obterPublico(id);
		model.addAttribute("item", item);
		if (resultado.hasErrors()) {
			return "item";
		}
		try {
			Interesse interesse = interesses.registrar(id, form);
			return "redirect:/interesse/" + interesse.getToken();
		} catch (RegraNegocioException e) {
			model.addAttribute("erroRegra", e.getMessage());
			return "item";
		}
	}

	@GetMapping("/interesse/{token}")
	String acompanhar(@PathVariable String token, Model model) {
		Interesse interesse = interesses.obterPorToken(token);
		model.addAttribute("interesse", interesse);
		model.addAttribute("item", ItemPublico.de(interesse.getItem()));
		return "interesse";
	}

	@PostMapping("/interesse/{token}/cancelar")
	String cancelar(@PathVariable String token) {
		interesses.cancelar(token);
		return "redirect:/interesse/" + token;
	}

	/** Fotos de itens sensíveis só podem ser vistas pelo responsável. */
	@GetMapping("/fotos/{id}")
	ResponseEntity<Resource> foto(@PathVariable Long id, Authentication autenticacao) {
		Item item = itens.obter(id);
		boolean permitido = !item.isSensivel() || GlobalControllerAdvice.ehResponsavel(autenticacao);
		if (!item.temFoto() || !permitido) {
			throw new RecursoNaoEncontradoException("Foto não encontrada.");
		}
		Path arquivo = fotos.caminho(item.getFotoArquivo());
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(fotos.tipoDe(item.getFotoArquivo())))
				.cacheControl(CacheControl.noCache().cachePrivate())
				.body(new FileSystemResource(arquivo));
	}
}
