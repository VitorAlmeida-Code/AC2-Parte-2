package br.com.petcare.clinica.dtos;

import br.com.petcare.clinica.models.Veterinario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosVeterinarioDTO {

    private Long id;
    private String nome;
    private String crmv;
    private List<DadosEspecieDTO> especies;

    public static DadosVeterinarioDTO de(Veterinario veterinario) {
        return DadosVeterinarioDTO.builder()
                .id(veterinario.getId())
                .nome(veterinario.getNome())
                .crmv(veterinario.getCrmv())
                .especies(veterinario.getEspecies().stream()
                        .map(DadosEspecieDTO::de)
                        .sorted(Comparator.comparing(DadosEspecieDTO::getNome))
                        .toList())
                .build();
    }
}
