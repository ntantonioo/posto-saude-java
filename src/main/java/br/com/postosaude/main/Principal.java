package br.com.postosaude.main;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

import br.com.postosaude.model.Paciente;
import br.com.postosaude.model.Especialidade;
import br.com.postosaude.model.Agendamento;

public class Principal {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        // Criação de listas para o armazenamento de agendamentos
        ArrayList<Agendamento> listaAgendamentos = new ArrayList<>();
        ArrayList<Especialidade> especialidades = new ArrayList<>();

        // Criando especialidades
        Especialidade clinico = new Especialidade("Clínico Geral");
        Especialidade pediatria = new Especialidade("Pediatria");
        Especialidade odontologia = new Especialidade("Odontologia");
        Especialidade nutrologia = new Especialidade("Nutrologia");

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

        while (opcao != 0) {
            System.out.println("\n--- SISTEMA DO POSTO DE SAÚDE ---");
            System.out.println("1 - Agendar Consulta (Cadastrar)");
            System.out.println("2 - Listar Todas as Consultas");
            System.out.println("3 - Buscar por Paciente");
            System.out.println("4 - Cancelar Consulta (Devolver Horário)");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            opcao = teclado.nextInt();
            teclado.nextLine();

            // CADASTRO
            if (opcao == 1) {
                System.out.println("\nNome do Paciente:");
                String nome = teclado.nextLine();

                System.out.println("CPF:");
                String cpf = teclado.nextLine();

                System.out.println("Dia da consulta (ex: 15/03):");
                String dia = teclado.nextLine();

                System.out.println("\nEscolha a especialidade:");
                for (int i = 0; i < especialidades.size(); i++) {
                    System.out.println((i + 1) + " - " + especialidades.get(i).getNome());
                }
                int escolhaEsp = teclado.nextInt();
                teclado.nextLine();

                Especialidade espEscolhida = especialidades.get(escolhaEsp - 1);
                List<String> horarios = espEscolhida.getHorariosDisponiveis();

                if (horarios.isEmpty()) {
                    System.out.println("Não há horários para esta especialidade.");
                    continue;
                }

                System.out.println("\nHorários disponíveis:");
                for (int i = 0; i < horarios.size(); i++) {
                    System.out.println((i + 1) + " - " + horarios.get(i));
                }

                int escolhaHorario = teclado.nextInt();
                teclado.nextLine();

                String horarioEscolhido = horarios.get(escolhaHorario - 1);

                // Remove do catálogo da especialidade
                horarios.remove(escolhaHorario - 1);

                // CRIAÇÃO DOS OBJETOS RELACIONADOS
                Paciente novoPaciente = new Paciente(nome, cpf);
                Agendamento novoAgendamento = new Agendamento(novoPaciente, dia, espEscolhida, horarioEscolhido);

                listaAgendamentos.add(novoAgendamento);
                System.out.println("Agendamento realizado com sucesso!");
            }

            //LISTAR AGENDAMENTOS
            else if (opcao == 2) {
                System.out.println("\n--- LISTA DE AGENDAMENTOS ---");
                if (listaAgendamentos.isEmpty()) {
                    System.out.println("Nenhum agendamento encontrado.");
                } else {
                    for (Agendamento a : listaAgendamentos) {
                        System.out.println(a); // Usa o toString de Agendamento
                    }
                }
            }

            //BUSCAR NOME
            else if (opcao == 3) {
                System.out.println("Digite o nome para busca:");
                String nomeBusca = teclado.nextLine();
                boolean encontrado = false;

                for (Agendamento a : listaAgendamentos) {
                    if (a.getPaciente().getNome().equalsIgnoreCase(nomeBusca)) {
                        System.out.println("Consulta encontrada: " + a);
                        encontrado = true;
                    }
                }
                if (!encontrado) System.out.println("Nenhum agendamento para este nome.");
            }

            //CANCELAR CONSULTA
            else if (opcao == 4) {
                System.out.println("Digite o nome do paciente para CANCELAR:");
                String nomeRemover = teclado.nextLine();
                boolean removido = false;

                for (int i = 0; i < listaAgendamentos.size(); i++) {
                    Agendamento agenda = listaAgendamentos.get(i);

                    if (agenda.getPaciente().getNome().equalsIgnoreCase(nomeRemover)) {
                        // DEVOLUÇÃO DO HORÁRIO
                        String h = agenda.getHorario();
                        agenda.getEspecialidade().adicionarHorario(h);

                        listaAgendamentos.remove(i);
                        System.out.println("Consulta removida! O horário " + h + " voltou para a lista.");
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

    public static void salvarDados(ArrayList<Agendamento> lista){
        String nomeArquivo = "consultas.txt";

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
            System.out.println("Dados salvos com sucesso em " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados. " + e.getMessage());
        }
    }
}