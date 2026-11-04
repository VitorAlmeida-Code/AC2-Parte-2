package br.com.petcare.clinica.exceptions;

public class ConflitoException extends RegraNegocioException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
