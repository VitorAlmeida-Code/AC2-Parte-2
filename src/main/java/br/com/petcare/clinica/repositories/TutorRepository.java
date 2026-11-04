package br.com.petcare.clinica.repositories;

import br.com.petcare.clinica.models.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    /** Consulta personalizada: busca tutores por parte do nome. */
    List<Tutor> findByNomeContainingIgnoreCaseOrderByNome(String nome);
}
