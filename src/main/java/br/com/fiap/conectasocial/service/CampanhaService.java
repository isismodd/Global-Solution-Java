
package br.com.fiap.conectasocial.service;

import br.com.fiap.conectasocial.model.Campanha;
import br.com.fiap.conectasocial.repository.CampanhaRepository;
import br.com.fiap.conectasocial.repository.CampanhaInteresseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CampanhaService {

    private final CampanhaRepository campanhas;
    private final CampanhaInteresseRepository interesses;

    public CampanhaService(
            CampanhaRepository campanhas,
            CampanhaInteresseRepository interesses) {

        this.campanhas = campanhas;
        this.interesses = interesses;
    }

    public List<Campanha> listarTodas() {
        return campanhas.listarTodas();
    }

    public List<Campanha> listarPorOng(Long usuarioId) {

        Long ongId = campanhas.buscarIdOngPorUsuario(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ONG não encontrada para este usuário."));

        List<Campanha> lista = campanhas.listarPorOng(ongId);

        for (Campanha campanha : lista) {
            campanha.setTemImagem(
                    campanhas.possuiImagemCampanha(campanha.getId())
            );
        }

        return lista;
    }

    public List<Map<String, Object>> listarCampanhasDoFeed() {
        return campanhas.listarCampanhasDoFeed();
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

    @Transactional
    public void cadastrar(Campanha campanha, Long usuarioId, MultipartFile arquivo)
            throws IOException {

        validar(campanha);

        Long ongId = campanhas.buscarIdOngPorUsuario(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ONG não encontrada para este usuário."));

        byte[] imagem = null;

        if (arquivo != null && !arquivo.isEmpty()) {

            if (arquivo.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "A imagem deve ter no máximo 5 MB.");
            }

            String tipo = arquivo.getContentType();

            if (tipo == null || !(
                    tipo.equals("image/jpeg")
                            || tipo.equals("image/png")
                            || tipo.equals("image/webp"))) {
                throw new IllegalArgumentException(
                        "Envie uma imagem JPG, PNG ou WEBP.");
            }

            imagem = arquivo.getBytes();
        }

        campanhas.inserir(campanha, ongId, imagem);
    }

    public byte[] buscarImagemCampanha(Long id) {
        return campanhas.buscarImagemCampanha(id);
    }

    public byte[] buscarFotoOng(Long ongId) {
        return campanhas.buscarFotoOng(ongId);
    }

    // Busca uma campanha para edição, verificando se pertence à ONG logada
    public Campanha buscarParaEdicao(Long campanhaId, Long usuarioId) {

        Long ongId = campanhas.buscarIdOngPorUsuario(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ONG não encontrada para este usuário."));

        Campanha campanha = campanhas.buscarPorIdEOng(campanhaId, ongId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Campanha não encontrada ou não pertence à sua ONG."));

        campanha.setTemImagem(
                campanhas.possuiImagemCampanha(campanha.getId()));

        return campanha;
    }

    // Edita uma campanha
    @Transactional
    public void editar(
            Long campanhaId,
            Campanha campanha,
            Long usuarioId,
            MultipartFile arquivo) throws IOException {

        validar(campanha);

        Long ongId = campanhas.buscarIdOngPorUsuario(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ONG não encontrada para este usuário."));

        if (campanhas.buscarPorIdEOng(campanhaId, ongId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Campanha não encontrada ou não pertence à sua ONG.");
        }

        byte[] imagem = null;

        if (arquivo != null && !arquivo.isEmpty()) {

            if (arquivo.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "A imagem deve ter no máximo 5 MB.");
            }

            String tipo = arquivo.getContentType();

            if (tipo == null || !(
                    tipo.equals("image/jpeg")
                            || tipo.equals("image/png")
                            || tipo.equals("image/webp"))) {
                throw new IllegalArgumentException(
                        "Envie uma imagem JPG, PNG ou WEBP.");
            }

            imagem = arquivo.getBytes();
        }

        int atualizadas = campanhas.atualizar(
                campanha,
                campanhaId,
                ongId,
                imagem
        );

        if (atualizadas == 0) {
            throw new IllegalArgumentException(
                    "Não foi possível atualizar a campanha.");
        }
    }

    // Exclui uma campanha
    @Transactional
    public void excluir(Long campanhaId, Long usuarioId) {

        Long ongId = campanhas.buscarIdOngPorUsuario(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ONG não encontrada para este usuário."));

        if (campanhas.buscarPorIdEOng(campanhaId, ongId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Campanha não encontrada ou não pertence à sua ONG.");
        }

        // Primeiro exclui os interesses vinculados à campanha
        interesses.excluirPorCampanha(campanhaId);

        // Depois exclui a campanha
        int excluidas = campanhas.excluir(campanhaId, ongId);

        if (excluidas == 0) {
            throw new IllegalArgumentException(
                    "Não foi possível excluir a campanha.");
        }
    }

}
