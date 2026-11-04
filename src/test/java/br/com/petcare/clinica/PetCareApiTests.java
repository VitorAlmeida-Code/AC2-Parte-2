package br.com.petcare.clinica;

import br.com.petcare.clinica.models.Animal;
import br.com.petcare.clinica.models.Consulta;
import br.com.petcare.clinica.models.StatusConsulta;
import br.com.petcare.clinica.models.Veterinario;
import br.com.petcare.clinica.repositories.AnimalRepository;
import br.com.petcare.clinica.repositories.AtendimentoRepository;
import br.com.petcare.clinica.repositories.ConsultaRepository;
import br.com.petcare.clinica.repositories.EspecieRepository;
import br.com.petcare.clinica.repositories.TutorRepository;
import br.com.petcare.clinica.repositories.VacinaRepository;
import br.com.petcare.clinica.repositories.VeterinarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetCareApiTests {

    private static final String CPF_1 = "123.456.789-09";
    private static final String CPF_1_SEM_MASCARA = "12345678909";
    private static final String CPF_2 = "111.444.777-35";
    private static final LocalDate DATA_FUTURA = LocalDate.now().plusDays(30);

    @Autowired private MockMvc mvc;
    @Autowired private AtendimentoRepository atendimentoRepository;
    @Autowired private VacinaRepository vacinaRepository;
    @Autowired private ConsultaRepository consultaRepository;
    @Autowired private AnimalRepository animalRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private EspecieRepository especieRepository;

    private long cao;
    private long gato;
    private long tutor;
    private long rex;
    private long mia;
    private long veterinario;

    @BeforeEach
    void prepararBase() throws Exception {
        atendimentoRepository.deleteAll();
        vacinaRepository.deleteAll();
        consultaRepository.deleteAll();
        animalRepository.deleteAll();
        veterinarioRepository.deleteAll();
        tutorRepository.deleteAll();
        especieRepository.deleteAll();

        cao = criarEspecie("Cão");
        gato = criarEspecie("Gato");
        tutor = criarTutor(CPF_1);
        rex = criarAnimal("Rex", cao, tutor);
        mia = criarAnimal("Mia", gato, tutor);
        veterinario = criarVeterinario("CRMV-SP 12345", cao);
    }

    @Nested
    class Tutores {

        @Test
        void guardaCpfSemMascara() throws Exception {
            mvc.perform(get("/api/tutor/{id}", tutor))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cpf").value(CPF_1_SEM_MASCARA));
        }

        @Test
        void recusaCpfInvalido() throws Exception {
            enviarPost("/api/tutor", corpoTutor("123.456.789-00"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem("Informe um CPF válido")));
        }

        @Test
        void recusaCpfDuplicadoMesmoComOutraMascara() throws Exception {
            enviarPost("/api/tutor", corpoTutor(CPF_1_SEM_MASCARA))
                    .andExpect(status().isConflict());
        }

        @Test
        void recusaCamposEmBranco() throws Exception {
            enviarPost("/api/tutor", "{}")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasSize(4)));
        }

        @Test
        void buscaPorParteDoNome() throws Exception {
            mvc.perform(get("/api/tutor").param("nome", "maria"))
                    .andExpect(jsonPath("$", hasSize(1)));
            mvc.perform(get("/api/tutor").param("nome", "inexistente"))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void tutorPossuiVariosAnimais() throws Exception {
            mvc.perform(get("/api/tutor/{id}/animais", tutor))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].nome").value("Mia"))
                    .andExpect(jsonPath("$[1].nome").value("Rex"));
        }

        @Test
        void editaOutroCpfJaUsadoRetornaConflito() throws Exception {
            long outro = criarTutor(CPF_2);
            mvc.perform(put("/api/tutor/{id}", outro).contentType(MediaType.APPLICATION_JSON).content(corpoTutor(CPF_1)))
                    .andExpect(status().isConflict());
        }

        @Test
        void naoRemoveTutorComAnimais() throws Exception {
            mvc.perform(delete("/api/tutor/{id}", tutor)).andExpect(status().isConflict());
        }

        @Test
        void removeTutorSemAnimais() throws Exception {
            long outro = criarTutor(CPF_2);
            mvc.perform(delete("/api/tutor/{id}", outro)).andExpect(status().isNoContent());
            mvc.perform(get("/api/tutor/{id}", outro)).andExpect(status().isNotFound());
        }

        @Test
        void idNaoNumericoRetornaBadRequest() throws Exception {
            mvc.perform(get("/api/tutor/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value(containsString("id")));
        }
    }

    @Nested
    class Animais {

        @Test
        void recusaNascimentoNoFuturo() throws Exception {
            enviarPost("/api/animal", corpoAnimal("Bob", cao, tutor, DATA_FUTURA))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem("Data de nascimento não pode estar no futuro")));
        }

        @Test
        void recusaTutorInexistente() throws Exception {
            enviarPost("/api/animal", corpoAnimal("Bob", cao, 9999, LocalDate.of(2020, 1, 1)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void recusaEspecieInexistente() throws Exception {
            enviarPost("/api/animal", corpoAnimal("Bob", 9999, tutor, LocalDate.of(2020, 1, 1)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void listaComEspecieETutor() throws Exception {
            mvc.perform(get("/api/animal"))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[1].especie.nome").value("Cão"))
                    .andExpect(jsonPath("$[1].tutor.id").value(tutor));
        }

        @Test
        void naoTrocaEspecieComConsultaAgendada() throws Exception {
            agendar(rex, veterinario, DATA_FUTURA, "10:00").andExpect(status().isCreated());
            mvc.perform(put("/api/animal/{id}", rex).contentType(MediaType.APPLICATION_JSON)
                            .content(corpoAnimal("Rex", gato, tutor, LocalDate.of(2020, 1, 1))))
                    .andExpect(status().isConflict());
        }

        @Test
        void naoRemoveAnimalComVacina() throws Exception {
            registrarVacina(rex, LocalDate.now()).andExpect(status().isCreated());
            mvc.perform(delete("/api/animal/{id}", rex)).andExpect(status().isConflict());
        }
    }

    @Nested
    class Veterinarios {

        @Test
        void recusaCrmvForaDoFormato() throws Exception {
            enviarPost("/api/veterinario", corpoVeterinario("12345", cao))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem("CRMV deve seguir o formato CRMV-UF 12345")));
        }

        @Test
        void recusaCrmvDuplicado() throws Exception {
            enviarPost("/api/veterinario", corpoVeterinario("CRMV-SP 12345", cao))
                    .andExpect(status().isConflict());
        }

        @Test
        void recusaSemEspecies() throws Exception {
            enviarPost("/api/veterinario", corpoVeterinario("CRMV-SP 777"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void recusaEspecieInexistente() throws Exception {
            enviarPost("/api/veterinario", corpoVeterinario("CRMV-SP 777", cao, 9999))
                    .andExpect(status().isNotFound());
        }

        @Test
        void aceitaEspecieRepetidaUmaVezSo() throws Exception {
            enviarPost("/api/veterinario", corpoVeterinario("CRMV-SP 777", cao, cao))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.especies", hasSize(1)));
        }

        @Test
        void filtraHabilitadosPorEspecie() throws Exception {
            criarVeterinario("CRMV-SP 777", gato);
            mvc.perform(get("/api/veterinario").param("especieId", String.valueOf(gato)))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].crmv").value("CRMV-SP 777"));
            mvc.perform(get("/api/veterinario"))
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        void naoRemoveHabilitacaoComConsultaAgendadaDaquelaEspecie() throws Exception {
            agendar(rex, veterinario, DATA_FUTURA, "10:00").andExpect(status().isCreated());
            mvc.perform(put("/api/veterinario/{id}", veterinario).contentType(MediaType.APPLICATION_JSON)
                            .content(corpoVeterinario("CRMV-SP 12345", gato)))
                    .andExpect(status().isConflict());
        }

        @Test
        void removeHabilitacaoDepoisDeCancelarConsulta() throws Exception {
            long consulta = idDe(agendar(rex, veterinario, DATA_FUTURA, "10:00").andExpect(status().isCreated()));
            mvc.perform(patch("/api/consulta/{id}/cancelar", consulta)).andExpect(status().isOk());
            mvc.perform(put("/api/veterinario/{id}", veterinario).contentType(MediaType.APPLICATION_JSON)
                            .content(corpoVeterinario("CRMV-SP 12345", gato)))
                    .andExpect(status().isOk());
        }

        @Test
        void naoRemoveVeterinarioComConsulta() throws Exception {
            agendar(rex, veterinario, DATA_FUTURA, "10:00").andExpect(status().isCreated());
            mvc.perform(delete("/api/veterinario/{id}", veterinario)).andExpect(status().isConflict());
        }
    }

    @Nested
    class Especies {

        @Test
        void recusaNomeDuplicadoIgnorandoMaiusculas() throws Exception {
            enviarPost("/api/especie", "{\"nome\": \" cão \"}").andExpect(status().isConflict());
        }

        @Test
        void naoRemoveEspecieEmUso() throws Exception {
            mvc.perform(delete("/api/especie/{id}", cao)).andExpect(status().isConflict());
        }

        @Test
        void removeEspecieSemUso() throws Exception {
            long ave = criarEspecie("Ave");
            mvc.perform(delete("/api/especie/{id}", ave)).andExpect(status().isNoContent());
        }
    }

    @Nested
    class Consultas {

        @Test
        void agendaConsulta() throws Exception {
            agendar(rex, veterinario, DATA_FUTURA, "10:00")
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("AGENDADA"))
                    .andExpect(jsonPath("$.horario").value("10:00"))
                    .andExpect(jsonPath("$.animal.nome").value("Rex"));
        }

        @Test
        void impedeMesmoVeterinarioNoMesmoHorario() throws Exception {
            agendar(rex, veterinario, DATA_FUTURA, "10:00").andExpect(status().isCreated());
            long outroCao = criarAnimal("Thor", cao, tutor);
            agendar(outroCao, veterinario, DATA_FUTURA, "10:00")
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errors[0]").value("Veterinário já possui consulta agendada nesta data e horário."));
        }

        @Test
        void permiteOutroHorarioOuOutroVeterinario() throws Exception {
            agendar(rex, veterinario, DATA_FUTURA, "10:00").andExpect(status().isCreated());
            long outroCao = criarAnimal("Thor", cao, tutor);
            agendar(outroCao, veterinario, DATA_FUTURA, "11:00").andExpect(status().isCreated());
            long outroVeterinario = criarVeterinario("CRMV-SP 777", cao);
            agendar(outroCao, outroVeterinario, DATA_FUTURA, "10:00").andExpect(status().isCreated());
        }

        @Test
        void agendamentosSimultaneosGeramApenasUmaConsulta() throws Exception {
            int tentativas = 8;
            ExecutorService executor = Executors.newFixedThreadPool(tentativas);
            CountDownLatch largada = new CountDownLatch(1);
            List<Future<Integer>> respostas = new ArrayList<>();
            for (int i = 0; i < tentativas; i++) {
                respostas.add(executor.submit(() -> {
                    largada.await();
                    return agendar(rex, veterinario, DATA_FUTURA, "10:00").andReturn().getResponse().getStatus();
                }));
            }
            largada.countDown();

            List<Integer> status = new ArrayList<>();
            for (Future<Integer> resposta : respostas) {
                status.add(resposta.get());
            }
            executor.shutdown();

            assertThat(status).containsOnly(201, 409);
            assertThat(status).filteredOn(s -> s == 201).hasSize(1);
            assertThat(consultaRepository.count()).isEqualTo(1);
        }

        @Test
        void impedeVeterinarioNaoHabilitadoParaEspecie() throws Exception {
            agendar(mia, veterinario, DATA_FUTURA, "10:00")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Veterinário não habilitado para atender a espécie Gato."));
        }

        @Test
        void impedeDataPassada() throws Exception {
            agendar(rex, veterinario, LocalDate.now().minusDays(1), "10:00")
                    .andExpect(status().isBadRequest());
        }

        @Test
        void impedeHorarioJaPassadoHoje() throws Exception {
            agendar(rex, veterinario, LocalDate.now(), "00:00")
                    .andExpect(status().isBadRequest());
        }

        @Test
        void recusaHorarioMalFormatado() throws Exception {
            agendar(rex, veterinario, DATA_FUTURA, "10h00")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value(containsString("HH:mm")));
            agendar(rex, veterinario, DATA_FUTURA, "25:00").andExpect(status().isBadRequest());
        }

        @Test
        void recusaAnimalOuVeterinarioInexistente() throws Exception {
            agendar(9999, veterinario, DATA_FUTURA, "10:00").andExpect(status().isNotFound());
            agendar(rex, 9999, DATA_FUTURA, "10:00").andExpect(status().isNotFound());
        }

        @Test
        void recusaCamposAusentes() throws Exception {
            enviarPost("/api/consulta", "{}")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasSize(5)));
        }

        @Test
        void cancelamentoLiberaOHorario() throws Exception {
            long consulta = idDe(agendar(rex, veterinario, DATA_FUTURA, "10:00"));
            mvc.perform(patch("/api/consulta/{id}/cancelar", consulta))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELADA"));
            agendar(rex, veterinario, DATA_FUTURA, "10:00").andExpect(status().isCreated());
        }

        @Test
        void naoCancelaDuasVezes() throws Exception {
            long consulta = idDe(agendar(rex, veterinario, DATA_FUTURA, "10:00"));
            mvc.perform(patch("/api/consulta/{id}/cancelar", consulta)).andExpect(status().isOk());
            mvc.perform(patch("/api/consulta/{id}/cancelar", consulta)).andExpect(status().isBadRequest());
        }

        @Test
        void naoCancelaConsultaRealizada() throws Exception {
            long consulta = consultaDeOntem();
            registrarAtendimento(consulta).andExpect(status().isCreated());
            mvc.perform(patch("/api/consulta/{id}/cancelar", consulta))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Uma consulta já realizada não pode ser cancelada."));
        }

        @Test
        void cancelarInexistenteRetornaNotFound() throws Exception {
            mvc.perform(patch("/api/consulta/9999/cancelar")).andExpect(status().isNotFound());
        }

        @Test
        void consultaPorId() throws Exception {
            long consulta = idDe(agendar(rex, veterinario, DATA_FUTURA, "10:00"));
            mvc.perform(get("/api/consulta/{id}", consulta))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.veterinario.id").value(veterinario));
        }

        @Test
        void filtraAgenda() throws Exception {
            long cancelada = idDe(agendar(rex, veterinario, DATA_FUTURA, "10:00"));
            mvc.perform(patch("/api/consulta/{id}/cancelar", cancelada)).andExpect(status().isOk());
            agendar(rex, veterinario, DATA_FUTURA, "11:00").andExpect(status().isCreated());
            agendar(rex, veterinario, DATA_FUTURA.plusDays(1), "11:00").andExpect(status().isCreated());

            mvc.perform(get("/api/consulta")).andExpect(jsonPath("$", hasSize(3)));
            mvc.perform(get("/api/consulta").param("status", "AGENDADA")).andExpect(jsonPath("$", hasSize(2)));
            mvc.perform(get("/api/consulta").param("data", DATA_FUTURA.toString())).andExpect(jsonPath("$", hasSize(2)));
            mvc.perform(get("/api/consulta").param("animalId", String.valueOf(mia))).andExpect(jsonPath("$", hasSize(0)));
            mvc.perform(get("/api/consulta")
                            .param("veterinarioId", String.valueOf(veterinario))
                            .param("data", DATA_FUTURA.toString())
                            .param("status", "AGENDADA"))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].horario").value("11:00"));
        }

        @Test
        void filtroComValorInvalidoRetornaBadRequest() throws Exception {
            mvc.perform(get("/api/consulta").param("status", "QUALQUER")).andExpect(status().isBadRequest());
            mvc.perform(get("/api/consulta").param("data", "15/03/2030")).andExpect(status().isBadRequest());
        }
    }

    @Nested
    class Atendimentos {

        @Test
        void registraEMarcaConsultaComoRealizada() throws Exception {
            long consulta = consultaDeOntem();
            registrarAtendimento(consulta)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.diagnostico").value("Otite leve"))
                    .andExpect(jsonPath("$.crmv").value("CRMV-SP 12345"));
            mvc.perform(get("/api/consulta/{id}", consulta))
                    .andExpect(jsonPath("$.status").value("REALIZADA"));
        }

        @Test
        void aceitaConsultaDeHoje() throws Exception {
            long consulta = idDe(agendar(rex, veterinario, LocalDate.now(), "23:59").andExpect(status().isCreated()));
            registrarAtendimento(consulta).andExpect(status().isCreated());
        }

        @Test
        void observacoesSaoOpcionais() throws Exception {
            enviarPost("/api/atendimento", """
                    {"consultaId": %d, "diagnostico": "Saudável", "orientacoes": "Retorno em 1 ano"}
                    """.formatted(consultaDeOntem()))
                    .andExpect(status().isCreated());
        }

        @Test
        void recusaSegundoAtendimentoDaMesmaConsulta() throws Exception {
            long consulta = consultaDeOntem();
            registrarAtendimento(consulta).andExpect(status().isCreated());
            registrarAtendimento(consulta).andExpect(status().isConflict());
        }

        @Test
        void recusaConsultaCancelada() throws Exception {
            long consulta = idDe(agendar(rex, veterinario, DATA_FUTURA, "10:00"));
            mvc.perform(patch("/api/consulta/{id}/cancelar", consulta)).andExpect(status().isOk());
            registrarAtendimento(consulta)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Não é possível registrar atendimento de uma consulta cancelada."));
        }

        @Test
        void recusaConsultaFutura() throws Exception {
            long consulta = idDe(agendar(rex, veterinario, DATA_FUTURA, "10:00"));
            registrarAtendimento(consulta).andExpect(status().isBadRequest());
        }

        @Test
        void recusaConsultaInexistente() throws Exception {
            registrarAtendimento(9999).andExpect(status().isNotFound());
        }

        @Test
        void recusaSemDiagnosticoEOrientacoes() throws Exception {
            enviarPost("/api/atendimento", "{\"consultaId\": " + consultaDeOntem() + "}")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasSize(2)));
        }

        @Test
        void consultaEListaPorAnimal() throws Exception {
            long atendimento = idDe(registrarAtendimento(consultaDeOntem()));
            mvc.perform(get("/api/atendimento/{id}", atendimento)).andExpect(status().isOk());
            mvc.perform(get("/api/atendimento")).andExpect(jsonPath("$", hasSize(1)));
            mvc.perform(get("/api/atendimento").param("animalId", String.valueOf(rex))).andExpect(jsonPath("$", hasSize(1)));
            mvc.perform(get("/api/atendimento").param("animalId", String.valueOf(mia))).andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    class Vacinas {

        @Test
        void registraVacina() throws Exception {
            registrarVacina(rex, LocalDate.of(2024, 5, 10))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.lote").value("LT-2024-A"));
        }

        @Test
        void recusaDataFutura() throws Exception {
            registrarVacina(rex, DATA_FUTURA).andExpect(status().isBadRequest());
        }

        @Test
        void recusaDataAnteriorAoNascimento() throws Exception {
            registrarVacina(rex, LocalDate.of(2019, 12, 31))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Data de aplicação não pode ser anterior ao nascimento do animal."));
        }

        @Test
        void recusaSemLote() throws Exception {
            enviarPost("/api/vacina", """
                    {"animalId": %d, "nome": "V10", "dataAplicacao": "2024-05-10"}
                    """.formatted(rex))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem("Lote é obrigatório")));
        }

        @Test
        void recusaAnimalInexistente() throws Exception {
            registrarVacina(9999, LocalDate.of(2024, 5, 10)).andExpect(status().isNotFound());
        }

        @Test
        void listaPorAnimalDaMaisRecenteParaAMaisAntiga() throws Exception {
            registrarVacina(rex, LocalDate.of(2022, 1, 1)).andExpect(status().isCreated());
            registrarVacina(rex, LocalDate.of(2024, 1, 1)).andExpect(status().isCreated());
            mvc.perform(get("/api/vacina").param("animalId", String.valueOf(rex)))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].dataAplicacao").value("2024-01-01"));
        }

        @Test
        void listarSemInformarAnimalRetornaBadRequest() throws Exception {
            mvc.perform(get("/api/vacina"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("O parâmetro 'animalId' é obrigatório."));
        }
    }

    @Nested
    class Prontuario {

        @Test
        void historicoReuneAtendimentosEVacinasDoAnimal() throws Exception {
            registrarAtendimento(consultaDeOntem()).andExpect(status().isCreated());
            registrarVacina(rex, LocalDate.of(2024, 5, 10)).andExpect(status().isCreated());
            registrarVacina(mia, LocalDate.of(2024, 5, 10)).andExpect(status().isCreated());

            mvc.perform(get("/api/animal/{id}/historico", rex))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.animal.nome").value("Rex"))
                    .andExpect(jsonPath("$.atendimentos", hasSize(1)))
                    .andExpect(jsonPath("$.vacinas", hasSize(1)));
        }

        @Test
        void historicoVazioEHistoricoDeAnimalInexistente() throws Exception {
            mvc.perform(get("/api/animal/{id}/historico", mia))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.atendimentos", hasSize(0)))
                    .andExpect(jsonPath("$.vacinas", hasSize(0)));
            mvc.perform(get("/api/animal/9999/historico")).andExpect(status().isNotFound());
        }

        @Test
        void impressaoDoAtendimentoEmHtmlComAcentos() throws Exception {
            long atendimento = idDe(registrarAtendimento(consultaDeOntem()));
            String html = mvc.perform(get("/api/atendimento/{id}/impressao", atendimento))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            assertThat(html).contains("Registro de Atendimento nº", "Otite leve", "Rex", "CRMV-SP 12345", "window.print()");
        }

        @Test
        void impressaoEscapaHtmlDigitadoPeloUsuario() throws Exception {
            long consulta = consultaDeOntem();
            String corpo = """
                    {"consultaId": %d, "diagnostico": "<script>alert(1)</script>", "orientacoes": "Repouso"}
                    """.formatted(consulta);
            long atendimento = idDe(enviarPost("/api/atendimento", corpo));
            mvc.perform(get("/api/atendimento/{id}/impressao", atendimento))
                    .andExpect(content().string(not(containsString("<script>alert"))))
                    .andExpect(content().string(containsString("&lt;script&gt;alert(1)&lt;/script&gt;")));
        }

        @Test
        void impressaoDoHistoricoComEsemRegistros() throws Exception {
            mvc.perform(get("/api/animal/{id}/historico/impressao", mia))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Nenhum atendimento registrado.")));

            registrarAtendimento(consultaDeOntem()).andExpect(status().isCreated());
            registrarVacina(rex, LocalDate.of(2024, 5, 10)).andExpect(status().isCreated());
            mvc.perform(get("/api/animal/{id}/historico/impressao", rex))
                    .andExpect(content().string(containsString("Otite leve")))
                    .andExpect(content().string(containsString("LT-2024-A")));
        }

        @Test
        void impressaoInexistenteRetornaJsonDeErroMesmoPedindoHtml() throws Exception {
            mvc.perform(get("/api/atendimento/9999/impressao").accept(MediaType.TEXT_HTML))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors[0]").value("Atendimento não encontrado."));
        }
    }

    private long criarEspecie(String nome) throws Exception {
        return idDe(enviarPost("/api/especie", "{\"nome\": \"" + nome + "\"}"));
    }

    private long criarTutor(String cpf) throws Exception {
        return idDe(enviarPost("/api/tutor", corpoTutor(cpf)));
    }

    private long criarAnimal(String nome, long especie, long dono) throws Exception {
        return idDe(enviarPost("/api/animal", corpoAnimal(nome, especie, dono, LocalDate.of(2020, 1, 1))));
    }

    private long criarVeterinario(String crmv, long... especies) throws Exception {
        return idDe(enviarPost("/api/veterinario", corpoVeterinario(crmv, especies)));
    }

    /** Agendar no passado é proibido pela API, por isso a consulta de ontem é gravada direto no banco. */
    private long consultaDeOntem() {
        Animal animal = animalRepository.findById(rex).orElseThrow();
        Veterinario medico = veterinarioRepository.findById(veterinario).orElseThrow();
        Consulta consulta = new Consulta();
        consulta.setAnimal(animal);
        consulta.setVeterinario(medico);
        consulta.setLocal("Sala 1");
        consulta.setData(LocalDate.now().minusDays(1));
        consulta.setHorario(LocalTime.of(9, 0));
        consulta.setStatus(StatusConsulta.AGENDADA);
        return consultaRepository.save(consulta).getId();
    }

    private ResultActions agendar(long animal, long medico, LocalDate data, String horario) throws Exception {
        return enviarPost("/api/consulta", """
                {"animalId": %d, "veterinarioId": %d, "local": "Sala 1", "data": "%s", "horario": "%s"}
                """.formatted(animal, medico, data, horario));
    }

    private ResultActions registrarAtendimento(long consulta) throws Exception {
        return enviarPost("/api/atendimento", """
                {"consultaId": %d, "diagnostico": "Otite leve", "observacoes": "Sem febre", "orientacoes": "Limpar por 7 dias"}
                """.formatted(consulta));
    }

    private ResultActions registrarVacina(long animal, LocalDate data) throws Exception {
        return enviarPost("/api/vacina", """
                {"animalId": %d, "nome": "V10", "dataAplicacao": "%s", "lote": "LT-2024-A"}
                """.formatted(animal, data));
    }

    private ResultActions enviarPost(String url, String corpo) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    private long idDe(ResultActions resposta) throws Exception {
        String corpo = resposta.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return ((Number) JsonPath.read(corpo, "$.id")).longValue();
    }

    private String corpoTutor(String cpf) {
        return """
                {"nome": "Maria Souza", "cpf": "%s", "telefone": "(11) 98888-7777", "endereco": "Rua das Flores, 100"}
                """.formatted(cpf);
    }

    private String corpoAnimal(String nome, long especie, long dono, LocalDate nascimento) {
        return """
                {"nome": "%s", "especieId": %d, "raca": "SRD", "dataNascimento": "%s", "tutorId": %d}
                """.formatted(nome, especie, nascimento, dono);
    }

    private String corpoVeterinario(String crmv, long... especies) {
        return """
                {"nome": "Dra. Ana Lima", "crmv": "%s", "especiesIds": %s}
                """.formatted(crmv, java.util.Arrays.toString(especies));
    }
}
