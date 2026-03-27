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

    // CORREÇÃO PARA ERRO: Nome vazio ou muito curto.
    // IllegalArgumentException -> Valor aprovado, mas vai contra a "regra" estabelecida no programa
    public void setNome(String nome) {
        if ( nome == null || nome.trim().length() <= 3 ) {
            throw new IllegalArgumentException("Nome muito curto");
        }
        this.nome = nome;

    }
    public String getCpf() {
        return cpf;
    }

    //CORREÇÃO PARA ERRO: CPF vazio ou com poucaos caracteres
    public void setCpf(String cpf) {
        if (cpf == null || cpf.trim().length() != 11) {
            throw new IllegalArgumentException("CPF deve ter no minimo 11 caracteres");
        }
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "Paciente: " + nome +
                " | CPF: " + cpf;
    }
}
