package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.model.CodigoConfirmacao;
import com.ducktecnology.pet_register.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoConfirmacaoRepository extends JpaRepository<CodigoConfirmacao, Long> {
    Optional<CodigoConfirmacao> findByUsuario(Usuario usuario);

    void deleteByUsuario(Usuario usuario);
}
