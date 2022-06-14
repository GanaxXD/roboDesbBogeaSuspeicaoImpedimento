package PAGE.pje21.turmaRecursal;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;

import CLIENT.SINAPSES.model.Resultado;
import DAO.JulgamentoTematicoDao;
import MODEL.Etiqueta;
import MODEL.FiltroEtiqueta;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;
import RN.JulgamentoTematicoPJERN;

/**
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA TODO: Colocar a configuração das etiquetas no próprio JSON
 */
public class JulgamentoTematico_Page extends PainelTarefasPJE {
	private static List<FiltroEtiqueta> filtros = new ArrayList<FiltroEtiqueta>();

	static {

		filtros.add(new FiltroEtiqueta(Etiqueta.COELBA_APAGAO, "OR", new String[] { "COELBA" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.COELBA_TARIFA_RURAL, "AND", new String[] { "COELBA", "TARIFA RURAL" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.COELBA_SUSPENSAO_FORNECIMENTO, "AND",
				new String[] { "COELBA", "INADIMPLEMENTO" }));
		filtros.add(
				new FiltroEtiqueta(Etiqueta.COELBA_LUZ_PARA_TODOS, "AND", new String[] { "COELBA", "LUZ PARA TODOS" }));
		filtros.add(
				new FiltroEtiqueta(Etiqueta.EMBASA_ROMPIMENTO_ADUTORA, "AND", new String[] { "EMBASA", "ADUTORA" }));////////////////
		filtros.add(new FiltroEtiqueta(Etiqueta.NEGATIVACAO_INDEVIDA, "OR", new String[] { "SPC", "SERASA", "NEGATIVAÇÃO" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.DIVISOR_200, "OR", new String[] { "DIVISOR 200", "DIVISOR 240" }));///////
		filtros.add(new FiltroEtiqueta(Etiqueta.ABONO_PECUNIARIO, "AND", new String[] { "ABONO", "PECUNIÁRIO" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.ABONO_PERMANENCIA, "AND", new String[] { "ABONO", "PERMANÊNCIA" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.BANCOS_EMPRESTIMO, "AND", new String[] { "BANCO", "EMPRÉSTIMO" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.EMBASA_MA_PRESTACAO_SERVICO, "AND", new String[] { "EMBASA", "SANEAMENTO" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.EMBASA_SUSPENSAO_DO_FORNECIMENTO, "AND",new String[] { "EMBASA", "INADIMPLEMENTO" }));///////////////////
		filtros.add(
				new FiltroEtiqueta(Etiqueta.MEDIA_CONSUMO, "OR", new String[] { "Média de Consumo", "REFATURAMENTO" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.LICENCA_PREMIO, "AND", new String[] { "Licença Prêmio" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.FERIAS_NAO_GOZADAS, "OR",new String[] { "Férias não usufruídas", "Férias Vencidas", "Férias Não Gozadas" }));
		filtros.add(new FiltroEtiqueta(Etiqueta.BANCOS_EMPRESTIMO_DECADENCIA_QUADRIENAL, "OR",
				new String[] { "Decadência quadrienal" }));///////////////////////////
		filtros.add(new FiltroEtiqueta(Etiqueta.INCOMPETENCIA_TERRITORIAL_RECOMENDACAO_02, "OR",
				new String[] { "Recomendação n. 02", "Recomendação 02", "Incompetência territorial" }));///////////////////
		filtros.add(new FiltroEtiqueta(Etiqueta.SERVIDOR_PROGRESSAO, "OR", new String[] { "7.867/2010" }));////////////////

	}

	public JulgamentoTematico_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);

	}

	/**
	 * Tive que sobrescrever o m�todo por conta do comportamento padr�o de clicar no
	 * processo na tarefa. Este robo nao deve clicar no processo, apenas etiqueta-lo
	 */
	protected void realizarTarefa(Processo processo) throws AutomacaoException {
		System.out.println("realizarTarefa.....");

		try {
			alternarFrame(new String[] {"ngFrame"});
			String campoPesquisa = "//*[@id=\"inputPesquisaTarefas\"]";

			limparDigitacao(campoPesquisa);
			digitar(campoPesquisa, processo.getNumeroProcesso(), 20, 2000);

			clicar("//button[@title = 'Pesquisar']", 20, 2000);

			if (filtrouEtiqueta(getParametros().isFiltrarEtiqueta(), getParametros().getFiltrarEtiqueta())
					&& deveProsseguir(processo)) {

				alternar(0);

				executar(processo);

				criarLog(processo, "Procedimento realizado com sucesso! Processo:  " + processo.getNumeroProcesso()
						+ " - Etiqueta  Inteligencia Artificial: " + processo.getEtiquetaIAFormatada());

			}

			Thread.sleep(2000);

		} catch (WebDriverException we) {
			we.printStackTrace();
			return;

		} catch (AutomacaoException ae) {
			throw ae;

		} catch (Exception e) {

			criarLog(processo, "\nOcorreu um erro: " + processo.getNumeroProcesso());

		} finally {
			alternar(0);
			getDriver().switchTo().defaultContent();
			limparDigitacao("//*[@id=\"inputPesquisaTarefas\"]", 20, 3000);

		}

		System.out.println("fim realizarTarefa....");

	}

	protected List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException {
		List<Processo> listaProcessos = new ArrayList<Processo>();
		try {
			JulgamentoTematicoPJERN rn = new JulgamentoTematicoPJERN(new JulgamentoTematicoDao(
					getParametros().getDbURL(), getParametros().getDbUser(), getParametros().getDbPass()));

			getParametros().setFiltrosEtiquetas(filtros);

			setMapaProcessos(rn.carregarProcessos(getParametros(), obterConexaoSinapses()));

			for (Processo processo : getMapaProcessos().values()) {

				if (deveProsseguir(processo)) {
					listaProcessos.add(processo);
					System.out.println("processo : " + processo.getNumeroProcessoFormatado()
							+ "\tEtiqueta Inteligencia Artificial: " + processo.getEtiquetaIAFormatada() + " OK!");
				} else {
					System.out.println("processo : " + processo.getNumeroProcessoFormatado()
							+ "\tEtiqueta Inteligencia Artificial: " + processo.getEtiquetaIAFormatada() + " NaO OK!");
				}

			}

		} catch (Exception e) {
			throw new AutomacaoException("\nErro ao obterLista de Processos no banco: " + e.getMessage());
		}

		criarLog(
				"\n\n\n\n\n******************************************************************************************"
						+ listaProcessos.toString() + "\n\nQuantidade de Processos: " + listaProcessos.size() + "\n",
				obterArquivoLog());
		return listaProcessos;

	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		try {
			System.out.println("EXECUTAR.....");
			
			
			alternarFrame(new String[] {"ngFrame"});

			clicar("//p-datalist/div/div/ul/li/processo-datalist-card/div/div[2]/button", 20, 2000);

			clicar("//button[@title='Vincular etiqueta']", 20, 2000);
			if (getParametros().getSinapsesURL() != null) {
				
				limparDigitacao("//input[@name='itPesquisarEtiquetas']", 20, 2000);
				
				digitar("//input[@name='itPesquisarEtiquetas']", processo.getEtiquetaIAFormatada().toString(), 20,2000);
			} else {
				digitar("//input[@name='itPesquisarEtiquetas']", processo.getEtiquetaAutomacao().toString(), 20, 2000);
			}
			
			int etiquetas = obterQuantidadeElementos("//pje-selecionar-etiquetas/div/div/table/tbody/tr");
			
			if(etiquetas==1) {
				clicar("//pje-selecionar-etiquetas/div/div/table/tbody/tr/td[1]/button", 20, 2000);
				
			}else
			if(etiquetas>1) {
				clicar("//pje-selecionar-etiquetas/div/div/table/tbody/tr[1]/td[1]/button", 20, 2000);
			}else {

				digitar("//input[@name='itPesquisarEtiquetas']", Keys.ENTER, 15, 2000);
				
			}

			clicar("//span[text()[contains(.,'Vincular')]]", 20, 2000);

			clicar("//etiquetar-lote/div/div/div/div[3]/div/button[2]", 20, 2000);// FECHAR
			

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar o procedimento do processo  " + processo.getNumeroProcesso());
		}
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		boolean deveProsseguir = false;
		if (getParametros().getSinapsesURL() != null) {
			deveProsseguir = (processo.getEtiquetaIA() != null && processo.getEtiquetaAutomacao() != null
					&& processo.getEtiquetaIA().equals(processo.getEtiquetaAutomacao())
					&& !processo.getEtiquetaIA().equals(Etiqueta.OUTROS));

			if (deveProsseguir && processo.getRetornoSinapses() != null) {

				System.out.println("processo : \t" + processo.getNumeroProcessoFormatado()
						+ " - Etiqueta Inteligencia Artificial: \t" + processo.getEtiquetaIAFormatada() + " - \t%"
						+ processo.getRetornoSinapses().getResultados().toArray()[0] + " OK!");

				Resultado resultado = (Resultado) processo.getRetornoSinapses().getResultados().toArray()[0];
				deveProsseguir = (resultado.getConviccao() > new Float(0.45));
			} else {
				System.out.println("processo : \t" + processo.getNumeroProcessoFormatado()
						+ " - Etiqueta Inteligencia Artificial: \t" + processo.getEtiquetaIAFormatada() + " \tOK!");

			}

		} else {
			deveProsseguir = (processo.getEtiquetaAutomacao() != null
					&& !processo.getEtiquetaAutomacao().equals(Etiqueta.OUTROS));
		}
		// System.out.println("Prosseguir: " + deveProsseguir);
		return deveProsseguir;
	}

	protected void validarCamposObrigatorios() throws AutomacaoException {
		super.validarCamposObrigatorios();

	}

}