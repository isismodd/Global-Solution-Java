package br.com.fiap.conectasocial.api.service;

import br.com.fiap.conectasocial.api.dto.InteresseResponse;
import br.com.fiap.conectasocial.api.dto.PageResponse;
import br.com.fiap.conectasocial.api.entity.CampanhaEntity;
import br.com.fiap.conectasocial.api.entity.InteresseCampanhaEntity;
import br.com.fiap.conectasocial.api.entity.OngEntity;
import br.com.fiap.conectasocial.api.entity.StatusCampanha;
import br.com.fiap.conectasocial.api.entity.TipoInteresse;
import br.com.fiap.conectasocial.api.exception.ConflitoException;
import br.com.fiap.conectasocial.api.exception.NaoEncontradoException;
import br.com.fiap.conectasocial.api.exception.ProibidoException;
import br.com.fiap.conectasocial.api.exception.RegraNegocioException;
import br.com.fiap.conectasocial.api.repository.CampanhaJpaRepository;
import br.com.fiap.conectasocial.api.repository.InteresseCampanhaJpaRepository;
import br.com.fiap.conectasocial.api.repository.OngJpaRepository;
import br.com.fiap.conectasocial.api.repository.UsuarioJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteresseApiService {

    private final InteresseCampanhaJpaRepository interesses;
    private final CampanhaJpaRepository campanhas;
    private final OngJpaRepository ongs;
    private final UsuarioJpaRepository usuarios;

    public InteresseApiService(InteresseCampanhaJpaRepository interesses,
                               CampanhaJpaRepository campanhas,
                               OngJpaRepository ongs,
                               UsuarioJpaRepository usuarios) {
        this.interesses = interesses;
        this.campanhas = campanhas;
        this.ongs = ongs;
        this.usuarios = usuarios;
    }

    @Transactional
    public InteresseResponse registrar(Long campanhaId, Long usuarioId, String perfil) {
        TipoInteresse tipo = switch (perfil == null ? "" : perfil) {
            case "VOLUNTARIO" -> TipoInteresse.VOLUNTARIAR;
            case "BENEFICIARIO" -> TipoInteresse.PEDIDO_AJUDA;
            default -> throw new RegraNegocioException(
                    "Somente voluntários e beneficiários podem demonstrar interesse.");
        };

        CampanhaEntity campanha = campanhas.findById(campanhaId)
                .orElseThrow(() -> new NaoEncontradoException("Campanha não encontrada: " + campanhaId));

        if (campanha.getStatus() != StatusCampanha.ATIVA) {
            throw new RegraNegocioException("Esta campanha não está disponível para receber interesses.");
        }
        if (interesses.existsByCampanhaIdAndUsuarioId(campanhaId, usuarioId)) {
            throw new ConflitoException("Você já demonstrou interesse nesta campanha.");
        }

        InteresseCampanhaEntity salvo = interesses.save(
                new InteresseCampanhaEntity(campanha, usuarios.getReferenceById(usuarioId), tipo));

        return InteresseResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public PageResponse<InteresseResponse> listarDaCampanha(Long campanhaId, Long usuarioId, Pageable pageable) {
        OngEntity ong = ongs.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ProibidoException("Este usuário não está vinculado a uma ONG."));
        CampanhaEntity campanha = campanhas.findById(campanhaId)
                .orElseThrow(() -> new NaoEncontradoException("Campanha não encontrada: " + campanhaId));
        if (!campanha.getOng().getId().equals(ong.getId())) {
            throw new ProibidoException("Esta campanha não pertence à sua ONG.");
        }
        return PageResponse.de(interesses.findByCampanhaId(campanhaId, pageable).map(InteresseResponse::de));
    }

    @Transactional(readOnly = true)
    public PageResponse<InteresseResponse> listarMeus(Long usuarioId, Pageable pageable) {
        return PageResponse.de(interesses.findByUsuarioId(usuarioId, pageable).map(InteresseResponse::de));
    }
}
