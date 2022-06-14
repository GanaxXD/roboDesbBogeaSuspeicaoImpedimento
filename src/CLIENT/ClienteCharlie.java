package CLIENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import MODEL.NegativaTema;
import MODEL.TemaProcessualEtiqueta;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.justicaComum.CI.AtoJudicialEtiqueta;

/**
 * 
 * Classe respons�vel pela inicializacao do servico. Como parAmetro e esperado o
 * novo do arquivo json. Para cada configura��o dentro do arquivo, um rob� �
 * criado
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class ClienteCharlie {

	private Robo robo;

	private static List<AtoJudicialEtiqueta> carregarAtosJudiciais(String[] args, int inicio)
			throws AutomacaoException {
		List<AtoJudicialEtiqueta> atos = new ArrayList<AtoJudicialEtiqueta>();
		String ato = "";
		try {
			Robo roboMaster = new Robo();
			for (int i = inicio; i < args.length; i++) {
				ato = args[i];
				System.out.println("Carregando tema: " + ato);
				for (AtoJudicialEtiqueta lAtoJudicialEtiqueta : roboMaster.carregarAtosJudiciais(args[i])) {
					atos.add(lAtoJudicialEtiqueta);
				}
			}

		} catch (Exception e) {
			System.out.println("Erro ao carregar atos: " + ato);
			e.printStackTrace();
			throw new AutomacaoException("Erro ao carregar atos: " + ato);
		}

		return atos;
	}

	public static void main(String[] args) throws Exception {

		try {

			if (args == null || args.length < 1) {
				System.out.println("Parametros Incompletos! Apenas " + args.length + " argumentos");
				return;
			} else {

				Robo roboMaster = new Robo();
				
				List<Parametros> listaConfiguracao = roboMaster.carregarParametrosGSON(args[0]);
				List<AtoJudicialEtiqueta> atos = null;
				if (args.length > 1) {
					atos = carregarAtosJudiciais(args, 1);
				}

				int count = 0;
				for (Iterator iterator = listaConfiguracao.iterator(); iterator.hasNext();) {
					Parametros parametro = (Parametros) iterator.next();

					parametro.setAtos(atos);
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

		if (parametro.getTipoPolos() == null || parametro.getTipoPolos().length == 0) {
			parametro.setTipoPolos(new String[] { "REQUERENTE", "REQUERIDO" });
			// - ser� necess�rio um refactoring para n�o precisar mais usar estes campos...
			// - pois a depender da classe isto muda (autor reu, ...)
		}

	}

	private static boolean validarParametros(Parametros parametro) {
		if (isNull(parametro.getRobo())) {
			System.out.println("Parametro Robo n�o informado no arquivo de configuracao!");
			return false;
		}

		if (isNull(parametro.getUrl())) {
			System.out.println("Parametro URL n�o informado no arquivo de configuracao!");
			return false;
		}

		if (isNull(parametro.getPerfil())) {
			System.out.println("Parametro Perfil n�o informado no arquivo de configuracao!");
			return false;
		}

		return true;
	}

}
