package CLIENT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import MODEL.NegativaTema;
import MODEL.Processo;
import MODEL.TemaProcessualEtiqueta;
import PAGE.AutomacaoException;
import PAGE.PaginaBase;
import PAGE.Parametros;
import PAGE.pje21.justicaComum.CI.AtoJudicialEtiqueta;

/**
 * 
 * Classe principal que representa um robô (Navegador).
 * Realiza o
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class Robo {

	protected PaginaBase pagina;

	public void iniciar() throws AutomacaoException, InterruptedException {

		if (!isEmpty(getPagina().getParametros().getQtdRobos())
				&& Integer.valueOf(getPagina().getParametros().getQtdRobos()) > 1) {
			// - multirobo

			Map<String, List<Processo>> mapaProcessos = getPagina().carregarListaProcessos();
			Integer qtdRobos = Integer.valueOf(getPagina().getParametros().getQtdRobos());
			for (int i = 0; i < qtdRobos; i++) {

				// Setando valor 1 para evitar loop infinito na criacao de rob�s
				pagina.getParametros().setQtdRobos("1");

				Robo roboEspecializado = criarRoboEspecializado(pagina.getParametros());
				roboEspecializado.getPagina().setListaParticionada(mapaProcessos.get("" + i));
				roboEspecializado.getPagina().setMapaProcessos(this.getPagina().getMapaProcessos());
				Thread t1 = new Thread(new RoboThread(roboEspecializado), i + "");
				t1.start();

				Thread.sleep(60000);
			}

			

		} else {
			// - Apenas um robo
			getPagina().iniciar();
		}
		
	}

	public PaginaBase getPagina() {
		return pagina;
	}

	public void setPagina(PaginaBase pagina) {
		this.pagina = pagina;
	}

	protected boolean isEmpty(String valor) {

		if (valor == null || valor.equals("")) {
			return true;
		}
		return false;
	}

	private String obterConteudoArquivo(String arquivo) {
		
		StringBuffer sb = new StringBuffer();
		try {
			Path path = FileSystems.getDefault().getPath(arquivo);

			BufferedReader reader = Files.newBufferedReader( path, StandardCharsets.UTF_8);
			
			String linha = "";

			while ((linha = reader.readLine()) != null) {

				sb.append(linha);

			}
			reader.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return sb.toString();
	}
	
	private String obterConteudoArquivo2(String arquivo) {
		StringBuffer sb = new StringBuffer();
		try {
			FileReader fileReader = new FileReader(arquivo);
			BufferedReader reader = new BufferedReader(fileReader);

			String linha = "";

			while ((linha = reader.readLine()) != null) {

				sb.append(linha);

			}
			fileReader.close();
			reader.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return sb.toString();
	}

	private String[] obterParametros(String parametro, JSONObject jObject, String delimitador) {
		try {

			String valor = jObject.getString(parametro);

			if (valor != null) {
				if (!valor.equals("") && valor.indexOf(delimitador) != -1) {
					return valor.split(delimitador);
				} else {
					return new String[] { valor };
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
		}
	}

	private String obterParametro(String parametro, JSONObject jObject) {
		try {
			return jObject.getString(parametro);

		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Método responsavel por processar o arquivo json, transformando em uma lista de Parametros.
	 * 
	 * @param arquivo
	 * @return
	 * @throws AutomacaoException
	 */
	public List<Parametros> carregarParametrosGSON(String arquivo) throws AutomacaoException {
		return carregarParametrosGSON(arquivo, Parametros[].class);//
	}
	
	/**
	 * @param arquivo
	 * @return
	 * @throws AutomacaoException
	 * 
	 * @author William Sodré
	 */
	public <T> List<T> carregarParametrosGSON(String arquivo, Class<T[]> classe) throws AutomacaoException {

		List<T> listaConfiguracaoRobo = new ArrayList<T>();
		String conteudoJson = obterConteudoArquivo(new File("").getAbsolutePath() + "\\" + arquivo);
		T[] parametros = new Gson().fromJson(conteudoJson, classe);//
		
		for (int i = 0; i < parametros.length; i++) {
			listaConfiguracaoRobo.add(parametros[i]);
		}
		return listaConfiguracaoRobo;
	}
	
	
	/**
	 * Método responsavel por processar o arquivo json, transformando em uma lista de Parametros.
	 * 
	 * @param arquivo
	 * @return
	 * @throws AutomacaoException
	 */
	protected TemaProcessualEtiqueta[] carregarTemas(String arquivo) throws AutomacaoException {
		try {
			String conteudoJson = obterConteudoArquivo(new File("").getAbsolutePath() + "\\" + arquivo);
			
			TemaProcessualEtiqueta[] temas = new Gson().fromJson(conteudoJson, TemaProcessualEtiqueta[].class);
			return temas;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
		
	}
	
	
	/**
	 * Método responsavel por processar o arquivo json, transformando em uma lista de Parametros.
	 * 
	 * @param arquivo
	 * @return
	 * @throws AutomacaoException
	 */
	protected AtoJudicialEtiqueta[] carregarAtosJudiciais(String arquivo) throws AutomacaoException {
		try {
			String conteudoJson = obterConteudoArquivo(new File("").getAbsolutePath() + "\\" + arquivo);
			
			AtoJudicialEtiqueta[] atos = new Gson().fromJson(conteudoJson, AtoJudicialEtiqueta[].class);
			return atos;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
		
	}
	
	
	protected NegativaTema[] carregarNegativaTemas(String arquivo) throws AutomacaoException {
		try {
			String conteudoJson = obterConteudoArquivo(new File("").getAbsolutePath() + "\\" + arquivo);
			
			NegativaTema[] temas = new Gson().fromJson(conteudoJson, NegativaTema[].class);
			return temas;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
		
	}
	
	
	
	protected Parametros carregarCredenciais(String arquivo) throws AutomacaoException {
		try {
			String conteudoJson = obterConteudoArquivo(new File("").getAbsolutePath() + "\\" + arquivo);

			Parametros p = new Gson().fromJson(conteudoJson, Parametros.class);
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
	
	/**
	 * M�todo respons�vel por processar o arquivo json, transformando em uma lista de Parametros.
	 * 
	 * TODO: Alterar o c�digo abaixo para utilizar a biblioteca com.google.gson.Gson 
	 * @param arquivo
	 * @return
	 * @throws AutomacaoException
	 */
	protected List<Parametros> carregarParametros(String arquivo) throws AutomacaoException {
		System.out.println("Carregar parametros....");
		JSONObject jObject = null;
		JSONArray jArray = null;
		List<Parametros> listaConfiguracaoRobo = new ArrayList<Parametros>();
		try {

			jArray = new JSONArray(obterConteudoArquivo(new File("").getAbsolutePath() + "\\" + arquivo));

			for (int i = 0; i < jArray.length(); i++) {

				jObject = jArray.getJSONObject(i);
				Parametros parametro = new Parametros();
				Set<String> chaves = jObject.keySet();
				for (String chave : chaves) {

					try {
						String nomeMetodo = "set" + chave.substring(0, 1).toUpperCase() + chave.substring(1);
						if (jObject.getString(chave).indexOf(",") != -1) {
							Method metodo = parametro.getClass().getMethod(nomeMetodo, String[].class);
							String[] lista = obterParametros(chave, jObject, ",");
							metodo.invoke(parametro, new Object[] { lista });
						} else {

							Method metodo = parametro.getClass().getMethod(nomeMetodo, String.class);
							metodo.invoke(parametro, obterParametro(chave, jObject));

						}

					} catch (Exception e) {
						throw new AutomacaoException("Erro ao acessar o metodo correspondente a chave " + chave
								+ "\nCaso o parametro seja uma lista, utilizar uma virgula para separar os elementos. \nCaso so exista um elemento, favor adicionar pelo menos uma virgula ao final. Ex: \""
								+ chave + "\":\"valor,\"");
					}

				}

				listaConfiguracaoRobo.add(parametro);
			}

		} catch (Exception e) {
			throw new AutomacaoException(
					"Erro ao carregar parametros de inicializacao da automacao! " + e.getMessage());
		}

		System.out.println("Fim Carregar parametros....");
		return listaConfiguracaoRobo;
	}

	public Robo criarRoboEspecializado(Parametros parametro) throws AutomacaoException {
		Robo roboEspecializado = new Robo();
		try {
			System.out.println("Criando robo "+ parametro.getRobo() + " - "+ parametro.getTarefa() + " ("+parametro.getQtdRobos()+ ")");
			Class classe = Class.forName(parametro.getRobo());
			
			Constructor construtor = classe.getDeclaredConstructor(Parametros.class);
			roboEspecializado.setPagina((PaginaBase) construtor.newInstance(parametro));

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao inicializar robo! " + e.getMessage());
		}
		return roboEspecializado;

	}

}
