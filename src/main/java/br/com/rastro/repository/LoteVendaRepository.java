package br.com.rastro.repository;

import br.com.rastro.entity.LoteVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteVendaRepository extends JpaRepository<LoteVenda, Long> {
}
