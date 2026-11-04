package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.AnimalRequestDTO;
import br.com.petcare.clinica.dtos.DadosAnimalDTO;
import br.com.petcare.clinica.dtos.HistoricoAnimalDTO;

import java.util.List;

public interface AnimalService {

    DadosAnimalDTO salvar(AnimalRequestDTO dto);

    DadosAnimalDTO obterPorId(Long id);

    List<DadosAnimalDTO> obterTodos();

    HistoricoAnimalDTO obterHistorico(Long id);

    DadosAnimalDTO editar(Long id, AnimalRequestDTO dto);

    void remover(Long id);
}
