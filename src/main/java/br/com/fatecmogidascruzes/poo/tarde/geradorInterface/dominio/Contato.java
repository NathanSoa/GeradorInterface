package br.com.fatecmogidascruzes.poo.tarde.geradorInterface.dominio;


import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.CRUD;
import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.ExibirNaBusca;
import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.Entity;
import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.Id;
import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.Table;

@CRUD(plural = "contatos", singular = "contato")
@Table(name = "contatos")
@Entity
public class Contato {

	@Id
	private Long id;
	@ExibirNaBusca(2)
	private String nome;
	@ExibirNaBusca(3)
	private String telefone;
	@ExibirNaBusca(1)
	private String email;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Contato{" +
				"id=" + id +
				", nome='" + nome + '\'' +
				", telefone='" + telefone + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
