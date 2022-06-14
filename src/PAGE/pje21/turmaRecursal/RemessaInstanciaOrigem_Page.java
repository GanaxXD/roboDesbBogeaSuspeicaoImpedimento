package PAGE.pje21.turmaRecursal;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * Robo que realiza apenas o envio dos processos para o primeiro grau. 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class RemessaInstanciaOrigem_Page extends PainelTarefasPJE {

	public RemessaInstanciaOrigem_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo)
			throws InterruptedException, AutomacaoException {

		
		alternarFrame(new String[] { "ngFrame","frame-tarefa" });
		
		if (getParametros().getTarefa().equals("(CCPC) Análise da Secretaria")) {
			
			movimentar("06 - Encaminhar para o primeiro grau");

			encerrarExpedientes();
	
		}

		clicar("//select[contains(@id,'comboClasseMotivoRemessa')]", 20, 2000);

		selecionar("//select[contains(@id,'comboClasseMotivoRemessa')]", getParametros().getMotivoRemessa(), 20, 2000);

		clicar("//input[contains(@id,'retornar')]", 5, 2000);

		try {
			
			clicar("//input[@value='Confirmar']", 60, 3000);
			
		} catch (Exception e) {
			//- confirmar nao existe
		}
		
		esperarElemento("//a[text()[contains(., 'Remetidos ao primeiro grau')]]", 60);

		System.out.println("Processo " + processo.getNumeroProcesso() + " remetido com sucesso!");
		criarLog("Processo " + processo.getNumeroProcesso() + " remetido com sucesso!", obterArquivoLog());

		Thread.sleep(4000);

	}
	
	protected void encerrarExpedientes() throws AutomacaoException, InterruptedException {
		try {
			alternarParaFramePrincipalTarefas();

			if (elementoExiste(By.xpath("//input[@value='Encerrar expedientes selecionados']"))) {

				clicar("//input[contains(@id,'fechadoHeader')]", 15, 5000);

				clicar("//input[@value='Encerrar expedientes selecionados']", 15, 5000);

				Thread.sleep(2000);
				Alert alert = driver.switchTo().alert();
				alert.accept();

				movimentar("Prosseguir");

			}
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao encerrar expedientes. " + e.getMessage());
		}

	}

	protected boolean deveProsseguir(Processo processo)
			throws InterruptedException, AutomacaoException {
		String numeroProcesso = processo.getNumeroProcesso();

		if (numeroProcesso.contains(".9000") || numeroProcesso.contains(".0000")) {
			criarLog(processo, "Processo não é originário do primeiro grau. Remessa não realizada! ");

			return false;

		} else {
			return true;
		}

	}

}
