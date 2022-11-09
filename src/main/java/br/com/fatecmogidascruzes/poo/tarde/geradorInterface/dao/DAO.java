package br.com.fatecmogidascruzes.poo.tarde.geradorInterface.dao;

import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.*;
import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.persistencia.BancoDados;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAO<T> {

	private Connection conexao;
	private PreparedStatement stmt;


	private void abreConexao() throws SQLException {
		if(conexao == null || conexao.isClosed()) {
			conexao = BancoDados.getConexao();
		} else {
			conexao.close();
		}
	}

	public void salvar(T objeto) throws IllegalArgumentException, IllegalAccessException, SQLException {
		verificaSeObjetoTemAnotacaoDeEntidade(objeto);
		abreConexao();

		String sql = criarSQLInsercao(objeto);
		int numeroInterrogacao = 1;

		stmt = conexao.prepareStatement(sql);

		Field[] atributos = objeto.getClass().getDeclaredFields();

		for (Field atributo : atributos) {
			if (isId(atributo) || isTransient(atributo)) {
				continue;
			}

			substituiValorNoSQL(atributo, objeto, numeroInterrogacao++);
		}

		stmt.execute();
		conexao.close();
	}

	public void atualizar(T objeto) throws IllegalArgumentException, IllegalAccessException, SQLException {
		verificaSeObjetoTemAnotacaoDeEntidade(objeto);
		abreConexao();

		String sql = criarSQLAtualizacao(objeto);
		Object valorID = null;
		int numeroInterrogacao = 1;

		stmt = conexao.prepareStatement(sql);

		Field[] atributos = objeto.getClass().getDeclaredFields();

		for (Field atributo : atributos) {
			if (isTransient(atributo)) {
				continue;
			}

			boolean eraPublico = atributo.isAccessible();
			atributo.setAccessible(true);
			Object valorAtributo = atributo.get(objeto);

			if(isId(atributo)){
				valorID = valorAtributo;
			}

			stmt.setObject(numeroInterrogacao++, valorAtributo);
			atributo.setAccessible(eraPublico);
		}

		stmt.setObject(numeroInterrogacao, valorID);

		stmt.execute();
		conexao.close();
	}

	public void deletar(T objeto) throws IllegalArgumentException, IllegalAccessException, SQLException {
		verificaSeObjetoTemAnotacaoDeEntidade(objeto);
		abreConexao();

		String sql = criarSQLDeletar(objeto);
		int numeroInterrogacao = 1;

		stmt = conexao.prepareStatement(sql);

		Field[] atributos = objeto.getClass().getDeclaredFields();

		for(Field atributo : atributos){
			if(isId(atributo)) {
				substituiValorNoSQL(atributo, objeto, numeroInterrogacao++);
			}
		}

		stmt.execute();
		conexao.close();
	}

	public T buscarPorId(T objeto) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
		verificaSeObjetoTemAnotacaoDeEntidade(objeto);
		abreConexao();

		String sql = criarSQLBuscaPorId(objeto);

		stmt = conexao.prepareStatement(sql);
		Field[] atributos = objeto.getClass().getDeclaredFields();
		for(Field atributo : atributos){
			if(isId(atributo))
				substituiValorNoSQL(atributo, objeto, 1);
		}

		ResultSet rs = stmt.executeQuery();
		T objetoBuscado = null;

		if(rs.next()){
			objetoBuscado = montaObjeto(objeto, atributos, rs);
		}
		conexao.close();
		return objetoBuscado;
	}

	public List<T> buscarTodos(Class<T> clazz) throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
		abreConexao();

		T objeto = clazz.getConstructor().newInstance();
		Field[] atributos = objeto.getClass().getDeclaredFields();
		List<T> listaObjetos = new ArrayList<>();
		String sql = criarSQLBuscarTodos(objeto);

		stmt = conexao.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()){
			listaObjetos.add(montaObjeto(objeto, atributos, rs));
		}
		conexao.close();
		return listaObjetos;
	}

	private void verificaSeObjetoTemAnotacaoDeEntidade(T objeto) {
		Entity entity = objeto.getClass().getDeclaredAnnotation(Entity.class);
		if (entity == null) {
			throw new IllegalArgumentException("A classe deve ser uma entidade");
		}
	}

	private void substituiValorNoSQL(Field atributo, T objeto, int numeroInterrogacao) throws IllegalAccessException, SQLException {
		boolean eraPublico = atributo.isAccessible();
		atributo.setAccessible(true);
		Object valorAtributo = atributo.get(objeto);
		stmt.setObject(numeroInterrogacao, valorAtributo);
		atributo.setAccessible(eraPublico);
	}
	
	private String getNomeTabela(T objeto) {
		Table table = objeto.getClass().getDeclaredAnnotation(Table.class);
		if (null == table || table.name().equals("NO_NAME")) {
			return "_" + objeto.getClass().getSimpleName().toLowerCase() + "s";
		}

		return table.name();
	}

	private String getNomeColuna(Field atributo) {
		Column column = atributo.getDeclaredAnnotation(Column.class);
		if (null == column || column.name().equals("NO_NAME")) {
			return atributo.getName().toLowerCase();
		}

		return column.name();
	}

	private boolean isId(Field atributo) {
		return null != atributo.getDeclaredAnnotation(Id.class);
	}

	private boolean isTransient(Field atributo) {
		return null != atributo.getDeclaredAnnotation(Transient.class);
	}

	private T montaObjeto(T objeto, Field[] atributos, ResultSet rs) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		T objetoBuscado = (T) objeto.getClass().getConstructor().newInstance();
		for(Field atributo : atributos){
			if(isTransient(atributo))
				continue;

			String nomeColuna = getNomeColuna(atributo);
			atributo.setAccessible(true);
			try {
				switch (atributo.getType().getName()) {
					case "java.lang.String":
						atributo.set(objetoBuscado, rs.getString(nomeColuna));
						break;

					case "java.lang.Integer":
					case "int":
						atributo.set(objetoBuscado, rs.getInt(nomeColuna));
						break;

					case "java.lang.Float":
					case "float":
						atributo.set(objetoBuscado, rs.getFloat(nomeColuna));
						break;

					case "java.lang.Double":
					case "double":
						atributo.set(objetoBuscado, rs.getDouble(nomeColuna));
						break;

					case "java.time.LocalDate":
						atributo.set(objetoBuscado, rs.getDate(nomeColuna).toLocalDate());
						break;

					default:
						atributo.set(objetoBuscado, rs.getObject(nomeColuna));
						break;
				}
			}
			catch (SQLException e) {
			}
			atributo.setAccessible(false);
		}
		return objetoBuscado;
	}

	private String criarSQLInsercao(T objeto) {
		String sql = "INSERT INTO " + getNomeTabela(objeto) + "(";

		Field[] atributos = objeto.getClass().getDeclaredFields();
		String sufixoSQL = "";
		for (Field atributo : atributos) {
			if (isId(atributo) || isTransient(atributo)) {
				continue;
			}
			sql += getNomeColuna(atributo) + ",";
			sufixoSQL += "?,";
		}
		sql = sql.substring(0, sql.length() - 1);
		sufixoSQL = sufixoSQL.substring(0, sufixoSQL.length() - 1);
		sql += ") VALUES (" + sufixoSQL + ")";
		return sql;
	}

	private String criarSQLAtualizacao(T objeto) {
		String sql = "UPDATE " + getNomeTabela(objeto) + " SET ";
		String whereClause = " WHERE ";

		Field[] atributos = objeto.getClass().getDeclaredFields();
		for (Field atributo : atributos) {
			if(isTransient(atributo)) {
				continue;
			}

			if(isId(atributo)) {
				whereClause += getNomeColuna(atributo) + " = ?";
			}

			sql += getNomeColuna(atributo) + " = ?, ";
		}
		sql = sql.substring(0, sql.length() - 2);
		sql += whereClause;
		return sql;
	}

	private String criarSQLBuscaPorId(T objeto){
		String sql = "SELECT * FROM " + getNomeTabela(objeto) + " WHERE ";

		Field[] atributos = objeto.getClass().getDeclaredFields();

		for (Field atributo : atributos) {
			if (isId(atributo) )
				sql += getNomeColuna(atributo) + " = ?";
		}
		return sql;
	}


	private String criarSQLBuscarTodos(T objeto) {
		return "SELECT * FROM " + getNomeTabela(objeto);
	}

	private String criarSQLDeletar(T objeto) {
		String sql =  "DELETE FROM " + getNomeTabela(objeto) + " WHERE ";

		Field[] atributos = objeto.getClass().getDeclaredFields();

		for (Field atributo : atributos) {
			if (isId(atributo))
				sql += getNomeColuna(atributo) + " = ?";
		}
		return sql;
	}
}
