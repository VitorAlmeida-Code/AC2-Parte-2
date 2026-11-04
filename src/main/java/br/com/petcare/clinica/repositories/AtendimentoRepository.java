package br.com.petcare.clinica.repositories;

import br.com.petcare.clinica.models.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    /** Consulta personalizada: atendimentos de um animal, do mais recente para o mais antigo. */
    @Query("""
            select a from Atendimento a
            join fetch a.consulta c
            join fetch c.animal an
            join fetch c.veterinario
            where an.id = :animalId
            order by c.data desc, c.horario desc
            """)
    List<Atendimento> buscarHistoricoDoAnimal(@Param("animalId") Long animalId);

    /** Consulta personalizada: todos os atendimentos, do mais recente para o mais antigo. */
    @Query("""
            select a from Atendimento a
            join fetch a.consulta c
            join fetch c.animal
            join fetch c.veterinario
            order by c.data desc, c.horario desc
            """)
    List<Atendimento> buscarTodosOrdenados();
}
