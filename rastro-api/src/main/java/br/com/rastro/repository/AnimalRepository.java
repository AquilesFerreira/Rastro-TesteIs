package br.com.rastro.repository;

import br.com.rastro.entity.Animal;
import br.com.rastro.entity.StatusAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findByNumeroBrinco(Integer numeroBrinco);
    boolean existsByNumeroBrinco(Integer numeroBrinco);
    boolean existsByNumeroBrincoAndIdNot(Integer numeroBrinco, Long id);

    // Brincos (da lista) que já existem no banco -> SELECT ... WHERE numero_brinco IN (...)
    List<Animal> findByNumeroBrincoIn(Collection<Integer> numerosBrinco);

    List<Animal> findByLoteCompraId(Long loteCompraId);
    List<Animal> findByLoteVendaId(Long loteVendaId);

    long countByLoteCompraId(Long loteCompraId);
    long countByLoteCompraIdAndStatus(Long loteCompraId, StatusAnimal status);
    boolean existsByLoteCompraIdAndStatusNot(Long loteCompraId, StatusAnimal status);
}
