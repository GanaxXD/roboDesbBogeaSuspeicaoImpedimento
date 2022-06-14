package PAGE.projudi.turmasRecursais;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import CLIENT.SINAPSES.GeradorDeDataSets;
import CLIENT.SINAPSES.model.Resultado;
import CLIENT.util.ArquivoUtil;
import CLIENT.util.Util;
import DAO.JulgamentoTematicoDao;
import MODEL.Etiqueta;
import MODEL.FiltroEtiqueta;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.projudi.PainelUsuarioPROJUDI;
import RN.JulgamentoTematicoProjudiRN;

/**
 * 
 * @autor Leonardo Ribeiro de Oliveira
 */
public class JulgamentoTematico_Page extends PainelUsuarioPROJUDI {

	Map<String, StringBuffer> mapaEtiquetas = new HashMap<String, StringBuffer>();

	public JulgamentoTematico_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);

	}

	protected void selecionarTarefa() throws AutomacaoException {
		try {
			buscarProcesso();
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a tarefa: " + getParametros().getTarefa());
		}

	}

	private void gerarDataSetSinapses(List<Processo> listaProcessos) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");

		try {
			for (Processo processo : listaProcessos) {

				try {
					if (processo.getEtiquetasAutomacao().size() == 1) {

						for (String etiqueta : processo.getEtiquetasAutomacao()) {

							StringBuffer sbEtiqueta = mapaEtiquetas.get(etiqueta);
							if (sbEtiqueta == null) {
								sbEtiqueta = new StringBuffer();

								sbEtiqueta.append(etiqueta + ",");
								sbEtiqueta
										.append(GeradorDeDataSets.converterEmBase64(processo.getDocumentoAto()) + ",");
								sbEtiqueta.append(processo.getNumeroProcessoFormatado() + ",");
								sbEtiqueta.append("VALIDO\n");
								mapaEtiquetas.put(etiqueta, sbEtiqueta);

							} else {

								sbEtiqueta.append(etiqueta + ",");
								sbEtiqueta
										.append(GeradorDeDataSets.converterEmBase64(processo.getDocumentoAto()) + ",");
								sbEtiqueta.append(processo.getNumeroProcessoFormatado() + ",");
								sbEtiqueta.append("VALIDO\n");

							}

						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			for (Map.Entry<String, StringBuffer> entry : mapaEtiquetas.entrySet()) {

				String nomeArquivo = entry.getKey() + "_" + sdf.format(Calendar.getInstance().getTime()) + ".csv";

				ArquivoUtil.salvarArquivo("datasets", nomeArquivo, entry.getValue());
			}

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("Erro ao gerar DATASET");
		}

	}
	
	
	private void gerarRelatorioEtiquetas(List<Processo> listaProcessos) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String nomeArquivo = getParametros().getPerfil().replaceAll(" ", "_").replaceAll(
				"[ª!@#$%¨&*()~^{}áéíóúâêîôûã~õ]", "") + "_" + sdf.format(Calendar.getInstance().getTime()) + ".csv";
		System.out.println();
		StringBuffer conteudo = new StringBuffer();
		conteudo.append("PROCESSO;SUGESTAO_IA;ETIQUETA1;ETIQUETA2;ETIQUETA3;ETIQUETA4;ETIQUETA4\n");
		try {

			for (Processo processo : listaProcessos) {

				if (processo.getEtiquetasAutomacao().size() > 0) {
					conteudo.append(processo.getNumeroProcessoFormatado() + ";");
					conteudo.append(processo.getEtiquetaSinapses() + ";");
					
					for (String etiqueta : processo.getEtiquetasAutomacao()) {
						conteudo.append(etiqueta + ";");
					}
					
					conteudo.append("\n");
				}

			}

			ArquivoUtil.salvarArquivo("datasets", nomeArquivo, conteudo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gerarRelatorioEtiquetas2(List<Processo> listaProcessos) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String nomeArquivo = getParametros().getPerfil().replaceAll(" ", "_").replaceAll(
				"[ª!@#$%¨&*()~^{}áéíóúâêîôûã~õ]", "") + "_" + sdf.format(Calendar.getInstance().getTime()) + ".csv";
		System.out.println();
		StringBuffer conteudo = new StringBuffer();
		conteudo.append("PROCESSO;ETIQUETA1;ETIQUETA2;ETIQUETA3;ETIQUETA4\n");
		try {

			for (Processo processo : listaProcessos) {

				if (processo.getEtiquetasAutomacao().size() > 0) {
					conteudo.append(processo.getNumeroProcessoFormatado() + ";");

					for (String etiqueta : processo.getEtiquetasAutomacao()) {
						conteudo.append(etiqueta + ";");
					}
					
					
					for (Iterator iterator = processo.getEtiquetasAutomacao().iterator(); iterator.hasNext();) {
						String localizador = (String) iterator.next();
						if (localizador.equals(processo.getEtiquetaSinapses())) {
							conteudo.append("SINAPSES:\t"+ processo.getEtiquetaSinapses() + ";");
							break;
						}
					}
					
					conteudo.append("\n");
				}

			}

			ArquivoUtil.salvarArquivo("datasets", nomeArquivo, conteudo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException {
		List<Processo> listaProcessos = new ArrayList<Processo>();
		try {
			JulgamentoTematicoProjudiRN rn = new JulgamentoTematicoProjudiRN(new JulgamentoTematicoDao(
					getParametros().getDbURL(), getParametros().getDbUser(), getParametros().getDbPass()));

			setMapaProcessos(rn.carregarProcessos(getParametros(), obterConexaoSinapses()));

			for (Processo processo : getMapaProcessos().values()) {
				
				if (processo.getEtiquetasAutomacao().size() >= 1) {
					listaProcessos.add(processo);
				}

				
				if (processo.getEtiquetasAutomacao().size() > 1) {
					rn.definirEtiquetaInteligenciaArtificial(processo, obterConexaoSinapses(),
							parametros.getAcuraciaIA());
				}

				if (processo.getEtiquetasAutomacao().size() > 0) {
					if (getParametros().getGerarRelatorios() != null
							&& getParametros().getGerarRelatorios().equalsIgnoreCase("sim")) {
						salvarArquivo(processo);
					}
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("\nErro ao obterLista de Processos no banco: " + e.getMessage());
		}

		criarLog(
				"\n\n******************************************************************************************"
						+ listaProcessos + "\n\nQuantidade de Processos: " + listaProcessos.size() + "\n",
				obterArquivoLog());

		if (getParametros().getGerarRelatorios() != null
				&& getParametros().getGerarRelatorios().equalsIgnoreCase("sim")) {
			gerarRelatorioEtiquetas(listaProcessos);
			gerarDataSetSinapses(listaProcessos);
		}

		return listaProcessos;

	}

	private void salvarArquivo(Processo processo) throws IOException {

		if (processo.getEtiquetasAutomacao().size() > 0) {
			StringBuffer conteudo = new StringBuffer();
			conteudo.append("\nETIQUETAS: \n");
			for (String etiqueta : processo.getEtiquetasAutomacao()) {
				conteudo.append(etiqueta + "\n");
			}

			if (processo.getEtiquetaSinapses() != null && processo.getEtiquetaSinapses().equals("")) {
				conteudo.append("\nSINAPSES:\t" + processo.getEtiquetaSinapses() + ";\n");
			}

			ArquivoUtil.salvarArquivo("arquivos", processo.getNumeroProcesso() + ".txt",
					new StringBuffer(conteudo.toString() + processo.getDocumentoAto()));
		}

	}

	protected void executar_(Processo processo) throws InterruptedException, AutomacaoException {
		System.out.println(processo.getNumeroProcessoFormatado() + "\t Sinapses: 	" + processo.getEtiquetaSinapses()
				+ "\t Automação: " + processo.getEtiquetasAutomacao());
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		clicar("//a[text()[contains(.,'" + processo.getNumeroProcesso() + "')]]", 20, 1000);

		clicar("//a/strong[text()[contains(.,'Modificar Dados')]]", 20, 1000);

		if (processo.getEtiquetasAutomacao().size() > 1) {
			// - Caso haja mais de uma etiqueta da automação, consultar o sinapses para
			// saber qual delas deverá ser atribuída ao processo
			boolean encontrou = false;
			for (Iterator iterator = processo.getEtiquetasAutomacao().iterator(); iterator.hasNext();) {

				String localizador = (String) iterator.next();
				if (localizador.equals(processo.getEtiquetaSinapses())) {
					encontrou = true;
					atribuirLocalizadorProjudi(processo, processo.getEtiquetaSinapses());
				}
			}
			
			if(!encontrou) {
				criarLog(
						"Houve conflito de localizadores com a sugestão da IA. A automação descobriu os seguintes localizadores: "
								+ processo.getEtiquetasAutomacao() + ". A IA sugeriu " + processo.getEtiquetaSinapses(),
						obterArquivoLog());
				criarLog("Nenhum localizador foi adicionado ao processo "
						+ processo.getNumeroProcessoFormatado(), obterArquivoLog());
			}

		} else {
			for (Iterator iterator = processo.getEtiquetasAutomacao().iterator(); iterator.hasNext();) {

				atribuirLocalizadorProjudi(processo, (String) iterator.next());
				break;

			}

		}
		clicar("//input[@name = 'Modificar']", 20, 1000);

	}

	private void atribuirLocalizadorProjudi(Processo processo, String localizador) throws AutomacaoException {

		try {
			
			selecionar("//select[@id='codTipoLocalizador']", localizador, 20, 1000);
			clicar("//input[@value = 'Adicionar']", 20, 1000);
			criarLog("O localizador "+ localizador + " foi adicionado ao processo "+ processo.getNumeroProcessoFormatado(), obterArquivoLog());
			criarLog("Procedimento realizado com sucesso!!\n", obterArquivoLog());
			
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar o procedimento no processo " + processo.getNumeroProcesso());
		}
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return processo.getEtiquetasAutomacao().size() >= 1;
	}

}