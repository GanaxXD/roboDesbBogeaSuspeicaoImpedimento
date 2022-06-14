package PAGE.projudi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import DAO.JulgamentoTematicoDao;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import RN.JulgamentoTematicoProjudiRN;

/**
 * @autor Leonardo Ribeiro de Oliveira
 */
public class SugestaoSentencas_Page extends PainelUsuarioPROJUDI {

	Map<String, StringBuffer> mapaEtiquetas = new HashMap<String, StringBuffer>();

	public SugestaoSentencas_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);

	}
	
	/**
	 * Strategy: Metodo Principal da automacao que define o ciclo de vida dos rob�s.
	 * 
	 */
	public void iniciar() throws AutomacaoException, InterruptedException {

		try {
			criarLog();
			executarProcedimento();

		} catch (AutomacaoException ae) {
			criarLog("\nProcedimento Não realizado: " + ae.getMessage(), obterArquivoLog());

		} finally {
			salvarLog();
		}

	}

	protected void realizarTarefa(Processo processo) throws InterruptedException, AutomacaoException {

		System.out.println("realizarTarefa.......");

		try {

			if (deveProsseguir(processo)) {

				executar(processo);

			} else {
				criarLog(processo,
						"Operacao Nao realizada! Processo possui alguma condicao impeditiva!\nServidor deve analisa-lo");
			}

		} catch (AutomacaoException ae) {
			throw ae;

		} catch (Exception e) {
			e.printStackTrace();
			criarLog(processo, "\nOcorreu um erro:" + processo.getNumeroProcesso() + " >" + e.getMessage());

		}

		System.out.println("fim executar....");

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

		if (processo.getEtiquetasAutomacao().size() > 1) {
			// - Caso haja mais de uma etiqueta da automação, consultar o sinapses para
			// saber qual delas deverá ser atribuída ao processo
			for (Iterator iterator = processo.getEtiquetasAutomacao().iterator(); iterator.hasNext();) {

				String localizador = (String) iterator.next();
				if (localizador.equals(processo.getEtiquetaSinapses())) {
					atribuirLocalizadorProjudi(processo, processo.getEtiquetaSinapses());
				}
			}

		} else {
			for (Iterator iterator = processo.getEtiquetasAutomacao().iterator(); iterator.hasNext();) {

				atribuirLocalizadorProjudi(processo, (String) iterator.next());
				break;

			}

		}

		criarLog("Procedimento realizado com sucesso!!\n", obterArquivoLog());

	}

	private void atribuirLocalizadorProjudi(Processo processo, String localizador) throws AutomacaoException {

		try {
			criarLog("O localizador "+ localizador + " foi adicionado ao processo "+ processo.getNumeroProcessoFormatado(), obterArquivoLog());
			processo.setEtiqueta(localizador);
			
			obterConexaoMongo().persistirProcessoMongoDB(processo);
			
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar o procedimento no processo " + processo.getNumeroProcesso());
		}
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return processo.getEtiquetasAutomacao().size() >= 1;
	}

}