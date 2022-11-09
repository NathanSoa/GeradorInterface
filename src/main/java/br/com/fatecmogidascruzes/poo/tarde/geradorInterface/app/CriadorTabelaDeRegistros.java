package br.com.fatecmogidascruzes.poo.tarde.geradorInterface.app;

import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.ExibirNaBusca;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CriadorTabelaDeRegistros {

    private Object[] valoresOrdenados;
    private final AsciiTable tabelaDeRegistros;
    private int opcao;

    public CriadorTabelaDeRegistros(Class classe) {
        tabelaDeRegistros = new AsciiTable();
        opcao = 1;
        criaCabecalhos(classe);
    }

    public void adicionaNovoRegistroNaTabela(Object objeto) {
        criaArrayComAtributosOrdenados(objeto);

        List<Object> valores = new ArrayList<>();
        valores.add(opcao++);

        for(Object valor : valoresOrdenados) {
            if(valor == null)
                continue;

            valores.add(valor);
        }

        tabelaDeRegistros.addRule();
        tabelaDeRegistros.addRow(valores);
    }

    public String renderizaTabela(){
        tabelaDeRegistros.addRule();
        tabelaDeRegistros.setTextAlignment(TextAlignment.CENTER);
        return tabelaDeRegistros.render();
    }

    private void criaArrayComAtributosOrdenados(Object objeto) {
        Field[] atributos = objeto.getClass().getDeclaredFields();
        valoresOrdenados = new Object[atributos.length];

        for(Field atributo : atributos) {
            if(atributo.getDeclaredAnnotation(ExibirNaBusca.class) == null)
                continue;

            adicionaValorNoArray(objeto, atributo);
        }
    }

    private void adicionaValorNoArray(Object objeto, Field atributo) {
        ExibirNaBusca exibirNaBusca = atributo.getAnnotation(ExibirNaBusca.class);
        boolean eraPublico = atributo.isAccessible();
        atributo.setAccessible(true);
        try {
            valoresOrdenados[exibirNaBusca.value() - 1] = atributo.get(objeto);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        atributo.setAccessible(eraPublico);
    }

    private void criaCabecalhos(Class classeUtilizada){
        String[] nomeDosAtributos = new String[classeUtilizada.getDeclaredFields().length];
        String[] cabecalhosFiltrado;
        int valoresNaoNulos = 0;
        
        for(Field atributo : classeUtilizada.getDeclaredFields()) {
            ExibirNaBusca exibirNaBusca = atributo.getDeclaredAnnotation(ExibirNaBusca.class);

            if(exibirNaBusca == null)
                continue;

            valoresNaoNulos++;
            nomeDosAtributos[exibirNaBusca.value() - 1] = atributo.getName();
        }

        cabecalhosFiltrado = new String[valoresNaoNulos + 1];
        cabecalhosFiltrado[0] = "opção";

        copiaValoresNaoNulos(nomeDosAtributos, cabecalhosFiltrado, valoresNaoNulos);

        tabelaDeRegistros.addRule();
        tabelaDeRegistros.addRow(cabecalhosFiltrado);
        tabelaDeRegistros.addRule();
    }

    private void copiaValoresNaoNulos(String[] nomeDosAtributos, String[] cabecalhosFiltrado, int valoresNaoNulos) {
        for(int i = 1; i < valoresNaoNulos + 1; i++){
            cabecalhosFiltrado[i] = nomeDosAtributos[i - 1];
        }
    }
}
