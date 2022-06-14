package tjma.PAGE.pje215.geral;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import tjma.PAGE.pje215.PainelTarefasPJE;

/**
 * Robô encarregado de redistribuir processos.
 * 
 * @author William Sodré
 * @TJMA
 */
public class RedistribuirProcesso_Page extends PainelTarefasPJE {
	public RedistribuirProcesso_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}
	
	@Override
	protected void movimentar(String label) throws AutomacaoException, InterruptedException {
		try {
			Thread.sleep(1000);
			alternarFrame(new String[] { "ngFrame" });
			
			clicar(By.xpath("//*[@id=\"btnTransicoesTarefa\"]/i"));
			
			if(elementoExiste(By.xpath("//*[@id=\"frameTarefas\"]/div/div[2]/div[2]/ul/li[3]/a"))) {
				clicar("//*[@id=\"frameTarefas\"]/div/div[2]/div[2]/ul/li[3]/a");
			}
			clicar("//button[@id='btnTransicoesTarefa']");
			clicar("//a[text()='"+label+"']",3, 1000);
			      
			Thread.sleep(3000);
		} catch (Exception e) {
			throw new AutomacaoException(
					"Nao foi possivel movimentar o processo. Transicao" + label + " nao localizada.");
		}

	}

	@Override
	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
	if (getParametros().getDadosRedistribuicao() != null) {
			
			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });
			
			String xpathBotaoRedistribuir = "/html/body/div[5]/div/div[3]/form/div/div[2]/span[1]/div/"
					+ "a[contains(@id, 'taskInstanceForm:Processo_Fluxo_abaRedistribuicaoProcesso')]";
			clicar(xpathBotaoRedistribuir, 3, 3000);
			
			alternarParaDetalhes();
			
			selecionar("//select[contains(@id,'tipoRedistribuicao')]",
					getParametros().getDadosRedistribuicao().getMotivo(), 20, 3000);
			selecionar("//select[contains(@id,'jurisdicaoRedistribuicao')]",
					getParametros().getDadosRedistribuicao().getJurisdicao(), 10, 2000);
			selecionar("//select[contains(@id,'comboCompetenciaExclusiva')]",
					getParametros().getDadosRedistribuicao().getCompetencia(), 10, 2000);

			clicar("//input[contains(@id,'btnGravarRedistribuicao')]", 10, 1000);

			alternarJanela();

			clicar("//input[contains(@id,'btnFecharResultadoProtocolacao')]", 10, 1000);

			alternarJanela();

			escreverLog("Processo" + processo.getNumeroProcessoFormatado() + " Redistribuído!!");

	} else {
			escreverLog("Necessário informar dados para redistribuição do(s) processo(s)");
		}

	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return true;
	}

}
