package br.com.postosaude.ui;

import br.com.postosaude.model.*;
import br.com.postosaude.service.CatalogoDeAtendimento;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private CatalogoDeAtendimento catalogo;

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoDia;

    private JComboBox<String> comboEspecialidade;
    private JComboBox<String> comboHorario;

    private JTextArea areaSaida;

    public TelaPrincipal() {

        catalogo = new CatalogoDeAtendimento();

        // ESPECIALIDADES
        Especialidade clinico = new Especialidade("Clínico Geral");
        Especialidade pediatria = new Especialidade("Pediatria");
        Especialidade odontologia = new Especialidade("Odontologia");

        clinico.adicionarHorario("08:00");
        clinico.adicionarHorario("09:00");

        pediatria.adicionarHorario("13:00");

        odontologia.adicionarHorario("16:00");

        catalogo.adicionarEspecialidade(clinico);
        catalogo.adicionarEspecialidade(pediatria);
        catalogo.adicionarEspecialidade(odontologia);


        // CONFIG JANELA
        setTitle("Sistema Posto de Saúde");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // PAINEL DE ENTRADA
        JPanel painelEntrada = new JPanel(new GridLayout(5, 2));

        painelEntrada.add(new JLabel("Nome:"));
        campoNome = new JTextField();
        painelEntrada.add(campoNome);

        painelEntrada.add(new JLabel("CPF:"));
        campoCpf = new JTextField();
        painelEntrada.add(campoCpf);

        painelEntrada.add(new JLabel("Dia:"));
        campoDia = new JTextField();
        painelEntrada.add(campoDia);

        painelEntrada.add(new JLabel("Especialidade:"));
        comboEspecialidade = new JComboBox<>();
        painelEntrada.add(comboEspecialidade);

        painelEntrada.add(new JLabel("Horário:"));
        comboHorario = new JComboBox<>();
        painelEntrada.add(comboHorario);

        add(painelEntrada, BorderLayout.NORTH);

        // ÁREA DE SAÍDA
        areaSaida = new JTextArea();
        add(new JScrollPane(areaSaida), BorderLayout.CENTER);

        // BOTÕES
        JPanel painelBotoes = new JPanel();

        JButton btnAgendar = new JButton("Agendar");
        JButton btnListar = new JButton("Listar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSair = new JButton("Sair");

        painelBotoes.add(btnAgendar);
        painelBotoes.add(btnListar);
        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.SOUTH);


        // PREENCHER ESPECIALIDADES
        for (Especialidade esp : catalogo.getEspecialidades()) {
            comboEspecialidade.addItem(esp.getNome());
        }

        // ATUALIZAR HORÁRIOS
        comboEspecialidade.addActionListener(e -> atualizarHorarios());

        atualizarHorarios();

        // AÇÕES DOS BOTÕES

        // AGENDAR
        btnAgendar.addActionListener(e -> {
            try {
                String nome = campoNome.getText();
                String cpf = campoCpf.getText();
                String dia = campoDia.getText();

                int indexEsp = comboEspecialidade.getSelectedIndex();
                int indexHorario = comboHorario.getSelectedIndex();

                if (comboHorario.getItemCount() == 0) {
                    areaSaida.setText("Sem horários disponíveis!");
                    return;
                }

                Especialidade esp = catalogo.getEspecialidades().get(indexEsp);

                Agendamento a = catalogo.agendar(
                        nome,
                        cpf,
                        dia,
                        esp,
                        indexHorario + 1
                );

                areaSaida.setText("✅ Agendado com sucesso:\n" + a);

                atualizarHorarios();
                limparCampos();

            } catch (Exception ex) {
                areaSaida.setText("❌ Erro: " + ex.getMessage());
            }
        });

        // LISTAR
        btnListar.addActionListener(e -> {
            List<Agendamento> lista = catalogo.listarTodos();

            areaSaida.setText("");
            for (Agendamento a : lista) {
                areaSaida.append(a + "\n");
            }
        });

        // BUSCAR
        btnBuscar.addActionListener(e -> {
            try {
                String nome = campoNome.getText();

                List<Agendamento> lista = catalogo.buscarPorNome(nome);

                if (lista.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Este nome não está cadastrado.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    areaSaida.setText("");
                    for (Agendamento a : lista) {
                        areaSaida.append(a + "\n");
                    }
                }

                limparCampos();

            } catch (Exception ex) {
                areaSaida.setText("❌ Erro: " + ex.getMessage());
            }
        });

        // CANCELAR
        btnCancelar.addActionListener(e -> {
            try {
                String nome = campoNome.getText();

                Agendamento a = catalogo.cancelarPorNome(nome);

                if (a != null) {
                    areaSaida.setText("🗑 Cancelado:\n" + a);
                } else {
                    areaSaida.setText("Paciente não encontrado.");
                }

                atualizarHorarios();
                limparCampos();

            } catch (Exception ex) {
                areaSaida.setText("❌ Erro: " + ex.getMessage());
            }
        });

        // SAIR
        btnSair.addActionListener(e -> {
            int opcao = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja realmente sair?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcao == JOptionPane.YES_OPTION) {
                catalogo.salvarEmArquivo("consultas.txt");
                dispose();
            }
        });
    }

    // ATUALIZA HORÁRIOS
    private void atualizarHorarios() {
        comboHorario.removeAllItems();

        int index = comboEspecialidade.getSelectedIndex();

        if (index >= 0) {
            Especialidade esp = catalogo.getEspecialidades().get(index);

            for (String h : esp.getHorariosDisponiveis()) {
                comboHorario.addItem(h);
            }
        }
    }

    // LIMPAR CAMPOS
    private void limparCampos() {
        campoNome.setText("");
        campoCpf.setText("");
        campoDia.setText("");

        comboEspecialidade.setSelectedIndex(0);
        atualizarHorarios();
    }
}