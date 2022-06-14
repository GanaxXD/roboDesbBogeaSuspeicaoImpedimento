package tjma.PAGE.pje215.geral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import CLIENT.util.StringSimilarity;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import tjma.PAGE.pje215.PainelTarefasPJE;

/**
 * Robô que realiza citações e intimações de acordo com a configuração passada.
 * Apenas partes devidamente qualificadas são intimadas (Com Procuradorias ou Advogados)
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class CitacaoIntimacao_Page extends PainelTarefasPJE {

	private static final String PARAM_ULTIMO_DOC_ASSINADO_MAGISTRADO = "{{ULTIMO_DOC_ASSINADO_PELO_MAGISTRADO}}";
	
	/**
	 * Identifica quais linhas da tabela de destinatários correspondem a procuradorias.
	 */
	private List<Boolean> trsProcuradoria;

	/**
	 * Armazena o conteúdo HTML do documento mais recente assinado pelo magistrado.
	 */
	private String conteudoHtmlUltimoDocMagistrado;
	

	public CitacaoIntimacao_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		//selecionarPrepararComunicacao();

		preencherDestinatarios(processo);

		prepararExpediente(processo);

		assinarExpediente();

	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		if (partesQualificadas(processo, getParametros().getPolos())) {

			return true;
		} else {

			criarLog(processo,
					"Citação/Intimação Não realizada! Nem todas as partes estão qualificadas para comunicação por diário ou sistema.\nServidor deve analisá-lo");

			return false;
		}

	}

	protected void preencherDestinatarios(Processo processo) throws InterruptedException, AutomacaoException {
		System.out.println("preencherDestinatarios()....");
		int countIntimacaoSistema = 0;
		int countIntimacaoDiario = 0;
		Thread.sleep(3000);
		try {
			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });

			removerDadosPreenchidosPreviamente();

			selecionarPartes();
			
//			Thread.sleep(8000);
			Thread.sleep(3000);
			


			informarPrazoTipoComunicacao(); 

//			Thread.sleep(5000);
			Thread.sleep(2000);
			WebElement tabela = getDriver()
					.findElement(By.xpath("//table[contains(@id, 'destinatariosTable')]//tbody"));

//			Thread.sleep(2000);
			Thread.sleep(1000);
			List<WebElement> linhas = tabela.findElements(By.tagName("tr"));
			Map<String, String> mapa = new HashMap<String, String>();

			List<String> procuradorias = unirProcuradoriasPolos(processo);

			if(getParametros().getComunicacao().equalsIgnoreCase("Citação")) {
				if(procuradorias.size()==0) {
					throw new AutomacaoException(getParametros().getComunicacao()
							+ " não realizada. \nPara citações é necessário que o polo ativo possua procuradoria. Humano deverá verificar! ");
				}
			}
			
			
			for (int i = 1; i <= linhas.size(); i++) {

				Thread.sleep(2000);

				List<WebElement> colunas = linhas.get(i - 1).findElements(By.tagName("td"));

				try {
					String destinatario = colunas.get(1).getText();
					boolean intimacaoSistema = false;

					for (String procuradoria : procuradorias) {
						
						
						boolean checagemProcuradoria = false;
						String identificacao = extrairIdentificacaoNome(destinatario);
						boolean existeImagemMickey = false;
						

						List<WebElement> img = tabela.findElements(
								By.xpath("//tr[" + i + "]/descendant::img[contains(@src,'proc.png')]"));
						if (img != null && img.size() > 0) {
							existeImagemMickey = true;
						}
						
						if(identificacao!=null) {
							checagemProcuradoria = procuradoria.indexOf(identificacao) != -1;
							
							double similaridade = StringSimilarity.similarity(procuradoria, identificacao);
							
							if(!checagemProcuradoria){
								checagemProcuradoria =  similaridade>0.90;
							}
							
						}else {
							
							double similaridade = StringSimilarity.similarity(procuradoria, destinatario);
							
							checagemProcuradoria =  similaridade>0.90;
							
						}
						
						if (checagemProcuradoria || existeImagemMickey) {

							intimacaoSistema = true;
							
						}
						
						/*else {

							// - se a parte entrou aqui existe alguma inconsist�ncia no nome da parte, pois
							// esta na lista de procuradorias mas n�o tem o �cone na tela
							throw new AutomacaoException(getParametros().getComunicacao()
									+ " não realizada. \nExiste alguma inconsistência no cadastro da parte "
									+ destinatario + ". Favor verificar! ");

						}*/
						
					}

					if (intimacaoSistema) {
						countIntimacaoSistema++;
						mapa.put("//select[contains(@id,'destinatariosTable:" + (i - 1) + ":meioCom')]", "Sistema");
						criarLog("\n " + destinatario + " >>>> MEIO: SISTEMA", obterArquivoLog());
					} else {
						countIntimacaoDiario++;
						mapa.put("//select[contains(@id,'destinatariosTable:" + (i - 1) + ":meioCom')]","Diário Eletrônico");
						criarLog("\n " + destinatario + " >>>> MEIO: Diário Eletrônico", obterArquivoLog());
					}

				} catch (Exception e) {
					throw new AutomacaoException("Erro ao preencher dados de intimação." + e.getMessage());
				}

			}
			
			if(getParametros().getPolos().length==2 && procuradorias.size()!=countIntimacaoSistema) {
				throw new AutomacaoException(getParametros().getComunicacao()
						+ " não realizada. \nExiste alguma inconsistência no cadastro das partes do processo  "+ processo.getNumeroProcesso());
			}

