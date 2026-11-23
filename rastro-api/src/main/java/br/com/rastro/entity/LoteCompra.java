package br.com.rastro.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "lote_compra")
public class LoteCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 100)
    private String fornecedor;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoTotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPesoTotal() { return pesoTotal; }
    public void setPesoTotal(BigDecimal pesoTotal) { this.pesoTotal = pesoTotal; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
