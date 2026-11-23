package br.com.rastro.service;

import br.com.rastro.dto.lotevenda.LoteVendaRequestDTO;
import br.com.rastro.entity.Animal;
import br.com.rastro.entity.LoteVenda;
import br.com.rastro.entity.StatusAnimal;
import br.com.rastro.exception.RecursoNaoEncontradoException;
import br.com.rastro.exception.RegraNegocioException;
import br.com.rastro.repository.AnimalRepository;
import br.com.rastro.repository.LoteVendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Regra principal: o lote de venda é a SAÍDA dos animais.
 * - O lote é montado selecionando animais ATIVOS.
 * - Os animais selecionados viram VENDIDO e apontam para o lote.
 * - Excluir a venda devolve os animais para ATIVO (estorno).
 */
@Service
public class LoteVendaService {

    @Autowired
    private LoteVendaRepository repository;

    @Autowired
    private AnimalRepository animalRepository;

    public List<LoteVenda> findAll() {
        return repository.findAll();
    }

    public LoteVenda findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lote de venda com id " + id + " não encontrado"));
    }

    public List<Long> idsDosAnimais(Long loteId) {
        return animalRepository.findByLoteVendaId(loteId).stream().map(Animal::getId).toList();
    }

    @Transactional
    public LoteVenda save(LoteVendaRequestDTO dto) {
        validarValores(dto);
        LoteVenda lote = new LoteVenda();
        preencherCabecalho(lote, dto);
        lote.setQuantidade(0);
        repository.save(lote);
        vincularAnimais(lote, dto.animaisIds());
        return lote;
    }

    /** PUT: altera o cabeçalho e a lista de animais (quem saiu volta a ATIVO). */
    @Transactional
    public LoteVenda update(Long id, LoteVendaRequestDTO dto) {
        LoteVenda lote = findById(id);
        validarValores(dto);
        preencherCabecalho(lote, dto);

        Set<Long> novos = new HashSet<>(dto.animaisIds());
        for (Animal a : animalRepository.findByLoteVendaId(id)) {
            if (!novos.contains(a.getId())) {
                estornar(a);
            }
        }
        vincularAnimais(lote, dto.animaisIds());
        return lote;
    }

    /** Exclui a venda e devolve os animais para ATIVO. */
    @Transactional
    public void delete(Long id) {
        LoteVenda lote = findById(id);
        animalRepository.findByLoteVendaId(id).forEach(this::estornar);
        animalRepository.flush();
        repository.delete(lote);
    }

    private void vincularAnimais(LoteVenda lote, List<Long> ids) {
        Set<Long> unicos = new HashSet<>(ids);
        for (Long animalId : unicos) {
            Animal a = animalRepository.findById(animalId)
                    .orElseThrow(() -> new RegraNegocioException("Animal com id " + animalId + " não existe"));
            boolean jaEstaNesteLote = a.getLoteVenda() != null && a.getLoteVenda().getId().equals(lote.getId());
            if (!jaEstaNesteLote && a.getStatus() != StatusAnimal.ATIVO) {
                throw new RegraNegocioException("O brinco " + a.getNumeroBrinco() + " não está ativo ("
                        + a.getStatus() + ") e não pode ser vendido");
            }
            a.setLoteVenda(lote);
            a.setStatus(StatusAnimal.VENDIDO);
            animalRepository.save(a);
        }
        lote.setQuantidade(unicos.size());
        repository.save(lote);
    }

    private void estornar(Animal a) {
        a.setLoteVenda(null);
        a.setStatus(StatusAnimal.ATIVO);
        animalRepository.save(a);
    }

    private void validarValores(LoteVendaRequestDTO dto) {
        if (dto.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor total deve ser maior que zero");
        }
    }

    private void preencherCabecalho(LoteVenda lote, LoteVendaRequestDTO dto) {
        lote.setData(dto.data());
        lote.setComprador(dto.comprador());
        lote.setPesoTotal(dto.pesoTotal());
        lote.setValorTotal(dto.valorTotal());
    }
}
