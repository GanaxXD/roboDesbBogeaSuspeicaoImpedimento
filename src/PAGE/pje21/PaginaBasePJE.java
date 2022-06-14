package PAGE.pje21;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import DAO.GenericDao;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.PaginaBase;
import RN.GenericRN;
import tjma.time.*;

/**
 * Classe abstrata que representa um rob� do PJE. Possui os m�todos principais
 * que todo rob� do PJE necessita (realizar login, alternar perfil,
 * obterDadosBanco)
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public abstract class PaginaBasePJE extends PaginaBase {

	protected void realizarLogin() throws AutomacaoException {
		
		pegarTempoInicialOpeRobo();
		
		System.out.println("Realizar Login...");
		if (isEmpty(getParametros().getUsuario()) || isEmpty(getParametros().getSenha())) {
			realizaLoginToken();
		} else {
			realizaLogin();
		}

	}


	protected void realizaLogin() throws AutomacaoException {

		try {

			navegateTo(getParametros().getUrl());
			digitar("//input[@id = 'username']", getParametros().getUsuario(), 10, 3000);
			digitar("//input[@id = 'password']", getParametros().getSenha(), 10, 1000);
			Thread.sleep(1000);
			clicar(By.id("btnEntrar"));
			Thread.sleep(3000);
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar login! " + getParametros().getUrl());
		}

	}

	protected void menuNovoProcesso() throws AutomacaoException {
		try {

			clicar("//a[@title='Abrir menu']", 10, 5000);

			clicar("//a[text()=' Processo ']", 10, 2000);

			clicar("//a[text()=' Novo processo ']", 10, 2000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar o menu novo Processo: " + e.getMessage());
		}
	}

	protected void realizaLoginToken() throws AutomacaoException {

		try {

			navegateTo(getParametros().getUrl());
			clicar("//input[@id='loginAplicacaoButton']", 15, 5000);

			Thread.sleep(5000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar login com token!");
		} finally {

		}
	}

	protected void selecionarPerfil() throws AutomacaoException {

		try {

			clicar("//span[@class='hidden-xs nome-sobrenome tip-bottom']", 20, 2000);
			Thread.sleep(5000);
			List<WebElement> perfis = obterElementos("//select[contains(@id,'usuarioLocalizacao')]");
			if (perfis.size() > 0) {
				selecionar("//select[contains(@id,'usuarioLocalizacao')]", getParametros().getPerfil());
				try {
					Thread.sleep(2000);
					if (elementoExiste(By.xpath("//a[@class='tip']"))
							&& ElementoClicavel(By.xpath("//a[@class='tip']"))) {
						clicar("//span[@class='hidden-xs nome-sobrenome tip-bottom']", 20, 2000);
					}
				} catch (Exception e) {

				}

				try {
					Thread.sleep(2000);

					if (elementoExiste(By.xpath("//input[@value='Painel do usuário']"))) {
						clicar("//input[@value='Painel do usuário']", 10, 1000);
					}
				} catch (Exception e) {
					System.out.println("Erro ao clicar no botão painel do usuário");
				}

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
