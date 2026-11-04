package br.com.petcare.clinica.dtos;

import br.com.petcare.clinica.models.Atendimento;
import br.com.petcare.clinica.models.Consulta;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosAtendimentoDTO {

    private Long id;
    private Long consultaId;
    private ResumoDTO animal;
    private ResumoDTO veterinario;
    private String crmv;
    private String local;
    private LocalDate data;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horario;

    private String diagnostico;
    private String observacoes;
    private String orientacoes;
    private LocalDateTime dataRegistro;

    public static DadosAtendimentoDTO de(Atendimento atendimento) {
        Consulta consulta = atendimento.getConsulta();
        return DadosAtendimentoDTO.builder()
                .id(atendimento.getId())
                .consultaId(consulta.getId())
                .animal(new ResumoDTO(consulta.getAnimal().getId(), consulta.getAnimal().getNome()))
                .veterinario(new ResumoDTO(consulta.getVeterinario().getId(), consulta.getVeterinario().getNome()))
                .crmv(consulta.getVeterinario().getCrmv())
                .local(consulta.getLocal())
                .data(consulta.getData())
                .horario(consulta.getHorario())
                .diagnostico(atendimento.getDiagnostico())
                .observacoes(atendimento.getObservacoes())
                .orientacoes(atendimento.getOrientacoes())
                .dataRegistro(atendimento.getDataRegistro())
                .build();
    }
}
