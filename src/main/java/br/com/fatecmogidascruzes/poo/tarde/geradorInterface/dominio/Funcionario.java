package br.com.fatecmogidascruzes.poo.tarde.geradorInterface.dominio;

import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.CRUD;
import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.ExibirNaBusca;

@CRUD(singular = "funcionário", plural = "funcionários")
public class Funcionario {

    @ExibirNaBusca(1)
    private String cpf;
    private String nome;
    private Double salario;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }
}
