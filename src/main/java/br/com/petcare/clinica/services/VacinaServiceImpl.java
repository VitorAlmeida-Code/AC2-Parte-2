package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.DadosVacinaDTO;
import br.com.petcare.clinica.dtos.VacinaRequestDTO;
import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.exceptions.RegraNegocioException;
import br.com.petcare.clinica.models.Animal;
import br.com.petcare.clinica.models.Vacina;
import br.com.petcare.clinica.repositories.AnimalRepository;
import br.com.petcare.clinica.repositories.VacinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacinaServiceImpl implements VacinaService {

    private final VacinaRepository vacinaRepository;
    private final AnimalRepository animalRepository;

    @Override
    @Transactional
    public DadosVacinaDTO registrar(VacinaRequestDTO dto) {
        Animal animal = buscarAnimal(dto.getAnimalId());
        if (dto.getDataAplicacao().isBefore(animal.getDataNascimento())) {
            throw new RegraNegocioException("Data de aplicação não pode ser anterior ao nascimento do animal.");
        }
        Vacina vacina = new Vacina();
        vacina.setAnimal(animal);
        vacina.setNome(dto.getNome().trim());
        vacina.setDataAplicacao(dto.getDataAplicacao());
        vacina.setLote(dto.getLote().trim());
        return DadosVacinaDTO.de(vacinaRepository.save(vacina));
    }

    @Override
    @Transactional(readOnly = true)
    public DadosVacinaDTO obterPorId(Long id) {
        return vacinaRepository.findById(id)
                .map(DadosVacinaDTO::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vacina não encontrada."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DadosVacinaDTO> obterPorAnimal(Long animalId) {
        buscarAnimal(animalId);
        return vacinaRepository.findByAnimalIdOrderByDataAplicacaoDesc(animalId).stream()
                .map(DadosVacinaDTO::de)
                .toList();
    }

    private Animal buscarAnimal(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado."));
    }
}
