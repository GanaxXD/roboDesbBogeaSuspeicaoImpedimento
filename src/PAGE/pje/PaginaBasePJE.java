package PAGE.pje;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import DAO.GenericDao;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.PaginaBase;
import RN.GenericRN;

/**
 * Classe abstrata que representa um robô do PJE. Possui os métodos principais
 * que todo robô do PJE necessita (realizar login, alternar perfil,
 * obterDadosBanco)
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public abstract class PaginaBasePJE extends PaginaBase {

	protected void realizarLogin() throws AutomacaoException {

		if (isEmpty(getParametros().getUsuario()) || isEmpty(getParametros().getSenha())) {
			realizaLoginToken();
		} else {
			realizaLogin();
		}

	}

	protected void realizaLogin() throws AutomacaoException {

		try {

			navegateTo(getParametros().getUrl());

			Thread.sleep(7000);

			atualizarPagina();

			digitar("//input[@id = 'username']", getParametros().getUsuario(), 30, 5000);

			digitar("//input[@id = 'password']", getParametros().getSenha(), 30, 3000);

			getDriver().findElement(By.id("btnEntrar")).click();

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar login!");
		}

	}

	protected void realizaLoginToken() throws AutomacaoException {

		try {

			navegateTo(getParametros().getUrl());

			Thread.sleep(10000);

			atualizarPagina();

			clicar("//input[contains(@value,'Certificado Digital')]", 60, 5000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar login com token!");
		}
	}

	protected void selecionarPerfil() throws AutomacaoException {

		try {

			clicar("//span[@class='hidden-xs nome-sobrenome tip-bottom']", 60, 20000);

			Thread.sleep(4000);

			List<WebElement> perfis = obterElementos("//select[contains(@id,'usuarioLocalizacao')]");
			if (perfis.size() > 0) {
				selecionar("//select[contains(@id,'usuarioLocalizacao')]", getParametros().getPerfil(), 60, 5000);
				Thread.sleep(3000);
			} else {

				List<WebElement> perfil = obterElementos(
						"//span[contains(text(),'" + getParametros().getPerfil() + "')]");
				if (perfil.size() == 0) {
					throw new AutomacaoException("Erro ao selecionar perfil: " + getParametros());
				}

			}

			/*clicar("//a[@title='Abrir menu']", 20, 3000);

			clicar("//a[text()=' Painel ']", 20, 3000);

			clicar("//a[text()=' Painel do usuário ']", 20, 3000);*/

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar perfil: " + getParametros());
		}

	}

	protected void menuNovoProcesso() throws AutomacaoException {
		try {

			System.out.println("Clicando em Abrir Menu...");
			clicar("//a[@title='Abrir menu']", 10, 5000);

			clicar("//a[text()=' Processo ']", 10, 2000);

			clicar("//a[text()=' Novo processo ']", 10, 2000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar o menu novo Processo: " + e.getMessage());
		}
	}

	protected void selecionarTarefa() throws AutomacaoException {

	}

	protected List<Processo> obterProcessosTarefa() throws AutomacaoException, InterruptedException {

		return null;
	}

	protected List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException {
		List<Processo> listaProcessos = new ArrayList<Processo>();
		try {
			GenericRN rn = new GenericRN(new GenericDao(getParametros().getDbURL(), getParametros().getDbUser(),
					getParametros().getDbPass()));

			setMapaProcessos(rn.carregarProcessos(getParametros()));

			for (Processo processo : getMapaProcessos().values()) {

				listaProcessos.add(processo);

			}
			criarLog(listaProcessos.toString(), obterArquivoLog());

		} catch (Exception e) {
			throw new AutomacaoException("\nErro ao obterLista de Processos no banco: " + e.getMessage());
		}

		return listaProcessos;

	}

}
