package br.com.fiap.conectasocial.api.repository;

import br.com.fiap.conectasocial.api.entity.OngEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OngJpaRepository extends JpaRepository<OngEntity, Long> {

    Optional<OngEntity> findByUsuarioId(Long usuarioId);
}
