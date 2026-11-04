package br.com.petcare.clinica.exceptions;

/** Regra de negócio violada. Devolvida à API como HTTP 400. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
