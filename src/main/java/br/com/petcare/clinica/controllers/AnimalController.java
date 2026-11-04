package br.com.petcare.clinica.controllers;

import br.com.petcare.clinica.dtos.AnimalRequestDTO;
import br.com.petcare.clinica.dtos.DadosAnimalDTO;
import br.com.petcare.clinica.dtos.HistoricoAnimalDTO;
import br.com.petcare.clinica.services.AnimalService;
import br.com.petcare.clinica.services.ImpressaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/animal")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;
    private final ImpressaoService impressaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DadosAnimalDTO salvar(@Valid @RequestBody AnimalRequestDTO dto) {
        return animalService.salvar(dto);
    }

    @GetMapping
    public List<DadosAnimalDTO> obterTodos() {
        return animalService.obterTodos();
    }

    @GetMapping("{id}")
    public DadosAnimalDTO obterPorId(@PathVariable Long id) {
        return animalService.obterPorId(id);
    }

    @GetMapping("{id}/historico")
    public HistoricoAnimalDTO obterHistorico(@PathVariable Long id) {
        return animalService.obterHistorico(id);
    }

    @GetMapping(value = "{id}/historico/impressao", produces = MediaType.TEXT_HTML_VALUE)
    public String imprimirHistorico(@PathVariable Long id) {
        return impressaoService.imprimirHistoricoDoAnimal(id);
    }

    @PutMapping("{id}")
    public DadosAnimalDTO editar(@PathVariable Long id, @Valid @RequestBody AnimalRequestDTO dto) {
        return animalService.editar(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        animalService.remover(id);
    }
}
