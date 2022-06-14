package tjma.PAGE.pje215;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import PAGE.AutomacaoException;

/**
 * Classe abstrata que representa um robô do PJE. Possui os métodos principais
 * que todo robô do PJE necessita (realizar login, alternar perfil, obterDadosBanco, etc).
 * 
 * @author William Sodré
 * @TJMA
 */
public abstract class PaginaBasePJE extends PAGE.pje21.PaginaBasePJE {

	protected void clicarBotaoHome() throws AutomacaoException {
		getDriver().switchTo().defaultContent();
		
		String xpathBotaoHome = "/html/body/nav//div/a[@id=\"home\"]";
		try {
			clicar(xpathBotaoHome, 10, 3000);
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new AutomacaoException("Não foi possível clicar no botão home do sistema: ");
		}
	}
	
	@Override
	protected void realizaLogin() throws AutomacaoException {
		super.realizaLogin();
		
		ignorarValidacaoTokenPjeMobile();
		ignorarQuadroAvisos();
	}
	
	private void ignorarQuadroAvisos() throws AutomacaoException{
		String xpathBotaoIrPainelUsuario="//div/form/input[@value='Painel do usuário']";
		String xpathBotaoHome="/home/body/nav//div/a[@id=\"home\"]";
		
		if(elementoExiste(By.xpath(xpathBotaoIrPainelUsuario))) {
			clicar(xpathBotaoIrPainelUsuario, 1, 1000);
			clicar(xpathBotaoHome, 1, 1000);
		}
	}
	
	
	private void ignorarValidacaoTokenPjeMobile() throws AutomacaoException {
		String xpathBotaoProsseguirSemToken = "//*[@id=\"divBas\"]/div[2]/div[4]/a[contains(text(),'Prosseguir sem o Token')]";
		String xpathBotaoHome = "/html/body/nav//div/a[@id=\"home\"]";
		
		if (elementoExiste(By.xpath(xpathBotaoProsseguirSemToken))) {
			clicar(xpathBotaoProsseguirSemToken, 1, 1000);
			clicar(xpathBotaoHome, 1, 1000);
		}
	}

	protected void selecionarPerfil() throws AutomacaoException {

		try {

			clicar("//span[@class='nome-sobrenome']", 1, 1000);
			List<WebElement> perfis = obterElementos("//select[contains(@id,'usuarioLocalizacao')]");
			if (perfis.size() > 0) {
				selecionar("//select[contains(@id,'usuarioLocalizacao')]", getParametros().getPerfil());
				try {
					Thread.sleep(2000);
					if (elementoExiste(By.xpath("//a[@class='tip']"))
							&& ElementoClicavel(By.xpath("//a[@class='tip']"))) {
						clicar("//span[@class='nome-sobrenome']", 1, 1000);
					}
				} catch (Exception e) {

				}

				/* [TJMA] Esse trecho foi comentado para deixar a seleção do perfil mais rápida */
//				try {
//					if (elementoExiste(By.xpath("//input[@value='Painel do usuário']"))) {
////						clicar("//input[@value='Painel do usuário']", 10, 1000);
//						clicar("//input[@value='Painel do usuário']", 1, 1000);
//					}
//				} catch (Exception e) {
//					System.out.println("Erro ao clicar no botão painel do usuário");
//				}

			} else {

				List<WebElement> perfil = obterElementos(
						"//span[contains(text(),'" + getParametros().getPerfil() + "')]");
				if (perfil.size() == 0) {
					throw new AutomacaoException("Erro ao selecionar perfil: " + getParametros().getPerfil());
				}

			}

		} catch (Exception e) {
			throw new AutomacaoException(" Erro ao selecionar perfil: " + getParametros().getPerfil());
		}

	}
}
