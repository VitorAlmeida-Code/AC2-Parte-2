package br.com.petcare.clinica.repositories;

import br.com.petcare.clinica.models.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    boolean existsByEspecieId(Long especieId);

    @Query("""
            select a from Animal a
            join fetch a.especie
            join fetch a.tutor
            order by a.nome
            """)
    List<Animal> buscarTodosComEspecieETutor();
}
