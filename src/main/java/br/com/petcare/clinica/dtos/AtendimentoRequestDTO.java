package br.com.petcare.clinica.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtendimentoRequestDTO {

    @NotNull(message = "Consulta é obrigatória")
    private Long consultaId;

    @NotBlank(message = "Diagnóstico é obrigatório")
    @Size(max = 2000, message = "Diagnóstico deve ter no máximo 2000 caracteres")
    private String diagnostico;

    @Size(max = 2000, message = "Observações devem ter no máximo 2000 caracteres")
    private String observacoes;

    @NotBlank(message = "Orientações são obrigatórias")
    @Size(max = 2000, message = "Orientações devem ter no máximo 2000 caracteres")
    private String orientacoes;
}
