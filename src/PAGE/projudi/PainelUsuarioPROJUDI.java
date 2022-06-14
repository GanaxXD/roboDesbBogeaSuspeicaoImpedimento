package PAGE.projudi;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import MODEL.Processo;
import PAGE.AutomacaoException;

/**
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public abstract class PainelUsuarioPROJUDI extends PaginaBasePROJUDI {

	protected abstract void executar(Processo processo) throws InterruptedException, AutomacaoException;

	protected abstract boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException;

	protected void buscarProcesso() throws AutomacaoException {
		try {
			alternarFrame(new String[] { "mainFrame" });

			Thread.sleep(3000);
			Actions action = new Actions(getDriver());
			WebElement we = getDriver().findElement(By.xpath(
					"/html/body/div[3]/div/div/table/tbody/tr/td/table/tbody/tr/td[3]/a/div/table/tbody/tr/td/font"));
			         
			action.moveToElement(we).build().perform();
			clicar("/html/body/div[7]/table/tbody/tr/td/table/tbody/tr[12]/td/a/div/table/tbody/tr/td[2]/font", 15,
					3000);

			System.out.println();
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a tarefa: " + getParametros().getTarefa());
		}

	}

	protected void buscarProcessoTurma() throws AutomacaoException {
		try {

			Actions action = new Actions(getDriver());
			WebElement we = getDriver().findElement(By.xpath(
					"/html/body/div[2]/div/div/table/tbody/tr/td/table/tbody/tr/td[3]/a/div/table/tbody/tr/td/font"));
			action.moveToElement(we).build().perform();
			clicar("//html/body/div[6]/table/tbody/tr/td/table/tbody/tr[13]/td/a/div/table/tbody/tr/td[2]/font", 15,
					3000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a tarefa: " + getParametros().getTarefa());
		}

	}

	protected void realizarTarefa(Processo processo) throws InterruptedException, AutomacaoException {

		System.out.println("realizarTarefa.......");
		alternarFrame(new String[] { "mainFrame", "userMainFrame" });

		try {

			String numeroProcesso = "//*[@id=\"numeroProcesso\"]";

			limparDigitacao(numeroProcesso, 20, 1000);

			digitar(numeroProcesso, processo.getNumeroProcesso(), 20, 2000);

			try {
				clicar("//input[@name = 'Buscar']", 15, 2000);
			} catch (Exception e) {
				clicar("//input[@name = 'Submeter']", 15, 2000);
			}

			Thread.sleep(2000);

			if (deveProsseguir(processo)) {

				executar(processo);

			} else {
				criarLog(processo,
						"Operacao Nao realizada! Processo possui alguma condicao impeditiva!\nServidor deve analisa-lo");
			}

			Thread.sleep(2000);

		} catch (AutomacaoException ae) {
			throw ae;

		} catch (Exception e) {
			e.printStackTrace();
			criarLog(processo, "\nOcorreu um erro:" + processo.getNumeroProcesso() + " >" + e.getMessage());

		} finally {

			if (!elementoExiste(By.xpath("//*[text()[contains(.,'Intimações Para Certificar')]]"))) {
				reiniciarProcedimento();
			}

		}

		System.out.println("fim executar....");

	}

	protected void reiniciarProcedimento() throws AutomacaoException {
		clicarEmLink("mainFrame", "Página Inicial", " ", 15, 3000);
		selecionarTarefa();
	}

}
