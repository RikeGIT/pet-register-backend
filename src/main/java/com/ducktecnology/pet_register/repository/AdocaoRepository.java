package com.ducktecnology.pet_register.repository;
import com.ducktecnology.pet_register.domain.model.Adocao;
import com.ducktecnology.pet_register.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdocaoRepository extends JpaRepository<Adocao, Long> {
    List<Adocao> findByInteressado(Usuario interessado);
}
