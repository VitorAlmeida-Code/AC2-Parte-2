package br.com.petcare.clinica.dtos;

import lombok.Getter;

import java.util.List;

public class ApiErrorDTO {

    @Getter
    private final List<String> errors;

    public ApiErrorDTO(String mensagem) {
        this.errors = List.of(mensagem);
    }

    public ApiErrorDTO(List<String> erros) {
        this.errors = erros;
    }
}
