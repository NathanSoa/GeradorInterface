package br.com.fatecmogidascruzes.poo.tarde.geradorInterface.ui;

import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.app.Menu;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Principal {

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Menu menu = new Menu("br.com.fatecmogidascruzes.poo.tarde.geradorInterface");
        menu.exibir();
    }
}
