package br.com.postosaude.main;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.InputMismatchException; // - necessário para capturar erro quando o tipo não é o esperado
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

import br.com.postosaude.model.Paciente;
import br.com.postosaude.model.Especialidade;
import br.com.postosaude.model.Agendamento;

public class Principal {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        ArrayList<Agendamento> listaAgendamentos = new ArrayList<>();
        ArrayList<Especialidade> especialidades = new ArrayList<>();

        // DEFINIÇÃO DAS ESPECIALIDADES
        Especialidade clinico = new Especialidade("Clínico Geral");
        Especialidade pediatria = new Especialidade("Pediatria");
        Especialidade odontologia = new Especialidade("Odontologia");
        Especialidade nutrologia = new Especialidade("Nutrologia");

        //ADIÇÃO DE HORÁRIO PARA CADA ESPECIALIDADE
        clinico.adicionarHorario("08:00");
        clinico.adicionarHorario("09:00");
        pediatria.adicionarHorario("13:00");
        odontologia.adicionarHorario("16:00");
        nutrologia.adicionarHorario("19:00");

        especialidades.add(clinico);
        especialidades.add(pediatria);
        especialidades.add(odontologia);
        especialidades.add(nutrologia);

        int opcao = -1;

        //LOOP PARA O MENU GUIADO PELO opcao(-1)
        while (opcao != 0) {
            System.out.println("\n--- SISTEMA DO POSTO DE SAÚDE ---");
            System.out.println("1 - Agendar Consulta");
            System.out.println("2 - Listar Todas as Consultas");
            System.out.println("3 - Buscar por Paciente");
            System.out.println("4 - Cancelar Consulta");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            // EXCEÇÃO 1: usuário digita letra/símbolo em vez de número
            // InputMismatchException é lançada quando o tipo não bate

            try {
                opcao = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(" ERRO: Digite apenas um número inteiro!");
                teclado.nextLine(); // limpa o buffer do teclado para não travar
                opcao = -1;        // define opção inválida para repetir o menu
                continue;          // volta ao início do while
            }
            teclado.nextLine();

            // CADASTRO
            if (opcao == 1) {
                System.out.println("\nNome do Paciente:");
                String nome = teclado.nextLine();


                // EXCEÇÃO 2: nome muito curto (validação manual com exceção)
                // Lançamos uma exceção personalizada com "throw new"

                try {
                    if (nome == null || nome.trim().length() <= 3) {
                        // "throw" = jogar/lançar uma exceção manualmente
                        throw new IllegalArgumentException("Nome muito curto. Digite ao menos 4 caracteres.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(" ERRO: " + e.getMessage());
                    continue; // volta ao menu
                }

                System.out.println("CPF:");
                String cpf = teclado.nextLine();

                System.out.println("Dia da consulta (ex: 15/03):");
                String dia = teclado.nextLine();

                System.out.println("\nEscolha a especialidade:");
                for (int i = 0; i < especialidades.size(); i++) {
                    System.out.println((i + 1) + " - " + especialidades.get(i).getNome());
                }

                int escolhaEsp;


                // EXCEÇÃO 3: digitou letra ao escolher a especialidade
                try {
                    escolhaEsp = teclado.nextInt();
                    teclado.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println(" ERRO: Digite apenas o número da especialidade!");
                    teclado.nextLine();
                    continue;
                }


                // EXCEÇÃO 4: número fora do intervalo de especialidades
                // IndexOutOfBoundsException ocorre ao acessar índice inválido
                Especialidade espEscolhida;
                try {
                    espEscolhida = especialidades.get(escolhaEsp - 1);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(" ERR0: Especialidade inválida. Escolha uma opção da lista.");
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

                int escolhaHorario;


                // EXCEÇÃO 5: digitou letra ao escolher o horário
                try {
                    escolhaHorario = teclado.nextInt();
                    teclado.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println(" ERRO: Digite apenas o número do horário!");
                    teclado.nextLine();
                    continue;
                }


                // EXCEÇÃO 6: número fora do intervalo de horários
                String horarioEscolhido;
                try {
                    horarioEscolhido = horarios.get(escolhaHorario - 1);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(" ERRO: Horário inválido. Escolha um horário da lista.");
                    continue;
                }

                horarios.remove(escolhaHorario - 1);

                Paciente novoPaciente = new Paciente(nome, cpf);
                Agendamento novoAgendamento = new Agendamento(novoPaciente, dia, espEscolhida, horarioEscolhido);

                listaAgendamentos.add(novoAgendamento);
                System.out.println(" Agendamento realizado com sucesso!");
            }

            // LISTAR
            else if (opcao == 2) {
                System.out.println("\n--- LISTA DE AGENDAMENTOS ---");
                if (listaAgendamentos.isEmpty()) {
                    System.out.println("Nenhum agendamento encontrado.");
                } else {
                    for (Agendamento a : listaAgendamentos) {
                        System.out.println(a);
                    }
                }
            }

            // BUSCAR
            else if (opcao == 3) {
                System.out.println("Digite o nome para busca:");
                String nomeBusca = teclado.nextLine();


                // EXCEÇÃO 7: nome vazio na busca
                try {
                    if (nomeBusca == null || nomeBusca.trim().isEmpty()) {
                        throw new IllegalArgumentException("O nome não pode estar vazio.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(" ERRO: " + e.getMessage());
                    continue;
                }

                boolean encontrado = false;
                for (Agendamento a : listaAgendamentos) {
                    if (a.getPaciente().getNome().equalsIgnoreCase(nomeBusca)) {
                        System.out.println("Consulta encontrada: " + a);
                        encontrado = true;
                    }
                }
                if (!encontrado) System.out.println("Nenhum agendamento para este nome.");
            }

            // CANCELAR
            else if (opcao == 4) {
                System.out.println("Digite o nome do paciente para CANCELAR:");
                String nomeRemover = teclado.nextLine();


                // EXCEÇÃO 8: nome vazio no cancelamento
                try {
                    if (nomeRemover == null || nomeRemover.trim().isEmpty()) {
                        throw new IllegalArgumentException("O nome não pode estar vazio.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(" ERRO: " + e.getMessage());
                    continue;
                }

                boolean removido = false;
                for (int i = 0; i < listaAgendamentos.size(); i++) {
                    Agendamento agenda = listaAgendamentos.get(i);
                    if (agenda.getPaciente().getNome().equalsIgnoreCase(nomeRemover)) {
                        String h = agenda.getHorario();
                        agenda.getEspecialidade().adicionarHorario(h);
                        listaAgendamentos.remove(i);
                        System.out.println(" Consulta removida! O horário " + h + " voltou para a lista.");
                        removido = true;
                        break;
                    }
                }
                if (!removido) System.out.println("Paciente não encontrado.");
            }
        }

        if (opcao == 0) {
            salvarDados(listaAgendamentos);
            System.out.println("Sistema encerrado.");
        }

        teclado.close();
    }

    public static void salvarDados(ArrayList<Agendamento> lista) {
        String nomeArquivo = "consultas.txt";


        // EXCEÇÃO 9: erro ao salvar o arquivo (ex: sem permissão de escrita)
        // IOException é lançada automaticamente por operações de arquivo
        // O "try-with-resources" (try com parênteses) fecha o arquivo
        // automaticamente mesmo se der erro — não precisa de finally!
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (Agendamento a : lista) {
                String linha = a.getPaciente().getNome() + ";" +
                        a.getPaciente().getCpf() + ";" +
                        a.getDia() + ";" +
                        a.getEspecialidade().getNome() + ";" +
                        a.getHorario();
                bw.write(linha);
                bw.newLine();
            }
            System.out.println(" Dados salvos com sucesso em " + nomeArquivo);
        } catch (IOException e) {
            // getMessage() retorna a descrição técnica do erro
            System.out.println(" ERRO ao salvar dados: " + e.getMessage());
        }
    }
}