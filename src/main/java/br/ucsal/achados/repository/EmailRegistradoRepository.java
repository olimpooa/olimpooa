package br.ucsal.achados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.achados.domain.EmailRegistrado;

public interface EmailRegistradoRepository extends JpaRepository<EmailRegistrado, Long> {

	List<EmailRegistrado> findTop50ByOrderByRegistradoEmDescIdDesc();
}
