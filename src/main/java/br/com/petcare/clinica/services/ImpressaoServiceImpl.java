package br.com.petcare.clinica.services;

import br.com.petcare.clinica.exceptions.RecursoNaoEncontradoException;
import br.com.petcare.clinica.models.Animal;
import br.com.petcare.clinica.models.Atendimento;
import br.com.petcare.clinica.models.Consulta;
import br.com.petcare.clinica.models.Vacina;
import br.com.petcare.clinica.repositories.AnimalRepository;
import br.com.petcare.clinica.repositories.AtendimentoRepository;
import br.com.petcare.clinica.repositories.VacinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImpressaoServiceImpl implements ImpressaoService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final String ESTILO = """
            body { font-family: Georgia, 'Times New Roman', serif; color: #1a1a1a; max-width: 720px;
                   margin: 32px auto; padding: 0 24px; line-height: 1.5; }
            header { border-bottom: 2px solid #1a1a1a; margin-bottom: 24px; padding-bottom: 8px; }
            h1 { font-size: 24px; margin: 0; }
            header p { margin: 4px 0 0; color: #555; }
            h2 { font-size: 16px; margin: 24px 0 8px; border-bottom: 1px solid #ccc; padding-bottom: 4px; }
            h3 { font-size: 14px; margin: 16px 0 4px; }
            p { margin: 4px 0; white-space: pre-wrap; }
            table { width: 100%; border-collapse: collapse; }
            th, td { text-align: left; padding: 4px 8px; border-bottom: 1px solid #ddd; font-size: 14px; }
            .registro { break-inside: avoid; }
            .assinatura { margin-top: 64px; text-align: center; }
            .assinatura div { border-top: 1px solid #1a1a1a; width: 320px; margin: 0 auto; padding-top: 4px; }
            .imprimir { float: right; padding: 8px 16px; font-size: 14px; cursor: pointer; }
            @media print { .imprimir { display: none; } body { margin: 0; } }
            """;

    private final AtendimentoRepository atendimentoRepository;
    private final AnimalRepository animalRepository;
    private final VacinaRepository vacinaRepository;

    @Override
    @Transactional(readOnly = true)
    public String imprimirAtendimento(Long atendimentoId) {
        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento não encontrado."));
        Consulta consulta = atendimento.getConsulta();
        String corpo = secaoAnimal(consulta.getAnimal())
                + secaoRegistro(atendimento)
                + """
                <div class="assinatura"><div>%s &mdash; %s</div></div>
                """.formatted(esc(consulta.getVeterinario().getNome()), esc(consulta.getVeterinario().getCrmv()));
        return pagina("Registro de Atendimento nº " + atendimento.getId(), corpo);
    }

    @Override
    @Transactional(readOnly = true)
    public String imprimirHistoricoDoAnimal(Long animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado."));
        List<Atendimento> atendimentos = atendimentoRepository.buscarHistoricoDoAnimal(animalId);
        List<Vacina> vacinas = vacinaRepository.findByAnimalIdOrderByDataAplicacaoDesc(animalId);

        String registros = atendimentos.isEmpty()
                ? "<h2>Atendimentos</h2><p>Nenhum atendimento registrado.</p>"
                : atendimentos.stream().map(this::secaoRegistro).collect(Collectors.joining());
        return pagina("Prontuário de " + animal.getNome(), secaoAnimal(animal) + registros + secaoVacinas(vacinas));
    }

    private String secaoAnimal(Animal animal) {
        return """
                <h2>Paciente</h2>
                <p><strong>Nome:</strong> %s</p>
                <p><strong>Espécie / raça:</strong> %s / %s</p>
                <p><strong>Nascimento:</strong> %s</p>
                <p><strong>Tutor:</strong> %s &mdash; %s</p>
                """.formatted(
                esc(animal.getNome()),
                esc(animal.getEspecie().getNome()),
                esc(animal.getRaca()),
                animal.getDataNascimento().format(DATA),
                esc(animal.getTutor().getNome()),
                esc(animal.getTutor().getTelefone()));
    }

    private String secaoRegistro(Atendimento atendimento) {
        Consulta consulta = atendimento.getConsulta();
        return """
                <section class="registro">
                <h2>Atendimento de %s às %s</h2>
                <p><strong>Local:</strong> %s</p>
                <p><strong>Veterinário:</strong> %s (%s)</p>
                <h3>Diagnóstico</h3>
                <p>%s</p>
                <h3>Observações</h3>
                <p>%s</p>
                <h3>Orientações</h3>
                <p>%s</p>
                </section>
                """.formatted(
                consulta.getData().format(DATA),
                consulta.getHorario().format(HORA),
                esc(consulta.getLocal()),
                esc(consulta.getVeterinario().getNome()),
                esc(consulta.getVeterinario().getCrmv()),
                esc(atendimento.getDiagnostico()),
                esc(atendimento.getObservacoes()),
                esc(atendimento.getOrientacoes()));
    }

    private String secaoVacinas(List<Vacina> vacinas) {
        if (vacinas.isEmpty()) {
            return "<h2>Vacinas</h2><p>Nenhuma vacina registrada.</p>";
        }
        String linhas = vacinas.stream()
                .map(vacina -> "<tr><td>%s</td><td>%s</td><td>%s</td></tr>".formatted(
                        esc(vacina.getNome()), vacina.getDataAplicacao().format(DATA), esc(vacina.getLote())))
                .collect(Collectors.joining());
        return """
                <section class="registro">
                <h2>Vacinas</h2>
                <table>
                <thead><tr><th>Vacina</th><th>Aplicação</th><th>Lote</th></tr></thead>
                <tbody>%s</tbody>
                </table>
                </section>
                """.formatted(linhas);
    }

    private String pagina(String titulo, String corpo) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                <meta charset="UTF-8">
                <title>%1$s</title>
                <style>%2$s</style>
                </head>
                <body>
                <button class="imprimir" onclick="window.print()">Imprimir</button>
                <header>
                <h1>PetCare</h1>
                <p>%1$s</p>
                </header>
                %3$s
                </body>
                </html>
                """.formatted(esc(titulo), ESTILO, corpo);
    }

    private String esc(String valor) {
        return valor == null || valor.isBlank() ? "&mdash;" : HtmlUtils.htmlEscape(valor, "UTF-8");
    }
}
