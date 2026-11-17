package br.ucsal.achados.web;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.security.Principal;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;
import br.ucsal.achados.ia.SugestaoItem;
import br.ucsal.achados.notificacao.NotificacaoService;
import br.ucsal.achados.service.CatalogacaoService;
import br.ucsal.achados.service.FotoStorage;
import br.ucsal.achados.service.ImagemEntrada;
import br.ucsal.achados.service.ItemForm;
import br.ucsal.achados.service.ItemService;
import br.ucsal.achados.service.PainelService;
import br.ucsal.achados.util.Texto;
import jakarta.validation.Valid;

/** Área do responsável (exige login): painel, cadastro com IA, conferência e devolução. */
@Controller
public class AdminController {

	private final ItemService itens;
	private final PainelService painel;
	private final CatalogacaoService catalogacao;
	private final NotificacaoService notificacoes;
	private final FotoStorage fotos;

	public AdminController(ItemService itens, PainelService painel, CatalogacaoService catalogacao,
			NotificacaoService notificacoes, FotoStorage fotos) {
		this.itens = itens;
		this.painel = painel;
		this.catalogacao = catalogacao;
		this.notificacoes = notificacoes;
		this.fotos = fotos;
	}

	@GetMapping("/admin/login")
	String login() {
		return "admin/login";
	}

	@GetMapping("/admin")
	String painel(@RequestParam(required = false) ItemStatus status, Model model) {
		model.addAttribute("painel", painel.gerar());
		model.addAttribute("itens", itens.listarTodos().stream()
				.filter(i -> status == null || i.getStatus() == status).toList());
		model.addAttribute("pedidosAtivos", painel.pedidosAtivosPorItem());
		model.addAttribute("statusSelecionado", status);
		model.addAttribute("statuses", ItemStatus.values());
		model.addAttribute("diasParado", PainelService.diasParado());
		return "admin/painel";
	}

	@GetMapping("/admin/itens/novo")
	String novo(Model model) {
		return formulario(model, new ItemForm(), null);
	}

	@PostMapping("/admin/itens/analisar")
	String analisar(@ModelAttribute("form") ItemForm form, @RequestParam(required = false) Long itemId, Model model) {
		ImagemEntrada imagem = obterImagem(form);
		SugestaoItem sugestao = catalogacao.catalogar(imagem, form.getInformacoesExtras());
		if (sugestao.titulo() != null || sugestao.descricao() != null) {
			form.setTitulo(sugestao.titulo());
			form.setCategoria(sugestao.categoria());
			form.setCor(sugestao.cor());
			form.setMarca(sugestao.marca());
			form.setDescricao(sugestao.descricao());
			form.setCaracteristicasIdentificadoras(sugestao.caracteristicasIdentificadoras());
			form.setCatalogadoPorIa(sugestao.geradaPorIa());
		}
		if (sugestao.localEncontrado() != null && Texto.vazioParaNulo(form.getLocalEncontrado()) == null) {
			form.setLocalEncontrado(sugestao.localEncontrado());
		}
		model.addAttribute("sugestao", sugestao);
		return formulario(model, form, itemId);
	}

	@PostMapping("/admin/itens")
	String criar(@Valid @ModelAttribute("form") ItemForm form, BindingResult resultado, Model model,
			Principal usuario, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return formulario(model, form, null);
		}
		Item item = itens.cadastrar(form, usuario.getName());
		redirect.addFlashAttribute("sucesso", "Item cadastrado com sucesso.");
		return "redirect:/admin/itens/" + item.getId();
	}

	@GetMapping("/admin/itens/{id}")
	String detalhe(@PathVariable Long id, Model model) {
		model.addAttribute("item", itens.obter(id));
		model.addAttribute("interesses", itens.interesses(id));
		model.addAttribute("historico", itens.historico(id));
		return "admin/item";
	}

	@GetMapping("/admin/itens/{id}/editar")
	String editar(@PathVariable Long id, Model model) {
		return formulario(model, ItemForm.de(itens.obter(id)), id);
	}

	@PostMapping("/admin/itens/{id}")
	String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") ItemForm form, BindingResult resultado,
			Model model, Principal usuario, RedirectAttributes redirect) {
		if (resultado.hasErrors()) {
			return formulario(model, form, id);
		}
		itens.atualizar(id, form, usuario.getName());
		redirect.addFlashAttribute("sucesso", "Item atualizado.");
		return "redirect:/admin/itens/" + id;
	}

	@PostMapping("/admin/itens/{id}/devolver")
	String devolver(@PathVariable Long id, @RequestParam(required = false) String retiradoPor,
			@RequestParam(required = false) String observacao, Principal usuario, RedirectAttributes redirect) {
		itens.devolver(id, retiradoPor, observacao, usuario.getName());
		redirect.addFlashAttribute("sucesso", "Item marcado como devolvido.");
		return "redirect:/admin/itens/" + id;
	}

	@PostMapping("/admin/itens/{id}/liberar")
	String liberar(@PathVariable Long id, Principal usuario, RedirectAttributes redirect) {
		itens.liberar(id, usuario.getName());
		redirect.addFlashAttribute("sucesso", "Item liberado: os pedidos foram encerrados e os alunos avisados.");
		return "redirect:/admin/itens/" + id;
	}

	@GetMapping("/admin/emails")
	String emails(Model model) {
		model.addAttribute("emails", notificacoes.recentes());
		model.addAttribute("modo", notificacoes.modo());
		return "admin/emails";
	}

	/** Pré-visualização de foto ainda não vinculada a um item (cadastro em duas etapas). */
	@GetMapping("/admin/fotos/{nome}")
	ResponseEntity<Resource> previaFoto(@PathVariable String nome) {
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(fotos.tipoDe(nome)))
				.cacheControl(CacheControl.noCache().cachePrivate())
				.body(new FileSystemResource(fotos.caminho(nome)));
	}

	private String formulario(Model model, ItemForm form, Long itemId) {
		model.addAttribute("form", form);
		model.addAttribute("itemId", itemId);
		model.addAttribute("categorias", Categoria.values());
		model.addAttribute("provedorIa", catalogacao.provedorAtivo());
		return "admin/item-form";
	}

	/** Usa a foto enviada agora (guardando-a) ou a já enviada numa etapa anterior. */
	private ImagemEntrada obterImagem(ItemForm form) {
		try {
			if (form.arquivoEnviado() != null) {
				ImagemEntrada imagem = fotos.validar(form.arquivoEnviado());
				form.setFotoArquivo(fotos.salvar(imagem));
				return imagem;
			}
			String nome = Texto.vazioParaNulo(form.getFotoArquivo());
			if (nome == null) {
				return null;
			}
			return new ImagemEntrada(Files.readAllBytes(fotos.caminho(nome)), fotos.tipoDe(nome));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
