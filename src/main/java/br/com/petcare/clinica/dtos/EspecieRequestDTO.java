package br.com.petcare.clinica.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EspecieRequestDTO {

    @NotBlank(message = "Nome da espécie é obrigatório")
    @Size(max = 60, message = "Nome da espécie deve ter no máximo 60 caracteres")
    private String nome;
}
