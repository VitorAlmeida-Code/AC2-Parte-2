package br.com.petcare.clinica.dtos;

import br.com.petcare.clinica.models.Consulta;
import br.com.petcare.clinica.models.StatusConsulta;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosConsultaDTO {

    private Long id;
    private ResumoDTO animal;
    private String especie;
    private ResumoDTO veterinario;
    private String local;
    private LocalDate data;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horario;

    private StatusConsulta status;

    public static DadosConsultaDTO de(Consulta consulta) {
        return DadosConsultaDTO.builder()
                .id(consulta.getId())
                .animal(new ResumoDTO(consulta.getAnimal().getId(), consulta.getAnimal().getNome()))
                .especie(consulta.getAnimal().getEspecie().getNome())
                .veterinario(new ResumoDTO(consulta.getVeterinario().getId(), consulta.getVeterinario().getNome()))
                .local(consulta.getLocal())
                .data(consulta.getData())
                .horario(consulta.getHorario())
                .status(consulta.getStatus())
                .build();
    }
}
