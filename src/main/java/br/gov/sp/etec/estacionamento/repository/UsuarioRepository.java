package br.gov.sp.etec.estacionamento.repository;

import br.gov.sp.etec.estacionamento.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    UsuarioEntity findByInputEmailCadastro(String inputEmailCadastro);
}
