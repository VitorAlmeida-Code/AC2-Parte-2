package br.com.petcare.clinica.services;

public interface ImpressaoService {

    String imprimirAtendimento(Long atendimentoId);

    String imprimirHistoricoDoAnimal(Long animalId);
}
