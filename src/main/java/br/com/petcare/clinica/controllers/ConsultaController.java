package br.com.petcare.clinica.controllers;

import br.com.petcare.clinica.dtos.ConsultaRequestDTO;
import br.com.petcare.clinica.dtos.DadosConsultaDTO;
import br.com.petcare.clinica.models.StatusConsulta;
import br.com.petcare.clinica.services.ConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consulta")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DadosConsultaDTO agendar(@Valid @RequestBody ConsultaRequestDTO dto) {
        return consultaService.agendar(dto);
    }

    @GetMapping
    public List<DadosConsultaDTO> buscar(
            @RequestParam(required = false) Long animalId,
            @RequestParam(required = false) Long veterinarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) StatusConsulta status) {
        return consultaService.buscar(animalId, veterinarioId, data, status);
    }

    @GetMapping("{id}")
    public DadosConsultaDTO obterPorId(@PathVariable Long id) {
        return consultaService.obterPorId(id);
    }

    @PatchMapping("{id}/cancelar")
    public DadosConsultaDTO cancelar(@PathVariable Long id) {
        return consultaService.cancelar(id);
    }
}
