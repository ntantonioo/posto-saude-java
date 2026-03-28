package br.com.postosaude.main;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import br.com.postosaude.model.Agendamento;
import br.com.postosaude.model.Especialidade;
import br.com.postosaude.service.CatalogoDeAtendimento;

/**
 * Ponto de entrada do sistema.
 * Esta classe é responsável APENAS pela interface com o usuário:
 *  - exibir menus
 *  - ler entradas do teclado
 *  - tratar exceções de entrada (InputMismatchException)
 *  - exibir resultados
 */

public class Principal {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);


        // Instancia o serviço central

        CatalogoDeAtendimento catalogo = new CatalogoDeAtendimento();

        // Criação das especialidades (clinico, pediatria, odontologia, nutrologia)
        Especialidade clinico     = new Especialidade("Clínico Geral");
        Especialidade pediatria   = new Especialidade("Pediatria");
        Especialidade odontologia = new Especialidade("Odontologia");
        Especialidade nutrologia  = new Especialidade("Nutrologia");

        // Adição de horários (Específicos)
        clinico.adicionarHorario("08:00");
        clinico.adicionarHorario("09:00");
        pediatria.adicionarHorario("13:00");
        odontologia.adicionarHorario("16:00");
        nutrologia.adicionarHorario("19:00");

        catalogo.adicionarEspecialidade(clinico);
        catalogo.adicionarEspecialidade(pediatria);
        catalogo.adicionarEspecialidade(odontologia);
        catalogo.adicionarEspecialidade(nutrologia);


        // Loop principal do menu
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n--- SISTEMA DO POSTO DE SAÚDE ---");
            System.out.println("1 - Agendar Consulta");
            System.out.println("2 - Listar Todas as Consultas");
            System.out.println("3 - Buscar por Paciente");
            System.out.println("4 - Cancelar Consulta");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            // EXCEÇÃO 1: letra/símbolo em vez de número
            try {
                opcao = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(" ERRO: Digite apenas um número inteiro!");
                teclado.nextLine();
                opcao = -1;
                continue;
            }
            teclado.nextLine();


            // OPÇÃO 1 — AGENDAR
            if (opcao == 1) {
                System.out.println("\nNome do Paciente:");
                String nome = teclado.nextLine();

                System.out.println("CPF:");
                String cpf = teclado.nextLine();

                System.out.println("Dia da consulta (ex: 15/03):");
                String dia = teclado.nextLine();

                // Exibe especialidades disponíveis
                List<Especialidade> especialidades = catalogo.getEspecialidades();
                System.out.println("\nEscolha a especialidade:");
                for (int i = 0; i < especialidades.size(); i++) {
                    System.out.println((i + 1) + " - " + especialidades.get(i).getNome());
                }

                // EXCEÇÃO 2: letra ao escolher especialidade
                int escolhaEsp;
                try {
                    escolhaEsp = teclado.nextInt();
                    teclado.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println(" ERRO: Digite apenas o número da especialidade!");
                    teclado.nextLine();
                    continue;
                }

                // EXCEÇÃO 3: índice fora do intervalo
                Especialidade espEscolhida;
                try {
                    espEscolhida = catalogo.getEspecialidadePorIndice(escolhaEsp);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(" ERRO: Especialidade inválida. Escolha uma opção da lista.");
                    continue;
                }

                List<String> horarios = espEscolhida.getHorariosDisponiveis();
                if (horarios.isEmpty()) {
                    System.out.println("Não há horários disponíveis para esta especialidade.");
                    continue;
                }

                System.out.println("\nHorários disponíveis:");
                for (int i = 0; i < horarios.size(); i++) {
                    System.out.println((i + 1) + " - " + horarios.get(i));
                }

                // EXCEÇÃO 4: letra ao escolher horário
                int escolhaHorario;
                try {
                    escolhaHorario = teclado.nextInt();
                    teclado.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println(" ERRO: Digite apenas o número do horário!");
                    teclado.nextLine();
                    continue;
                }

                // EXCEÇÃO 5 e 6: nome inválido ou índice de horário fora do intervalo
                // (ambos lançados dentro do catalogo.agendar())
                try {
                    Agendamento novo = catalogo.agendar(nome, cpf, dia, espEscolhida, escolhaHorario);
                    System.out.println(" Agendamento realizado com sucesso! → " + novo);
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    System.out.println(" ERRO: " + e.getMessage());
                }
            }

            // OPÇÃO 2 — LISTAR
            else if (opcao == 2) {
                System.out.println("\n--- LISTA DE AGENDAMENTOS (" +
                        catalogo.totalDeAgendamentos() + ") ---");
                if (catalogo.estaVazio()) {
                    System.out.println("Nenhum agendamento encontrado.");
                } else {
                    for (Agendamento a : catalogo.listarTodos()) {
                        System.out.println(a);
                    }
                }
            }

            // OPÇÃO 3 — BUSCAR
            else if (opcao == 3) {
                System.out.println("Digite o nome para busca:");
                String nomeBusca = teclado.nextLine();

                try {
                    List<Agendamento> encontrados = catalogo.buscarPorNome(nomeBusca);
                    if (encontrados.isEmpty()) {
                        System.out.println("Nenhum agendamento para este nome.");
                    } else {
                        for (Agendamento a : encontrados) {
                            System.out.println("Consulta encontrada: " + a);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(" ERRO: " + e.getMessage());
                }
            }

            // OPÇÃO 4 — CANCELAR
            else if (opcao == 4) {
                System.out.println("Digite o nome do paciente para CANCELAR:");
                String nomeRemover = teclado.nextLine();

                try {
                    Agendamento cancelado = catalogo.cancelarPorNome(nomeRemover);
                    if (cancelado != null) {
                        System.out.println(" Consulta removida! O horário " +
                                cancelado.getHorario() + " voltou para a lista.");
                    } else {
                        System.out.println("Paciente não encontrado.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(" ERRO: " + e.getMessage());
                }
            }
        }

        // Ao sair, salva os dados via serviço
        if (opcao == 0) {
            catalogo.salvarEmArquivo("consultas.txt");
            System.out.println("Sistema encerrado.");
        }

        teclado.close();
    }
}