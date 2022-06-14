package tjma.CLIENT;

import java.util.Iterator;
import java.util.List;

import CLIENT.Robo;
import CLIENT.RoboThread;
import MODEL.TemaProcessualEtiqueta;
import PAGE.Parametros;

/**
 * Classe responsável pela inicialização do serviço de um ou mais robôs.
 * 
 * @author William Sodré
 * @TJMA
 */
public class ClienteRobos extends CLIENT.ClienteRobos {

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

				if (args.length == 1) {

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

					if (!isNull(parametro.getPerfil()) && !isNull(parametro.getTarefa())) {

						if (validarParametros(parametro)) {

							Robo roboEspecializado = roboMaster.criarRoboEspecializado(parametro);
							Thread t1 = new Thread(new RoboThread(roboEspecializado), String.valueOf(count++));
							t1.start();
						} else {
							return;
						}

					} else {

						if (parametro.getPerfis() != null && parametro.getTarefas() != null) {

							String[] perfis = parametro.getPerfis();
							String[] tarefas = parametro.getTarefas();

							for (int i = 0; i < perfis.length; i++) {
								for (int j = 0; j < tarefas.length; j++) {

									Parametros clone = (Parametros) parametro.clone();
									clone.setPerfil(perfis[i]);
									clone.setTarefa(tarefas[j]);

									if (validarParametros(clone)) {

										Robo roboEspecializado = roboMaster.criarRoboEspecializado(clone);
										Thread t1 = new Thread(new RoboThread(roboEspecializado),
												String.valueOf(count++));
										t1.start();
									} else {
										return;
									}

								}
							}

						} else {
							System.out.println("Parâmetro {perfis} e {tarefas} precisam ser informados no arquivo de configuração!");
							return;
						}

					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
