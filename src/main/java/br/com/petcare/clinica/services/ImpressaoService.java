package br.com.petcare.clinica.services;

/** Gera as versões para impressão (HTML) dos registros de prontuário. */
public interface ImpressaoService {

    String imprimirAtendimento(Long atendimentoId);

    String imprimirHistoricoDoAnimal(Long animalId);
}
