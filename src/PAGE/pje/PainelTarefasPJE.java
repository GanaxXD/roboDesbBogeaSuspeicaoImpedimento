package PAGE.pje;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import MODEL.Processo;
import PAGE.AutomacaoException;

/**
 * Classe abstrata que representa um rob� que realiza trabalho no painel de
 * tarefas do PJE.
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public abstract class PainelTarefasPJE extends PaginaBasePJE {

	protected abstract void executar(Processo processo) throws InterruptedException, AutomacaoException;

	protected abstract boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException;

	protected void movimentar() throws InterruptedException, AutomacaoException {

		if (getParametros().getMovimentar() != null && getParametros().getMovimentar().length > 0) {

			for (int i = 0; i < getParametros().getMovimentar().length; i++) {
				movimentar(getParametros().getMovimentar()[i]);
			}
		}

	}

	protected void selecionarTarefa() throws AutomacaoException {
		try {

			atualizarPagina();
			System.out.println("Clicando em Abrir Menu...");
			/*
			clicar("//a[@title='Abrir menu']", 30, 5000);
			
			System.out.println("Clicando em Painel...");

			clicar("//a[text()=' Painel ']", 30, 3000);

			System.out.println("Clicando em Painel do usuário ...");
			
			clicar("//a[text()=' Painel do usuário ']", 30, 3000);
			
			//System.out.println("Clicando no Painel do Usu�rio...");
			//clicar("//input[@value='Painel do usu�rio']", 60, 5000);
			//atualizarPagina();
			*/
			
			clicar("(//a[@title='Tarefas'])|( //span[text()[contains(.,'Tarefas')]]/preceding-sibling::i)", 60, 5000);
			
			filtrarEtiqueta();

			clicar("//span[text()[contains(.,'" + getParametros().getTarefa() + "')]]", 120, 5000);
			//clicar("//span[text()='"+getParametros().getTarefa()+"']", 120, 5000);

			Thread.sleep(5000);

		} catch (Exception e) {
			
			if (getParametros().isFiltrarEtiqueta()) {
				throw new AutomacaoException("Nenhum processo foi encontrado na tarefa: " + getParametros().getTarefa()
						+ " com a etiqueta " + getParametros().getFiltrarEtiqueta()+ ". \n"+e.getMessage());
			} else {
				throw new AutomacaoException("A tarefa n�o foi encontrada: " + getParametros().getTarefa()
						+ ".\nVerifique se existe processo na tarefa ou se o sistema demorou de responder por algum problema t�cnico."+ ". \n"+e.getMessage());
			}
			
		}

	}

	protected void filtrarEtiqueta() throws AutomacaoException {
		try {
			if (getParametros().isFiltrarEtiqueta()) {

				clicar("//*[@id=\"tabTarefas_content\"]/div[1]/div[1]/span[1]", 60, 3000);

				digitar("//*[@id=\"tabTarefas_content\"]/div[1]/div[3]/div[2]/tags-input/div/div/input",
						getParametros().getFiltrarEtiqueta(), 30, 2000);
				digitar("//*[@id=\"tabTarefas_content\"]/div[1]/div[3]/div[2]/tags-input/div/div/input", Keys.ENTER, 30,
						2000);
				if (elementoExiste(By.xpath("//a[text()='×']"))) {
					clicar("//*[@id=\"tabTarefas_content\"]/div[1]/div[2]", 30, 2000);

				} else {
					throw new AutomacaoException(
							"N�o foi poss�vel localizar a etiqueta " + getParametros().getFiltrarEtiqueta());
				}

			}

		} catch (Exception e) {
			throw new AutomacaoException(
					"N�o foi poss�vel filtrar os processos pela etiqueta " + getParametros().getFiltrarEtiqueta());
		}
	}
	
	protected void filtrarEtiqueta(String etiqueta) throws AutomacaoException {
		try {
				clicar("//*[@id=\"tabTarefas_content\"]/div[1]/div[1]/span[1]", 60, 3000);

				digitar("//*[@id=\"tabTarefas_content\"]/div[1]/div[3]/div[2]/tags-input/div/div/input",
						etiqueta, 30, 2000);
				digitar("//*[@id=\"tabTarefas_content\"]/div[1]/div[3]/div[2]/tags-input/div/div/input", Keys.ENTER, 30,
						2000);
				if (elementoExiste(By.xpath("//a[text()='×']"))) {
					clicar("//*[@id=\"tabTarefas_content\"]/div[1]/div[2]", 30, 2000);

				} else {
					throw new AutomacaoException(
							"N�o foi poss�vel localizar a etiqueta " + etiqueta);
				}

		} catch (Exception e) {
			throw new AutomacaoException(
					"N�o foi poss�vel filtrar os processos pela etiqueta " + etiqueta);
		}
	}

	protected boolean verificarEtiqueta(String etiqueta) throws AutomacaoException {

		// return elementoExiste(By.xpath("//span[text()[contains(.,'" + etiqueta +
		// "')]]"));

		return false;

	}
	
	protected boolean filtrouEtiqueta(Processo processo, String etiqueta) throws AutomacaoException {

		String xpath = "//div[contains(@id, 'panelTags')]//span[text()[contains(.,'" + etiqueta + "')]]";
		boolean existe = elementoExiste(By.xpath(xpath));

		if (!existe) {
			throw new AutomacaoException(
					"A etiqueta " + getParametros().getFiltrarEtiqueta() + " n�o est� presente no processo "
							+ processo.getNumeroProcessoFormatado() + " ou est� indispon�vel nesta tarefa");

		}else {
			criarLog(processo, "Etiqueta foi filtrada corretamente!");
		}

		return true;

	}

	protected boolean filtrouEtiqueta(Processo processo) throws AutomacaoException {
		
		if (getParametros().isFiltrarEtiqueta()) {
			String xpath = "//div[contains(@id, 'panelTags')]//span[text()[contains(.,'" + getParametros().getFiltrarEtiqueta() + "')]]";
			boolean existe = elementoExiste(By.xpath(xpath));

			if (!existe ) {
				throw new AutomacaoException(
						"A etiqueta " + getParametros().getFiltrarEtiqueta() + " não está presente no processo "
								+ processo.getNumeroProcessoFormatado() + " ou est� indisponível nesta tarefa");

			}else {
				criarLog(processo, "Etiqueta foi filtrada corretamente!");
			}

		}
		
		return true;

	}

	protected void realizarTarefa(Processo processo) throws AutomacaoException {
		System.out.println("realizarTarefa........");
		try {

			String campoPesquisa = "//*[@id=\"inputPesquisaTarefas\"]";

			limparDigitacao(campoPesquisa);
			digitar(campoPesquisa, processo.getNumeroProcesso(), 20, 2000);

			clicar("//button[@title = 'Pesquisar']", 20, 2000);

			if (!processoBloqueado() && !blackList() && filtrouEtiqueta(processo) && deveProsseguir(processo)) {

				fecharJanelaDetalhes();
				
				atualizarPagina();

				clicar("//span[text()[contains(.,'" + processo.getNumeroProcesso() + "')]]", 20, 2000);

				atribuirEtiqueta(getParametros().getAtribuirEtiqueta(), processo);

				movimentar();

				executar(processo);

				removerEtiqueta(getParametros().getAtribuirEtiqueta(), processo);

				criarLog(processo, "Procedimento realizado com sucesso!");

			}

			Thread.sleep(2000);

		} catch (WebDriverException we) {
			we.printStackTrace();
			return;

		} catch (AutomacaoException ae) {
			throw ae;

		} catch (Exception e) {

			criarLog(processo, "\nOcorreu um erro: " + processo.getNumeroProcesso()+"\n"+e.getMessage());

		} finally {
			fecharJanelaDetalhes();

			getDriver().switchTo().defaultContent();
			limparDigitacao("//*[@id=\"inputPesquisaTarefas\"]", 20, 3000);

		}

		System.out.println("fim realizarTarefa....");

	}

	protected void atribuirEtiqueta(String etiqueta, Processo processo) throws AutomacaoException {
		try {
			if (getParametros().atribuirEtiqueta()) {
				getDriver().switchTo().defaultContent();
				
				clicar("//*[@id=\"btn-gerenciar-etiquetas\"]", 20, 5000);

				limparDigitacao("//*[@id=\"dropdownmenu-combo-etiquetas\"]/li/fieldset/tags-input/div/div/input");

				digitar("//*[@id=\"dropdownmenu-combo-etiquetas\"]/li/fieldset/tags-input/div/div/input", etiqueta, 20,
						2000);

				digitar("//*[@id=\"dropdownmenu-combo-etiquetas\"]/li/fieldset/tags-input/div/div/input", Keys.ENTER, 20,
						2000);
				
				Thread.sleep(2000);
				
				limparDigitacao("//*[@id=\"dropdownmenu-combo-etiquetas\"]/li/fieldset/tags-input/div/div/input");

				clicar("//*[@id=\"btn-gerenciar-etiquetas\"]", 20, 3000);

			}

		} catch (Exception e) {
			
			criarLog("Processo: " + processo.getNumeroProcesso() + ". Erro ao atribuir etiqueta "
					+ getParametros().getAtribuirEtiqueta(), obterArquivoLog());
		}
	}

	protected void removerEtiqueta(String etiqueta, Processo processo) throws AutomacaoException {
		try {
			if (getParametros().removerEtiqueta()) {

				for (String winHandle : getDriver().getWindowHandles()) {
					getDriver().switchTo().window(winHandle);
					break;
				}

				clicar("//*[@id=\"btn-gerenciar-etiquetas\"]", 30, 3000);

				WebElement ngInclude = getDriver().findElement(By
						.xpath("//ng-include[span[contains(text(),'" + etiqueta + "')]]/a[@title='Remover etiqueta']"));
				Thread.sleep(1000);
				ngInclude.click();

			}

		} catch (Exception e) {
			criarLog("Processos: " + processo.getNumeroProcesso() + ". Erro ao remover etiqueta "
					+ etiqueta, obterArquivoLog());

		}
	}

	protected List<Processo> obterProcessosTarefa() throws AutomacaoException, InterruptedException {

		
		esperarElemento("//div[contains(@id,'TarefasPendentes')]/div/ul/li", 150);
		
		Thread.sleep(5000);

		
		int qtdProcessos = obterQuantidadeElementos("//div[contains(@id,'TarefasPendentes')]/div/ul/li");
		List<Processo> nProcessos = new ArrayList<Processo>();

		boolean existsNext;
		boolean enableNext;

		try {

			ScrollAteElemento(By.xpath("//div[contains(@id,'TarefasPendentes')]/div[3]/span[contains(@class,'next')]"));

			existsNext = elementoExiste(
					By.xpath("//div[contains(@id,'TarefasPendentes')]/div[3]/span[contains(@class,'next')]"));

		} catch (Exception e) {
			existsNext = false;
		}

		try {
			enableNext = ElementoClicavel(
					By.xpath("//div[contains(@id,'TarefasPendentes')]/div[3]/span[contains(@class,'next')]"));

		} catch (Exception e) {
			enableNext = false;
		}

		Thread.sleep(2000);

		while ((existsNext == true && enableNext == true) || (qtdProcessos) > 0) {
			Thread.sleep(2000);

			qtdProcessos = obterQuantidadeElementos("//div[contains(@id,'TarefasPendentes')]/div/ul/li");
			for (int i = 1; i <= qtdProcessos; i++) {
				String nProcesso = obterTexto("//div[contains(@id,'TarefasPendentes')]/div/ul/li["+i+"]//a/div/span[contains(@class,'tarefa-numero-processo')]");
				if(nProcesso.equals("")) {
					break;
				}
				if (nProcesso.indexOf(" ") != -1) {
					nProcesso = nProcesso.substring(nProcesso.indexOf(" ") + 1);
				}

				Processo proc = new Processo();
				proc.setNumeroProcesso(nProcesso);
				nProcessos.add(proc);
				System.out.println("Processo: " + i + " - " + proc.getNumeroProcesso());

			}

			if (existsNext) {

				try {

					if (ElementoClicavel((By.xpath(
							"//div[contains(@id,'TarefasPendentes')]/div[3]/span[contains(@class,'next') and contains(@class,'disable')]")))) {
						enableNext = false;
						break;
					}

				} catch (Exception e) {
					
					try {
						clicar("//div[contains(@id,'TarefasPendentes')]/div[3]/span[contains(@class,'next')]",30,2000);

						Thread.sleep(2000);
						
						qtdProcessos = obterQuantidadeElementos("//div[contains(@id,'TarefasPendentes')]/div/ul/li");
						
						enableNext = true;
					} catch (Exception e2) {
						return nProcessos;
					}
					
					
				}

			} else {
				break;
			}
		}
		return nProcessos;
	}

	protected boolean processoBloqueado() {

		try {

			List<WebElement> lista = getDriver().findElements(By
					.xpath("//span[@class[contains(.,'ng-hide')]]//span[text()[contains(.,'Tarefa bloqueada por')]]"));
			if (lista != null && lista.size() > 0) {
				// - significa que o processo n�o est� bloqueado, ou seja.. o icone n�o est�
				// presente na tela
				return false;
			}

		} catch (Exception e) {
			// significa que o processo est� bloqueado por outra pessoa.
		}

		criarLog(
				getParametros().getComunicacao()
						+ " N�o realizada! O Processo est� bloqueado por algum usu�rio. \nServidor deve analisá-lo",
				obterArquivoLog());
		return true;

		// return false;
	}

	protected boolean blackList() {

		if (getParametros().getBlackList() == null || getParametros().getBlackList().length == 0) {
			return false;

		}

		try {
			String poloPassivo = obterTexto("//span[@class[contains(.,'dtPoloPassivo')]]");
			String poloAtivo = obterTexto("//span[@class[contains(.,'dtPoloAtivo')]]");

			for (String parte : getParametros().getBlackList()) {
				if (poloPassivo.toUpperCase().indexOf(parte.trim().toUpperCase()) != -1
						|| poloAtivo.toUpperCase().indexOf(parte.trim().toUpperCase()) != -1) {
					criarLog(getParametros().getComunicacao()
							+ " N�o realizada! Alguma parte do processo est� na blacklist. \nServidor deve analisá-lo",
							obterArquivoLog());
					return true;
				}
			}

		} catch (Exception e) {

		}

		return false;
	}

	protected void encaminharParaAssinatura() throws AutomacaoException, InterruptedException {

		movimentar("Encaminhar para assinatura");

	}

	protected void movimentar(String label) throws AutomacaoException, InterruptedException {

		try {
			getDriver().switchTo().defaultContent();
			ScrollAteElemento(By.xpath("//button[@id='menu-mais-opcoes']"));
			clicar("//button[@id='menu-mais-opcoes']", 30, 3000);
			clicar("//a[@title='" + label + "']", 30, 3000);
			Thread.sleep(8002);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void escolherTarefaSecretaria(String tarefa) throws AutomacaoException, InterruptedException {
		clicar("//input[contains(@id, '" + tarefa + "')]", 10, 3000);

	}

	protected void finalizarESairDaTarefa() throws AutomacaoException, InterruptedException {
		movimentar("Finalizar e sair da tarefa");

	}

	protected void escolherTarefaCertificarDecurso() throws AutomacaoException, InterruptedException {
		escolherTarefaSecretaria("ccpc_fbg_certificar_decurso");

	}

	protected void escolherTarefaCitarIntimar() throws AutomacaoException, InterruptedException {
		escolherTarefaSecretaria("ccpc_fbg_citar_intimar");
	}

	protected void selecionarTipoAto() throws AutomacaoException, InterruptedException {
		// esperarElemento("//select[contains(@id,'tipoProcessoDocumento')]", 15);
		selecionar("//select[contains(@id,'tipoProcessoDocumento')]", getParametros().getTipoAto(), 20, 3000);

	}

	protected void selecionarModeloAto() throws AutomacaoException, InterruptedException {
		selecionar("//select[contains(@id,'selectModeloDocumento')]", getParametros().getModeloAto(), 20, 3000);
	}

	protected void selecionarTipoAto(String tipoAto) throws AutomacaoException, InterruptedException {
		// esperarElemento("//select[contains(@id,'tipoProcessoDocumento')]", 15);
		selecionar("//select[contains(@id,'tipoProcessoDocumento')]", tipoAto, 20, 3000);

	}

	protected void selecionarModeloAto(String modeloAto) throws AutomacaoException, InterruptedException {
		selecionar("//select[contains(@id,'selectModeloDocumento')]", modeloAto, 20, 3000);
	}

	
	protected void digitarMovimentacaoProcessual() throws AutomacaoException, InterruptedException {

		digitar("//input[contains(@id, 'tarefaEventoTreeParamPesquisaInput')]", getParametros().getMovimento(), 20,
				4000);
	}

	protected void digitarMovimentacaoProcessual(String movimento) throws AutomacaoException, InterruptedException {

		limparDigitacao("//input[contains(@id, 'tarefaEventoTreeParamPesquisaInput')]");
		digitar("//input[contains(@id, 'tarefaEventoTreeParamPesquisaInput')]", movimento, 20, 4000);
	}

	protected void salvar() throws AutomacaoException, InterruptedException {
		clicar("//input[@value = 'Salvar']", 20, 3000);
		Thread.sleep(5000);
	}

	protected void assinar() throws AutomacaoException, InterruptedException {
		alternarParaFramePrincipalTarefas();
		clicar("//input[@value = 'Assinar documento(s)']", 30, 10000);
		Thread.sleep(15000);
	}

	protected void alternarParaDetalhes() throws AutomacaoException {
		for (String winHandle : getDriver().getWindowHandles()) {
			getDriver().switchTo().window(winHandle);

		}
	}
	
	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		// TODO Auto-generated method stub
		
	}

}
