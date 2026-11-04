package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.DadosVeterinarioDTO;
import br.com.petcare.clinica.dtos.VeterinarioRequestDTO;

import java.util.List;

public interface VeterinarioService {

    DadosVeterinarioDTO salvar(VeterinarioRequestDTO dto);

    DadosVeterinarioDTO obterPorId(Long id);

    List<DadosVeterinarioDTO> obterTodos(Long especieId);

    DadosVeterinarioDTO editar(Long id, VeterinarioRequestDTO dto);

    void remover(Long id);
}
