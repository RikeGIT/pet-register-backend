package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.model.Solicitacao;
import com.ducktecnology.pet_register.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByUsuario(Usuario usuario);

}