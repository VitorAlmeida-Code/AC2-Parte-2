package br.com.petcare.clinica.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
public class TutorRequestDTO {

    @NotBlank(message = "Nome do tutor é obrigatório")
    @Size(max = 120, message = "Nome do tutor deve ter no máximo 120 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "Informe um CPF válido")
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$",
            message = "Informe um telefone válido, por exemplo (11) 98888-7777")
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    private String endereco;
}
