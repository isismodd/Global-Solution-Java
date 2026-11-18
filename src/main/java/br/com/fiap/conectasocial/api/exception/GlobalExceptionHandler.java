package br.com.fiap.conectasocial.api.exception;

import br.com.fiap.conectasocial.api.dto.ErroResponse;
import br.com.fiap.conectasocial.api.dto.ErroResponse.CampoErro;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * Tratamento global de erros da API REST.
 * Só vale para classes @RestController, então as telas Thymeleaf não são afetadas.
 */
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- exceções de negócio ----------

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(NaoEncontradoException ex, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler({ProibidoException.class, AccessDeniedException.class})
    public ResponseEntity<ErroResponse> proibido(RuntimeException ex, HttpServletRequest req) {
        String msg = ex instanceof ProibidoException
                ? ex.getMessage()
                : "Você não tem permissão para acessar este recurso.";
        return resposta(HttpStatus.FORBIDDEN, msg, req);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> conflito(ConflitoException ex, HttpServletRequest req) {
        return resposta(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler({RegraNegocioException.class, IllegalArgumentException.class})
    public ResponseEntity<ErroResponse> regraNegocio(RuntimeException ex, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    // ---------- segurança / banco ----------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponse> naoAutenticado(AuthenticationException ex, HttpServletRequest req) {
        return resposta(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.", req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        return resposta(HttpStatus.CONFLICT,
                "Operação não permitida: o registro já existe ou está vinculado a outros dados.", req);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErroResponse> ordenacaoInvalida(PropertyReferenceException ex, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Campo de ordenação inválido: " + ex.getPropertyName(), req);
    }

    @ExceptionHandler(ServicoIaIndisponivelException.class)
    public ResponseEntity<ErroResponse> iaIndisponivel(ServicoIaIndisponivelException ex, HttpServletRequest req) {
        log.error("Falha ao consultar o serviço de IA", ex);
        return resposta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> erroInesperado(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {}", req.getRequestURI(), ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor. Tente novamente mais tarde.", req);
    }

    // ---------- exceções do Spring MVC (mesmo formato de resposta) ----------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<CampoErro> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new CampoErro(f.getField(), f.getDefaultMessage()))
                .toList();
        ErroResponse corpo = ErroResponse.de(HttpStatus.BAD_REQUEST,
                "Há campos inválidos na requisição.", caminho(request), campos);
        return ResponseEntity.badRequest().body(corpo);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ErroResponse corpo = ErroResponse.de(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido ou malformado.", caminho(request));
        return ResponseEntity.badRequest().body(corpo);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        String mensagem = switch (status) {
            case BAD_REQUEST -> "Requisição inválida.";
            case NOT_FOUND -> "Recurso não encontrado.";
            case METHOD_NOT_ALLOWED -> "Método HTTP não suportado para este endereço.";
            case UNSUPPORTED_MEDIA_TYPE -> "Tipo de conteúdo não suportado. Use application/json.";
            default -> status.getReasonPhrase();
        };
        return ResponseEntity.status(statusCode).headers(headers)
                .body(ErroResponse.de(status, mensagem, caminho(request)));
    }

    // ---------- helpers ----------

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ErroResponse.de(status, mensagem, req.getRequestURI()));
    }

    private String caminho(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
