package PAGE.projudi;

import java.util.List;

import org.openqa.selenium.By;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.PaginaBase;

/**
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public abstract class PaginaBasePROJUDI extends PaginaBase {

	protected void realizarLogin() throws AutomacaoException {

		if (getParametros().getSenha() != null && !getParametros().getSenha().equals("")) {
			realizaLogin();
		}

	}

	protected void realizaLogin() throws AutomacaoException {

		try {

			navegateTo(getParametros().getUrl());

			alternarFrame(new String[] { "mainFrame" });

			digitar("//input[@id = 'login']", getParametros().getUsuario(), 10, 2000);

			digitar("//input[@id = 'senha']", getParametros().getSenha(), 10, 2000);

			clicar("//img[@alt='entrar']", 10, 2000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar login!");
		}

	}

	protected void selecionarPerfil() {

		try {
			alternarFrame(new String[] { "mainFrame" });

			int size = driver.findElements(By.tagName("iframe")).size();
			for (int i = 0; i <= size; i++) {
				driver.switchTo().frame(i);
				if (existeElementoTexto(getParametros().getPerfil())) {
					break;
				}

			}
			//clicar("//a[contains(text(),'" + getParametros().getPerfil() + "')]", 10, 2000);
			clicar("//a[text()='" + getParametros().getPerfil() + "']", 15, 2000);

		} catch (Exception e) {
			System.out.println("Erro ao selecionar perfil: " + getParametros().getPerfil());
		}

	}

	protected void selecionarTarefa() throws AutomacaoException {
		try {

			alternarFrame(new String[] { "mainFrame", "userMainFrame" });

			clicarEmLink(getParametros().getTarefa(), " ", 20, 10000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a tarefa: " + getParametros().getTarefa());
		}

		

	}

	protected List<Processo> obterProcessosTarefa() throws AutomacaoException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	protected List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException {

		return null;
	}
	
	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		// TODO Auto-generated method stub
		
	}

}
