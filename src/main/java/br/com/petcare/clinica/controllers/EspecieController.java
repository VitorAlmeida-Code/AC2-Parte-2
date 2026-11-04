package br.com.petcare.clinica.controllers;

import br.com.petcare.clinica.dtos.DadosEspecieDTO;
import br.com.petcare.clinica.dtos.EspecieRequestDTO;
import br.com.petcare.clinica.services.EspecieService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/especie")
@RequiredArgsConstructor
public class EspecieController {

    private final EspecieService especieService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DadosEspecieDTO salvar(@Valid @RequestBody EspecieRequestDTO dto) {
        return especieService.salvar(dto);
    }

    @GetMapping
    public List<DadosEspecieDTO> obterTodas() {
        return especieService.obterTodas();
    }

    @GetMapping("{id}")
    public DadosEspecieDTO obterPorId(@PathVariable Long id) {
        return especieService.obterPorId(id);
    }

    @PutMapping("{id}")
    public DadosEspecieDTO editar(@PathVariable Long id, @Valid @RequestBody EspecieRequestDTO dto) {
        return especieService.editar(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        especieService.remover(id);
    }
}
