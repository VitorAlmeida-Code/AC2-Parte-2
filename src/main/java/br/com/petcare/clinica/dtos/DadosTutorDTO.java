package br.com.petcare.clinica.dtos;

import br.com.petcare.clinica.models.Tutor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosTutorDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String endereco;

    public static DadosTutorDTO de(Tutor tutor) {
        return DadosTutorDTO.builder()
                .id(tutor.getId())
                .nome(tutor.getNome())
                .cpf(tutor.getCpf())
                .telefone(tutor.getTelefone())
                .endereco(tutor.getEndereco())
                .build();
    }
}
