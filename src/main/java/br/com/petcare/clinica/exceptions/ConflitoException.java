package br.com.petcare.clinica.exceptions;

/** Operação conflita com dados existentes (duplicidade, horário ocupado, registro em uso). Devolvida como HTTP 409. */
public class ConflitoException extends RegraNegocioException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
