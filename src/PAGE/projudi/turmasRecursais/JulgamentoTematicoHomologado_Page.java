package PAGE.projudi.turmasRecursais;

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
public class JulgamentoTematicoHomologado_Page extends PainelUsuarioPROJUDI {

	Map<String, StringBuffer> mapaEtiquetas = new HashMap<String, StringBuffer>();

	public JulgamentoTematicoHomologado_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);

	}

	protected void selecionarTarefa() throws AutomacaoException {
		try {
			buscarProcesso();
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a tarefa: " + getParametros().getTarefa());
		}

	}

	protected List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException {
		List<Processo> listaProcessos = new ArrayList<Processo>();
		try {
			JulgamentoTematicoProjudiRN rn = new JulgamentoTematicoProjudiRN(new JulgamentoTematicoDao(
					getParametros().getDbURL(), getParametros().getDbUser(), getParametros().getDbPass()));

			setMapaProcessos(rn.carregarProcessos(getParametros(), obterConexaoSinapses()));

			for (Processo processo : getMapaProcessos().values()) {
				if (processo.getEtiquetasAutomacao().size() > 0) {

					listaProcessos.add(processo);

					if (processo.getEtiquetasAutomacao().size() > 1) {
						rn.definirEtiquetaInteligenciaArtificial(processo, obterConexaoSinapses(),
								parametros.getAcuraciaIA());
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

		return listaProcessos;

	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		clicar("//a[text()[contains(.,'" + processo.getNumeroProcesso() + "')]]", 20, 2000);

		clicar("//a/strong[text()[contains(.,'Modificar Dados')]]", 20, 2000);

		if (processo.getEtiquetasAutomacao().size() >= 1) {

			for (Iterator iterator = processo.getEtiquetasAutomacao().iterator(); iterator.hasNext();) {

				atribuirLocalizadorProjudi(processo, (String)iterator.next());

			}

		}

		clicar("//input[@name = 'Modificar']", 20, 2000);
		criarLog("Procedimento realizado com sucesso!!\n", obterArquivoLog());

	}

	private void atribuirLocalizadorProjudi(Processo processo, String localizador)
			throws AutomacaoException {
		
		try {
			selecionar("//select[@id='codTipoLocalizador']", localizador, 20, 2000);
			clicar("//input[@value = 'Adicionar']", 20, 2000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar o procedimento no processo " + processo.getNumeroProcesso());
		}
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return processo.getEtiquetasAutomacao().size() > 0;
	}


}