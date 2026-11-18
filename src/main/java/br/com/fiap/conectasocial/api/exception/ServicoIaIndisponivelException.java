package br.com.fiap.conectasocial.api.exception;

public class ServicoIaIndisponivelException extends RuntimeException {
    public ServicoIaIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
