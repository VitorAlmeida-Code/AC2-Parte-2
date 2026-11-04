package br.com.petcare.clinica.dtos;

import br.com.petcare.clinica.models.Vacina;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosVacinaDTO {

    private Long id;
    private ResumoDTO animal;
    private String nome;
    private LocalDate dataAplicacao;
    private String lote;

    public static DadosVacinaDTO de(Vacina vacina) {
        return DadosVacinaDTO.builder()
                .id(vacina.getId())
                .animal(new ResumoDTO(vacina.getAnimal().getId(), vacina.getAnimal().getNome()))
                .nome(vacina.getNome())
                .dataAplicacao(vacina.getDataAplicacao())
                .lote(vacina.getLote())
                .build();
    }
}
