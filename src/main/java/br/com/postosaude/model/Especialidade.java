package br.com.postosaude.model;

import java.util.ArrayList;
import java.util.List;

public class Especialidade {
    private String nome;
    private List<String> horariosDisponiveis;

    public Especialidade(String nome) {
        this.nome = nome;
        this.horariosDisponiveis = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public List<String> getHorariosDisponiveis() {
        return horariosDisponiveis;
    }
    public void setHorariosDisponiveis(List<String> horariosDisponiveis) {
        this.horariosDisponiveis = horariosDisponiveis;
    }
    public void adicionarHorario(String horario) {
        horariosDisponiveis.add(horario);
    }
    public void listarHorarios() {
        for (String h:  horariosDisponiveis) {
            System.out.println(h);
        }
    }

    @Override
    public String toString() {
        return "Especialidade: " + nome;
    }
}
