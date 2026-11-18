package br.com.fiap.conectasocial.api.repository;

import br.com.fiap.conectasocial.api.entity.InteresseCampanhaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InteresseCampanhaJpaRepository extends JpaRepository<InteresseCampanhaEntity, Long> {

    boolean existsByCampanhaIdAndUsuarioId(Long campanhaId, Long usuarioId);

    @EntityGraph(attributePaths = {"campanha", "usuario"})
    Page<InteresseCampanhaEntity> findByCampanhaId(Long campanhaId, Pageable pageable);

    @EntityGraph(attributePaths = {"campanha", "usuario"})
    Page<InteresseCampanhaEntity> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Modifying
    @Query("delete from InteresseCampanhaEntity i where i.campanha.id = :campanhaId")
    void deleteByCampanhaId(@Param("campanhaId") Long campanhaId);
}
