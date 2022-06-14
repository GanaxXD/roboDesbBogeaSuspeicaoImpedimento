package PAGE.pje.justicaComum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje.geral.CitacaoIntimacao_Page;

public class IntimarMigrados_Page extends CitacaoIntimacao_Page {

	public IntimarMigrados_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}

	protected void preencherDestinatarios(Processo processo) throws InterruptedException, AutomacaoException {
		System.out.println("preencherDestinatarios().....");
		alternarParaFramePrincipalTarefas();
		
		clicar("//a[@title = 'Mostrar todos']", 60, 5000);

		for (int i = 0; i < getParametros().getPolos().length; i++) {
			if (getParametros().getPolos()[i].indexOf("Ativo") != -1) {
				clicar("//a[contains(text(), 'Polo ativo')]", 5, 3000);
			}

			if (getParametros().getPolos()[i].indexOf("Passivo") != -1) {
				clicar("//a[contains(text(), 'Polo passivo')]", 5, 8000);
			}
		}

		Thread.sleep(3000);

		List<WebElement> trs = obterElementos("//table[contains(@id, 'destinatariosTable')]//tbody//tr");
		for (int i = 0; i < trs.size(); i++) {

			if (!getParametros().getPrazo().equals("sem prazo")) {

				selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoPrazoCombo')]", "dias", 15, 2000);

				limparDigitacao("//input[contains(@id,'destinatariosTable:" + i + ":quantidadePrazoDia')]", 15, 2000);

				digitar("//input[contains(@id,'destinatariosTable:" + i + ":quantidadePrazoDia')]",
						getParametros().getPrazo(), 15, 2000);

			} else {
				selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoPrazoCombo')]",
						getParametros().getPrazo(), 15, 2000);

			}

			Thread.sleep(2000);

		}

		for (int i = 0; i < trs.size(); i++) {
			// - a comunicação sempre é a mesma para ambos os polos
			selecionar("//select[contains(@id,'destinatariosTable:" + i + ":tipoAtoCombo')]",
					getParametros().getComunicacao(), 15, 2000);

		}

		Thread.sleep(2000);
		WebElement tabela = getDriver().findElement(By.xpath("//table[contains(@id, 'destinatariosTable')]//tbody"));

		Thread.sleep(2000);
		List<WebElement> linhas = tabela.findElements(By.tagName("tr"));
		Map<String, String> mapa = new HashMap<String, String>();

		List<String> procuradorias = processo.getListaProcuradoriasPoloAtivo();

		for (int i = 1; i <= linhas.size(); i++) {

			Thread.sleep(2000);

			List<WebElement> colunas = linhas.get(i - 1).findElements(By.tagName("td"));

			try {
				String destinatario = colunas.get(1).getText();
				boolean intimacaoSistema = false;

				for (String procuradoria : procuradorias) {
					if (procuradoria.indexOf(extrairIdentificacaoNome(destinatario)) != -1) {
						// - Neste ponto a parte esta na lista de procuradorias
						List<WebElement> img = tabela
								.findElements(By.xpath("//tr[" + i + "]/descendant::img[contains(@src,'proc.png')]"));
						if (img != null && img.size() > 0) {
							// - (dupla checagem) Neste ponto significa que o �cone de
							// procuradoria/defensoria (mickey) est� presente na tela

							intimacaoSistema = true;
						} else {

							// - se a parte entrou aqui existe alguma inconsist�ncia no nome da parte, pois
							// est� na lista de procuradorias mas n�o tem o �cone na tela
							throw new AutomacaoException(getParametros().getComunicacao()
									+ " n�o realizada. \nExiste alguma inconsistência no cadastro da parte "
									+ destinatario + ". Favor verificar! ");
						}

					}
				}

				if (intimacaoSistema) {
					mapa.put("//select[contains(@id,'destinatariosTable:" + (i - 1) + ":meioCom')]", "Sistema");
					criarLog("\n " + destinatario + " >>>> MEIO: SISTEMA", obterArquivoLog());
				} else {
					mapa.put("//select[contains(@id,'destinatariosTable:" + (i - 1) + ":meioCom')]",
							"Diário Eletrônico");
					criarLog("\n " + destinatario + " >>>> MEIO: Diário Eletrônico", obterArquivoLog());
				}

			} catch (Exception e) {
				throw new AutomacaoException("Erro ao preencher dados de intimaçãoo." + e.getMessage());
			}

		}

		Thread.sleep(3000);

		for (Iterator iterator = mapa.keySet().iterator(); iterator.hasNext();) {
			String chave = (String) iterator.next();
			selecionar(chave, mapa.get(chave));
			Thread.sleep(3000);

		}

		clicar("//input[contains(@id,'taskInstanceForm:Processo_Fluxo_prepararExpediente') and @value='Próximo']", 15,
				3000);
		Thread.sleep(2000);
		System.out.println("fim preencherDestinatarios().....");
	}
	
	protected boolean partesQualificadas(Processo processo, String[] polos, String[] tipoParte)
			throws InterruptedException, AutomacaoException {

		if (getParametros().getValidarPartes() != null && getParametros().getValidarPartes().equalsIgnoreCase("sim")) {
			return super.partesQualificadas(processo, polos, tipoParte);
		} else {

			clicar("//a[@title='Abrir autos']", 20, 2000);

			Thread.sleep(8000);

			alternarParaDetalhes();

			clicar("//a[@title='Mais detalhes']", 60, 2000);

			Thread.sleep(1000);

			carregarPartes(processo, tipoParte);
			
			if(processo.getListaAdvogadosPoloAtivo().size()>=1) {
				return true;	
			}else {
				return false;
			}
			
		}

	}
	
	/*protected void carregarPartes(Processo processo, String[] tipoParte) throws AutomacaoException {

		List<String> listaAdvogadosPoloAtivo = obterListaElementos(
				"//div[@id='poloAtivo']//span[text()[contains(.,\"(ADVOGADO)\")]]");
		processo.setListaAdvogadosPoloAtivo(listaAdvogadosPoloAtivo);
		
		if(listaAdvogadosPoloAtivo.size()==0) {
			return;
		}
		
		List<String> listaRecorrente = new ArrayList<String>();
		WebElement tabela1 = getDriver().findElement(By.xpath("//html/body/div/div[1]/div/form/ul/li/ul/li/div[3]/table/tbody"));
		obterListaPolo(listaRecorrente, tabela1);
		
		//List<String> listaRecorrido = new ArrayList<String>();
		//WebElement tabela2 = getDriver().findElement(By.xpath("//html/body/div/div[1]/div/form/ul/li/ul/li/div[4]/table/tbody"));
		//obterListaPolo(listaRecorrido, tabela2);
		
		processo.setListaPartePoloAtivo(listaRecorrente);
		
	}*/

	private void obterListaPolo(List<String> listaRecorrente, WebElement tabela) {
		try {
			Thread.sleep(2000);
			List<WebElement> linhas = tabela.findElements(By.tagName("tr"));

			for (int i = 1; i <= linhas.size(); i++) {

				List<WebElement> colunas = linhas.get(i - 1).findElements(By.tagName("td"));

				List<WebElement> elements = colunas.get(0).findElements(By.tagName("a"));
				for (WebElement webElement : elements) {
					listaRecorrente.add(webElement.getText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
