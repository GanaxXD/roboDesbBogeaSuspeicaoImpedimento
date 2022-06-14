package PAGE.pje.geral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import CLIENT.util.Util;
import DAO.ProcessoDto;
import MODEL.Parte;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje.PaginaBasePJE;

/**
 * Rob� utilizado para protocolo de processos.
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class CadastrarProcesso_Page extends PaginaBasePJE {

	public CadastrarProcesso_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void realizarTarefa(Processo processo) throws InterruptedException, AutomacaoException {

		menuNovoProcesso();

		selecionarJurisdicaoeClasse(processo);

		selecionarAssunto(processo);

		preencherPartes(processo);

		preencherPeticaoInicial();

		protocolar(processo);

		fecharJanelaPopup(processo);

	}

	private void fecharJanelaPopup(Processo processo) throws AutomacaoException {
		try {
			Thread.sleep(5000);
			for (String winHandle : getDriver().getWindowHandles()) {
				getDriver().switchTo().window(winHandle);
			}

			getDriver().close();
			Thread.sleep(1000);
			for (String winHandle : getDriver().getWindowHandles()) {
				getDriver().switchTo().window(winHandle);
			}

			String numeroProcesso = obterTexto(By.xpath("//span[@id='numeroProcessoDistribuido']"));
			processo.setNumeroProcesso(numeroProcesso);
			screenShot(processo.getNumeroProcesso(), "DADOS_PROCESSO");

			criarLog(" Processo " + processo.getNumeroProcesso() + " protocolado com sucesso!", obterArquivoLog());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected List<Processo> carregarProcessosArquivo() throws AutomacaoException {
		Map<String, Processo> map = new HashMap<String, Processo>();

		List<Processo> listaProcessos = super.carregarProcessosArquivo();
		for (Processo registro : listaProcessos) {

			String linha = registro.getNumeroProcesso();
			if (linha != null && !linha.trim().equals("")) {
				String[] elementosLinha = linha.split(",");

				ProcessoDto procDto = new ProcessoDto();
				procDto.setIdprocesso(elementosLinha[1].trim());
				procDto.setJurisdicao(elementosLinha[2].trim());
				procDto.setClasse(elementosLinha[3].trim());
				procDto.setAssunto(elementosLinha[4].trim());
				procDto.setTipoparte(elementosLinha[5].trim());
				procDto.setDocumentoparte(elementosLinha[6].trim());

				if (elementosLinha.length == 8) {
					procDto.setRepresentante(elementosLinha[7].trim());
				}

				consolidarPartes(map, procDto);
			}
		}
		listaProcessos = new ArrayList<Processo>();
		for (Processo processo : map.values()) {
			listaProcessos.add(processo);
		}

		return listaProcessos;

	}

	private void consolidarPartes(Map<String, Processo> mapaProcessos_, ProcessoDto processoDto) {

		Processo processo = mapaProcessos_.get(processoDto.getIdprocesso());
		if (processo == null) {
			Processo novoProc = new Processo();
			Util.atribuirValores(novoProc, processoDto);
			adicionarParte(novoProc, processoDto);
			mapaProcessos_.put(processoDto.getIdprocesso(), novoProc);

		} else {
			adicionarParte(processo, processoDto);
		}

	}

	private void adicionarParte(Processo processo, ProcessoDto processoDto) {

		Parte parte = new Parte();
		Util.atribuirValores(parte, processoDto);
		processo.getPartes().add(parte);

	}

	private void preencherPeticaoInicial() throws AutomacaoException {

		try {

			clicar("//td[contains(@id,'novoAnexo_lbl')]", 20, 5000);

			digitar("//iframe[@id='docPrincipalEditorTextArea_ifr']", "TESTE PETICAO", 15, 3000);

			clicar("//input[@value='Salvar']", 10, 3000);

			clicar("//input[@id='btn-assinador']", 10, 3000);

			esperarElemento("//span[@id='_viewRoot:status'][@style='display: block;']", 20);

		} catch (Exception e) {
			throw new AutomacaoException("Ocorreu um erro ao preencher a peti��o inicial: " + e.getMessage());
		}

	}

	private void selecionarJurisdicaoeClasse(Processo processo) throws AutomacaoException {
		try {
			selecionar("//select[contains(@id,'jurisdicaoCombo')]", processo.getJurisdicao(), 20, 2000);
			selecionar("//select[contains(@id,'classeJudicialComboClasseJudicial')]", processo.getClasse(), 10, 2000);
			clicar("//input[@value='Incluir']", 10, 2000);
		} catch (Exception e) {
			throw new AutomacaoException(
					"Falha no preenchimento da jurisdi��o e da classe judicial: " + e.getMessage());
		}
	}

	private void selecionarAssunto(Processo processo) throws AutomacaoException {
		try {
			clicar("//td[contains(@id,'assunto_lbl')] | //td[contains(@id,'assuntoTab_lbl')]", 30, 3000);

			digitar("//input[contains(@id,'codAssuntoTrf')]", processo.getAssunto(), 10, 3000);

			clicar("//input[@id='r_processoAssuntoListSearchForm:search']", 10, 3000);

			clicar("//div[text()[contains(.,'" + processo.getAssunto()
					+ "')]]/parent::span/parent::td/preceding-sibling::td//child::a", 30, 5000);

			esperarElemento("//span[@id='_viewRoot:status'][@style='display: block;']", 20);
		} catch (Exception e) {
			throw new AutomacaoException("Falha no preenchimento do assunto " + e.getMessage());
		}
	}

	private void preencherPartes(Processo processo) throws AutomacaoException {
		try {
			// selecionando as partes
			clicar("//td[@id='tabPartes_lbl']", 10, 5000);
			System.out.println();
			List<Parte> partes = processo.getPartes();

			for (Parte parte : partes) {

				if (parte.getTipoParte().equalsIgnoreCase("AUTOR")) {
					clicar("//span[@id='addParteA']", 30, 5000);
				} else {
					clicar("//span[@id='addParteP']", 30, 5000);

				}

				// - Dados parte
				selecionar("//b[text()='Tipo da Parte']//following-sibling::select", parte.getTipoParte(), 30, 5000);

				digitarDocumentoComMascara("//input[contains(@id,'preCadastroPessoaFisica_nrCPF')]",
						parte.getDocumentoParte(), 20, 8000);

				clicar("//input[contains(@id,'pesquisarDocumentoPrincipal')]", 10, 2000);
				clicar("//input[contains(@id,'btnConfirmarCadastro')]", 30, 3000);

				// - Dados Endere�o
				clicar("//td[contains(@id,'enderecoUsuario_lbl')]", 20, 5000);
				// preencherEndereco("41720100", "01", "COMPLEMENTO");
				if (elementoExiste(By.xpath(
						"//div[text()[contains(.,'Endere�os')]]//parent::div//child::span[text()[contains(.,'0 resultados encontrados')]]"))) {

					preencherEndereco("41720100", "01", "COMPLEMENTO");

				}
				if (!isEmpty(parte.getRepresentante())) {
					selecionar(
							"//select[@name='formInserirParteProcesso:comboRepresentanteDecoration:comboRepresentante']",
							parte.getRepresentante(), 20, 5000);
				}
				clicar("//input[@value='Inserir']", 10, 10000);

			}

			esperarElemento("//span[@id='_viewRoot:status'][@style='display: block;']", 20);
		} catch (Exception e) {
			throw new AutomacaoException("Ocorreu um erro ao preencher os dados das partes: " + e.getMessage());
		}
	}

	private void preencherEndereco(String cep, String numero, String complemento) throws AutomacaoException {
		try {
			digitar("//input[contains(@id,'cadastroPartePessoaEnderecoCEP')]", cep, 40, 8000);
			clicar("//span[contains(@id,'colunaSugestaoEnderecoLogradouro')]", 15, 5000);

			digitar("//input[contains(@id,'EndereconumeroEndereco')]", numero, 15, 5000);

			digitar("//input[contains(@id,'PessoaEnderecocomplemento')]", complemento, 15, 5000);

			clicar("//input[contains(@id,'btnGravarEndereco')]", 15, 5000);

			esperarElemento("//span[@id='_viewRoot:status'][@style='display: block;']", 20);
		} catch (Exception e) {
			throw new AutomacaoException("Falha no preenchimento do endere�o: " + e.getMessage());
		}
	}

	private void protocolar(Processo processo) throws AutomacaoException {

		try {
			clicar("//td[contains(@id,'informativo_lbl')]", 15, 3000);
			Thread.sleep(5000);

			List<WebElement> listaCompetencia = obterElementos("//select[contains(@id,'comboConflitoCompetencia')]");
			if (listaCompetencia.size() > 0) {
				selecionar("//select[contains(@id,'comboConflitoCompetencia')]", "C�vel", 10, 2000);
			}

			clicar("//input[@value='Protocolar']", 10, 10000);

			Thread.sleep(10000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao protocolar processo. " + e.getMessage());
		}
	}

	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		// TODO Auto-generated method stub
		
	}

}
