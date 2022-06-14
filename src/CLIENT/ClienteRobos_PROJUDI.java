package CLIENT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import CLIENT.util.ArquivoUtil;
import MODEL.NegativaTema;
import MODEL.TemaProcessualEtiqueta;
import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * 
 * Classe respons�vel pela inicializa��o do servi�o. Como par�metro � esperado o
 * novo do arquivo json. Para cada configura��o dentro do arquivo, um rob� �
 * criado
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class ClienteRobos_PROJUDI {

	private Robo robo;

	private static List<TemaProcessualEtiqueta> carregarTemas(String[] args, int inicio) throws AutomacaoException {
		List<TemaProcessualEtiqueta> temas = new ArrayList<TemaProcessualEtiqueta>();
		String tema = "";
		try {
			Robo roboMaster = new Robo();
			for (int i = inicio; i < args.length; i++) {
				tema = args[i];
				System.out.println("Carregando tema: " + tema);

				if (tema.indexOf(".json") != -1) {

					TemaProcessualEtiqueta[] vetorTemas = roboMaster.carregarTemas(args[i]);
					salvarTemasTxt(tema, vetorTemas);
					for (TemaProcessualEtiqueta temaProcessualEtiqueta : vetorTemas) {
						temas.add(temaProcessualEtiqueta);
					}

				} else {
					List<TemaProcessualEtiqueta> vetorTemas = carregarTemas(args[i]);
					for (TemaProcessualEtiqueta temaProcessualEtiqueta : vetorTemas) {
						temas.add(temaProcessualEtiqueta);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("Erro ao carregar tema: " + tema);
			e.printStackTrace();
			throw new AutomacaoException("Erro ao carregar tema: " + tema);
		}

		return temas;
	}

	private static void carregarCredenciais(List<Parametros> configuracoes, String arquivoCredenciais) {

		try {

			for (Parametros parametro : configuracoes) {
				if (parametro.getFonteDeDados().equals("BD")) {
					Parametros p = new Robo().carregarCredenciais(arquivoCredenciais);
					parametro.atribuirCredenciais(p);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static StringBuffer readTextFile(String filePath) {
		StringBuffer fileContent = new StringBuffer();
		try {

			FileReader fileReader = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(fileReader);
			String data = null;
			while ((data = reader.readLine()) != null) {
				fileContent.append(data + " \n");
			}
			fileReader.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileContent;
	}

	private static List<TemaProcessualEtiqueta> carregarTemas(String arg) throws AutomacaoException {
		List<TemaProcessualEtiqueta> temas = new ArrayList<TemaProcessualEtiqueta>();

		try {

			FileReader fileReader = new FileReader(arg);
			BufferedReader reader = new BufferedReader(fileReader);
			String linha = null;
			TemaProcessualEtiqueta temaProcessualEtiqueta = null;
			int numeroLinha = 0;
			while ((linha = reader.readLine()) != null) {

				numeroLinha++;

				if ((linha.indexOf("[") != -1 && linha.indexOf("]") != -1)) {

					if (temaProcessualEtiqueta == null) {
						temaProcessualEtiqueta = novaEtiqueta(linha);
					} else {
						temas.add(temaProcessualEtiqueta);
						temaProcessualEtiqueta = novaEtiqueta(linha);
					}

				} else if ((linha.length() <= 1 || linha.equals("\n"))) {

					if (temaProcessualEtiqueta != null) {
						temas.add(temaProcessualEtiqueta);
						temaProcessualEtiqueta = null;
					}

				} else if (linha.indexOf("NOT:") != -1) {
					String palavrasNot = linha.substring(linha.indexOf("NOT:") + 4);

					if (temaProcessualEtiqueta != null) {
						temaProcessualEtiqueta.setOperadorNOT(palavrasNot.split(";"));
					} else {
						throw new AutomacaoException(
								"Erro de formatação do arquivo. Etiqueta não especificada no arquivo. Favor verificar a linha: "
										+ numeroLinha + " - " + linha);
					}

				} else {

					String[] palavrasANDOR = linha.split(";");
					List<String> lista = new ArrayList<String>();
					for (int i = 0; i < palavrasANDOR.length; i++) {
						lista.add(palavrasANDOR[i]);
					}

					if (temaProcessualEtiqueta != null) {
						temaProcessualEtiqueta.getListaPalavrasANDOR().add(lista);
					} else {
						throw new AutomacaoException(
								"Erro de formatação do arquivo. Etiqueta não especificada no arquivo. Favor verificar a linha: "
										+ numeroLinha + " - " + linha);
					}

				}

			}

			if (temaProcessualEtiqueta != null) {
				temas.add(temaProcessualEtiqueta);
				temaProcessualEtiqueta = null;
			}

			fileReader.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Arquivo de Configuração " + arg + "inválido. " + e.getMessage());
		}

		return temas;

	}

	private static TemaProcessualEtiqueta novaEtiqueta(String linha) {
		TemaProcessualEtiqueta temaProcessualEtiqueta = new TemaProcessualEtiqueta();
		String stringTema = linha.substring(linha.indexOf("[") + 1, linha.indexOf("]")).trim();
		temaProcessualEtiqueta.setEtiqueta(stringTema);
		return temaProcessualEtiqueta;
	}

	private static void salvarTemasTxt(String arquivo, TemaProcessualEtiqueta[] temas) {
		try {

			StringBuffer sb = new StringBuffer();
			int contadorTemasArquivo = 0;
			for (TemaProcessualEtiqueta tema : temas) {

				if ((contadorTemasArquivo++) > 0) {
					sb.append("\n\n");
				}

				sb.append("[" + tema.getEtiqueta() + "]");
				for (int j = 0; j < tema.getPalavrasChaveANDOR().length; j++) {

					List<String> palavras = tema.getPalavrasChaveANDOR()[j];
					int contador = 0;
					for (String palavra : palavras) {

						if ((contador++) == 0) {
							sb.append("\n");
						} else {
							sb.append(";");
						}
						sb.append(palavra);
					}

				}

				String[] palavrasNot = tema.getOperadorNOT();
				for (int i = 0; i < palavrasNot.length; i++) {
					if (i == 0) {
						sb.append("\nNOT:");
					} else {
						sb.append(";");
					}
					sb.append(palavrasNot[i]);
				}

			}

			sb.append("\n");

			ArquivoUtil.salvarArquivo(arquivo.replace(".json", ".txt"), sb);

			sb = new StringBuffer();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main2(String[] args) {
		try {
			List<TemaProcessualEtiqueta> temas = carregarTemas(args, 2);
			System.out.println(temas);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {

		try {

			for (String string : args) {
				System.out.println(">>>>" + string);
			}

			if (args == null || args.length < 1) {
				System.out.println("Parametros Incompletos! Apenas " + args.length + " argumentos");
				return;
			} else {

				Robo roboMaster = new Robo();
				List<Parametros> listaConfiguracao = roboMaster.carregarParametrosGSON(args[0]);

				List<TemaProcessualEtiqueta> temas = null;

				if (args.length ==1) {
					
					
					
				} else if (args.length > 3) {
					temas = carregarTemas(args, 2);
					carregarCredenciais(listaConfiguracao, args[1]);

				} else {
					temas = carregarTemas(args, 2);
					carregarCredenciais(listaConfiguracao, args[1]);
				}

				int count = 0;
				for (Iterator iterator = listaConfiguracao.iterator(); iterator.hasNext();) {

					Parametros parametro = (Parametros) iterator.next();
					parametro.setTemas(temas);
					setarValoresPadrao(parametro);

					if (validarParametros(parametro)) {

						Robo roboEspecializado = roboMaster.criarRoboEspecializado(parametro);
						Thread t1 = new Thread(new RoboThread(roboEspecializado), String.valueOf(count++));
						t1.start();
					} else {
						return;
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean isNull(String parametro) {
		if (parametro == null || parametro.equals("")) {
			return true;
		}
		return false;
	}

	private static void setarValoresPadrao(Parametros parametro) {

		if (isNull(parametro.getTimeout())) {
			parametro.setTimeout("15");
		}

		if (isNull(parametro.getQtdRobos())) {
			parametro.setQtdRobos("1");
		}

	}

	private static boolean validarParametros(Parametros parametro) {
		if (isNull(parametro.getRobo())) {
			System.out.println("Parametro Robo nao informado no arquivo de configuracao!");
			return false;
		}

		if (isNull(parametro.getUrl())) {
			System.out.println("Parametro URL nao informado no arquivo de configuracao!");
			return false;
		}

		if (isNull(parametro.getPerfil())) {
			System.out.println("Parametro Perfil nao informado no arquivo de configuracao!");
			return false;
		}

		return true;
	}

}
