package tjma.PAGE.pje215;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import MODEL.Processo;
import PAGE.AutomacaoException;

/**
 * Classe abstrata que representa um robô que realiza trabalho no painel de
 * tarefas do PJE.
 * 
 * @autor William Sodré
 * @TJMA
 */
public abstract class PainelTarefasPJE extends PaginaBasePJE {

	protected abstract void executar(Processo processo) throws InterruptedException, AutomacaoException;

	protected abstract boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException;

	protected void movimentar() throws InterruptedException, AutomacaoException {

		if (getParametros().getMovimentar() != null && getParametros().getMovimentar().length > 0) {

			for (int i = 0; i < getParametros().getMovimentar().length; i++) {
				System.out.println(i+") "+getParametros().getMovimentar()[i]);
				movimentar(getParametros().getMovimentar()[i]);
			}
		}

	}
	
	
	

	protected void selecionarTarefa() throws AutomacaoException {
		try {
			alternarFrame(new String[] { "ngFrame" });
			filtrarEtiqueta(getParametros().isFiltrarEtiqueta(), getParametros().getFiltrarEtiqueta());

			String caminhoTarefa = "//right-panel/div/div/div[3]//span[text()='" + getParametros().getTarefa() + "']";
			Thread.sleep(0000);

			esperarElemento(caminhoTarefa, 1);

			Thread.sleep(0000);

			List<WebElement> tarefa = obterElementos(caminhoTarefa);
			if (tarefa.size() == 0) {
				clicarEmLink(getParametros().getTarefa(), " ", 15, 3000);
			} else if (tarefa.size() > 1) {
				for (WebElement webElement : tarefa) {
					webElement.click();
					break;
				}
			} else if (tarefa.size() == 1) {
				clicar(caminhoTarefa, 1, 1000);
			}
			
			if(getParametros().getFiltrarClasse()!=null && !getParametros().getFiltrarClasse().equalsIgnoreCase("")) {
				clicar("//processos-tarefa/div[1]/div[1]/filtro-tarefas/div/div[2]/span/button[1]", 20, 5000);
				limparDigitacao(
						"//processos-tarefa/div[1]/div[1]/filtro-tarefas/div/div[2]/span/div/form/fieldset/div[4]/input");
				digitar("//processos-tarefa/div[1]/div[1]/filtro-tarefas/div/div[2]/span/div/form/fieldset/div[4]/input",
						getParametros().getFiltrarClasse(), 20, 5000);
				clicar("//processos-tarefa/div[1]/div[1]/filtro-tarefas/div/div[2]/span/div/form/fieldset/div[14]/button[1]", 20, 5000);
				clicar("//processos-tarefa/div[1]/div[1]/filtro-tarefas/div/div[2]/span/button[1]", 20, 5000);
				Thread.sleep(3000);
			}
					
			System.out.println();

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a tarefa: " + getParametros().getTarefa()
					+ ".\nVerifique se existe processo na tarefa ou se o sistema demorou de responder por algum problema técnico.");
		}

	}

	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		try {
			System.out.println("Selecionar a Tarefa " + tarefa);

			alternarFrame(new String[] { "ngFrame" });
			filtrarEtiqueta(isFiltrarEtiqueta, etiqueta);
			String caminhoTarefa = "//right-panel/div/div/div[3]//span[text()[contains(.,'" + tarefa + "')]]";

			ScrollAteElemento(By.xpath(caminhoTarefa));

			List<WebElement> listaTarefas = obterElementos(caminhoTarefa);
			if (listaTarefas.size() == 0) {
				clicarEmLink(getParametros().getTarefa(), " ", 8, 3000);
			} else if (listaTarefas.size() > 1) {
				for (WebElement webElement : listaTarefas) {
					webElement.click();
					break;
				}
			} else if (listaTarefas.size() == 1) {
				clicar(caminhoTarefa, 10, 2000);
			}

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a tarefa: " + tarefa
					+ ".\nVerifique se existe processo na tarefa ou se o sistema demorou de responder por algum problema técnico."
					+ e.getMessage());
		}

	}

	protected void filtrarEtiqueta(boolean flag, String etiqueta) throws AutomacaoException {
		try {
			if (etiqueta != null && !etiqueta.equals("")) {
				System.out.println("Filtrar Etiqueta " + etiqueta);
			}

			if (flag) {

				alternarFrame(new String[] { "ngFrame" });

				String caminhoComponenteCasinha = "//selector/div/div/div[1]/side-bar/nav/ul/li[1]/a/i";

				clicar(caminhoComponenteCasinha, 8, 2000);

				String caminhoComponenteTarefas = "//right-panel/div/div/div[3]/tarefas/div";

				clicar(caminhoComponenteTarefas + "/div[1]/div", 8, 2000);
				Thread.sleep(2000);
				limparDigitacao(
						caminhoComponenteTarefas + "/div[2]/filtro-tarefas-pendentes/div/form/fieldset/div[3]/input");

				digitar(caminhoComponenteTarefas + "/div[2]/filtro-tarefas-pendentes/div/form/fieldset/div[3]/input",
						etiqueta, 8, 2000);

				clicar(caminhoComponenteTarefas + "/div[2]/filtro-tarefas-pendentes/div/form/fieldset/div[4]/button[1]",
						8, 2000);
				System.out.println();
				Thread.sleep(3000);
			}

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível filtrar os processos pela etiqueta " + etiqueta);
		}
	}

	protected boolean filtrouEtiqueta(boolean flag, String etiqueta) throws AutomacaoException {
		if (flag) {
			boolean filtrou = elementoExiste(
					By.xpath("//p-datalist/div/div/ul/li//span[text()[contains(.,'" + etiqueta + "')]]"));

			if (!filtrou) {
				throw new AutomacaoException("Nao foi possivel filtrar os processos pela etiqueta " + etiqueta);

			}
		}

		return true;

	}

	protected boolean existeEtiqueta(String etiqueta) throws AutomacaoException {
		return elementoExiste(By.xpath("//p-datalist/div/div/ul/li//span[text()[contains(.,'" + etiqueta + "')]]"));
	}
	
	
	protected void realizarTarefa(Processo processo) throws AutomacaoException {
		System.out.println("realizarTarefa......" + processo.getNumeroProcessoFormatado());
		String campoPesquisa = "//input[@id='inputPesquisaTarefas']";

		try {
			alternarFrame(new String[] { "ngFrame" });

			limparDigitacao(campoPesquisa);

			digitar(campoPesquisa, processo.getNumeroProcessoFormatado(), 2, 2000);

			clicar("//button[@title = 'Pesquisar']", 2, 2000);
			Thread.sleep(2000);
			int qtdProcessos = obterQuantidadeElementos("//p-datalist/div/div/ul/li");

			if (filtrouEtiqueta(getParametros().isFiltrarEtiqueta(), getParametros().getFiltrarEtiqueta())
					&& deveProsseguir(processo)) {

				fecharJanelaDetalhes();
				alternarFrame(new String[] { "ngFrame" });
				clicar("//span[text()[contains(.,'" + processo.getNumeroProcessoFormatado() + "')]]", 2, 2000);

				String etiqueta = getParametros().getAtribuirEtiqueta();
				if (qtdProcessos > 1 && !processo.getNumeroRecursoInterno().equals("")) {
					etiqueta = getParametros().getAtribuirEtiqueta() + processo.getNumeroRecursoInterno();
				}
				
				atribuirEtiqueta(etiqueta, getParametros().atribuirEtiqueta(), processo);

				movimentar();
				
				antesExecutar(processo);
				
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

			criarLog(processo, "\nOcorreu um erro: " + processo.getNumeroProcesso());

		} finally {
			fecharJanelaDetalhes();

			// getDriver().switchTo().defaultContent();
			// limparDigitacao("//*[@id=\"inputPesquisaTarefas\"]", 10, 3000);

		}

		System.out.println("fim realizarTarefa....");

	}

	protected void atribuirEtiqueta(String etiqueta) throws AutomacaoException {
		try {

			alternarFrame(new String[] { "ngFrame" });

			clicar("//button[@tooltip='Etiquetas do processo']", 10, 1000);

			limparDigitacao("//input[@name='itPesquisarEtiquetas']", 10, 2000);

			digitar("//input[@name='itPesquisarEtiquetas']", etiqueta, 10, 2000);

			digitar("//input[@name='itPesquisarEtiquetas']", Keys.ENTER, 10, 2000);

			Thread.sleep(500);
			if (!elementoExiste(By.xpath("//div[text()='Nova etiqueta criada']"))) {
				System.out.println("Analisando lista de etiquetas existentes");
				String caminhoEtiquetas = "//pje-selecionar-etiquetas/div/div/table/tbody";

				int qtdListaEtiquetas = obterElementos(caminhoEtiquetas + "/tr").size();
				if (qtdListaEtiquetas >= 1) {
					for (int i = 1; i <= qtdListaEtiquetas; i++) {

						String textoEtiqueta = obterTexto(caminhoEtiquetas + "//tr[" + i + "]//td[2]");
						System.out.println("ETIQUETA: " + textoEtiqueta);
						if (textoEtiqueta.equalsIgnoreCase(etiqueta)) {

							if (elementoExiste(By.xpath(
									caminhoEtiquetas + "//tr[" + i + "]//td[1]//button//i[@class='far fa-square']"))) {
								clicar(caminhoEtiquetas + "//tr[" + i + "]//td[1]//button//i[@class='far fa-square']",
										10, 2000);
								Thread.sleep(0000);
								break;
							} /*else {
								criarLog("Etiqueta " + etiqueta + " já existia no processo ");
							}*/
						}

					}
				}

			} else {
				System.out.println("Etiqueta Criada!!");
			}

			limparDigitacao("//input[@name='itPesquisarEtiquetas']", 1, 1000);

			clicar("//button[@tooltip='Etiquetas do processo']", 2, 1000);
		} catch (Exception e) {
			throw new AutomacaoException("Nao foi possivel atribuir a etiqueta " + etiqueta);
		}

	}

	protected void atribuirEtiqueta(String etiqueta, boolean flag, Processo processo) throws AutomacaoException {
		//System.out			.println("Atribuindo a etiqueta " + etiqueta + " ao processo " + processo.getNumeroProcessoFormatado());

		try {
			if (flag) {

				atribuirEtiqueta(etiqueta);

			}

		} catch (Exception e) {
			throw new AutomacaoException(
					"Processo: " + processo.getNumeroProcesso() + ". Erro ao atribuir etiqueta " + flag);

		}
	}

	protected void removerEtiqueta(String etiqueta, Processo processo) throws AutomacaoException {
		try {
			
			if (getParametros().removerEtiqueta()) {
				System.out.println("Removendo a etiqueta "+ etiqueta);
				alternarFrame(new String[] { "ngFrame" });
				clicar("//button[@tooltip='Etiquetas do processo']", 15, 2000);
				clicar("//span[contains(@title, '" + etiqueta + "')]", 15, 2000);
				Thread.sleep(2000);
			}
			

		} catch (Exception e) {
			throw new AutomacaoException("Processo: " + processo.getNumeroProcesso() + ". Erro ao remover etiqueta "
					+ getParametros().getAtribuirEtiqueta());

		}
	}

	protected List<Processo> obterProcessosTarefa() throws AutomacaoException, InterruptedException {

		Thread.sleep(2000);

		int qtdProcessos = obterQuantidadeElementos("//p-datalist/div/div/ul/li");
		List<Processo> nProcessos = new ArrayList<Processo>();

		boolean existsNext = false;
		boolean enableNext = false;

		try {
/*
 * //*[@id="processosTarefa"]/p-datalist/div/p-paginator/div/a[3]/span
 * 
 * //*[@id="processosTarefa"]/p-datalist/div/p-paginator/div/a[3]
 * 
 * //*[@id="processosTarefa"]/p-datalist/div/p-paginator/div/a[3]
 * 
 * //*[@id="processosTarefa"]/p-datalist/div/p-paginator/div/a[3]
 */
			ScrollAteElemento(By
					.xpath("//div/p-paginator/div/a[3]/span[contains(@class,'ui-paginator-icon pi pi-caret-right')]"));
			existsNext = elementoExiste(By
					.xpath("//div/p-paginator/div/a[3]/span[contains(@class,'ui-paginator-icon pi pi-caret-right')]"));

		} catch (Exception e) {
			existsNext = false;
		}

		try {
			enableNext = ElementoClicavel(By
					.xpath("//div/p-paginator/div/a[3]/span[contains(@class,'ui-paginator-icon pi pi-caret-right')]"));
			////*[@id="processosTarefa"]/p-datalist/div/p-paginator/div/a[3]

		} catch (Exception e) {
			enableNext = false;
		}

//		Thread.sleep(2000);

		while ((existsNext == true && enableNext == true) || (qtdProcessos) > 0) {
			Thread.sleep(2000);

			List<WebElement> linhas = obterElementos("//p-datalist/div/div/ul/li");
			for (int i = 1; i <= linhas.size(); i++) {

				WebElement li = linhas.get(i - 1).findElement(By.xpath(
						"//p-datalist/div/div/ul/li[" + i + "]//span[contains(@class,'tarefa-numero-processo')]"));

				String nProcesso = li.getText();
				if (nProcesso.indexOf(" ") != -1) {
					nProcesso = nProcesso.substring(nProcesso.indexOf(" ") + 1);
				}

				Processo proc = new Processo();
				proc.setNumeroProcesso(nProcesso);
				nProcessos.add(proc);
				System.out.println("Processos: " + i + " - " + proc.getNumeroProcesso());
			}
////*[@id="processosTarefa"]/p-datalist/div/p-paginator/div/a[3]
			
			if (existsNext) {

				try {

					if (ElementoClicavel(By.xpath(
							"//div/p-paginator/div/a[3][contains(@class,'ui-paginator-next') and contains(@class,'ui-state-disabled')]"))) {
						enableNext = false;
						break;
					}

				} catch (Exception e) {
					clicar("//div/p-paginator/div/a[3]/span[contains(@class,'ui-paginator-icon pi pi-caret-right')]",
							20, 1000);

					enableNext = true;
				}

			} else {
				break;
			}
		}

		try {
//			clicar("//div/p-paginator/div/a[1]/span[contains(@class,'ui-paginator-icon pi pi-step-backward')]", 20,
//					1000);
			clicar("//div/p-paginator/div/a[1]/span[contains(@class,'ui-paginator-icon pi pi-step-backward')]", 2,
					1000);
			// - apenas para voltar para a primeira página.
		} catch (Exception e) {
			// - se der erro não precisa fazer nada
		}

		return nProcessos;
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
							+ " N�o realizada! Alguma parte do processo est� na blacklist. \nServidor deve analis�-lo",
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
			Thread.sleep(1000);
			alternarFrame(new String[] { "ngFrame" });
			
			clicar("//button[@id='btnTransicoesTarefa']", 30, 1000);
			//clicar("//*[@id=\"frameTarefas\"]/div/div[2]/div[2]/ul/li[3]/a", 30, 3000);
			//clicar("//button[@id='btnTransicoesTarefa']", 30, 1000);
			clicar("//a[text()='" + label + "']", 30, 3000);
			
			Thread.sleep(3000);
		} catch (Exception e) {
			throw new AutomacaoException(
					"Nao foi possivel movimentar o processo. Transicao" + label + " nao localizada.");
		}

	}
	
	protected void antesExecutar(Processo processo) {
	}

	protected void escolherTarefaSecretaria(String tarefa) throws AutomacaoException, InterruptedException {
		try {
			clicar("//input[contains(@id, '" + tarefa + "')]", 10, 3000);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

		selecionar("//select[contains(@id,'selectMenuTipoDocumento')]", getParametros().getTipoAto(), 15, 3000);

	}

	protected void selecionarModeloAto() throws AutomacaoException, InterruptedException {
		selecionar("//select[contains(@id,'selectModeloDocumento')]", getParametros().getModeloAto(), 15, 3000);
	}

	protected void selecionarTipoAto(String tipoAto) throws AutomacaoException, InterruptedException {
		System.out.println("Selecionando o tipo de ato " + tipoAto);
		// esperarElemento("//select[contains(@id,'tipoProcessoDocumento')]", 15);
		try {
			selecionar("//select[contains(@id,'selectMenuTipoDocumento')]", tipoAto, 20, 3000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void selecionarModeloAto(String modeloAto) throws AutomacaoException, InterruptedException {
		try {
			
			System.out.println("Selecionando o modelo " + modeloAto);
			selecionar("//select[contains(@id,'selectModeloDocumento')]", modeloAto, 20, 3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void digitarMovimentacaoProcessual(String movimento) throws AutomacaoException, InterruptedException {

		System.out.println("Informando a movimentação processual " + movimento);

		limparDigitacao("//input[contains(@id, 'homologadorEventoTreeParamPesquisaInput')]", 15, 1000);
		digitar("//input[contains(@id, 'homologadorEventoTreeParamPesquisaInput')]", movimento, 20, 2000);

	}

	protected void salvar() throws AutomacaoException, InterruptedException {
		System.out.println("Salvando...");
		clicar("//input[@value = 'Salvar']", 40, 10000);
		Thread.sleep(5000);
	}

	protected void assinar() throws AutomacaoException, InterruptedException {
		alternarParaFramePrincipalTarefas();
		System.out.println("Assinando...");

		clicar("//input[@value = 'Assinar documento(s)']", 20, 10000);
		Thread.sleep(10000);
	}

	protected void abrirDetalhesProcesso() throws AutomacaoException, InterruptedException {
		clicar("//button[@title='Abrir autos']", 15, 2000);
		Thread.sleep(5000);
		alternarParaDetalhes();
	}
	
	/**
	 * Expande o painel "Mais Detalhes" disponível na janela dos autos de um processo.
	 * 
	 * @throws AutomacaoException
	 * @throws InterruptedException
	 */
	protected void abrirPainelMaisDetalhesProcesso() throws AutomacaoException, InterruptedException {
		clicar("//a[@title='Mais detalhes']", 3, 1000);
		Thread.sleep(1000);
	}

	protected void alternarParaDetalhes() throws AutomacaoException, InterruptedException {
		for (String winHandle : getDriver().getWindowHandles()) {
			getDriver().switchTo().window(winHandle);

		}
	}

}
