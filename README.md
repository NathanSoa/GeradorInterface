# GeradorInterface

Projeto que usa a API de reflexão computacional do Java para gerar um menu que disponibiliza operações de CRUD para classes que utilizem as anotações `CRUD` e `ExibirNaBusca`, por exemplo: 
```java
@CRUD(singular="cliente", plural="clientes")
public class Cliente {
    @ExibirNaBusca(1)
    private String cpf;
    @ExibirNaBusca(2)
    private String nome;
    private LocalDate dataNascimento;
    
    // outros métodos da classe
}
```

- A anotação `ExibirNaBusca` recebe um valor para ordenar os atributos na visualização.
- A anotação `CRUD` recebe a versão singular e plural do nome da classe.
- A aplicação faz uso da biblioteca ASCIITable para gerar uma tabela com os registros na opção de buscar todos.

A ideia da aplicação é praticar o uso da API de reflection.
