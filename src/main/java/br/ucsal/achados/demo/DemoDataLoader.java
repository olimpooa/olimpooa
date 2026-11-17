package br.ucsal.achados.demo;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.ucsal.achados.domain.Categoria;
import br.ucsal.achados.domain.Item;
import br.ucsal.achados.repository.ItemRepository;
import br.ucsal.achados.service.InteresseForm;
import br.ucsal.achados.service.InteresseService;
import br.ucsal.achados.service.ItemForm;
import br.ucsal.achados.service.ItemService;

/** Itens e alunos 100% fictícios para demonstração; só roda com o banco vazio. */
@Component
@ConditionalOnProperty(name = "achados.demo.carregar", havingValue = "true", matchIfMissing = true)
class DemoDataLoader implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);
	private static final String AUTOR = "demo";

	private final ItemRepository repositorio;
	private final ItemService itens;
	private final InteresseService interesses;

	DemoDataLoader(ItemRepository repositorio, ItemService itens, InteresseService interesses) {
		this.repositorio = repositorio;
		this.itens = itens;
		this.interesses = interesses;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (repositorio.count() > 0) {
			return;
		}
		Item celular = cadastrar("Smartphone preto", Categoria.CELULAR, "preto", "Capinha transparente; tela de bloqueio com foto de praia.",
				"Cantina", 2, "Capinha transparente com um cartão de ônibus atrás");
		cadastrar("Carteira marrom", Categoria.CARTEIRA_DOCUMENTOS, "marrom", "Carteira de couro com documentos.",
				"Biblioteca – recepção", 6, "Contém cartões e um documento de identificação");
		Item fone = cadastrar("Fone de ouvido branco", Categoria.ELETRONICO, "branco", "Fone sem fio com estojo de carregamento.",
				"Laboratório de Informática 3", 1, "Estojo com adesivo de estrela");
		cadastrar("Garrafa térmica azul", Categoria.GARRAFA_MARMITA, "azul", "Garrafa térmica de 500 ml com tampa de rosca.",
				"Biblioteca – 2º andar", 3, "Adesivo de gato na base");
		cadastrar("Mochila preta com chaveiro", Categoria.MOCHILA_BOLSA, "preto", "Mochila média com chaveiro de pelúcia.",
				"Bloco A – corredor", 5, "Chaveiro de pelúcia em formato de raposa");
		cadastrar("Casaco cinza", Categoria.VESTUARIO, "cinza", "Casaco de moletom com capuz, tamanho M.",
				"Auditório", 10, "Etiqueta com iniciais escritas na gola");
		cadastrar("Calculadora científica", Categoria.ELETRONICO, "preto", "Calculadora científica com tampa deslizante.",
				"Sala B-204", 12, "Nome riscado na parte de trás");
		cadastrar("Chaves com chaveiro vermelho", Categoria.CHAVES, "vermelho", "Molho com três chaves e chaveiro de borracha.",
				"Estacionamento", 20, "Uma das chaves tem fita azul");
		cadastrar("Livro de Cálculo", Categoria.MATERIAL_ESCOLAR, "azul", "Livro de Cálculo I com capa azul.",
				"Sala C-101", 40, "Anotações a lápis nas primeiras páginas");
		cadastrar("Óculos de grau", Categoria.ACESSORIO, "preto", "Óculos com armação preta e estojo cinza.",
				"Bloco C", 4, "Estojo com a lente esquerda trincada");
		Item guardaChuva = cadastrar("Guarda-chuva preto", Categoria.ACESSORIO, "preto", "Guarda-chuva automático.",
				"Portaria", 15, "Cabo com fita adesiva amarela");

		interesses.registrar(celular.getId(), aluno("Aluno Exemplo Um", "aluno.exemplo1@ucsal.edu.br", "100000001", "Perdi na cantina ontem."));
		interesses.registrar(celular.getId(), aluno("Aluno Exemplo Dois", "aluno.exemplo2@ucsal.edu.br", "100000002", "Acho que é o meu."));
		interesses.registrar(fone.getId(), aluno("Aluno Exemplo Três", "aluno.exemplo3@ucsal.edu.br", "100000003", null));
		itens.devolver(guardaChuva.getId(), "Aluno Exemplo Quatro", "Conferido presencialmente.", AUTOR);
		log.info("Dados de demonstração carregados ({} itens).", repositorio.count());
	}

	private Item cadastrar(String titulo, Categoria categoria, String cor, String descricao, String local, int diasAtras,
			String caracteristicas) {
		ItemForm form = new ItemForm();
		form.setTitulo(titulo);
		form.setCategoria(categoria);
		form.setCor(cor);
		form.setDescricao(descricao);
		form.setLocalEncontrado(local);
		form.setDataEncontrado(LocalDate.now().minusDays(diasAtras));
		form.setCaracteristicasIdentificadoras(caracteristicas);
		return itens.cadastrar(form, AUTOR);
	}

	private static InteresseForm aluno(String nome, String email, String matricula, String mensagem) {
		InteresseForm form = new InteresseForm();
		form.setNome(nome);
		form.setEmail(email);
		form.setMatricula(matricula);
		form.setMensagem(mensagem);
		return form;
	}
}
