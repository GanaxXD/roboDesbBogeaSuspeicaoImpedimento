package tjma.PAGE.pje215.geral;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import tjma.PAGE.pje215.PainelTarefasPJE;

/**
 * 
 * @autor William Sodré
 * @TJMA
 */
public class ReceberProcessosDevolvidos extends PainelTarefasPJE {
	
	private static final String TAREFA_DEVOLVIDOS = "Devolvidos para instância de origem";

	public ReceberProcessosDevolvidos(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return true;
	}

	@Override
	protected void realizarTarefa(Processo processo) throws AutomacaoException {
		selecionarTarefaDevolverOrigem(processo);
		super.realizarTarefa(processo);
	}

	@Override
	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
	}

	private void selecionarTarefaDevolverOrigem(Processo processo) {
//		System.out.println("Selecionar a Tarefa " + tarefa);

		try {
			alternarFrame(new String[] { "ngFrame" });
			filtrarProcesso(processo);
			
			String textoTarefa = TAREFA_DEVOLVIDOS;
			
			String caminhoTarefa = "//right-panel/div/div/div[3]//span[text()[contains(.,'" + textoTarefa + "')]]";
			
			ScrollAteElemento(By.xpath(caminhoTarefa));
			
			List<WebElement> listaTarefas = obterElementos(caminhoTarefa);
			if (listaTarefas.size() == 0) {
				clicarEmLink(getParametros().getTarefa(), " ", 5, 3000);
			} else if (listaTarefas.size() > 1) {
				for (WebElement webElement : listaTarefas) {
					webElement.click();
					break;
				}
			} else if (listaTarefas.size() == 1) {
				clicar(caminhoTarefa, 3, 2000);
			}
		} catch (AutomacaoException e) {
			e.printStackTrace();
		}
	}
	
	private void filtrarProcesso(Processo processo) throws AutomacaoException {
		String numeroProcesso = processo.getNumeroProcessoFormatado();
		
		try {
			alternarFrame(new String[] { "ngFrame" });
	
			String caminhoComponenteCasinha = "//selector/div/div/div[1]/side-bar/nav/ul/li[1]/a/i";
	
			clicar(caminhoComponenteCasinha, 2, 1000);
	
			String caminhoComponenteTarefas = "//right-panel/div/div/div[3]/tarefas/div";
	
			clicar(caminhoComponenteTarefas + "/div[1]/div", 2, 1000);
			Thread.sleep(2000);
			limparDigitacao(
					caminhoComponenteTarefas + "/div[2]/filtro-tarefas-pendentes/div/form/fieldset/div[1]/input");
	
			digitar(caminhoComponenteTarefas + "/div[2]/filtro-tarefas-pendentes/div/form/fieldset/div[1]/input",
					numeroProcesso, 2, 1000);
	
			clicar(caminhoComponenteTarefas + "/div[2]/filtro-tarefas-pendentes/div/form/fieldset/div[4]/button[1]",
					2, 1000);
			System.out.println();
			Thread.sleep(3000);

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível filtrar o processo " + numeroProcesso);
		}
	}
	
	@Override
	protected void selecionarTarefa() throws AutomacaoException {
	}
}
