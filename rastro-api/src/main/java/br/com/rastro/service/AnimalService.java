package br.com.rastro.service;

import br.com.rastro.dto.animal.AnimalRequestDTO;
import br.com.rastro.dto.animal.BaixaRequestDTO;
import br.com.rastro.entity.Animal;
import br.com.rastro.entity.LoteCompra;
import br.com.rastro.entity.StatusAnimal;
import br.com.rastro.exception.RecursoNaoEncontradoException;
import br.com.rastro.exception.RegraNegocioException;
import br.com.rastro.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Regras do animal.
 * O animal NÃO é cadastrado sozinho: ele nasce dentro de um lote de compra
 * (LoteCompraService chama criarNoLote). Aqui ficam a consulta, a edição básica e a baixa.
 */
@Service
public class AnimalService {

    @Autowired
    private AnimalRepository repository;

    public List<Animal> findAll() {
        return repository.findAll();
    }

    public Animal findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Animal com id " + id + " não encontrado"));
    }

    public Animal findByNumeroBrinco(Integer numeroBrinco) {
        return repository.findByNumeroBrinco(numeroBrinco)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Animal com brinco " + numeroBrinco + " não encontrado"));
    }

    /**
     * Edição básica: corrige brinco, sexo, castrado e raça.
     * Não muda lote nem status (isso é feito pelos lotes e pela baixa).
     */
    @Transactional
    public Animal update(Long id, AnimalRequestDTO dto) {
        Animal animal = findById(id);
        if (dto.numeroBrinco() <= 0) {
            throw new RegraNegocioException("Número do brinco deve ser maior que zero");
        }
        if (repository.existsByNumeroBrincoAndIdNot(dto.numeroBrinco(), id)) {
            throw new RegraNegocioException("Já existe animal com o brinco " + dto.numeroBrinco());
        }
        animal.setNumeroBrinco(dto.numeroBrinco());
        animal.setSexo(dto.sexo());
        animal.setCastrado(dto.castrado());
        animal.setRaca(dto.raca());
        return repository.save(animal);
    }

    /** Dá baixa num animal ativo (morte, descarte ou outro motivo). */
    @Transactional
    public Animal darBaixa(Long id, BaixaRequestDTO dto) {
        Animal animal = findById(id);
        if (animal.getStatus() != StatusAnimal.ATIVO) {
            throw new RegraNegocioException("Só animais ativos podem receber baixa. O brinco "
                    + animal.getNumeroBrinco() + " está " + animal.getStatus());
        }
        animal.setStatus(StatusAnimal.BAIXA);
        animal.setMotivoBaixa(dto.motivo());
        animal.setDataBaixa(dto.data() != null ? dto.data() : LocalDate.now());
        return repository.save(animal);
    }

    /**
     * Valida uma lista de brincos novos: maior que zero, sem repetir na própria
     * lista e sem repetir no banco. Usado ao criar o lote e ao adicionar animal.
     */
    public void validarBrincosNovos(List<AnimalRequestDTO> animais) {
        Set<Integer> vistos = new HashSet<>();
        for (AnimalRequestDTO a : animais) {
            if (a.numeroBrinco() <= 0) {
                throw new RegraNegocioException("Número do brinco deve ser maior que zero");
            }
            if (!vistos.add(a.numeroBrinco())) {
                throw new RegraNegocioException("O brinco " + a.numeroBrinco() + " foi informado mais de uma vez");
            }
        }
        List<Animal> existentes = repository.findByNumeroBrincoIn(vistos);
        if (!existentes.isEmpty()) {
            throw new RegraNegocioException("Já existe animal com o brinco " + existentes.get(0).getNumeroBrinco());
        }
    }

    /** Cria um animal ATIVO dentro do lote de compra. */
    public Animal criarNoLote(LoteCompra lote, AnimalRequestDTO dto) {
        Animal animal = new Animal();
        animal.setNumeroBrinco(dto.numeroBrinco());
        animal.setSexo(dto.sexo());
        animal.setCastrado(dto.castrado());
        animal.setRaca(dto.raca());
        animal.setLoteCompra(lote);
        animal.setStatus(StatusAnimal.ATIVO);
        return repository.save(animal);
    }
}
