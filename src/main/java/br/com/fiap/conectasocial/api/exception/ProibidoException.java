package br.com.fiap.conectasocial.api.exception;

public class ProibidoException extends RuntimeException {
    public ProibidoException(String mensagem) {
        super(mensagem);
    }
}
