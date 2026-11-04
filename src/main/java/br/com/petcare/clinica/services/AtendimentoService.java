package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.AtendimentoRequestDTO;
import br.com.petcare.clinica.dtos.DadosAtendimentoDTO;

import java.util.List;

public interface AtendimentoService {

    DadosAtendimentoDTO registrar(AtendimentoRequestDTO dto);

    DadosAtendimentoDTO obterPorId(Long id);

    List<DadosAtendimentoDTO> obterTodos(Long animalId);
}
