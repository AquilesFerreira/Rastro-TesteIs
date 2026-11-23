package br.com.rastro.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numeroBrinco;

    @Column(nullable = false, length = 1)
    private String sexo;

    @Column(nullable = false, length = 1)
    private String castrado;

    @Column(nullable = false, length = 100)
    private String raca;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lote_compra_id", nullable = false)
    private LoteCompra loteCompra;

    @ManyToOne(optional = true)
    @JoinColumn(name = "lote_venda_id", nullable = true)
    private LoteVenda loteVenda;

    // Situação do animal: ATIVO, VENDIDO ou BAIXA.
    // Guardado como texto no banco (coluna "status").
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private StatusAnimal status = StatusAnimal.ATIVO;

    // Preenchidos só quando o animal recebe baixa
    @Column(length = 20)
    private String motivoBaixa;

    private LocalDate dataBaixa;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumeroBrinco() { return numeroBrinco; }
    public void setNumeroBrinco(Integer numeroBrinco) { this.numeroBrinco = numeroBrinco; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getCastrado() { return castrado; }
    public void setCastrado(String castrado) { this.castrado = castrado; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public LoteCompra getLoteCompra() { return loteCompra; }
    public void setLoteCompra(LoteCompra loteCompra) { this.loteCompra = loteCompra; }

    public LoteVenda getLoteVenda() { return loteVenda; }
    public void setLoteVenda(LoteVenda loteVenda) { this.loteVenda = loteVenda; }

    // Registros antigos (de antes desta coluna existir) vêm com status nulo: tratamos como ATIVO
    public StatusAnimal getStatus() { return status != null ? status : StatusAnimal.ATIVO; }
    public void setStatus(StatusAnimal status) { this.status = status; }

    public String getMotivoBaixa() { return motivoBaixa; }
    public void setMotivoBaixa(String motivoBaixa) { this.motivoBaixa = motivoBaixa; }

    public LocalDate getDataBaixa() { return dataBaixa; }
    public void setDataBaixa(LocalDate dataBaixa) { this.dataBaixa = dataBaixa; }
}
