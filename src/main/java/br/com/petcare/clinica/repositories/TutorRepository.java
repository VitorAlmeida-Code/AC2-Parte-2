package br.com.petcare.clinica.repositories;

import br.com.petcare.clinica.models.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    List<Tutor> findByNomeContainingIgnoreCaseOrderByNome(String nome);
}
