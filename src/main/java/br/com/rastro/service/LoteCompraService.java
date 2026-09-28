package br.com.rastro.service;

import br.com.rastro.dto.lotecompra.LoteCompraRequestDTO;
import br.com.rastro.entity.LoteCompra;
import br.com.rastro.exception.RecursoNaoEncontradoException;
import br.com.rastro.exception.RegraNegocioException;
import br.com.rastro.repository.AnimalRepository;
import br.com.rastro.repository.LoteCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoteCompraService {

    @Autowired
    private LoteCompraRepository repository;

    @Autowired
    private AnimalRepository animalRepository;

    public List<LoteCompra> findAll() {
        return repository.findAll();
    }

    public LoteCompra findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lote de compra com id " + id + " não encontrado"));
    }

    public LoteCompra save(LoteCompraRequestDTO dto) {
        LoteCompra lote = new LoteCompra();
        preencherDados(lote, dto);
        return repository.save(lote);
    }

    public LoteCompra update(Long id, LoteCompraRequestDTO dto) {
        LoteCompra lote = findById(id);
        preencherDados(lote, dto);
        return repository.save(lote);
    }

    public void delete(Long id) {
        LoteCompra lote = findById(id);
        if (animalRepository.existsByLoteCompraId(id)) {
            throw new RegraNegocioException(
                    "Não é possível excluir o lote de compra " + id + ": existem animais vinculados a ele");
        }
        repository.delete(lote);
    }

    private void preencherDados(LoteCompra lote, LoteCompraRequestDTO dto) {
        if (dto.quantidade() <= 0) {
            throw new RegraNegocioException("Quantidade deve ser maior que zero");
        }
        if (dto.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor total deve ser maior que zero");
        }
        lote.setData(dto.data());
        lote.setFornecedor(dto.fornecedor());
        lote.setQuantidade(dto.quantidade());
        lote.setPesoTotal(dto.pesoTotal());
        lote.setValorTotal(dto.valorTotal());
    }
}