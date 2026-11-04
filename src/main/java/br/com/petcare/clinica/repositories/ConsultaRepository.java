package br.com.petcare.clinica.repositories;

import br.com.petcare.clinica.models.Consulta;
import br.com.petcare.clinica.models.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    /** Consulta personalizada: indica se o veterinário já ocupa o horário (consultas canceladas liberam a vaga). */
    boolean existsByVeterinarioIdAndDataAndHorarioAndStatusNot(Long veterinarioId, LocalDate data, LocalTime horario,
                                                               StatusConsulta status);

    boolean existsByAnimalIdAndStatus(Long animalId, StatusConsulta status);

    boolean existsByVeterinarioIdAndStatusAndAnimalEspecieIdNotIn(Long veterinarioId, StatusConsulta status,
                                                                  Collection<Long> especiesIds);

    /** Consulta personalizada: agenda com filtros opcionais por animal, veterinário, data e status. */
    @Query("""
            select c from Consulta c
            join fetch c.animal a
            join fetch a.especie
            join fetch c.veterinario v
            where (:animalId is null or a.id = :animalId)
              and (:veterinarioId is null or v.id = :veterinarioId)
              and (:data is null or c.data = :data)
              and (:status is null or c.status = :status)
            order by c.data, c.horario
            """)
    List<Consulta> buscarComFiltros(@Param("animalId") Long animalId,
                                    @Param("veterinarioId") Long veterinarioId,
                                    @Param("data") LocalDate data,
                                    @Param("status") StatusConsulta status);
}
