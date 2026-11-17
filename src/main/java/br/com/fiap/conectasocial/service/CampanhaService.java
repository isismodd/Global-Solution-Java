
package br.com.fiap.conectasocial.service;

import br.com.fiap.conectasocial.model.Campanha;
import br.com.fiap.conectasocial.repository.CampanhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CampanhaService {

    private final CampanhaRepository campanhas;

    public CampanhaService(CampanhaRepository campanhas) {
        this.campanhas = campanhas;
    }

    public List<Campanha> listarTodas() {
        return campanhas.listarTodas();
    }

    public List<Campanha> listarPorOng(Long usuarioId) {
        Long ongId = campanhas.buscarIdOngPorUsuario(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException("ONG não encontrada para este usuário."));

        return campanhas.listarPorOng(ongId);
    }

    @Transactional
    public void cadastrar(Campanha campanha, Long usuarioId) {
        validar(campanha);

        Long ongId = campanhas.buscarIdOngPorUsuario(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException("ONG não encontrada para este usuário."));

        campanhas.inserir(campanha, ongId);
    }

    private void validar(Campanha campanha) {
        if (campanha.getTitulo() == null || campanha.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Informe o título da campanha.");
        }

        if (campanha.getDescricao() == null || campanha.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Informe a descrição da campanha.");
        }

        if (campanha.getCategoria() == null || campanha.getCategoria().isBlank()) {
            throw new IllegalArgumentException("Selecione uma categoria.");
        }

        if (campanha.getLocal() == null || campanha.getLocal().isBlank()) {
            throw new IllegalArgumentException("Informe a localização da campanha.");
        }
    }
}
