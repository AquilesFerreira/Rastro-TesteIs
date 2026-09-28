package br.com.rastro.service;

import br.com.rastro.dto.animal.AnimalRequestDTO;
import br.com.rastro.entity.Animal;
import br.com.rastro.entity.LoteCompra;
import br.com.rastro.entity.LoteVenda;
import br.com.rastro.exception.RecursoNaoEncontradoException;
import br.com.rastro.exception.RegraNegocioException;
import br.com.rastro.repository.AnimalRepository;
import br.com.rastro.repository.LoteCompraRepository;
import br.com.rastro.repository.LoteVendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository repository;

    @Autowired
    private LoteCompraRepository loteCompraRepository;

    @Autowired
    private LoteVendaRepository loteVendaRepository;

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

    public Animal save(AnimalRequestDTO dto) {
        validarBrinco(dto.numeroBrinco(), null);
        Animal animal = new Animal();
        preencherDados(animal, dto);
        return repository.save(animal);
    }

    public Animal update(Long id, AnimalRequestDTO dto) {
        Animal animal = findById(id);
        validarBrinco(dto.numeroBrinco(), id);
        preencherDados(animal, dto);
        return repository.save(animal);
    }

    public void delete(Long id) {
        Animal animal = findById(id);
        repository.delete(animal);
    }

    private void validarBrinco(Integer numeroBrinco, Long idAtual) {
        if (numeroBrinco <= 0) {
            throw new RegraNegocioException("Número do brinco deve ser maior que zero");
        }
        boolean duplicado = (idAtual == null)
                ? repository.existsByNumeroBrinco(numeroBrinco)
                : repository.existsByNumeroBrincoAndIdNot(numeroBrinco, idAtual);
        if (duplicado) {
            throw new RegraNegocioException("Já existe um animal com o brinco " + numeroBrinco);
        }
    }

    private void preencherDados(Animal animal, AnimalRequestDTO dto) {
        LoteCompra loteCompra = loteCompraRepository.findById(dto.loteCompraId())
                .orElseThrow(() -> new RegraNegocioException(
                        "Lote de compra com id " + dto.loteCompraId() + " não existe"));

        animal.setNumeroBrinco(dto.numeroBrinco());
        animal.setSexo(dto.sexo());
        animal.setCastrado(dto.castrado());
        animal.setRaca(dto.raca());
        animal.setLoteCompra(loteCompra);

        if (dto.loteVendaId() != null) {
            LoteVenda loteVenda = loteVendaRepository.findById(dto.loteVendaId())
                    .orElseThrow(() -> new RegraNegocioException(
                            "Lote de venda com id " + dto.loteVendaId() + " não existe"));
            animal.setLoteVenda(loteVenda);
        } else {
            animal.setLoteVenda(null);
        }
    }
}

/*
 * TODO (refatoração futura) — Venda de animais pelo lote (Opção C)
 *
 * Situação atual (Opção A):
 *   O vínculo Animal → LoteVenda é feito animal por animal, pelo campo
 *   opcional "loteVendaId" do AnimalRequestDTO (POST/PUT /animais).
 *
 * Evolução proposta:
 *   Permitir que o próprio lote de venda receba os animais vendidos,
 *   refletindo a operação real da pecuária (vende-se o lote inteiro).
 *
 * Passos para implementar:
 *   1. Adicionar "List<Long> animaisIds" ao LoteVendaRequestDTO.
 *   2. No save/update deste service, para cada id:
 *      - validar se o animal existe (RecursoNaoEncontradoException);
 *      - validar se o animal já não pertence a outro lote de venda
 *        (RegraNegocioException);
 *      - vincular com animal.setLoteVenda(lote).
 *   3. No update, desvincular (setLoteVenda(null)) os animais que
 *      saíram da lista.
 *   4. Validar se a "quantidade" do lote bate com o total de animais
 *      informados.
 *   5. Opcional: incluir "animaisIds" no LoteVendaResponseDTO.
 *   6. Avaliar remover "loteVendaId" do AnimalRequestDTO, para que a
 *      venda tenha um único ponto de entrada.
 */