package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.AnimalRequestDTO;
import br.com.petcare.clinica.dtos.DadosAnimalDTO;
import br.com.petcare.clinica.dtos.DadosAtendimentoDTO;
import br.com.petcare.clinica.dtos.DadosVacinaDTO;
import br.com.petcare.clinica.dtos.HistoricoAnimalDTO;
import br.com.petcare.clinica.exceptions.ConflitoException;
import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.models.Animal;
import br.com.petcare.clinica.models.Especie;
import br.com.petcare.clinica.models.StatusConsulta;
import br.com.petcare.clinica.models.Tutor;
import br.com.petcare.clinica.repositories.AnimalRepository;
import br.com.petcare.clinica.repositories.AtendimentoRepository;
import br.com.petcare.clinica.repositories.ConsultaRepository;
import br.com.petcare.clinica.repositories.EspecieRepository;
import br.com.petcare.clinica.repositories.TutorRepository;
import br.com.petcare.clinica.repositories.VacinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final TutorRepository tutorRepository;
    private final EspecieRepository especieRepository;
    private final ConsultaRepository consultaRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final VacinaRepository vacinaRepository;

    @Override
    @Transactional
    public DadosAnimalDTO salvar(AnimalRequestDTO dto) {
        Animal animal = new Animal();
        preencher(animal, dto, buscarEspecie(dto.getEspecieId()));
        return DadosAnimalDTO.de(animalRepository.save(animal));
    }

    @Override
    @Transactional(readOnly = true)
    public DadosAnimalDTO obterPorId(Long id) {
        return DadosAnimalDTO.de(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DadosAnimalDTO> obterTodos() {
        return animalRepository.buscarTodosComEspecieETutor().stream().map(DadosAnimalDTO::de).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoricoAnimalDTO obterHistorico(Long id) {
        Animal animal = buscar(id);
        return HistoricoAnimalDTO.builder()
                .animal(DadosAnimalDTO.de(animal))
                .atendimentos(atendimentoRepository.buscarHistoricoDoAnimal(id).stream()
                        .map(DadosAtendimentoDTO::de)
                        .toList())
                .vacinas(vacinaRepository.findByAnimalIdOrderByDataAplicacaoDesc(id).stream()
                        .map(DadosVacinaDTO::de)
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public DadosAnimalDTO editar(Long id, AnimalRequestDTO dto) {
        Animal animal = buscar(id);
        Especie especie = buscarEspecie(dto.getEspecieId());
        boolean trocouEspecie = !especie.getId().equals(animal.getEspecie().getId());
        if (trocouEspecie && consultaRepository.existsByAnimalIdAndStatus(id, StatusConsulta.AGENDADA)) {
            throw new ConflitoException("Não é possível alterar a espécie de um animal com consultas agendadas.");
        }
        preencher(animal, dto, especie);
        return DadosAnimalDTO.de(animal);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Animal animal = buscar(id);
        if (!animal.getConsultas().isEmpty() || !animal.getVacinas().isEmpty()) {
            throw new ConflitoException("Animal com consultas ou vacinas registradas não pode ser removido.");
        }
        animalRepository.delete(animal);
    }

    private void preencher(Animal animal, AnimalRequestDTO dto, Especie especie) {
        Tutor tutor = tutorRepository.findById(dto.getTutorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor não encontrado."));
        animal.setNome(dto.getNome().trim());
        animal.setRaca(dto.getRaca().trim());
        animal.setDataNascimento(dto.getDataNascimento());
        animal.setEspecie(especie);
        animal.setTutor(tutor);
    }

    private Especie buscarEspecie(Long id) {
        return especieRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Espécie não encontrada."));
    }

    private Animal buscar(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado."));
    }
}
