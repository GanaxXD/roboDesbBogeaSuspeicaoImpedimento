package PAGE.pje.fazendaPublica;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje.PainelTarefasPJE;

/**
 * 
 * Rob� que realiza a minuta do despacho de cita��o por AR DIGITAL, no fluxo da
 * classe EXECUCAO FISCAL
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class CitarArFazendaPublica_Page extends PainelTarefasPJE {

	public CitarArFazendaPublica_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		minutarDespacho();

		// Parametrizar para quando o usu�rio for magistrado o robo assinar: assinar();

		criarLog(processo, "Cita��o realizada com sucesso!");

	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		if (processo.getNumeroProcesso().startsWith("8")) {
			return true;
		} else {
			criarLog(processo, "O processo " + processo.getNumeroProcesso()
					+ " n�o � origin�rio do PJE. Humano dever� analis�-lo");
			return false;

		}
	}

	public void minutarDespacho() throws InterruptedException, AutomacaoException {

		alternarParaFramePrincipalTarefas();

		Thread.sleep(8000);

		selecionarTipoAto();

		selecionarModeloAto();

		preencherExpediente();

		preencherMovimentoProcessual();

		encaminharParaAssinatura();

		Thread.sleep(2000);

	}

	private void preencherMovimentoProcessual() throws AutomacaoException, InterruptedException {
		Thread.sleep(3000);
		digitar("//input[contains(@id, 'tarefaEventoTreeParamPesquisaInput')]", getParametros().getMovimento());
		Thread.sleep(3000);

		clicar("//input[@value='Pesquisar']");

		Thread.sleep(3000);

		clicar("//span[text()[contains(.,'" + getParametros().getMovimento() + "')]]");

		Thread.sleep(3000);

		clicar("//input[@value = 'Salvar']");
	}

	private void preencherExpediente() throws InterruptedException, AutomacaoException {
		Thread.sleep(3000);

		limparDigitacao("//input[contains(@id, ':tableDestinatarios:prazoGeralInput')]");
		Thread.sleep(3000);

		digitar("//input[contains(@id, ':tableDestinatarios:prazoGeralInput')]", getParametros().getPrazo());

		Thread.sleep(3000);

		int qtdPartes = obterQuantidadeElementos("//tbody[contains(@id, 'tableDestinatarios:tb')]/tr");
		if (qtdPartes > 1) {
			clicar("//th[text() = 'AR Digital']");

		}

		Thread.sleep(3000);

		clicar("//input[@value = 'Gravar dados do(s) expediente(s)']");
	}

	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		// TODO Auto-generated method stub
		
	}

}
