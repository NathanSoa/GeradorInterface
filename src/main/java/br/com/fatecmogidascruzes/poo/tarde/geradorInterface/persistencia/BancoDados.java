package br.com.fatecmogidascruzes.poo.tarde.geradorInterface.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BancoDados {

	public static Connection getConexao() throws SQLException {
		return
			DriverManager.
				getConnection("URL",
								"USER",
								"PASSWORD");
	}
	
}
