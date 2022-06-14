package PAGE.pje.geral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje.PainelTarefasPJE;

/**
 * Robô utilizado par realização de citações e intimações
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class CitacaoIntimacao_Page extends PainelTarefasPJE {

	public CitacaoIntimacao_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo)
			throws InterruptedException, AutomacaoException {

		preencherDestinatarios(processo);

		prepararExpediente(processo);

		assinarExpediente();

	}

	protected boolean deveProsseguir(Processo processo)
			throws InterruptedException, AutomacaoException {

		if (partesQualificadas(processo, getParametros().getPolos(), getParametros().getTipoPolos())) {

			return true;
		} else {

			criarLog(processo,
					"Citação/Intimação Não realizada! Nem todas as partes estão qualificadas para comunicação por diário ou sistema.\nServidor deve analisá-lo");

			return false;
		}

	}

	protected void preencherDestinatarios(Processo processo) throws InterruptedException, AutomacaoException {
		System.out.println("preencherDestinatarios().....");

		//fecharJanelaDetalhes();
		//getDriver().switchTo().defaultContent();
		
		removerDadosPreenchidosPreviamente();

		clicar("//a[@title = 'Mostrar todos']", 60, 5000);

		for (int i = 0; i < getParametros().getPolos().length; i++) {
			if (getParametros().getPolos()[i].indexOf("Ativo") != -1) {
				clicar("//a[contains(text(), 'Polo ativo')]", 20, 3000);
			}

			if (getParametros().getPolos()[i].indexOf("Passivo") != -1) {
				clicar("//a[contains(text(), 'Polo passivo')]", 20, 8000);
			}
		}

		Thread.sleep(4000);

		List<WebElement> trs = obterElementos("//table[contains(@id, 'destinatariosTable')]//tbody//tr");
		for (int i = 0; i < trs.size(); i++) {

			if (!getParametros().getPrazo().equals("sem prazo")) {
				
				selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoPrazoCombo')]", "dias", 20, 3000);

				limparDigitacao("//input[contains(@id,'destinatariosTable:" + i + ":quantidadePrazoDia')]", 20, 3000);

				digitar("//input[contains(@id,'destinatariosTable:" + i + ":quantidadePrazoDia')]",
						getParametros().getPrazo(), 20, 3000);

			} else {
				selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoPrazoCombo')]",
						getParametros().getPrazo(), 20, 3000);

			}

			Thread.sleep(2000);

		}

		for (int i = 0; i < trs.size(); i++) {
			// - a comunicacao sempre é a mesma para ambos os polos
			selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoAtoCombo')]",
					getParametros().getComunicacao(), 20, 3000);

		}

		Thread.sleep(2000);
		WebElement tabela = getDriver().findElement(By.xpath("//table[contains(@id, 'destinatariosTable')]//tbody"));

		Thread.sleep(2000);
		List<WebElement> linhas = tabela.findElements(By.tagName("tr"));
		Map<String, String> mapa = new HashMap<String, String>();

		List<String> procuradorias = unirProcuradoriasPolos(processo);

		
		for (int i = 1; i <= linhas.size(); i++) {

			Thread.sleep(2000);

			List<WebElement> colunas = linhas.get(i - 1).findElements(By.tagName("td"));

			try {
				String destinatario = colunas.get(1).getText();
				boolean intimacaoSistema = false;
				
				for (String procuradoria : procuradorias) {
					if (procuradoria.indexOf(extrairIdentificacaoNome(destinatario)) != -1) {
						//- Neste ponto a parte esta na lista de procuradorias
						List<WebElement> img = tabela.findElements(By.xpath("//tr[" + i + "]/descendant::img[contains(@src,'proc.png')]"));
						if (img != null && img.size()> 0) {
							//- (dupla checagem) Neste ponto significa que o �cone de procuradoria/defensoria (mickey) esta presente na tela  
						
							intimacaoSistema = true;
						}else {
						
							//- se a parte entrou aqui existe alguma inconsist�ncia no nome da parte, pois est� na lista de procuradorias mas nao tem o icone na tela
							throw new AutomacaoException(getParametros().getComunicacao()+ " não realizada. \nExiste alguma inconsistência no cadastro da parte "+ destinatario+ ". Favor verificar! ");
						}
						
					} 
				}
				
				if(intimacaoSistema) {
					mapa.put("//select[contains(@id,'destinatariosTable:" + (i - 1) + ":meioCom')]", "Sistema");
					criarLog("\n " + destinatario + " >>>> MEIO: SISTEMA", obterArquivoLog());
				}else {
					mapa.put("//select[contains(@id,'destinatariosTable:" + (i - 1) + ":meioCom')]",
							"Diário Eletrônico");
					criarLog("\n " + destinatario + " >>>> MEIO: Diário Eletrônico", obterArquivoLog());
				}
				
				
				
				
			} catch (Exception e) {
				throw new AutomacaoException("Erro ao preencher dados de intimação." + e.getMessage());
			}

		}

		Thread.sleep(3000);

		for (Iterator iterator = mapa.keySet().iterator(); iterator.hasNext();) {
			String chave = (String) iterator.next();
			selecionar(chave, mapa.get(chave));
			Thread.sleep(3000);

		}

		clicar("//input[contains(@id,'taskInstanceForm:Processo_Fluxo_prepararExpediente') and @value='Próximo']", 20, 3000);
		Thread.sleep(2000);
		System.out.println("fim preencherDestinatarios().....");
	}

	protected List<String> unirProcuradoriasPolos(Processo processo) {
		List<String> procuradorias = new ArrayList<String>();
		 for (String procuradoria : processo.getListaProcuradoriasPoloAtivo()) {
			procuradorias.add(procuradoria.toString());
		 }

		 for (String procuradoria : processo.getListaProcuradoriasPoloPassivo()) {
			procuradorias.add(procuradoria.toString());
		 }
		 
		 return procuradorias;
	}



	
	protected String extrairIdentificacaoNome(String destinatario) {
		String id = "";
		if (destinatario.indexOf("CNPJ") != -1) {
			id = destinatario.substring(0, destinatario.indexOf("CNPJ"));
			id = id.trim();

		} else if (destinatario.indexOf("CPF") != -1) {
			id = destinatario.substring(0, destinatario.indexOf("CPF"));
			id = id.trim();
		}
		return id;
	}

	protected String extrairIdentificacao(String destinatario) {
		String id = "";
		if (destinatario.indexOf("CNPJ") != -1) {
			id = destinatario.substring(destinatario.indexOf("CNPJ"));
			id = id.substring(0, id.indexOf("\n"));
			id = id.trim();

		} else if (destinatario.indexOf("CPF") != -1) {
			id = destinatario.substring(destinatario.indexOf("CPF"));
			id = id.substring(0, id.indexOf("\n"));
			id = id.trim();
		}
		return id;
	}

	protected void removerDadosPreenchidosPreviamente() {
		try {
			// - Apagando as partes pre-selecionadas
			alternarParaFramePrincipalTarefas();

			List<WebElement> trsListaPreSelecionada = obterElementos("//a[@title='Remover']");
			
				while(trsListaPreSelecionada!=null || trsListaPreSelecionada.size()>0) {
					trsListaPreSelecionada.get(0).click();
					Thread.sleep(5000);
					trsListaPreSelecionada = obterElementos("//a[@title='Remover']");
				}
				

		} catch (Exception e) {
			//- OK
			System.out.println("N�o foi possivel encontrar o bot�o remover na tela. "+e.getMessage());
		}
	}

	protected void prepararExpediente(Processo processo) throws AutomacaoException, InterruptedException {

		System.out.println("prepararAto().....");

		Thread.sleep(3000);

		int qtdDestinatarios = obterQuantidadeElementos("//tbody[contains(@id, 'tabelaDestinatarios:tb')]//tr");
		for (int i = 0; i < qtdDestinatarios; i++) {

			clicar("//a[contains(@id,'tabelaDestinatarios:" + i + "')]", 10, 5000);

			if (processo.getDocumentoAto() != null && !processo.getDocumentoAto().equals("")) {

				
				clicar("//input[contains(@id,'selectInstrumentoRadio:0')]", 15, 3000);
				
				
				try {
					clicar("//a[contains(@id, 'docExistentesTable:" + processo.getDocumentoAto()
							+ "')][@title=\"Usar como ato de comunicação\"]", 15, 3000);
				}catch(Exception e) {
					//- se não conseguir clicar no documento do processo, faz uma paginação e tenta novamente.
					clicar("//html/body/div[5]/div/div[4]/form/div/div[2]/span/div/div/div/div/div/div/div/div[2]/div/div/div[2]/div[2]/div/table/tfoot/tr/td/div/table/tbody/tr/td[5]", 15, 3000);
					clicar("//a[contains(@id, 'docExistentesTable:" + processo.getDocumentoAto()+ "')][@title=\"Usar como ato de comunicação\"]", 15, 3000);
					
				}
				
				
				
				

			} else {
				clicar("//input[contains(@id,'selectInstrumentoRadio:1')]", 10, 3000);
				selecionar(
						"//select[contains(@name,'taskInstanceForm:Processo_Fluxo_prepararExpediente')][not(contains(@name, 'comboAgrupar'))]",
						getParametros().getModeloAto(), 10, 3000);

			}

			clicar("//input[@value='Confirmar']", 15, 5000);
		}

		System.out.println("fim prepararAto().....");
	}

	protected void assinarExpediente() throws AutomacaoException, InterruptedException {
		System.out.println("Assinar().....");
		clicar("//input[contains(@id,'taskInstanceForm:Processo_Fluxo_prepararExpediente') and @value='Próximo']", 15,
				10000);
		clicar("//input[@value='Assinar digitalmente']", 15, 10000);

		Thread.sleep(5000);

		System.out.println("fim Assinar().....");
	}
	
	
	protected boolean partesQualificadas(Processo processo, String[] polos, String[] tipoParte)
			throws InterruptedException, AutomacaoException {

		System.out.println("Verificando se todas as partes estão qualificadas.");

		Thread.sleep(2000);

		clicar("//a[@title='Abrir autos']", 30, 2000);
		
		Thread.sleep(8000);

		alternarParaDetalhes();

		carregarDocumentoProcesso(processo);

		clicar("//a[@title='Mais detalhes']", 30, 2000);

		Thread.sleep(1000);

		if(existeElementoTexto("Outros Interessados")) {

			System.out.println("Quando existir outros interessados o humano deve analisar!.");
			fecharJanelaDetalhes();
			return false;
		}
		
		carregarPartes(processo, tipoParte);

		for (int i = 0; i < polos.length; i++) {
			if (!verificaPoloAdvProc(polos[i])) {

				fecharJanelaDetalhes();
				return false;

			}
		}
		
		
		

		return true;

	}
	
	protected void carregarDocumentoProcesso(Processo processo) throws AutomacaoException {
		if (getParametros().getDocumentoProcesso() != null && !getParametros().getDocumentoProcesso().equals("")) {

			try {
				atualizarPagina();
				
				esperarElemento("//span[text()[contains(.,\"" + getParametros().getDocumentoProcesso() + "\")]]", 60);
				
				String documentoProcesso = obterTexto(
						"//span[text()[contains(.,\"" + getParametros().getDocumentoProcesso() + "\")]]");
				if (documentoProcesso == null || documentoProcesso.equals("")
						|| documentoProcesso.indexOf("- " + getParametros().getDocumentoProcesso()) == -1) {

					throw new AutomacaoException("Não foi possível achar o documento"
							+ getParametros().getDocumentoProcesso() + " do processo " + processo.getNumeroProcesso()
							+ ". Procedimento não poderá ser concluído para ");

				} else {

					documentoProcesso = documentoProcesso.substring(0,
							documentoProcesso.indexOf("- " + getParametros().getDocumentoProcesso()));
					documentoProcesso = documentoProcesso.trim();
					processo.setDocumentoAto(documentoProcesso);
				}
			} catch (Exception e) {
				throw new AutomacaoException(
						"Não foi possível achar o documento " + getParametros().getDocumentoProcesso() + " do processo "
								+ processo.getNumeroProcesso() + ". Procedimento não poderá ser concluído para ");
			}

		}
	}
	
	protected void validarCamposObrigatorios() throws AutomacaoException {

		if (getParametros().getPolos() == null || getParametros().getPolos().length == 0) {
			throw new AutomacaoException(
					"Parâmetro polos(poloAtivo,PoloPassivo) deve ser informado para utilização da automação!");
		}

		if (getParametros().getTipoPolos() == null || getParametros().getTipoPolos().length != 2) {
			throw new AutomacaoException(
					"Parâmetro tipoParte(AUTOR,RÉU) devem ser informados para utilização da automação!");
		}

		if (isEmpty(getParametros().getDocumentoProcesso()) && isEmpty(getParametros().getModeloAto())) {
			throw new AutomacaoException(
					"É necessário informar o documento do processo ou modelo do documento a ser utilizado no procedimento.");
		}

		if (isEmpty(getParametros().getComunicacao())) {
			throw new AutomacaoException("É necessário informar a comunicação para realizar o procedimento.");

		}

	}
	
	
	protected void carregarPartes(Processo processo, String[] tipoParte) throws AutomacaoException {

		List<String> listaAdvogadosPoloAtivo = obterListaElementos(
				"//div[@id='poloAtivo']//span[text()[contains(.,\"(ADVOGADO)\")]]");
		List<String> listaAdvogadosPoloPassivo = obterListaElementos(
				"//div[@id='poloPassivo']//span[text()[contains(.,\"(ADVOGADO)\")]]");

		List<String> listaProcuradoriasPoloAtivo = new ArrayList<String>();
		List<String> listaProcuradoriasPoloPassivo = new ArrayList<String>();

		if (listaAdvogadosPoloAtivo == null || listaAdvogadosPoloAtivo.size() == 0) {
			// - neste ponto aqui... se não tem advogado, tem procuradoria
			listaProcuradoriasPoloAtivo = obterPartes("poloAtivo", true);
			
		}

		if (listaAdvogadosPoloPassivo == null || listaAdvogadosPoloPassivo.size() == 0) {
			// - neste ponto aqui... se não tem advogado, tem procuradoria
			listaAdvogadosPoloPassivo = obterPartes("poloPassivo", true);

		}
		
		//List<String> listaRecorrente = obterListaElementos("//span[text()[contains(.,\"(" + tipoParte[0] + ")\")]]");
		//List<String> listaRecorrido = obterListaElementos("//span[text()[contains(.,\"(" + tipoParte[1] + ")\")]]");
		
		List<String> listaPartePA = obterPartes("poloAtivo", false);
		List<String> listaPartePP = obterPartes("poloPassivo", false);

		processo.setListaAdvogadosPoloAtivo(listaAdvogadosPoloAtivo);
		processo.setListaAdvogadosPoloPassivo(listaAdvogadosPoloPassivo);
		processo.setListaPartePoloAtivo(listaPartePA);
		processo.setListaPartePoloPassivo(listaPartePP);
		processo.setListaProcuradoriasPoloAtivo(listaProcuradoriasPoloAtivo);
		processo.setListaProcuradoriasPoloPassivo(listaProcuradoriasPoloPassivo);
	}

	private List<String> obterPartes(String polo, boolean procuradoria) throws AutomacaoException {
		

		//ATIVO
		// /html/body/div/div[1]/div/form/ul/li/ul/li/div[3]/table/tbody/tr/td/a/span
		
		//- passivo
		// /html/body/div/div[1]/div/form/ul/li/ul/li/div[4]/table/tbody/tr[1]/td/a/span

		
			List<String> listaProcuradorias = new ArrayList<String>();
			
			String xPathParte = "";
			if(polo.equals("poloAtivo")) {
				xPathParte = "//html/body/div/div[1]/div/form/ul/li/ul/li/div[3]/table/tbody";
				
			}else {
				xPathParte = "//html/body/div/div[1]/div/form/ul/li/ul/li/div[4]/table/tbody";
			}
		
			WebElement tabela1 = getDriver().findElement(By.xpath(xPathParte));

			try {
				Thread.sleep(2000);
				List<WebElement> linhas = tabela1.findElements(By.tagName("tr"));

				for (int i = 1; i <= linhas.size(); i++) {

					List<WebElement> colunas = linhas.get(i - 1).findElements(By.tagName("td"));
					
					if(procuradoria) {
							List<WebElement> procs = linhas.get(i - 1).findElements(By.tagName("ul"));
							if(procs!= null && procs.size()>0) {
							
								List<WebElement> elements = colunas.get(0).findElements(By.tagName("a"));
								for (WebElement webElement : elements) {
									listaProcuradorias.add(webElement.getText());
								}
								
							}
					}else {
						List<WebElement> elements = colunas.get(0).findElements(By.tagName("a"));
						for (WebElement webElement : elements) {
							listaProcuradorias.add(webElement.getText());
						}
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		
		
		return listaProcuradorias;
	}
	
	
	protected int obterQtdProcuradorias(String polo) throws AutomacaoException {
		int qtd = obterQuantidadeElementos("//div[@id='" + polo + "']//span[@title='Procuradoria']");

		if (qtd == 0) {
			qtd = obterQuantidadeElementos("//div[@id='" + polo + "']//span[@title='Defensoria']");
		}
		return qtd;
	}

	protected boolean verificaPoloAdvProc(String polo) throws AutomacaoException {
		System.out.println("verificaPolo().....");
		// Verifica se todas as partes possuem procuradoria. Caso negativo, verifica se
		// possuem advogados. Caso negativo, retorna falso.
		try {
			Thread.sleep(2000);

			int qtdProcuradorias = obterQtdProcuradorias(polo);

			int qtdPartes = obterQuantidadeElementos("//div[@id='" + polo + "']//td");

			Thread.sleep(1000);

			if (qtdProcuradorias > 0 && qtdProcuradorias == qtdPartes) {

				System.out.println("OK. Todas as partes do " + polo + " possuem procuradorias!");
				return true;

			} else {

				if (verificaAdvPartes(polo)) {
					System.out.println("OK. TOdas as partes do " + polo + " possuem advogados!");
					return true;
				} else {

					throw new AutomacaoException(
							"\nNem todos os destinat�rios possuem procuradorias. Intimação não realizada pelo ROBO!");

				}

			}

		} catch (AutomacaoException ae) {
			throw ae;

		} catch (Exception e) {
			criarLog("Erro verificaPolo : " + e.getMessage(), obterArquivoLog());
			System.out.println("Erro verifica polo : " + e.getMessage());
		}

		return false;

	}

	protected boolean verificaAdvPartes(String polo) throws AutomacaoException {
		System.out.println("verificaPartesPoloAtivo().....");
		try {
			Thread.sleep(1000);

			WebElement tabela = getDriver().findElement(By.xpath("//div[@id='" + polo + "']//table//tbody"));

			List<WebElement> linhas = tabela.findElements(By.tagName("tr"));

			for (int i = 0; i < linhas.size(); i++) {

				String texto = linhas.get(i).getText();

				if (texto.indexOf("ADVOGADO") != -1 && texto.indexOf("OAB") != -1) {
					System.out.println("Tem ADVOGADO");
					Thread.sleep(1000);
					continue;
				} else {
					throw new AutomacaoException(
							"\nNem todas as partes possuem advogado. Intimação não realizada pelo ROBO!");
				}

			}

			return true;
		} catch (AutomacaoException ne) {
			throw ne;

		} catch (Exception e) {
			criarLog("Erro ao verificar partes : " + e.getMessage(), obterArquivoLog());
			System.out.println("Erro ao verificar partes : " + e.getMessage());
		}

		return false;

	}

	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		// TODO Auto-generated method stub
		
	}


}
