package br.com.postosaude.model;

public class Paciente {

    private String nome;
    private String cpf;

    public Paciente(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        if ( nome != null &&  nome.length() > 3 ){
            this.nome = nome;
        }
        else {
            System.out.println("Erro: Nome muito curto ou invalido!");
            this.nome = "Sem nome";
        }
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "Paciente: " + nome +
                " | CPF: " + cpf;
    }
}
