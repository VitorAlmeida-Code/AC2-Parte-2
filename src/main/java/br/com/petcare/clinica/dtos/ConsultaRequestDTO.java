package br.com.petcare.clinica.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ConsultaRequestDTO {

    @NotNull(message = "Animal é obrigatório")
    private Long animalId;

    @NotNull(message = "Veterinário é obrigatório")
    private Long veterinarioId;

    @NotBlank(message = "Local é obrigatório")
    @Size(max = 120, message = "Local deve ter no máximo 120 caracteres")
    private String local;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    @NotNull(message = "Horário é obrigatório")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horario;
}
