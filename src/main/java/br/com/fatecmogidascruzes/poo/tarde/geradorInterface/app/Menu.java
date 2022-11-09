package br.com.fatecmogidascruzes.poo.tarde.geradorInterface.app;

import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.CRUD;
import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.dao.DAO;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class Menu {

    private final String packagePath;
    private final Scanner scanner;

    public Menu(String packagePath) {
        this.packagePath = packagePath;
        this.scanner = new Scanner(System.in);
    }

    public void exibir() throws IOException, ClassNotFoundException, SQLException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Set<Class> classes = ProcuradorDeClassesDoProjeto.execute(packagePath);
        Map<Integer, Class> opcaoClasse = new HashMap<>();

        System.out.println("Menu Principal\nSelecione uma das opções a seguir:");

        int contador = 1;
        for(Class cadaClasse : classes){
            CRUD crud = (CRUD) cadaClasse.getDeclaredAnnotation(CRUD.class);

            String nomePlural = crud.plural();
            opcaoClasse.put(contador, cadaClasse);
            System.out.println((contador++) + ". Gerir " + nomePlural);
        }
        System.out.println(contador + ". Sair");
        System.out.print("Opção escolhida: ");
        int opcao = Integer.parseInt(scanner.nextLine());

        System.out.println("---");

        if(opcao == contador){
            System.out.println("Saindo...");
            return;
        }

        if(!opcaoClasse.containsKey(opcao)){
            System.out.println("Opção inválida! Fechando programa...");
            return;
        }

        Class classeSelecionada = opcaoClasse.get(opcao);
        exibirMenuDeGerencia(classeSelecionada);

    }

    private void exibirMenuDeGerencia(Class classeSelecionada) throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        int opcao;
        CRUD crud = (CRUD) classeSelecionada.getDeclaredAnnotation(CRUD.class);

        String nomeSingular = crud.singular();
        String nomePlural = crud.plural();
        System.out.println("Menu Gerir " + nomePlural);
        System.out.println("1. Buscar todos\n2. Inserir " + nomeSingular);
        System.out.print("Opção Escolhida: ");

        opcao = Integer.parseInt(scanner.nextLine());
        System.out.println("---");

        DAO dao = new DAO();

        if(opcao == 1){
            exibirMenuMostrarTodos(classeSelecionada, crud, dao);


        } else if (opcao == 2){
            exibirMenuCriarNovoRegistro(classeSelecionada, dao);

        } else {
            System.out.println("Opção inválida! Fechando programa...");
        }
    }

    private void exibirMenuCriarNovoRegistro(Class classeSelecionada, DAO dao) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
        Object novoObjeto = classeSelecionada.getConstructor().newInstance();
        Field[] atributos = novoObjeto.getClass().getDeclaredFields();

        for(Field atributo : atributos){
            montaObjeto(novoObjeto, atributo);
        }

        dao.salvar(novoObjeto);
    }

    private void exibirMenuMostrarTodos(Class classeSelecionada, CRUD crud, DAO dao) throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        int opcao;
        int contador = 1;
        Map<Integer, Object> opcaoPorRegistro = new HashMap<>();
        CriadorTabelaDeRegistros criadorStringEmOrdem = new CriadorTabelaDeRegistros(classeSelecionada);

        System.out.println("Menu buscar todos");
        System.out.println("Os seguintes "  + crud.plural() + " já estão cadastrados: ");

        for (Object objeto : dao.buscarTodos(classeSelecionada)) {
            opcaoPorRegistro.put(contador++, objeto);
            criadorStringEmOrdem.adicionaNovoRegistroNaTabela(objeto);
        }

        System.out.println(criadorStringEmOrdem.renderizaTabela());

        System.out.println(contador + ". Voltar");

        System.out.print("Opção escolhida: ");
        opcao = Integer.parseInt(scanner.nextLine());
        System.out.println("---");

        if(opcao == contador){
            System.out.println("Voltando...");
            exibirMenuDeGerencia(classeSelecionada);
            return;
        }

        if(!opcaoPorRegistro.containsKey(opcao)){
            System.out.println("Opção inválida! Fechando Programa...");
            return;
        }

        Object registroSelecionado = opcaoPorRegistro.get(opcao);

        System.out.println("Selecione uma opção: \n1. Editar registro\n2. Excluir registro");
        System.out.print("Opção escolhida: ");
        opcao = Integer.parseInt(scanner.nextLine());
        System.out.println("---");

        String mensagemSucesso = "";
        Field[] atributos = registroSelecionado.getClass().getDeclaredFields();
        if(opcao == 1){
            for(Field atributo : atributos){
                montaObjeto(registroSelecionado, atributo);
            }

            dao.atualizar(registroSelecionado);
            mensagemSucesso = "Registro atualizado com sucesso!";
            System.out.println("---");
        } else
        if(opcao == 2){
            dao.deletar(registroSelecionado);
            mensagemSucesso = "Registro excluido com sucesso!";

        } else {
            System.out.println("Opção inválida! Fechando programa...");
            return;
        }

        System.out.println(mensagemSucesso);
        System.out.println("Voltando para o menu principal...");
        System.out.println("---");

        try {
            Thread.sleep(1000);
            exibir();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void montaObjeto(Object objeto, Field atributo) throws IllegalAccessException {
        boolean eraPublico = atributo.isAccessible();
        atributo.setAccessible(true);
        String tipoObjeto = atributo.getType().getName();
        System.out.print("Digite o valor do "  + atributo.getName() + ": ");

        switch (tipoObjeto) {
            case "java.lang.String": {
                String valor = scanner.nextLine();
                atributo.set(objeto, valor);
                break;
            }

            case "java.lang.Integer":
            case "int": {
                int valor = Integer.parseInt(scanner.nextLine());
                atributo.set(objeto, valor);
                break;
            }

            case "java.lang.Float":
            case "float": {
                float valor = Float.parseFloat(scanner.nextLine());
                atributo.set(objeto, valor);
                break;
            }

            case "java.lang.Double":
            case "double": {
                double valor = Double.parseDouble(scanner.nextLine());
                atributo.set(objeto, valor);
                break;
            }

            case "java.lang.Long":
            case "long": {
                long valor = Long.parseLong(scanner.nextLine());
                scanner.nextLine();
                atributo.set(objeto, valor);
                break;
            }
        }

        atributo.setAccessible(eraPublico);
    }
}
