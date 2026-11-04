package br.com.petcare.clinica.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VacinaRequestDTO {

    @NotNull(message = "Animal é obrigatório")
    private Long animalId;

    @NotBlank(message = "Nome da vacina é obrigatório")
    @Size(max = 120, message = "Nome da vacina deve ter no máximo 120 caracteres")
    private String nome;

    @NotNull(message = "Data de aplicação é obrigatória")
    @PastOrPresent(message = "Data de aplicação não pode estar no futuro")
    private LocalDate dataAplicacao;

    @NotBlank(message = "Lote é obrigatório")
    @Size(max = 60, message = "Lote deve ter no máximo 60 caracteres")
    private String lote;
}
