package br.ucsal.achados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.ucsal.achados.domain.Item;
import br.ucsal.achados.domain.ItemStatus;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

	List<Item> findAllByOrderByCriadoEmDesc();

	List<Item> findByStatusInOrderByDataEncontradoDesc(List<ItemStatus> status);

	long countByStatus(ItemStatus status);
}
