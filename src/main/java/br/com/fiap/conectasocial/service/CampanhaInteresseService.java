
package br.com.fiap.conectasocial.service;

import br.com.fiap.conectasocial.api.entity.TipoInteresse;
import br.com.fiap.conectasocial.messaging.InteresseRegistradoEvent;
import br.com.fiap.conectasocial.model.CampanhaInteresse;
import br.com.fiap.conectasocial.repository.CampanhaInteresseRepository;
import br.com.fiap.conectasocial.repository.CampanhaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public class CampanhaInteresseService {

    private final CampanhaInteresseRepository repository;
    private final CampanhaRepository campanhaRepository;
    private final ApplicationEventPublisher eventos;

    public CampanhaInteresseService(
            CampanhaInteresseRepository repository,
            CampanhaRepository campanhaRepository,
            ApplicationEventPublisher eventos) {
        this.repository = repository;
        this.campanhaRepository = campanhaRepository;
        this.eventos = eventos;
    }

    @Transactional
    public void registrarInteresse(Long campanhaId, Long usuarioId, String perfil) {
        String tipoInteresse;

        if ("VOLUNTARIO".equals(perfil)) {
            tipoInteresse = "VOLUNTARIAR";
        } else if ("BENEFICIARIO".equals(perfil)) {
            tipoInteresse = "PEDIDO_AJUDA";
        } else {
            throw new IllegalArgumentException(
                    "Somente voluntários e beneficiários podem demonstrar interesse.");
        }

        boolean campanhaAtiva = campanhaRepository.listarTodas().stream()
                .anyMatch(c -> c.getId().equals(campanhaId)
                        && "ATIVA".equals(c.getStatus()));

        if (!campanhaAtiva) {
            throw new IllegalArgumentException(
                    "Esta campanha não está disponível para receber interesses.");
        }

        // Registra o interesse no banco
        repository.inserir(campanhaId, usuarioId, tipoInteresse);

        // Recupera os dados necessários para a notificação
        Map<String, Object> dados =
                repository.buscarDadosNotificacao(campanhaId, usuarioId);

        // Publica o evento para o fluxo existente do RabbitMQ
        eventos.publishEvent(new InteresseRegistradoEvent(
                ((Number) dados.get("INTERESSE_ID")).longValue(),
                ((Number) dados.get("CAMPANHA_ID")).longValue(),
                (String) dados.get("TITULO_CAMPANHA"),
                (String) dados.get("NOME_ONG"),
                (String) dados.get("EMAIL_ONG"),
                ((Number) dados.get("USUARIO_ID")).longValue(),
                (String) dados.get("NOME_USUARIO"),
                TipoInteresse.valueOf((String) dados.get("TIPO_INTERESSE")),
                ((Timestamp) dados.get("DATA_INTERESSE")).toLocalDateTime()
        ));
    }

    public boolean jaDemonstrouInteresse(Long campanhaId, Long usuarioId) {
        return repository.jaDemonstrouInteresse(campanhaId, usuarioId);
    }

    public List<CampanhaInteresse> listarVoluntarios(Long ongId) {
        return repository.listarPorOng(ongId, "VOLUNTARIAR");
    }

    public List<CampanhaInteresse> listarPedidosAjuda(Long ongId) {
        return repository.listarPorOng(ongId, "PEDIDO_AJUDA");
    }
}
