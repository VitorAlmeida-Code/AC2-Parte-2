package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.AtendimentoRequestDTO;
import br.com.petcare.clinica.dtos.DadosAtendimentoDTO;
import br.com.petcare.clinica.exceptions.ConflitoException;
import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.exceptions.RegraNegocioException;
import br.com.petcare.clinica.models.Atendimento;
import br.com.petcare.clinica.models.Consulta;
import br.com.petcare.clinica.models.StatusConsulta;
import br.com.petcare.clinica.repositories.AtendimentoRepository;
import br.com.petcare.clinica.repositories.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AtendimentoServiceImpl implements AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final ConsultaRepository consultaRepository;

    @Override
    @Transactional
    public DadosAtendimentoDTO registrar(AtendimentoRequestDTO dto) {
        Consulta consulta = consultaRepository.findById(dto.getConsultaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));
        validarConsulta(consulta);

        Atendimento atendimento = new Atendimento();
        atendimento.setConsulta(consulta);
        atendimento.setDiagnostico(dto.getDiagnostico().trim());
        atendimento.setObservacoes(dto.getObservacoes());
        atendimento.setOrientacoes(dto.getOrientacoes().trim());
        atendimento.setDataRegistro(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        consulta.setStatus(StatusConsulta.REALIZADA);
        return DadosAtendimentoDTO.de(atendimentoRepository.save(atendimento));
    }

    @Override
    @Transactional(readOnly = true)
    public DadosAtendimentoDTO obterPorId(Long id) {
        return atendimentoRepository.findById(id)
                .map(DadosAtendimentoDTO::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento não encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DadosAtendimentoDTO> obterTodos(Long animalId) {
        List<Atendimento> atendimentos = animalId == null
                ? atendimentoRepository.buscarTodosOrdenados()
                : atendimentoRepository.buscarHistoricoDoAnimal(animalId);
        return atendimentos.stream().map(DadosAtendimentoDTO::de).toList();
    }

    private void validarConsulta(Consulta consulta) {
        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new RegraNegocioException("Não é possível registrar atendimento de uma consulta cancelada.");
        }
        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new ConflitoException("Esta consulta já possui atendimento registrado.");
        }
        if (consulta.getData().isAfter(LocalDate.now())) {
            throw new RegraNegocioException("O atendimento só pode ser registrado a partir da data da consulta.");
        }
    }
}
