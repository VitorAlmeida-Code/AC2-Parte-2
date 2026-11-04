package br.com.petcare.clinica.controllers;

import br.com.petcare.clinica.dtos.DadosVacinaDTO;
import br.com.petcare.clinica.dtos.VacinaRequestDTO;
import br.com.petcare.clinica.services.VacinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vacina")
@RequiredArgsConstructor
public class VacinaController {

    private final VacinaService vacinaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DadosVacinaDTO registrar(@Valid @RequestBody VacinaRequestDTO dto) {
        return vacinaService.registrar(dto);
    }

    @GetMapping
    public List<DadosVacinaDTO> obterPorAnimal(@RequestParam Long animalId) {
        return vacinaService.obterPorAnimal(animalId);
    }

    @GetMapping("{id}")
    public DadosVacinaDTO obterPorId(@PathVariable Long id) {
        return vacinaService.obterPorId(id);
    }
}
