package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.DadosAnimalDTO;
import br.com.petcare.clinica.dtos.DadosTutorDTO;
import br.com.petcare.clinica.dtos.TutorRequestDTO;

import java.util.List;

public interface TutorService {

    DadosTutorDTO salvar(TutorRequestDTO dto);

    DadosTutorDTO obterPorId(Long id);

    List<DadosTutorDTO> obterTodos(String nome);

    List<DadosAnimalDTO> obterAnimais(Long tutorId);

    DadosTutorDTO editar(Long id, TutorRequestDTO dto);

    void remover(Long id);
}
