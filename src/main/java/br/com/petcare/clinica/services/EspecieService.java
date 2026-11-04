package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.DadosEspecieDTO;
import br.com.petcare.clinica.dtos.EspecieRequestDTO;

import java.util.List;

public interface EspecieService {

    DadosEspecieDTO salvar(EspecieRequestDTO dto);

    DadosEspecieDTO obterPorId(Long id);

    List<DadosEspecieDTO> obterTodas();

    DadosEspecieDTO editar(Long id, EspecieRequestDTO dto);

    void remover(Long id);
}
