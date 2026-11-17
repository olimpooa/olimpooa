package br.ucsal.achados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.achados.domain.HistoricoStatus;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {

	List<HistoricoStatus> findByItemIdOrderByEmDescIdDesc(Long itemId);
}
