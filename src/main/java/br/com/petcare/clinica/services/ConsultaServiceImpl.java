package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.ConsultaRequestDTO;
import br.com.petcare.clinica.dtos.DadosConsultaDTO;
import br.com.petcare.clinica.exceptions.ConflitoException;
import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.exceptions.RegraNegocioException;
import br.com.petcare.clinica.models.Animal;
import br.com.petcare.clinica.models.Consulta;
import br.com.petcare.clinica.models.StatusConsulta;
import br.com.petcare.clinica.models.Veterinario;
import br.com.petcare.clinica.repositories.AnimalRepository;
import br.com.petcare.clinica.repositories.ConsultaRepository;
import br.com.petcare.clinica.repositories.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaServiceImpl implements ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final AnimalRepository animalRepository;
    private final VeterinarioRepository veterinarioRepository;

    @Override
    @Transactional
    public DadosConsultaDTO agendar(ConsultaRequestDTO dto) {
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado."));
        Veterinario veterinario = veterinarioRepository.buscarPorIdComBloqueio(dto.getVeterinarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado."));

        validarDataFutura(dto);
        validarHabilitacao(veterinario, animal);
        validarDisponibilidade(veterinario, dto);

        Consulta consulta = new Consulta();
        consulta.setAnimal(animal);
        consulta.setVeterinario(veterinario);
        consulta.setLocal(dto.getLocal().trim());
        consulta.setData(dto.getData());
        consulta.setHorario(dto.getHorario());
        return DadosConsultaDTO.de(consultaRepository.save(consulta));
    }

    @Override
    @Transactional(readOnly = true)
    public DadosConsultaDTO obterPorId(Long id) {
        return DadosConsultaDTO.de(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DadosConsultaDTO> buscar(Long animalId, Long veterinarioId, LocalDate data, StatusConsulta status) {
        return consultaRepository.buscarComFiltros(animalId, veterinarioId, data, status).stream()
                .map(DadosConsultaDTO::de)
                .toList();
    }

    @Override
    @Transactional
    public DadosConsultaDTO cancelar(Long id) {
        Consulta consulta = buscar(id);
        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new RegraNegocioException("Esta consulta já está cancelada.");
        }
        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new RegraNegocioException("Uma consulta já realizada não pode ser cancelada.");
        }
        consulta.setStatus(StatusConsulta.CANCELADA);
        return DadosConsultaDTO.de(consulta);
    }

    private void validarDataFutura(ConsultaRequestDTO dto) {
        if (LocalDateTime.of(dto.getData(), dto.getHorario()).isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível agendar uma consulta para data ou horário passados.");
        }
    }

    private void validarHabilitacao(Veterinario veterinario, Animal animal) {
        Long especieDoAnimal = animal.getEspecie().getId();
        boolean habilitado = veterinario.getEspecies().stream()
                .anyMatch(especie -> especie.getId().equals(especieDoAnimal));
        if (!habilitado) {
            throw new RegraNegocioException("Veterinário não habilitado para atender a espécie "
                    + animal.getEspecie().getNome() + ".");
        }
    }

    private void validarDisponibilidade(Veterinario veterinario, ConsultaRequestDTO dto) {
        boolean ocupado = consultaRepository.existsByVeterinarioIdAndDataAndHorarioAndStatusNot(
                veterinario.getId(), dto.getData(), dto.getHorario(), StatusConsulta.CANCELADA);
        if (ocupado) {
            throw new ConflitoException("Veterinário já possui consulta agendada nesta data e horário.");
        }
    }

    private Consulta buscar(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));
    }
}
