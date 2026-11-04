package br.com.petcare.clinica.dtos;

import br.com.petcare.clinica.models.Especie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosEspecieDTO {

    private Long id;
    private String nome;

    public static DadosEspecieDTO de(Especie especie) {
        return DadosEspecieDTO.builder()
                .id(especie.getId())
                .nome(especie.getNome())
                .build();
    }
}
