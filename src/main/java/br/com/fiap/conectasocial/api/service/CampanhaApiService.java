package br.com.fiap.conectasocial.api.service;

import br.com.fiap.conectasocial.api.dto.CampanhaRequest;
import br.com.fiap.conectasocial.api.dto.CampanhaResponse;
import br.com.fiap.conectasocial.api.dto.PageResponse;
import br.com.fiap.conectasocial.api.entity.CampanhaEntity;
import br.com.fiap.conectasocial.api.entity.OngEntity;
import br.com.fiap.conectasocial.api.entity.StatusCampanha;
import br.com.fiap.conectasocial.api.exception.NaoEncontradoException;
import br.com.fiap.conectasocial.api.exception.ProibidoException;
import br.com.fiap.conectasocial.api.repository.CampanhaJpaRepository;
import br.com.fiap.conectasocial.api.repository.InteresseCampanhaJpaRepository;
import br.com.fiap.conectasocial.api.repository.OngJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampanhaApiService {

    private final CampanhaJpaRepository campanhas;
    private final OngJpaRepository ongs;
    private final InteresseCampanhaJpaRepository interesses;

    public CampanhaApiService(CampanhaJpaRepository campanhas,
                              OngJpaRepository ongs,
                              InteresseCampanhaJpaRepository interesses) {
        this.campanhas = campanhas;
        this.ongs = ongs;
        this.interesses = interesses;
    }

    @Transactional(readOnly = true)
    public PageResponse<CampanhaResponse> listarAtivas(String categoria, Pageable pageable) {
        Page<CampanhaEntity> pagina = (categoria == null || categoria.isBlank())
                ? campanhas.findByStatus(StatusCampanha.ATIVA, pageable)
                : campanhas.findByStatusAndCategoriaIgnoreCase(StatusCampanha.ATIVA, categoria.trim(), pageable);
        return PageResponse.de(pagina.map(CampanhaResponse::de));
    }

    @Transactional(readOnly = true)
    public PageResponse<CampanhaResponse> listarDaOng(Long usuarioId, Pageable pageable) {
        OngEntity ong = buscarOng(usuarioId);
        return PageResponse.de(campanhas.findByOngId(ong.getId(), pageable).map(CampanhaResponse::de));
    }

    @Transactional(readOnly = true)
    public CampanhaResponse buscarPorId(Long id) {
        return CampanhaResponse.de(buscarCampanha(id));
    }

    @Transactional
    public CampanhaResponse criar(CampanhaRequest req, Long usuarioId) {
        OngEntity ong = buscarOng(usuarioId);
        CampanhaEntity campanha = new CampanhaEntity(
                ong,
                req.titulo().trim(),
                req.descricao().trim(),
                req.categoria().trim(),
                req.local().trim());
        if (req.status() != null) {
            campanha.setStatus(req.status());
        }
        return CampanhaResponse.de(campanhas.save(campanha));
    }

    @Transactional
    public CampanhaResponse atualizar(Long id, CampanhaRequest req, Long usuarioId) {
        CampanhaEntity campanha = buscarCampanhaDaOng(id, usuarioId);
        campanha.setTitulo(req.titulo().trim());
        campanha.setDescricao(req.descricao().trim());
        campanha.setCategoria(req.categoria().trim());
        campanha.setLocalizacao(req.local().trim());
        if (req.status() != null) {
            campanha.setStatus(req.status());
        }
        return CampanhaResponse.de(campanhas.save(campanha));
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        CampanhaEntity campanha = buscarCampanhaDaOng(id, usuarioId);
        interesses.deleteByCampanhaId(campanha.getId());
        campanhas.delete(campanha);
    }

    // ---------- helpers ----------

    private OngEntity buscarOng(Long usuarioId) {
        return ongs.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ProibidoException("Este usuário não está vinculado a uma ONG."));
    }

    private CampanhaEntity buscarCampanha(Long id) {
        return campanhas.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Campanha não encontrada: " + id));
    }

    private CampanhaEntity buscarCampanhaDaOng(Long campanhaId, Long usuarioId) {
        OngEntity ong = buscarOng(usuarioId);
        CampanhaEntity campanha = buscarCampanha(campanhaId);
        if (!campanha.getOng().getId().equals(ong.getId())) {
            throw new ProibidoException("Esta campanha não pertence à sua ONG.");
        }
        return campanha;
    }
}
