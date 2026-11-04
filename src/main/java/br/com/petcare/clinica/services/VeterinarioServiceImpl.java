package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.DadosVeterinarioDTO;
import br.com.petcare.clinica.dtos.VeterinarioRequestDTO;
import br.com.petcare.clinica.exceptions.ConflitoException;
import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.models.Especie;
import br.com.petcare.clinica.models.StatusConsulta;
import br.com.petcare.clinica.models.Veterinario;
import br.com.petcare.clinica.repositories.ConsultaRepository;
import br.com.petcare.clinica.repositories.EspecieRepository;
import br.com.petcare.clinica.repositories.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final EspecieRepository especieRepository;
    private final ConsultaRepository consultaRepository;

    @Override
    @Transactional
    public DadosVeterinarioDTO salvar(VeterinarioRequestDTO dto) {
        if (veterinarioRepository.existsByCrmv(dto.getCrmv())) {
            throw new ConflitoException("Já existe um veterinário cadastrado com este CRMV.");
        }
        Veterinario veterinario = new Veterinario();
        preencher(veterinario, dto, buscarEspecies(dto.getEspeciesIds()));
        return DadosVeterinarioDTO.de(veterinarioRepository.save(veterinario));
    }

    @Override
    @Transactional(readOnly = true)
    public DadosVeterinarioDTO obterPorId(Long id) {
        return DadosVeterinarioDTO.de(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DadosVeterinarioDTO> obterTodos(Long especieId) {
        List<Veterinario> veterinarios = especieId == null
                ? veterinarioRepository.findAll(Sort.by("nome"))
                : veterinarioRepository.buscarHabilitadosParaEspecie(especieId);
        return veterinarios.stream().map(DadosVeterinarioDTO::de).toList();
    }

    @Override
    @Transactional
    public DadosVeterinarioDTO editar(Long id, VeterinarioRequestDTO dto) {
        Veterinario veterinario = veterinarioRepository.buscarPorIdComBloqueio(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado."));
        if (veterinarioRepository.existsByCrmvAndIdNot(dto.getCrmv(), id)) {
            throw new ConflitoException("Já existe um veterinário cadastrado com este CRMV.");
        }
        Set<Especie> especies = buscarEspecies(dto.getEspeciesIds());
        Set<Long> especiesIds = new HashSet<>(dto.getEspeciesIds());
        if (consultaRepository.existsByVeterinarioIdAndStatusAndAnimalEspecieIdNotIn(
                id, StatusConsulta.AGENDADA, especiesIds)) {
            throw new ConflitoException(
                    "Não é possível remover a habilitação de uma espécie com consultas agendadas para o veterinário.");
        }
        preencher(veterinario, dto, especies);
        return DadosVeterinarioDTO.de(veterinario);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Veterinario veterinario = buscar(id);
        if (!veterinario.getConsultas().isEmpty()) {
            throw new ConflitoException("Veterinário com consultas registradas não pode ser removido.");
        }
        veterinarioRepository.delete(veterinario);
    }

    private void preencher(Veterinario veterinario, VeterinarioRequestDTO dto, Set<Especie> especies) {
        veterinario.setNome(dto.getNome().trim());
        veterinario.setCrmv(dto.getCrmv());
        veterinario.setEspecies(especies);
    }

    private Set<Especie> buscarEspecies(List<Long> ids) {
        Set<Long> idsUnicos = new HashSet<>(ids);
        List<Especie> encontradas = especieRepository.findAllById(idsUnicos);
        if (encontradas.size() != idsUnicos.size()) {
            throw new RecursoNaoEncontradoException("Uma ou mais espécies informadas não foram encontradas.");
        }
        return new HashSet<>(encontradas);
    }

    private Veterinario buscar(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado."));
    }
}
