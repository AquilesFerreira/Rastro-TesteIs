package br.com.rastro.repository;

import br.com.rastro.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findByNumeroBrinco(Integer numeroBrinco);
    boolean existsByNumeroBrinco(Integer numeroBrinco);
    boolean existsByNumeroBrincoAndIdNot(Integer numeroBrinco, Long id);
    boolean existsByLoteCompraId(Long loteCompraId);
    boolean existsByLoteVendaId(Long loteVendaId);
}
