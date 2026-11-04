package br.com.petcare.clinica.exceptions;

/** Registro inexistente. Devolvida à API como HTTP 404. */
public class RecursoNaoEncontradoException extends RegraNegocioException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
