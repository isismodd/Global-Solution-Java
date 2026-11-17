package br.com.fiap.conectasocial.service;

import br.com.fiap.conectasocial.model.CampanhaInteresse;
import br.com.fiap.conectasocial.repository.CampanhaInteresseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampanhaInteresseService {

    private final CampanhaInteresseRepository repository;

    public CampanhaInteresseService(CampanhaInteresseRepository repository) {
        this.repository = repository;
    }

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

        repository.inserir(campanhaId, usuarioId, tipoInteresse);
    }

    public List<CampanhaInteresse> listarVoluntarios(Long ongId) {
        return repository.listarPorOng(ongId, "VOLUNTARIAR");
    }

    public List<CampanhaInteresse> listarPedidosAjuda(Long ongId) {
        return repository.listarPorOng(ongId, "PEDIDO_AJUDA");
    }
}