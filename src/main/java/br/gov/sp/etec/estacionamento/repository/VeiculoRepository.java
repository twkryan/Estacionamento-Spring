package br.gov.sp.etec.estacionamento.repository;

import br.gov.sp.etec.estacionamento.entity.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface VeiculoRepository extends JpaRepository<VeiculoEntity, Long> {
    long countByHoraSaidaIsNull();
    List<VeiculoEntity> findByHoraSaidaIsNullOrderByHoraEntradaAsc();
    List<VeiculoEntity> findByHoraSaidaIsNotNullOrderByHoraSaidaDesc();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update tb_veiculo v set v.horaSaida = :horaSaida where v.id = :id and v.horaSaida is null")
    int registrarSaida(@Param("id") Long id, @Param("horaSaida") LocalDateTime horaSaida);
}
