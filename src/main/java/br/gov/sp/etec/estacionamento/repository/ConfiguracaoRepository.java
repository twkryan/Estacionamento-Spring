package br.gov.sp.etec.estacionamento.repository;
import br.gov.sp.etec.estacionamento.entity.Configuracao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Configuracao c where c.id = 1")
    Configuracao bloquear();
}
