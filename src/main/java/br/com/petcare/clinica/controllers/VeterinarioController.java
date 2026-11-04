package br.com.petcare.clinica.controllers;

import br.com.petcare.clinica.dtos.DadosVeterinarioDTO;
import br.com.petcare.clinica.dtos.VeterinarioRequestDTO;
import br.com.petcare.clinica.services.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/veterinario")
@RequiredArgsConstructor
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DadosVeterinarioDTO salvar(@Valid @RequestBody VeterinarioRequestDTO dto) {
        return veterinarioService.salvar(dto);
    }

    @GetMapping
    public List<DadosVeterinarioDTO> obterTodos(@RequestParam(required = false) Long especieId) {
        return veterinarioService.obterTodos(especieId);
    }

    @GetMapping("{id}")
    public DadosVeterinarioDTO obterPorId(@PathVariable Long id) {
        return veterinarioService.obterPorId(id);
    }

    @PutMapping("{id}")
    public DadosVeterinarioDTO editar(@PathVariable Long id, @Valid @RequestBody VeterinarioRequestDTO dto) {
        return veterinarioService.editar(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        veterinarioService.remover(id);
    }
}
