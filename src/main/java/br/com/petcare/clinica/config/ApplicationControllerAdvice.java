package br.com.petcare.clinica.config;

import br.com.petcare.clinica.dtos.ApiErrorDTO;
import br.com.petcare.clinica.exceptions.ConflitoException;
import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.exceptions.RegraNegocioException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ApiErrorDTO> handleRegraNegocio(RegraNegocioException ex) {
        return resposta(HttpStatus.BAD_REQUEST, new ApiErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorDTO> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, new ApiErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ApiErrorDTO> handleConflito(ConflitoException ex) {
        return resposta(HttpStatus.CONFLICT, new ApiErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidacao(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult().getAllErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .distinct()
                .sorted()
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, new ApiErrorDTO(erros));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST, new ApiErrorDTO(
                "Corpo da requisição inválido. Verifique o JSON e os formatos: data yyyy-MM-dd e horário HH:mm."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorDTO> handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return resposta(HttpStatus.BAD_REQUEST, new ApiErrorDTO("Valor inválido para o parâmetro '" + ex.getName() + "'."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorDTO> handleParametroAusente(MissingServletRequestParameterException ex) {
        return resposta(HttpStatus.BAD_REQUEST, new ApiErrorDTO("O parâmetro '" + ex.getParameterName() + "' é obrigatório."));
    }

    /** Duas requisições simultâneas podem passar pelas verificações do service; a restrição do banco barra a segunda. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleIntegridade(DataIntegrityViolationException ex) {
        return resposta(HttpStatus.CONFLICT, new ApiErrorDTO("A operação conflita com dados já registrados."));
    }

    /** O tipo é fixado em JSON porque os endpoints de impressão produzem HTML. */
    private ResponseEntity<ApiErrorDTO> resposta(HttpStatus status, ApiErrorDTO erro) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(erro);
    }
}
