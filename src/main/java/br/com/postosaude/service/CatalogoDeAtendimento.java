package br.com.postosaude.service;

import br.com.postosaude.model.Agendamento;
import br.com.postosaude.model.Especialidade;
import br.com.postosaude.model.Paciente;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço central do sistema de agendamentos do posto de saúde.
 *
 * Responsabilidades:
 *  - Gerenciar a lista de agendamentos (adicionar, remover, buscar)
 *  - Gerenciar as especialidades disponíveis
 *  - Persistir os dados em arquivo
 */

public class CatalogoDeAtendimento {


    // ATRIBUTOS
    private final ArrayList<Agendamento> agendamentos;
    private final ArrayList<Especialidade> especialidades;


    // CONSTRUTOR
    public CatalogoDeAtendimento() {
        this.agendamentos = new ArrayList<>();
        this.especialidades = new ArrayList<>();
    }


    // ESPECIALIDADES
    /** Registra uma nova especialidade no catálogo. */
    public void adicionarEspecialidade(Especialidade especialidade) {
        if (especialidade == null) {
            throw new IllegalArgumentException("Especialidade não pode ser nula.");
        }
        especialidades.add(especialidade);
    }

    /** Retorna a lista completa de especialidades (somente leitura). */
    public List<Especialidade> getEspecialidades() {
        return new ArrayList<>(especialidades); // cópia defensiva
    }

    /**
     * Retorna uma especialidade pelo índice exibido no menu (base 1).
     * Lança IndexOutOfBoundsException se o índice for inválido
     * (a Principal.java já captura esse erro e exibe mensagem amigável).
     */
    public Especialidade getEspecialidadePorIndice(int indiceMais1) {
        return especialidades.get(indiceMais1 - 1);
    }

    // AGENDAMENTOS — CRIAÇÃO
    /**
     * Realiza um agendamento completo:
     *  1. Valida os dados (nome, horário disponível)
     *  2. Remove o horário da lista de disponíveis
     *  3. Cria Paciente e Agendamento
     *  4. Adiciona à lista interna
     *
     * @return o Agendamento criado
     */
    public Agendamento agendar(String nomePaciente, String cpfPaciente, String dia, Especialidade especialidade, int indiceHorario) {

        // Valida nome (regra de negócio: mínimo 4 caracteres)
        if (nomePaciente == null || nomePaciente.trim().length() <= 3) {
            throw new IllegalArgumentException("Nome muito curto. Digite ao menos 4 caracteres.");
        }

        List<String> horarios = especialidade.getHorariosDisponiveis();

        // Valida índice do horário
        if (indiceHorario < 1 || indiceHorario > horarios.size()) {
            throw new IndexOutOfBoundsException("Horário inválido. Escolha um horário da lista.");
        }

        String horarioEscolhido = horarios.get(indiceHorario - 1);

        // Remove o horário para que não seja agendado novamente
        horarios.remove(indiceHorario - 1);

        Paciente paciente = new Paciente(nomePaciente.trim(), cpfPaciente);
        Agendamento agendamento = new Agendamento(paciente, dia, especialidade, horarioEscolhido);

        agendamentos.add(agendamento);
        return agendamento;
    }


    // AGENDAMENTOS — LEITURA
    /** Retorna todos os agendamentos (cópia defensiva). */
    public List<Agendamento> listarTodos() {
        return new ArrayList<>(agendamentos);
    }

    /**
     * Busca agendamentos pelo nome do paciente (case-insensitive).
     *
     * @param nome nome a ser pesquisado
     * @return lista de agendamentos que correspondem ao nome
     */
    public List<Agendamento> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        }

        List<Agendamento> resultado = new ArrayList<>();
        for (Agendamento a : agendamentos) {
            if (a.getPaciente().getNome().equalsIgnoreCase(nome.trim())) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    /** Retorna true se não há nenhum agendamento cadastrado. */
    public boolean estaVazio() {
        return agendamentos.isEmpty();
    }

    /** Retorna a quantidade total de agendamentos. */
    public int totalDeAgendamentos() {
        return agendamentos.size();
    }


    // AGENDAMENTOS — REMOÇÃO (CANCELAMENTO)
    /**
     * Cancela o primeiro agendamento encontrado para o nome informado.
     * Devolve o horário cancelado à lista de disponíveis da especialidade.
     *
     * @param nome nome do paciente
     * @return o Agendamento cancelado, ou null se não encontrado
     */
    public Agendamento cancelarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        }

        for (int i = 0; i < agendamentos.size(); i++) {
            Agendamento a = agendamentos.get(i);
            if (a.getPaciente().getNome().equalsIgnoreCase(nome.trim())) {
                // Devolve o horário para a especialidade
                a.getEspecialidade().adicionarHorario(a.getHorario());
                agendamentos.remove(i);
                return a; // retorna o agendamento cancelado para exibição
            }
        }
        return null; // não encontrado
    }

    // PERSISTÊNCIA
    /**
     * Salva todos os agendamentos em um arquivo de texto CSV simples.
     * Usa try-with-resources para fechar o arquivo automaticamente.
     *
     * Formato de cada linha:
     *   nome;cpf;dia;especialidade;horario
     *
     * @param nomeArquivo caminho/nome do arquivo de destino
     */
    public void salvarEmArquivo(String nomeArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (Agendamento a : agendamentos) {
                String linha = a.getPaciente().getNome()    + ";" +
                        a.getPaciente().getCpf()     + ";" +
                        a.getDia()                   + ";" +
                        a.getEspecialidade().getNome() + ";" +
                        a.getHorario();
                bw.write(linha);
                bw.newLine();
            }
            System.out.println(" Dados salvos com sucesso em: " + nomeArquivo);
        } catch (IOException e) {
            System.out.println(" ERRO ao salvar dados: " + e.getMessage());
        }
    }
}