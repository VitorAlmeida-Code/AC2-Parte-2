package br.com.petcare.clinica.services;

import br.com.petcare.clinica.dtos.DadosEspecieDTO;
import br.com.petcare.clinica.dtos.EspecieRequestDTO;
import br.com.petcare.clinica.exceptions.ConflitoException;
import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.models.Especie;
import br.com.petcare.clinica.repositories.AnimalRepository;
import br.com.petcare.clinica.repositories.EspecieRepository;
import br.com.petcare.clinica.repositories.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecieServiceImpl implements EspecieService {

    private final EspecieRepository especieRepository;
    private final AnimalRepository animalRepository;
    private final VeterinarioRepository veterinarioRepository;

    @Override
    @Transactional
    public DadosEspecieDTO salvar(EspecieRequestDTO dto) {
        String nome = dto.getNome().trim();
        if (especieRepository.existsByNomeIgnoreCase(nome)) {
            throw new ConflitoException("Já existe uma espécie com o nome informado.");
        }
        Especie especie = new Especie();
        especie.setNome(nome);
        return DadosEspecieDTO.de(especieRepository.save(especie));
    }

    @Override
    @Transactional(readOnly = true)
    public DadosEspecieDTO obterPorId(Long id) {
        return DadosEspecieDTO.de(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DadosEspecieDTO> obterTodas() {
        return especieRepository.findAll(Sort.by("nome")).stream().map(DadosEspecieDTO::de).toList();
    }

    @Override
    @Transactional
    public DadosEspecieDTO editar(Long id, EspecieRequestDTO dto) {
        Especie especie = buscar(id);
        String nome = dto.getNome().trim();
        if (especieRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ConflitoException("Já existe uma espécie com o nome informado.");
        }
        especie.setNome(nome);
        return DadosEspecieDTO.de(especie);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Especie especie = buscar(id);
        if (animalRepository.existsByEspecieId(id) || veterinarioRepository.existsByEspeciesId(id)) {
            throw new ConflitoException("Espécie em uso por animais ou veterinários não pode ser removida.");
        }
        especieRepository.delete(especie);
    }

    private Especie buscar(Long id) {
        return especieRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Espécie não encontrada."));
    }
}
