package br.com.petcare.clinica.controllers;

import br.com.petcare.clinica.dtos.DadosAnimalDTO;
import br.com.petcare.clinica.dtos.DadosTutorDTO;
import br.com.petcare.clinica.dtos.TutorRequestDTO;
import br.com.petcare.clinica.services.TutorService;
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
@RequestMapping("/api/tutor")
@RequiredArgsConstructor
public class TutorController {

    private final TutorService tutorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DadosTutorDTO salvar(@Valid @RequestBody TutorRequestDTO dto) {
        return tutorService.salvar(dto);
    }

    @GetMapping
    public List<DadosTutorDTO> obterTodos(@RequestParam(required = false) String nome) {
        return tutorService.obterTodos(nome);
    }

    @GetMapping("{id}")
    public DadosTutorDTO obterPorId(@PathVariable Long id) {
        return tutorService.obterPorId(id);
    }

    @GetMapping("{id}/animais")
    public List<DadosAnimalDTO> obterAnimais(@PathVariable Long id) {
        return tutorService.obterAnimais(id);
    }

    @PutMapping("{id}")
    public DadosTutorDTO editar(@PathVariable Long id, @Valid @RequestBody TutorRequestDTO dto) {
        return tutorService.editar(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        tutorService.remover(id);
    }
}
