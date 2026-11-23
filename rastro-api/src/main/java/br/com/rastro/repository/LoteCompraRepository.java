package br.com.rastro.repository;

import br.com.rastro.entity.LoteCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteCompraRepository extends JpaRepository<LoteCompra, Long> {
}
