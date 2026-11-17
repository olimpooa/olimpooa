package br.ucsal.achados.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import br.ucsal.achados.config.SecurityConfig;
import br.ucsal.achados.service.RecursoNaoEncontradoException;
import br.ucsal.achados.service.RegraNegocioException;

@ControllerAdvice
public class GlobalControllerAdvice {

	private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

	@ModelAttribute("logado")
	boolean logado(Authentication autenticacao) {
		return ehResponsavel(autenticacao);
	}

	static boolean ehResponsavel(Authentication autenticacao) {
		return autenticacao != null && autenticacao.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_" + SecurityConfig.PAPEL_RESPONSAVEL));
	}

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	ModelAndView naoEncontrado(RecursoNaoEncontradoException e) {
		return erro(HttpStatus.NOT_FOUND, "Não encontrado", e.getMessage());
	}

	@ExceptionHandler(RegraNegocioException.class)
	ModelAndView regraDeNegocio(RegraNegocioException e) {
		return erro(HttpStatus.BAD_REQUEST, "Não foi possível concluir", e.getMessage());
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	ModelAndView arquivoGrande() {
		return erro(HttpStatus.BAD_REQUEST, "Arquivo muito grande", "A foto deve ter no máximo 5 MB.");
	}

	@ExceptionHandler(Exception.class)
	ModelAndView inesperado(Exception e) throws Exception {
		if (e instanceof AccessDeniedException || e instanceof ErrorResponse) {
			throw e;
		}
		log.error("Erro inesperado", e);
		return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Algo deu errado", "Tente novamente em instantes.");
	}

	private static ModelAndView erro(HttpStatus status, String titulo, String mensagem) {
		ModelAndView mv = new ModelAndView("erro", status);
		mv.addObject("titulo", titulo);
		mv.addObject("mensagem", mensagem);
		return mv;
	}
}
