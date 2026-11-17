
package br.com.fiap.conectasocial.service;

import br.com.fiap.conectasocial.model.CampanhaInteresse;
import br.com.fiap.conectasocial.repository.CampanhaInteresseRepository;
import br.com.fiap.conectasocial.repository.CampanhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CampanhaInteresseService {

    private final CampanhaInteresseRepository repository;
    private final CampanhaRepository campanhaRepository;

    public CampanhaInteresseService(
            CampanhaInteresseRepository repository,
            CampanhaRepository campanhaRepository) {
        this.repository = repository;
        this.campanhaRepository = campanhaRepository;
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
                    "Somente voluntários e beneficiários podem demonstrar interesse."
            );
        }

        boolean campanhaAtiva = campanhaRepository.listarTodas()
                .stream()
                .anyMatch(c -> c.getId().equals(campanhaId)
                        && "ATIVA".equals(c.getStatus()));

        if (!campanhaAtiva) {
            throw new IllegalArgumentException(
                    "Esta campanha não está disponível para receber interesses."
            );
        }

        repository.inserir(campanhaId, usuarioId, tipoInteresse);
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
