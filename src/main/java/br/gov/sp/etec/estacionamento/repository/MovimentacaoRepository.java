package br.gov.sp.etec.estacionamento.repository;

import br.gov.sp.etec.estacionamento.entity.VeiculoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoRepository extends Repository<VeiculoEntity, Long> {
    @Query("""
            select v from tb_veiculo v
            where (:inicio is null or v.horaEntrada >= :inicio)
              and (:fimExclusivo is null or v.horaEntrada < :fimExclusivo)
            order by v.horaEntrada desc, v.id desc
            """)
    List<VeiculoEntity> pesquisar(
            @Param("inicio") LocalDateTime inicio,
            @Param("fimExclusivo") LocalDateTime fimExclusivo
    );
}
