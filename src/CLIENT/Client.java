package CLIENT;

import java.util.Iterator;
import java.util.List;
import PAGE.Parametros;

/**
 * 
 * Classe respons�vel pela inicializa��o do servi�o.
 * Como par�metro e esperado o novo do arquivo json.
 * Para cada configuracao dentro do arquivo, um rob� � criado 
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class Client {

	private Robo robo;
	
	public static void main(String[] args) throws Exception {

		try {

			if (args == null || args.length < 1) {
				System.out.println("Parametros Incompletos, favor informar o arquivo de configuracao!");
				return;
			} else {

				Robo roboMaster = new Robo();
				List<Parametros> listaConfiguracao = roboMaster.carregarParametros(args[0]);
				int count = 0;
				for (Iterator iterator = listaConfiguracao.iterator(); iterator.hasNext();) {
					Parametros parametro = (Parametros) iterator.next();
					
					setarValoresPadrao(parametro);
					if (validarParametros(parametro)) {

						Robo roboEspecializado = roboMaster.criarRoboEspecializado(parametro);
						Thread t1 = new Thread(new RoboThread(roboEspecializado), String.valueOf(count++));
						t1.start();
					} else {
						return;
					}
					
					
					if (args.length == 2) {

						Integer intervaloRobos = Integer.valueOf(args[1]);
						Thread.sleep(intervaloRobos);

					} else {

						Thread.sleep(60000);
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
			parametro.setTimeout("20");
		}
		
		if(parametro.getTipoPolos()==null || parametro.getTipoPolos().length==0) {
			parametro.setTipoPolos(new String[] {"REQUERENTE","REQUERIDO"});
			//- ser� necess�rio um refactoring para n�o precisar mais usar estes campos...
			//- pois a depender da classe isto muda (autor reu, ...)
		}

	}

	private static boolean validarParametros(Parametros parametro) {
		if (isNull(parametro.getRobo())) {
			System.out.println("Parametro Robo n�o informado no arquivo de configura��o!");
			return false;
		}

		if (isNull(parametro.getUrl())) {
			System.out.println("Parametro URL n�o informado no arquivo de configura��o!");
			return false;
		}

		if (isNull(parametro.getPerfil())) {
			System.out.println("Parametro Perfil n�o informado no arquivo de configura��o!");
			return false;
		}

		return true;
	}

	

}
