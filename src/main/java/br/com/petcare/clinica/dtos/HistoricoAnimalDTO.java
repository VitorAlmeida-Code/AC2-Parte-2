package br.com.petcare.clinica.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoAnimalDTO {

    private DadosAnimalDTO animal;
    private List<DadosAtendimentoDTO> atendimentos;
    private List<DadosVacinaDTO> vacinas;
}
