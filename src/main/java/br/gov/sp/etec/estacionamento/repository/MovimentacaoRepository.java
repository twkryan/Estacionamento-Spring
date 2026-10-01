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
            where (:placa is null or locate(:placa,
                function('replace', function('replace', upper(v.placa), ' ', ''), '-', '')) > 0)
              and (:inicio is null or v.horaEntrada >= :inicio)
              and (:fimExclusivo is null or v.horaEntrada < :fimExclusivo)
            order by v.horaEntrada desc, v.id desc
            """)
    List<VeiculoEntity> pesquisar(
            @Param("placa") String placa,
            @Param("inicio") LocalDateTime inicio,
            @Param("fimExclusivo") LocalDateTime fimExclusivo
    );
}
