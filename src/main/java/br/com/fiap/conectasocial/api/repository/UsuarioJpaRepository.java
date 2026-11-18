package br.com.fiap.conectasocial.api.repository;

import br.com.fiap.conectasocial.api.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
}
