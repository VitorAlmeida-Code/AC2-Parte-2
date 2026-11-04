package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.ConsultaRequestDTO;
import br.com.petcare.clinica.dtos.DadosConsultaDTO;
import br.com.petcare.clinica.models.StatusConsulta;

import java.time.LocalDate;
import java.util.List;

public interface ConsultaService {

    DadosConsultaDTO agendar(ConsultaRequestDTO dto);

    DadosConsultaDTO obterPorId(Long id);

    List<DadosConsultaDTO> buscar(Long animalId, Long veterinarioId, LocalDate data, StatusConsulta status);

    DadosConsultaDTO cancelar(Long id);
}
