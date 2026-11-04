package br.com.petcare.clinica.repositories;

import br.com.petcare.clinica.models.Vacina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacinaRepository extends JpaRepository<Vacina, Long> {

    List<Vacina> findByAnimalIdOrderByDataAplicacaoDesc(Long animalId);
}
