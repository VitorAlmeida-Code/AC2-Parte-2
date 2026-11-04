package br.com.petcare.clinica.repositories;

import br.com.petcare.clinica.models.Veterinario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    boolean existsByCrmv(String crmv);

    boolean existsByCrmvAndIdNot(String crmv, Long id);

    boolean existsByEspeciesId(Long especieId);

    @Query("""
            select distinct v from Veterinario v
            join v.especies e
            where e.id = :especieId
            order by v.nome
            """)
    List<Veterinario> buscarHabilitadosParaEspecie(@Param("especieId") Long especieId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Veterinario v where v.id = :id")
    Optional<Veterinario> buscarPorIdComBloqueio(@Param("id") Long id);
}