//			Thread.sleep(3000);
			Thread.sleep(1000);

			for (Iterator iterator = mapa.keySet().iterator(); iterator.hasNext();) {
				String chave = (String) iterator.next();
				selecionar(chave, mapa.get(chave));
//				Thread.sleep(3000);
				Thread.sleep(1000);
			}

//			clicar("//input[contains(@id,'taskInstanceForm:Processo_Fluxo_prepararExpediente') and @value='Próximo']", 40, 5000);
 			clicar("//input[contains(@id,'taskInstanceForm:Processo_Fluxo_prepararExpediente') and @value='Próximo']", 20, 2000);
//			Thread.sleep(2000);
			Thread.sleep(1000);
			System.out.println("fim preencherDestinatarios()......");
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao preencher os dados dos destinatários no PAC." + e.getMessage());
		}
	}

	private void selecionarPartes() throws AutomacaoException, InterruptedException {
		try {
			clicar("//a[@title = 'Mostrar todos']", 60, 5000);

			for (int i = 0; i < getParametros().getPolos().length; i++) {

				if (getParametros().getPolos()[i].indexOf("Passivo") != -1) {
					System.out.println("//a[contains(text(), 'Polo passivo')]");
//					clicar("//a[contains(text(), 'Polo passivo')]", 50, 10000);
					clicar("//a[contains(text(), 'Polo passivo')]", 25, 5000);
				}
				if (getParametros().getPolos()[i].indexOf("Ativo") != -1) {
					System.out.println("//a[contains(text(), 'Polo ativo')]");
//					clicar("//a[contains(text(), 'Polo ativo')]", 50, 10000);
					clicar("//a[contains(text(), 'Polo ativo')]", 25, 5000);
				}

			}

			Thread.sleep(4000);
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar as partes. "+e.getMessage());
		}
	}

	/**
	 * Identifica quais linhas da tabela de destinatários correspondem a procuradorias.
	 * 
	 * Essa informação é registrada para ser obtida na tela posterior, através do método
	 * {@link #getTrsProcuradoria()}.
	 * 
	 * @throws AutomacaoException
	 */
	private void identificarProcuradorias() throws AutomacaoException {
		List<WebElement> trs = obterElementos("//table[contains(@id, 'destinatariosTable')]//tbody//tr");
		
		setTrsProcuradoria( new ArrayList<>(trs.size()) );
		
		for (int i = 0; i < trs.size(); i++) {
			boolean existeImagemMickey = false;
			List<WebElement> img = obterElementos("//table[contains(@id, 'destinatariosTable')]//tbody"
					+ "//tr["+(i+1)+"]//td[1]//form//img[contains(@src,'proc.png')]");
			if (img != null && img.size() > 0) {
				existeImagemMickey = true;
			}
			
			getTrsProcuradoria().add(existeImagemMickey);
		}
	}	

	private void informarPrazoTipoComunicacao() throws AutomacaoException, InterruptedException {
		try {
			List<WebElement> trs = obterElementos("//table[contains(@id, 'destinatariosTable')]//tbody//tr");
			
			/* Preenchimento dos campos "Comunicação" */
			
			for (int i = 0; i < trs.size(); i++) {
				// - a comunicação sempre é a mesma para ambos os polos
				selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoAtoCombo')]",
//						getParametros().getComunicacao(), 15, 3000);
						getParametros().getComunicacao(), 1, 1000);
			}
			
			/* Preenchimento dos campos "Meio" */
			
			for (int i = 0; i < trs.size(); i++) {
				String meioComunicacao = null;
				
				if (isProcuradoria(i)) {
					meioComunicacao = "Sistema";
				} else {
					meioComunicacao = "Diário Eletrônico";
				}
				
//				selecionar("//select[contains(@id,'destinatariosTable:" + i + ":meioCom')]",
//						meioComunicacao, 15, 3000);
				selecionar("//select[contains(@id,'destinatariosTable:" + i + ":meioCom')]",
						meioComunicacao, 1, 1000);
			}
			
			/* Preenchimento dos campos "Prazo" */
			
			String prazoGeral = getParametros().getPrazo();
			String prazoAdvogado = getParametros().getPrazoAdvogado();
			String prazoProcuradoria = getParametros().getPrazoProcuradoria();
			
			for (int i = 0; i < trs.size(); i++) {
				String prazo = null;
				
				if (isProcuradoria(i)) {
					prazo = prazoProcuradoria;
				} else {
					prazo = prazoAdvogado;
				}
				if (prazo == null) {
					prazo = prazoGeral;
				}
				
				
				if (!prazo.equals("sem prazo")) {

					selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoPrazoCombo')]", "dias");

					String campoQtdPrazo = "//input[contains(@id,'destinatariosTable:" + i + ":quantidadePrazoAto')]";
//					limparDigitacao(campoQtdPrazo, 15, 2000);
					limparDigitacao(campoQtdPrazo, 5, 1000);
					
//					digitar(campoQtdPrazo, getParametros().getPrazo(), 15, 2000);
					digitar(campoQtdPrazo, prazo, 5, 1000);
					

				} else {
					selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoPrazoCombo')]", prazo);

				}

//				Thread.sleep(3000);

			}

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar o tipo de comunicação.");
		}
	}

	/**
	 * Indica se o destinatário na linha informada se trata de uma Procuradoria.
	 * 
	 * @param indexLinhaTabela índica da linha da tabela de destinatários.
	 * @return
	 */
	private Boolean isProcuradoria(int indexLinhaTabela) {
		return getTrsProcuradoria().get(indexLinhaTabela);
	}

	private List<String> unirProcuradoriasPolos(Processo processo) {
		List<String> procuradorias = new ArrayList<String>();
		for (String procuradoria : processo.getListaProcuradoriasPoloAtivo()) {
			procuradorias.add(procuradoria.toString());
		}

		for (String procuradoria : processo.getListaProcuradoriasPoloPassivo()) {
			procuradorias.add(procuradoria.toString());
		}

		return procuradorias;
	}

	private String extrairIdentificacaoNome(String destinatario) {
		String id = null;
		if (destinatario.indexOf("CNPJ") != -1) {
			id = destinatario.substring(0, destinatario.indexOf("CNPJ"));
			id = id.trim();

		} else if (destinatario.indexOf("CPF") != -1) {
			id = destinatario.substring(0, destinatario.indexOf("CPF"));
			id = id.trim();
		}
		return id;
	}

	protected void removerDadosPreenchidosPreviamente() {
		try {
			// - Apagando as partes pre-selecionadas

			List<WebElement> trsListaPreSelecionada = obterElementos("//a[@title='Remover']");

			while (trsListaPreSelecionada != null || trsListaPreSelecionada.size() > 0) {
				trsListaPreSelecionada.get(0).click();
				Thread.sleep(10000);
				trsListaPreSelecionada = obterElementos("//a[@title='Remover']");
			}

		} catch (Exception e) {
			// - OK
			System.out.println("Não foi possivel encontrar o bot�o remover na tela.  " + e.getMessage());
		}
	}

	protected void prepararExpediente(Processo processo) throws AutomacaoException, InterruptedException {

		System.out.println("prepararAto()......");

//		Thread.sleep(10000);
		Thread.sleep(5000);

		try {
//			esperarElemento("//tbody[contains(@id, 'tabelaDestinatarios:tb')]//tr", 50);
			esperarElemento("//tbody[contains(@id, 'tabelaDestinatarios:tb')]//tr", 25);
			
			int qtdDestinatarios = obterQuantidadeElementos("//tbody[contains(@id, 'tabelaDestinatarios:tb')]//tr");
			for (int i = 0; i < qtdDestinatarios; i++) {

//				clicar("//a[contains(@id,'tabelaDestinatarios:" + i + "')]", 50, 15000);
				clicar("//a[contains(@id,'tabelaDestinatarios:" + i + "')]", 25, 7000);
				System.out.println(i);

				if (processo.getDocumentoAto() != null && !processo.getDocumentoAto().equals("")) {
					
//					clicar("//input[contains(@id,'selectInstrumentoRadio:0')]", 30, 10000);
					clicar("//input[contains(@id,'selectInstrumentoRadio:1')]", 15, 5000); //antes estava definido com O e foi substituído pelo 1
					
					try {
						clicar("//a[contains(@id, 'docExistentesTable:" + processo.getDocumentoAto()
//						+ "')][@title=\"Usar como ato de comunicação\"]", 20, 8000);
						+ "')][@title=\"Usar como ato de comunicação\"]", 10, 4000);
					}catch(Exception e) {
						//- se não conseguir clicar no documento do processo, faz uma paginação e tenta novamente.
						clicar("//html/body/div[5]/div/div[4]/form/div/div[2]/span/div/div/div/div/div/div/div/div[2]/div/div/div[2]/div[2]/div/table/tfoot/tr/td/div/table/tbody/tr/td[5]", 15, 3000);
						clicar("//a[contains(@id, 'docExistentesTable:" + processo.getDocumentoAto()
//						+ "')][@title=\"Usar como ato de comunicação\"]", 20, 8000);
						+ "')][@title=\"Usar como ato de comunicação\"]", 10, 4000);
						
					}
					

				} else {
//					clicar("//input[contains(@id,'selectInstrumentoRadio:1')]", 30, 10000);
					clicar("//input[contains(@id,'selectInstrumentoRadio:1')]", 15, 5000);
					
					String modeloAto = null;
					if (isProcuradoria(i)) {
						modeloAto = getParametros().getModeloAtoProcuradoria();
					} else {
						modeloAto = getParametros().getModeloAtoAdvogado();
					}
					if (modeloAto == null) {
						modeloAto = getParametros().getModeloAto();
					}
					selecionar(
							"//select[contains(@name,'taskInstanceForm:Processo_Fluxo_prepararExpediente')][not(contains(@name, 'comboAgrupar'))]",
//							getParametros().getModeloAto(), 30, 3000);
							modeloAto, 15, 3000);
					
					preencherModeloComDocumentoMagistrado();
				}

//				clicar("//input[@value='Confirmar']", 30, 5000);
				clicar("//input[@value='Confirmar']", 15, 2000);
			}
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível preparar o ato. " + e.getMessage());
		}
		System.out.println("fim prepararAto().....");
	}

	private void preencherModeloComDocumentoMagistrado() throws AutomacaoException, InterruptedException {
		
		System.out.println("prepararModeloComDocumentoMagistrado()......");
		
		alternarFrame(new String[] { "ngFrame", "frame-tarefa" });
		Thread.sleep(3000);
		
		String xpathFrameConteudoDocumento = "//iframe[contains(@id,'EditorTextArea_ifr')]";
		esperarElemento(xpathFrameConteudoDocumento, 10);
		WebElement iframeTextEditor = getDriver().findElement(By.xpath(xpathFrameConteudoDocumento));
		getDriver().switchTo().frame(iframeTextEditor);
		Thread.sleep(3000);

		String xpathParamUltimoDocAssinadoMagistrado = "/html/body[@id=\"tinymce\"]//p//*[text()=\"" + PARAM_ULTIMO_DOC_ASSINADO_MAGISTRADO + "\"]//ancestor::p";
		if (elementoExiste(By.xpath(xpathParamUltimoDocAssinadoMagistrado))) {
			System.out.println("Adicionando conteúdo do último documento assinado pelo magistrado...");
			substituirConteudoHTML(xpathParamUltimoDocAssinadoMagistrado, getConteudoHtmlUltimoDocMagistrado(), 10, 3000);
			System.out.println("Concluído - conteúdo adicionado com sucesso.");
		}
		
		alternarFrame(new String[] { "ngFrame", "frame-tarefa" });
		Thread.sleep(3000);
		
		System.out.println("fim prepararModeloComDocumentoMagistrado()......");
	}

	protected void assinarExpediente() throws AutomacaoException, InterruptedException {
		try {

			System.out.println("Assinar().....");
			clicar("//input[contains(@id,'taskInstanceForm:Processo_Fluxo_prepararExpediente') and @value='Próximo']", 30, 15000);
//			clicar("//input[contains(@id,'taskInstanceForm:Processo_Fluxo_prepararExpediente') and @value='Próximo']", 15, 5000);
//			clicar("//input[@value='Assinar digitalmente']", 30, 15000);
			clicar("//input[@value='Assinar digitalmente']", 15, 5000);

//			Thread.sleep(5000);
			Thread.sleep(2000);

			System.out.println("fim Assinar().....");
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possivel assinar o expediente! " + e.getMessage());
		}
	}

	protected boolean partesQualificadas(Processo processo, String[] polos)
			throws InterruptedException, AutomacaoException {
		try {
			System.out.println(" Verificando se todas as partes estão qualificadas.");

//			Thread.sleep(2000);

//			clicar("//button[@title='Abrir autos']", 15, 2000);
			clicar("//button[@title='Abrir autos']", 1, 1000);

//			Thread.sleep(15000);
			Thread.sleep(5000);

			alternarParaDetalhes();
			
			carregarDocumentoProcesso(processo);
			
//			clicar("//a[@title='Mais detalhes']", 30, 2000);
			clicar("//a[@title='Mais detalhes']", 3, 1000);

			Thread.sleep(1000);
			
			if(existeElementoTexto("Outros Interessados")) {

				System.out.println("Quando existir outros interessados o humano deve analisar!.");
				fecharJanelaDetalhes();
				return false;
			}

			carregarPartes(processo);
			
			for (int i = 0; i < polos.length; i++) {
				if (!verificaPoloAdvProc(polos[i])) {

					fecharJanelaDetalhes();
					return false;

				}
			}
			
			clicar("//a[@title='Mais detalhes']", 1, 1000);
			
			carregarUltimoDocumentoMagistrado();
			
		} catch (Exception e) {
			throw new AutomacaoException(
					" Ocorreu um erro ao fazer a verificação das partes(partesQualificadas). " + e.getMessage());
		}

		return true;

	}

	private void carregarUltimoDocumentoMagistrado() 
			throws AutomacaoException, InterruptedException {
		String xpathUltimoDocAssinadoMagistrado = 
				"(//*[@id=\"divTimeLine:eventosTimeLineElement\"]/div[contains(@class,\"media interno tipo-D\")]//div[@class=\"anexos\"]"
				+ "/a[1]/span[contains(text(),\"- Sentença\") or contains(text(),\"- Despacho\") or contains(text(),\"- Decisão\")])[1]"
				+ "/parent::a";
		
		if (elementoExiste(By.xpath(xpathUltimoDocAssinadoMagistrado))) {
			clicar(xpathUltimoDocAssinadoMagistrado);
		} else {
			throw new AutomacaoException("Não foi possível encontrar o último documento assinado pelo magistrado.");
		}

		Thread.sleep(3000);
		
		alternarFrame(new String[] { "frameHtml" });
		
		setConteudoHtmlUltimoDocMagistrado(getDriver().getPageSource());
	}

	private void carregarDocumentoProcesso(Processo processo) throws AutomacaoException {

		if (getParametros().getDocumentoProcesso() != null && !getParametros().getDocumentoProcesso().equals("")) {

			try {

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
						"Não foi possível achar o documento" + getParametros().getDocumentoProcesso() + " do processo "
								+ processo.getNumeroProcesso() + ". Procedimento não poderá ser concluído para ");
			}

		}
	}

	protected void validarCamposObrigatorios() throws AutomacaoException {

		if (getParametros().getPolos() == null || getParametros().getPolos().length == 0) {
			throw new AutomacaoException(
					"Parâmetro polos(poloAtivo,PoloPassivo) deve ser informado para utilização da automação!");
		}

		if (isEmpty(getParametros().getDocumentoProcesso()) && isEmpty(getParametros().getModeloAto())) {
			throw new AutomacaoException(
					"É necessário informar o documento do processo ou modelo do documento a ser utilizado no procedimento.");
		}

		if (isEmpty(getParametros().getComunicacao())) {
			throw new AutomacaoException("É necessário informar a comunicação para realizar o procedimento.");

		}

	}

	protected void carregarPartes(Processo processo) throws AutomacaoException {

		List<String> listaAdvogadosPoloAtivo = obterListaElementos(
				"//div[@id='poloAtivo']//span[text()[contains(.,\"(ADVOGADO)\")]]");
		List<String> listaAdvogadosPoloPassivo = obterListaElementos(
				"//div[@id='poloPassivo']//span[text()[contains(.,\"(ADVOGADO)\")]]");

		List<String> listaProcuradoriasPoloAtivo = new ArrayList<String>();
		List<String> listaProcuradoriasPoloPassivo = new ArrayList<String>();

		if (listaAdvogadosPoloAtivo == null || listaAdvogadosPoloAtivo.size() == 0) {
			// - neste ponto aqui... se não tem advogado, tem procuradoria
			listaProcuradoriasPoloAtivo = obterPartes("poloAtivo", true);
			
		}else {
			int qtdProcuradoriasPoloAtivo = obterQtdProcuradorias("poloAtivo");
			if(qtdProcuradoriasPoloAtivo>0) {
				listaProcuradoriasPoloAtivo = obterPartes("poloAtivo", true);
			}
		}

		if (listaAdvogadosPoloPassivo == null || listaAdvogadosPoloPassivo.size() == 0) {
			// - neste ponto aqui... se não tem advogado, tem procuradoria
			listaProcuradoriasPoloPassivo = obterPartes("poloPassivo", true);
			
		}else {
			int qtdProcuradoriasPoloPassivo = obterQtdProcuradorias("poloPassivo");
			if(qtdProcuradoriasPoloPassivo>0) {
				listaProcuradoriasPoloPassivo = obterPartes("poloPassivo", true);
			}
		}
		
		System.out.println();
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
		
		/*
		 
	<tbody>
					<tr>
						<td>
						<a href="/pje/Processo/ConsultaProcesso/Detalhe/detalheParte.seam?idProcessoTrf=430688&amp;pessoaHome=MUNICIPIO+DE+ITAPARICA+-+CNPJ%3A+13.882.949%2F0001-04+%28RECORRIDO%29&amp;id=2132787" id="navbar:j_id150" target="comprovantePeticaoInicial" onclick="abrirPopUpPeticao();">
				<span class="">MUNICIPIO DE ITAPARICA - CNPJ: 13.882.949/0001-04 (RECORRIDO)
				</span></a>
				<ul class="tree">
						<li>
							<small class="text-muted">
								<i class="fa fa-user mr-10" title="Representante" alt="ícone de pessoa"></i>
								<a href="/pje/Processo/ConsultaProcesso/Detalhe/detalheParte.seam?idProcessoTrf=430688&amp;pessoaHome=ITAMARA+PEREIRA+DOS+SANTOS+%28ADVOGADO%29&amp;id=2132788" id="navbar:j_id160" target="comprovantePeticaoInicial" onclick="abrirPopUpPeticao();">
									
									<span class="">ITAMARA PEREIRA DOS SANTOS - OAB BA60131-A - CPF: 054.658.745-38 (ADVOGADO)
									</span>
								</a>
							</small>
						</li>
				</ul>
		<ul class="tree">
			<li>
				<small class="text-muted">
					<i class="fa fa-users mr-10" title="Procuradoria" alt="ícone de grupo"></i>
					<span title="Procuradoria" class="">Município de Itaparica</span>
				</small>
			</li>
		</ul>
						</td>
					</tr>
			</tbody>
		  
		 */
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
				
				if(getParametros().getComunicacao().equalsIgnoreCase("Citação")) {
					throw new AutomacaoException(
							"\nPara citações todas as partes do polo passivo deverá ser representadas por procuradorias.");
				}
				
				if (verificaAdvPartes(polo)) {
					System.out.println("OK. Todas as partes do " + polo + " possuem advogados!");
					return true;
				} else {

					throw new AutomacaoException(
							"\nNem todos os destinatários possuem advogados ou procuradoria. Intimação não realizada pelo ROBO!");

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
	
	private List<String> obterPartes(String polo, boolean procuradoria) throws AutomacaoException {
		

			List<String> listaProcuradorias = new ArrayList<String>();
			
			String xPathParte = "";
			if(polo.equals("poloAtivo")) {
				xPathParte = "//html/body/div/div[1]/div/form/ul/li/ul/li/div[3]/table/tbody";
				
			}else {
				xPathParte = "//html/body/div/div[1]/div/form/ul/li/ul/li/div[4]/table/tbody";
			}
		
			WebElement tabela1 = getDriver().findElement(By.xpath(xPathParte));

			try {
//				Thread.sleep(2000);
				List<WebElement> linhas = tabela1.findElements(By.tagName("tr"));

				for (int i = 1; i <= linhas.size(); i++) {

					List<WebElement> colunas = linhas.get(i - 1).findElements(By.tagName("td"));
					
					if(procuradoria) {

						try {
							WebElement spanProcuradoria = tabela1.findElement(By.xpath("//span[@title='Procuradoria']"));
							listaProcuradorias.add(spanProcuradoria.getText());
						}catch(Exception e2) {
							//- não achou a procuradoria
						}
						

					}else {

						List<WebElement> elements = colunas.get(0).findElements(By.tagName("a"));
						for (WebElement webElement : elements) {
							listaProcuradorias.add(webElement.getText());
						}
						
					}
					
				}
			} catch (Exception e) {
				//- não faz nada
			}

		
		
		return listaProcuradorias;
	}

	public List<Boolean> getTrsProcuradoria() {
		return trsProcuradoria;
	}

	public void setTrsProcuradoria(List<Boolean> trsPocuradoria) {
		this.trsProcuradoria = trsPocuradoria;
	}

	public String getConteudoHtmlUltimoDocMagistrado() {
		return conteudoHtmlUltimoDocMagistrado;
	}

	public void setConteudoHtmlUltimoDocMagistrado(String conteudoHtmlDocumento) {
		this.conteudoHtmlUltimoDocMagistrado = conteudoHtmlDocumento;
	}

}
