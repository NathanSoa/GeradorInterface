package br.com.fatecmogidascruzes.poo.tarde.geradorInterface.app;

import br.com.fatecmogidascruzes.poo.tarde.geradorInterface.anotacoes.CRUD;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ProcuradorDeClassesDoProjeto {

	private static Set<Class> classesComAnotacaoCRUD = null;

	public static Set<Class> execute(String caminhoPacoteBase) throws ClassNotFoundException, IOException {
		classesComAnotacaoCRUD = new HashSet<>();
		buscarClasses(caminhoPacoteBase);
		return classesComAnotacaoCRUD;
	}

	private static void buscarClasses(String caminhoPacoteBase) throws IOException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String pastaDoPacoteBase = caminhoPacoteBase.replace(".", "/");
		Enumeration<URL> urlDosPacotes = classLoader.getResources(pastaDoPacoteBase);
		
		if (urlDosPacotes.hasMoreElements()) {
			URL url = urlDosPacotes.nextElement();
			List<Class> classesEncontradas = buscarClasses(caminhoPacoteBase, url.getFile());
			
			for(Class cadaClasse : classesEncontradas) {
				CRUD crud = (CRUD) cadaClasse.getDeclaredAnnotation(CRUD.class);

				if(classePossuiAnotacaoCRUD(crud)){
					classesComAnotacaoCRUD.add(cadaClasse);
				}
			}
		}
	}
	
	private static List<Class> buscarClasses(String caminhoDoPacote, String caminhoDePastas) throws ClassNotFoundException {
		List<Class> classesEncontradas = new ArrayList<>();
		File arquivo = new File(caminhoDePastas);
		
		if(arquivo.isDirectory()) {
			File[] arquivosInternos = arquivo.listFiles();
			
			for(File arquivoInterno : arquivosInternos) {
				if(arquivoInterno.isDirectory()) {
					classesEncontradas.addAll(buscarClasses(caminhoDoPacote + "." + arquivoInterno.getName(), caminhoDePastas + "/" + arquivoInterno.getName()));
				} 
				else if(arquivoInternoEUmaClasseJava(arquivoInterno)) {
					classesEncontradas.add(Class.forName(caminhoDoPacote + "." + arquivoInterno.getName().replace(".class", "")));
				}
			}
		}
		return classesEncontradas;
	}

	private static boolean classePossuiAnotacaoCRUD(CRUD crud) {
		return crud != null;
	}

	private static boolean arquivoInternoEUmaClasseJava(File arquivoInterno) {
		return arquivoInterno.getName().endsWith(".class");
	}
}
