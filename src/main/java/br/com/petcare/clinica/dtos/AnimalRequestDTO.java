package br.com.petcare.clinica.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AnimalRequestDTO {

    @NotBlank(message = "Nome do animal é obrigatório")
    @Size(max = 80, message = "Nome do animal deve ter no máximo 80 caracteres")
    private String nome;

    @NotNull(message = "Espécie é obrigatória")
    private Long especieId;

    @NotBlank(message = "Raça é obrigatória")
    @Size(max = 80, message = "Raça deve ter no máximo 80 caracteres")
    private String raca;

    @NotNull(message = "Data de nascimento é obrigatória")
    @PastOrPresent(message = "Data de nascimento não pode estar no futuro")
    private LocalDate dataNascimento;

    @NotNull(message = "Tutor responsável é obrigatório")
    private Long tutorId;
}
