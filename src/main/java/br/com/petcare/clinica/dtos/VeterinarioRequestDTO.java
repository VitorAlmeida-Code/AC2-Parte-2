package br.com.petcare.clinica.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VeterinarioRequestDTO {

    @NotBlank(message = "Nome do veterinário é obrigatório")
    @Size(max = 120, message = "Nome do veterinário deve ter no máximo 120 caracteres")
    private String nome;

    @NotBlank(message = "CRMV é obrigatório")
    @Pattern(regexp = "^CRMV-[A-Z]{2} \\d{1,6}$", message = "CRMV deve seguir o formato CRMV-UF 12345")
    private String crmv;

    @NotEmpty(message = "Informe ao menos uma espécie que o veterinário pode atender")
    private List<@NotNull(message = "Id de espécie não pode ser nulo") Long> especiesIds;
}
