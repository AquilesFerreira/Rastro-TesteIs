package br.com.rastro.service;

import br.com.rastro.dto.lotevenda.LoteVendaRequestDTO;
import br.com.rastro.entity.LoteVenda;
import br.com.rastro.exception.RecursoNaoEncontradoException;
import br.com.rastro.exception.RegraNegocioException;
import br.com.rastro.repository.AnimalRepository;
import br.com.rastro.repository.LoteVendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

    public LoteVenda save(LoteVendaRequestDTO dto) {
        LoteVenda lote = new LoteVenda();
        preencherDados(lote, dto);
        return repository.save(lote);
    }

    public LoteVenda update(Long id, LoteVendaRequestDTO dto) {
        LoteVenda lote = findById(id);
        preencherDados(lote, dto);
        return repository.save(lote);
    }

    public void delete(Long id) {
        LoteVenda lote = findById(id);
        if (animalRepository.existsByLoteVendaId(id)) {
            throw new RegraNegocioException(
                    "Não é possível excluir o lote de venda " + id + ": existem animais vinculados a ele");
        }
        repository.delete(lote);
    }

    private void preencherDados(LoteVenda lote, LoteVendaRequestDTO dto) {
        if (dto.quantidade() <= 0) {
            throw new RegraNegocioException("Quantidade deve ser maior que zero");
        }
        if (dto.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor total deve ser maior que zero");
        }
        lote.setData(dto.data());
        lote.setComprador(dto.comprador());
        lote.setQuantidade(dto.quantidade());
        lote.setPesoTotal(dto.pesoTotal());
        lote.setValorTotal(dto.valorTotal());
    }
}