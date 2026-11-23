package br.com.rastro.config;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Roda uma vez a cada vez que a API sobe.
 * Arruma registros criados ANTES da regra nova:
 * - animal sem status: VENDIDO se já tinha lote de venda, senão ATIVO;
 * - quantidade dos lotes recalculada pelo número real de animais.
 * Em banco novo/limpo não faz nada.
 */
@Component
public class AjusteDadosAntigos implements ApplicationRunner {

    @Autowired
    private EntityManager em;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // SQL nativo (MySQL), direto nas tabelas
        em.createNativeQuery("UPDATE animal SET status = 'VENDIDO' "
                + "WHERE status IS NULL AND lote_venda_id IS NOT NULL").executeUpdate();
        em.createNativeQuery("UPDATE animal SET status = 'ATIVO' WHERE status IS NULL").executeUpdate();
        em.createNativeQuery("UPDATE lote_compra l SET quantidade = "
                + "(SELECT COUNT(*) FROM animal a WHERE a.lote_compra_id = l.id)").executeUpdate();
        em.createNativeQuery("UPDATE lote_venda l SET quantidade = "
                + "(SELECT COUNT(*) FROM animal a WHERE a.lote_venda_id = l.id)").executeUpdate();
    }
}
