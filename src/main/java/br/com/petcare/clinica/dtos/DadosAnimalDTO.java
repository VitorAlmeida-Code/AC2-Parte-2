package br.com.petcare.clinica.dtos;

import br.com.petcare.clinica.models.Animal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosAnimalDTO {

    private Long id;
    private String nome;
    private DadosEspecieDTO especie;
    private String raca;
    private LocalDate dataNascimento;
    private ResumoDTO tutor;

    public static DadosAnimalDTO de(Animal animal) {
        return DadosAnimalDTO.builder()
                .id(animal.getId())
                .nome(animal.getNome())
                .especie(DadosEspecieDTO.de(animal.getEspecie()))
                .raca(animal.getRaca())
                .dataNascimento(animal.getDataNascimento())
                .tutor(new ResumoDTO(animal.getTutor().getId(), animal.getTutor().getNome()))
                .build();
    }
}
