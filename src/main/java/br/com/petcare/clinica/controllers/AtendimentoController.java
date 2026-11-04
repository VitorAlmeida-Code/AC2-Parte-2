package br.com.petcare.clinica.controllers;

import br.com.petcare.clinica.dtos.AtendimentoRequestDTO;
import br.com.petcare.clinica.dtos.DadosAtendimentoDTO;
import br.com.petcare.clinica.services.AtendimentoService;
import br.com.petcare.clinica.services.ImpressaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/atendimento")
@RequiredArgsConstructor
public class AtendimentoController {

    private final AtendimentoService atendimentoService;
    private final ImpressaoService impressaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DadosAtendimentoDTO registrar(@Valid @RequestBody AtendimentoRequestDTO dto) {
        return atendimentoService.registrar(dto);
    }

    @GetMapping
    public List<DadosAtendimentoDTO> obterTodos(@RequestParam(required = false) Long animalId) {
        return atendimentoService.obterTodos(animalId);
    }

    @GetMapping("{id}")
    public DadosAtendimentoDTO obterPorId(@PathVariable Long id) {
        return atendimentoService.obterPorId(id);
    }

    @GetMapping(value = "{id}/impressao", produces = MediaType.TEXT_HTML_VALUE)
    public String imprimir(@PathVariable Long id) {
        return impressaoService.imprimirAtendimento(id);
    }
}
