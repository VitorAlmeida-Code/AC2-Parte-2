package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.DadosAnimalDTO;
import br.com.petcare.clinica.dtos.DadosTutorDTO;
import br.com.petcare.clinica.dtos.TutorRequestDTO;
import br.com.petcare.clinica.exceptions.ConflitoException;
import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.models.Tutor;
import br.com.petcare.clinica.repositories.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorServiceImpl implements TutorService {

    private final TutorRepository tutorRepository;

    @Override
    @Transactional
    public DadosTutorDTO salvar(TutorRequestDTO dto) {
        String cpf = somenteDigitos(dto.getCpf());
        if (tutorRepository.existsByCpf(cpf)) {
            throw new ConflitoException("Já existe um tutor cadastrado com este CPF.");
        }
        Tutor tutor = new Tutor();
        preencher(tutor, dto, cpf);
        return DadosTutorDTO.de(tutorRepository.save(tutor));
    }

    @Override
    @Transactional(readOnly = true)
    public DadosTutorDTO obterPorId(Long id) {
        return DadosTutorDTO.de(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DadosTutorDTO> obterTodos(String nome) {
        List<Tutor> tutores = (nome == null || nome.isBlank())
                ? tutorRepository.findAll(Sort.by("nome"))
                : tutorRepository.findByNomeContainingIgnoreCaseOrderByNome(nome.trim());
        return tutores.stream().map(DadosTutorDTO::de).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DadosAnimalDTO> obterAnimais(Long tutorId) {
        return buscar(tutorId).getAnimais().stream()
                .map(DadosAnimalDTO::de)
                .sorted(Comparator.comparing(DadosAnimalDTO::getNome))
                .toList();
    }

    @Override
    @Transactional
    public DadosTutorDTO editar(Long id, TutorRequestDTO dto) {
        Tutor tutor = buscar(id);
        String cpf = somenteDigitos(dto.getCpf());
        if (tutorRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new ConflitoException("Já existe um tutor cadastrado com este CPF.");
        }
        preencher(tutor, dto, cpf);
        return DadosTutorDTO.de(tutor);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Tutor tutor = buscar(id);
        if (!tutor.getAnimais().isEmpty()) {
            throw new ConflitoException("Tutor com animais cadastrados não pode ser removido.");
        }
        tutorRepository.delete(tutor);
    }

    private void preencher(Tutor tutor, TutorRequestDTO dto, String cpf) {
        tutor.setNome(dto.getNome().trim());
        tutor.setCpf(cpf);
        tutor.setTelefone(dto.getTelefone().trim());
        tutor.setEndereco(dto.getEndereco().trim());
    }

    private String somenteDigitos(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    private Tutor buscar(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor não encontrado."));
    }
}
