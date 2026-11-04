package br.com.petcare.clinica.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Referência curta a outro registro (tutor, animal ou veterinário) dentro de uma resposta. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoDTO {

    private Long id;
    private String nome;
}
