package br.ucsal.achados.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.achados.domain.Interesse;
import br.ucsal.achados.domain.Interesse.Situacao;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {

	Optional<Interesse> findByToken(String token);

	List<Interesse> findByItemIdOrderByCriadoEmAsc(Long itemId);

	List<Interesse> findByItemIdAndSituacao(Long itemId, Situacao situacao);

	boolean existsByItemIdAndMatriculaAndSituacao(Long itemId, String matricula, Situacao situacao);

	long countByItemIdAndSituacao(Long itemId, Situacao situacao);

	long countBySituacao(Situacao situacao);
}
