package br.com.fiap.conectasocial.api.repository;

import br.com.fiap.conectasocial.api.entity.CampanhaEntity;
import br.com.fiap.conectasocial.api.entity.StatusCampanha;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampanhaJpaRepository extends JpaRepository<CampanhaEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"ong", "ong.usuario"})
    Optional<CampanhaEntity> findById(Long id);

    @EntityGraph(attributePaths = {"ong", "ong.usuario"})
    Page<CampanhaEntity> findByStatus(StatusCampanha status, Pageable pageable);

    @EntityGraph(attributePaths = {"ong", "ong.usuario"})
    Page<CampanhaEntity> findByStatusAndCategoriaIgnoreCase(StatusCampanha status,
                                                            String categoria,
                                                            Pageable pageable);

    @EntityGraph(attributePaths = {"ong", "ong.usuario"})
    Page<CampanhaEntity> findByOngId(Long ongId, Pageable pageable);
}
