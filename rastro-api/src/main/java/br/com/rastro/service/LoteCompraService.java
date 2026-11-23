package br.com.rastro.service;

import br.com.rastro.dto.animal.AnimalRequestDTO;
import br.com.rastro.dto.lotecompra.LoteCompraRequestDTO;
import br.com.rastro.entity.Animal;
import br.com.rastro.entity.LoteCompra;
import br.com.rastro.entity.StatusAnimal;
import br.com.rastro.exception.RecursoNaoEncontradoException;
import br.com.rastro.exception.RegraNegocioException;
import br.com.rastro.repository.AnimalRepository;
import br.com.rastro.repository.LoteCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Regra principal: o lote de compra é a ENTRADA dos animais.
 * - Ao criar o lote, os animais são criados junto (mesma transação).
 * - A quantidade é sempre o número de animais do lote (nunca digitada).
 */
@Service
public class LoteCompraService {

    @Autowired
    private LoteCompraRepository repository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AnimalService animalService;

    public List<LoteCompra> findAll() {
        return repository.findAll();
    }

    public LoteCompra findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lote de compra com id " + id + " não encontrado"));
    }

    public List<Animal> animaisDoLote(Long id) {
        findById(id);
        return animalRepository.findByLoteCompraId(id);
    }

    public long contar(Long loteId, StatusAnimal status) {
        return animalRepository.countByLoteCompraIdAndStatus(loteId, status);
    }

    /** POST: cria o lote e todos os animais. Se um brinco falhar, nada é salvo (@Transactional). */
    @Transactional
    public LoteCompra save(LoteCompraRequestDTO dto) {
        if (dto.animais() == null || dto.animais().isEmpty()) {
            throw new RegraNegocioException("Informe pelo menos um animal no lote");
        }
        validarValores(dto);
        animalService.validarBrincosNovos(dto.animais());

        LoteCompra lote = new LoteCompra();
        preencherCabecalho(lote, dto);
        lote.setQuantidade(dto.animais().size());
        repository.save(lote);

        for (AnimalRequestDTO a : dto.animais()) {
            animalService.criarNoLote(lote, a);
        }
        return lote;
    }

    /** PUT: altera só o cabeçalho. A quantidade continua sendo a dos animais. */
    @Transactional
    public LoteCompra update(Long id, LoteCompraRequestDTO dto) {
        LoteCompra lote = findById(id);
        validarValores(dto);
        preencherCabecalho(lote, dto);
        return repository.save(lote);
    }

    /** Adiciona um animal esquecido no cadastro. Quantidade sobe 1. */
    @Transactional
    public Animal adicionarAnimal(Long id, AnimalRequestDTO dto) {
        LoteCompra lote = findById(id);
        animalService.validarBrincosNovos(List.of(dto));
        Animal animal = animalService.criarNoLote(lote, dto);
        atualizarQuantidade(lote);
        return animal;
    }

    /**
     * Remove um animal cadastrado por engano (ex.: brinco digitado errado).
     * Só se ele ainda estiver ATIVO e o lote não ficar vazio.
     */
    @Transactional
    public void removerAnimal(Long id, Long animalId) {
        LoteCompra lote = findById(id);
        Animal animal = animalService.findById(animalId);
        if (!animal.getLoteCompra().getId().equals(id)) {
            throw new RegraNegocioException("O animal " + animal.getNumeroBrinco() + " não pertence a este lote");
        }
        if (animal.getStatus() != StatusAnimal.ATIVO) {
            throw new RegraNegocioException("O brinco " + animal.getNumeroBrinco()
                    + " já foi vendido ou recebeu baixa e não pode ser removido");
        }
        if (animalRepository.countByLoteCompraId(id) <= 1) {
            throw new RegraNegocioException("O lote precisa ter pelo menos um animal. Para desfazer a compra, exclua o lote");
        }
        animalRepository.delete(animal);
        animalRepository.flush();
        atualizarQuantidade(lote);
    }

    /**
     * Exclui a compra inteira (lançamento errado): apaga o lote e seus animais.
     * Bloqueado se algum animal já foi vendido ou recebeu baixa.
     */
    @Transactional
    public void delete(Long id) {
        LoteCompra lote = findById(id);
        if (animalRepository.existsByLoteCompraIdAndStatusNot(id, StatusAnimal.ATIVO)) {
            throw new RegraNegocioException(
                    "Não é possível excluir o lote: há animais dele vendidos ou com baixa");
        }
        animalRepository.deleteAll(animalRepository.findByLoteCompraId(id));
        repository.delete(lote);
    }

    private void atualizarQuantidade(LoteCompra lote) {
        lote.setQuantidade((int) animalRepository.countByLoteCompraId(lote.getId()));
        repository.save(lote);
    }

    private void validarValores(LoteCompraRequestDTO dto) {
        if (dto.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor total deve ser maior que zero");
        }
    }

    private void preencherCabecalho(LoteCompra lote, LoteCompraRequestDTO dto) {
        lote.setData(dto.data());
        lote.setFornecedor(dto.fornecedor());
        lote.setPesoTotal(dto.pesoTotal());
        lote.setValorTotal(dto.valorTotal());
    }
}
