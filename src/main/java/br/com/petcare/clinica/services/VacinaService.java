package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.DadosVacinaDTO;
import br.com.petcare.clinica.dtos.VacinaRequestDTO;

import java.util.List;

public interface VacinaService {

    DadosVacinaDTO registrar(VacinaRequestDTO dto);

    DadosVacinaDTO obterPorId(Long id);

    List<DadosVacinaDTO> obterPorAnimal(Long animalId);
}
