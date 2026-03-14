package br.com.postosaude.model;

public class Agendamento {

    private Paciente paciente;
    private Especialidade especialidade;
    private String horario;
    private String dia;

    public Agendamento(Paciente paciente, String dia,  Especialidade especialidade, String horario) {
        this.paciente = paciente;
        this.dia = dia;
        this.especialidade = especialidade;
        this.horario = horario;
    }

    public Paciente getPaciente() {return paciente;}
    public Especialidade getEspecialidade() {return especialidade;}
    public String getHorario() {return horario;}
    public String getDia() {return dia;}

    @Override
    public String toString() {
        return "Dia: " + dia + " | " + paciente.getNome() +
                " | Especialidade: " + especialidade.getNome() +
                " | Horário: " + horario;
    }
}