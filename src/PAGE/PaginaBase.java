package PAGE;

import PAGE.pje21.PaginaBasePJE;
import tjma.time.TempoRoboProcessamento;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import CLIENT.SINAPSES.SinapsesClient;
import CLIENT.util.Util;
import DAO.DAOMongoDB;
import MODEL.Processo;

/**
 * Classe principal que representa as funcionalidades b�sicas herdadas por todos
 * os rob�s. Define o ciclo de vida dos rob�s, opera��es b�sicas como clicar,
 * navegar, esperar elemento, ...
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public abstract class PaginaBase extends TempoRoboProcessamento {

	protected FileWriter logFile;
	protected Parametros parametros = new Parametros();
	protected WebDriver driver;
	protected SinapsesClient sinapses;
	protected DAOMongoDB mongoDB;
	
	protected List<Processo> listaParticionada;
	protected Map<String, Processo> mapaProcessos = new HashMap<String, Processo>();
	protected SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

	protected Map<String, String> mapaIntimacoesDiario = new HashMap<String, String>();

	public SinapsesClient obterConexaoSinapses() {

		if (sinapses == null) {
			sinapses = new SinapsesClient(getParametros().getSinapsesURL(), getParametros().getSinapsesUser(),
					getParametros().getSinapsesPassword());

		}
		return sinapses;
	}

	
	public DAOMongoDB obterConexaoMongo() {

		if (mongoDB == null) {
			mongoDB = new DAOMongoDB(getParametros().getMongoHost(), getParametros().getMongoDB(),getParametros().getMongoCollection(), getParametros().getMongoPort());

		}
		return mongoDB;
	}
	
	public Map<String, String> getMapaIntimacoesDiario() {
		return mapaIntimacoesDiario;
	}

	public void setMapaIntimacoesDiario(Map<String, String> mapaIntimacoesDiario) {
		this.mapaIntimacoesDiario = mapaIntimacoesDiario;
	}

	public void setSinapses(SinapsesClient sinapses) {
		this.sinapses = sinapses;
	}

	public Map<String, Processo> getMapaProcessos() {
		return mapaProcessos;
	}

	public void setMapaProcessos(Map<String, Processo> mapaProcessos) {
		this.mapaProcessos = mapaProcessos;
	}

	public List<Processo> getListaParticionada() {
		return listaParticionada;
	}

	public void setListaParticionada(List<Processo> listaParticionadaThreads) {
		System.out.println("Quantidade de processos atribu�dos ao robo: " + listaParticionadaThreads.size());
		this.listaParticionada = listaParticionadaThreads;
	}

	public Parametros getParametros() {
		if (parametros == null) {
			parametros = new Parametros();
		}
		return parametros;
	}
	
	public void setParametros(Parametros parametros) {
		this.parametros = parametros;
	}

	protected abstract void realizarLogin() throws AutomacaoException;

	protected abstract void selecionarPerfil() throws AutomacaoException;
	
	protected void antesSelecionarTarefa() throws AutomacaoException {
	}

	protected abstract void selecionarTarefa() throws AutomacaoException;

	protected abstract void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta) throws AutomacaoException;

	protected abstract List<Processo> obterProcessosTarefa() throws AutomacaoException, InterruptedException;

	protected abstract List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException;

	protected abstract void realizarTarefa(Processo processo) throws InterruptedException, AutomacaoException;

	protected void alternar(int indice) throws AutomacaoException {

		try {
			List<String> abas = new ArrayList<>(getDriver().getWindowHandles());
			driver.switchTo().window(abas.get(indice));

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao alterar abas.");
		}

	}

	protected void abrirDiario() throws AutomacaoException {

		try {

			if (!isEmpty(getParametros().getUrlTJBA()) && !isEmpty(getParametros().getUrlDiario())) {
				navegateTo(getParametros().getUrlTJBA());
				clicar(getParametros().getXpathDiario(), 3000, 15);
				alternar(0);

			}

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao carregar di�rio!");
		}

	}

	/**
	 * Strategy: Metodo Principal da automacao que define o ciclo de vida dos rob�s.
	 * 
	 */
	public void iniciar() throws AutomacaoException, InterruptedException {

		try {
			System.out.println("validarCamposObrigatorios...");
			validarCamposObrigatorios();
			System.out.println("inicializarDriver...");
			inicializarDriver();
			abrirDiario();
			System.out.println("realizarLogin...");
			realizarLogin();
			System.out.println("criarLog...");
			criarLog();
			System.out.println("selecionarPerfil...");
			selecionarPerfil();
			System.out.println("antes de selecionarTarefa...");
			antesSelecionarTarefa();
			System.out.println("selecionarTarefa...");
			selecionarTarefa();
			System.out.println("executarProcedimento...");
			executarProcedimento();
			System.out.println("Concluindo...");

		} catch (AutomacaoException ae) {
			criarLog("\nProcedimento Não realizado: " + ae.getMessage(), obterArquivoLog());

		} finally {
			salvarLog();
			finalizarDriver();
		}

	}
	

	protected void validarCamposObrigatorios() throws AutomacaoException {

		if (getParametros().getUrl() == null || getParametros().getUrl().equals("")) {
			throw new AutomacaoException("Parâmetro URL deve ser informado para utilização da automação!");
		}

		if (isEmpty(getParametros().getFonteDeDados())) {

			throw new AutomacaoException("Parâmetro fonteDeDados deve ser informado para utilização da automação!");

		} else {
			switch (getParametros().getFonteDeDados()) {
			case "ARQUIVO":
				if (isEmpty(getParametros().getNomeArquivo())) {
					throw new AutomacaoException(
							"Parâmetro fonteDeDados deve ser informado para utilização da automação!");
				}
				break;
			case "TAREFA":

				break;
			case "BD":
				if (isEmpty(getParametros().getQueryProcessos()) || isEmpty(getParametros().getDbURL())
						|| isEmpty(getParametros().getDbUser()) || isEmpty(getParametros().getDbPass())) {
					throw new AutomacaoException(
							"Os parâmetros queryProcessos, dbURL, dbUser e dbPass são obrigatórios quando fonteDeDados é BD!");
				}

				break;
			default:
				throw new AutomacaoException("Parâmetro fonteDeDados deve ser ARQUIVO, TAREFA ou BD!");
			}
		}

	}

	protected WebDriver inicializarDriver() throws AutomacaoException {
		return getDriver();
	}

	protected WebDriver getDriver() throws AutomacaoException {

		if (driver == null) {

			createDriver(getParametros().getNavegador(), getParametros().getTimeout());
		}
		return driver;
	}

	private WebDriver createDriver(String navegador, String implicityWait) throws AutomacaoException {
		try {

			if (navegador == null || navegador.equals("") || navegador.equalsIgnoreCase("CHROME")) {
				driver = createChromeDriver(implicityWait);
			} else if (navegador.equalsIgnoreCase("FIREFOX")) {
				driver = createFirefoxDriver(implicityWait);
			} else if (navegador.equalsIgnoreCase("OPERA")) {
				driver = createOperaDriver(implicityWait);
			}

		} catch (Exception e) {
			throw new AutomacaoException(
					"Não foi possível carregar o driver do navegador " + navegador + "\n" + e.getMessage());
		}

		return driver;
	}

	protected boolean isEmpty(String valor) {

		if (valor == null || valor.equals("")) {
			return true;
		}
		return false;
	}

	private WebDriver createChromeDriver(String impliciteWaitTime) throws InterruptedException {

		System.setProperty("webdriver.chrome.driver",
				new File("").getAbsolutePath() + "/drivers/" + "chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		if (getParametros().isJanelaOculta()) {
			options.addArguments("--headless");
		}
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Integer.valueOf(impliciteWaitTime), TimeUnit.SECONDS);

		return driver;
	}
	
	private WebDriver createFirefoxDriver(String impliciteWaitTime) throws InterruptedException {
		String caminhoDriver = new File("").getAbsolutePath() + "\\drivers\\" + "geckodriver.exe";

		System.out.println(caminhoDriver);

		System.setProperty("webdriver.gecko.driver", caminhoDriver);
		FirefoxOptions options = new FirefoxOptions();
		options.setLegacy(true);
		if (getParametros().isJanelaOculta()) {
			options.addArguments("--headless");
		}
		WebDriver driver = new FirefoxDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Integer.valueOf(impliciteWaitTime), TimeUnit.SECONDS);

		return driver;
	}

	private WebDriver createOperaDriver(String impliciteWaitTime) throws InterruptedException {

		String caminhoDriver = new File("").getAbsolutePath() + "/drivers/" + "operadriver.exe";

		System.setProperty("webdriver.opera.driver", caminhoDriver);
		OperaOptions options = new OperaOptions();
		options.addArguments("--no-sandbox");
		if (getParametros().isJanelaOculta()) {
			options.addArguments("--headless");
		}
		WebDriver driver = new OperaDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Integer.valueOf(impliciteWaitTime), TimeUnit.SECONDS);

		return driver;
	}

	protected void printarTela() throws AutomacaoException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

			String pastaDestino = new File("").getAbsolutePath() + "\\imagens\\";

			criarPasta(pastaDestino);

			String arquivo = pastaDestino + sdf.format(Calendar.getInstance().getTime()) + ".png";

			executarPrintScreen(arquivo);

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Não foi possível salvar a imagem  - " + e.getMessage());
		}
	}

	private void executarPrintScreen(String arquivo) throws AutomacaoException, IOException {
		File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		Util.copyFile(scrFile, new File(arquivo));
	}

	protected void printarTela(String pasta, String nomeArquivo, Exception e) throws AutomacaoException {
		try {
			String pastaDestino = new File("").getAbsolutePath() + "\\erros\\";

			criarPasta(pastaDestino);
			pastaDestino = pastaDestino + pasta + "\\";
			criarPasta(pastaDestino);
			SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

			String arquivoPNG = pastaDestino + nomeArquivo + "_" + sdf.format(Calendar.getInstance().getTime())
					+ ".png";
			String arquivoTXT = pastaDestino + nomeArquivo + "_" + sdf.format(Calendar.getInstance().getTime())
					+ ".txt";
			escreverLogErro(arquivoTXT, obterConteudoStackTrace(e));
			executarPrintScreen(arquivoPNG);

		} catch (Exception e2) {

			escreverLog("Não foi possível salvar a imagem \" + nomeArquivo + \" - \" + e2.getMessage()");

		}
	}

	private String obterConteudoStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String conteudoStack = sw.toString();
		return conteudoStack;
	}

	protected void screenShot(String pasta, String nomeArquivo) throws AutomacaoException {
		try {
			String pastaDestino = new File("").getAbsolutePath() + "\\imagens\\";

			criarPasta(pastaDestino);
			pastaDestino = pastaDestino + pasta + "\\";
			criarPasta(pastaDestino);
			String arquivo = pastaDestino + nomeArquivo + "_"
					+ Calendar.getInstance().getTime().toString().replaceAll(":", "_") + ".png";

			executarPrintScreen(arquivo);

		} catch (Exception e) {
			throw new AutomacaoException(" Não foi possível salvar a imagem " + nomeArquivo + " - " + e.getMessage());
		}
	}

	private void criarPasta(String caminho) {
		File folder = new File(caminho);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public Map<String, List<Processo>> carregarListaProcessos() throws AutomacaoException, InterruptedException {

		List<Processo> nProcessos = new ArrayList<Processo>();
		if (getListaParticionada() != null && getListaParticionada().size() > 0) {
			nProcessos = getListaParticionada();

		} else {

			switch (getParametros().getFonteDeDados()) {
			case "ARQUIVO":
				nProcessos = carregarProcessosArquivo();
				break;

			case "TAREFA":

				System.out.println("realizarLogin...");
				realizarLogin();

				System.out.println("criarLog...");
				criarLog();

				System.out.println("selecionarPerfil...");
				selecionarPerfil();

				System.out.println("selecionarTarefa...");
				selecionarTarefa();

				System.out.println("obterProcessosTarefa...");
				nProcessos = obterProcessosTarefa();

				System.out.println("salvarLog...");
				salvarLog();

				System.out.println("finalizarDriver...");
				finalizarDriver();

				break;

			case "BD":
				nProcessos = obterDadosBanco();
				break;
			}
		}

		return particionar(nProcessos);

	}

	protected Map<String, List<Processo>> particionar(List<Processo> listaProcesso) {

		Map<String, List<Processo>> mapa = new HashMap<String, List<Processo>>();

		if (listaProcesso == null || listaProcesso.size() == 0) {
			return mapa;
		}

		int qtdRobos = Integer.valueOf(getParametros().getQtdRobos());

		for (int j = 0; j < qtdRobos; j++) {
			mapa.put("" + j, new ArrayList<Processo>());
		}

		int contador = 0;
		for (Iterator iterator = listaProcesso.iterator(); iterator.hasNext();) {

			Processo processo = (Processo) iterator.next();

			List<Processo> lista = mapa.get("" + contador);
			lista.add(processo);
			contador++;
			if (contador == qtdRobos) {
				contador = 0;
			}

		}
		return mapa;
	}
	
	protected List<Processo> carregarProcessosCSV() throws AutomacaoException {
		List<Processo> nProcessos = new ArrayList<Processo>();

		try {
			String arquivo = new File("").getAbsolutePath() + "\\" + getParametros().getNomeArquivo();
			
			
			InputStreamReader inputStream = new InputStreamReader(new FileInputStream(arquivo), "UTF-8");
			BufferedReader reader = new BufferedReader(inputStream);

			String linha = "";
			int count = 0;
			while ((linha = reader.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				
				Processo processo = new Processo();
				String[] colunas = linha.trim().split(getParametros().getSeparador());
				processo.setNumeroProcesso(colunas[getParametros().getColunaProcesso()]);
				processo.setIdProcesso(colunas[getParametros().getColunaIdProcesso()]);
				processo.setEtiqueta(colunas[getParametros().getColunaEtiqueta()]);
				System.out.println(linha.trim());
				nProcessos.add(processo);
			}
			reader.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Não foi possível CARREGAR PROCESSOS DO ARQUIVO");

		}

		return nProcessos;
	}


	protected List<Processo> carregarProcessosArquivo() throws AutomacaoException {
		List<Processo> nProcessos = new ArrayList<Processo>();

		try {
			String arquivo = new File("").getAbsolutePath() + "\\" + getParametros().getNomeArquivo();
			
			if(arquivo.indexOf("csv")!=-1) {
				
				if (isEmpty(getParametros().getSeparador())) {
					throw new AutomacaoException(
							"Para arquivos CSV é necessário informar o separador, a colunaProcesso e a colunaEtiqueta.");
				}
				
				return carregarProcessosCSV();
			}
			
			InputStreamReader inputStream = new InputStreamReader(new FileInputStream(arquivo), "UTF-8");
			BufferedReader reader = new BufferedReader(inputStream);

			String linha = "";
			int count = 0;
			while ((linha = reader.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				Processo processo = new Processo();
				processo.setNumeroProcesso(linha.trim());
				System.out.println(linha.trim());
				nProcessos.add(processo);
			}
			reader.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Não foi possível CARREGAR PROCESSOS DO ARQUIVO");

		}

		return nProcessos;
	}
	
	/**
	 * Método que permite ao robo realizar o preenchimento de decisão de suspeição ou impedimento
	 * 
	 * @autor Pedro Victor de Sousa Dantas
	 * @ToadaLab
	 */
	protected void realizarSubmissaoDeSuspeicao(Processo processo) throws AutomacaoException {
		System.out.println("realizarTarefa......" + processo.getNumeroProcessoFormatado());
		String campoPesquisa = "//input[@id='inputPesquisaTarefas']";

		try {
			alternarFrame(new String[] { "ngFrame" });

			limparDigitacao(campoPesquisa);

			digitar(campoPesquisa, processo.getNumeroProcessoFormatado(), 1, 500);

			clicar("//button[@title = 'Pesquisar']", 2, 2000);
			Thread.sleep(2000);
			int qtdProcessos = obterQuantidadeElementos("//p-datalist/div/div/ul/li");


				fecharJanelaDetalhes();
				alternarFrame(new String[] { "ngFrame" });
				clicar("//span[text()[contains(.,'" + processo.getNumeroProcessoFormatado() + "')]]", 2, 2000);
				
				//WebElement xpathTipoDocumento = driver.findElement(By.xpath("//*[contains(@id=\"taskInstanceForm:minutaEmElaboracao\"])"));
				//System.out.println(xpathTipoDocumento.getTagName());
				Thread.sleep(2000);
				//clicar("//select[contains(@name,'taskInstanceForm:minutaEmElaboracao')]");
				//clicar("/html/body/div[5]/div/div[3]/form/div/div[2]/span[1]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div[2]/select");
				Select select = new Select(driver.findElement(By.name("//select[contains(@name, 'taskInstanceForm:minutaEmElaboracao')]")));
				//Thread.sleep(2000);
				//select.selectByIndex(0);
				//Thread.sleep(2000);
				//System.out.println(select.toString());
				//selecionar("//select[contains(@id,':selectMenuTipoDocumentoDecoration:selectMenuTipoDoc')]", "Decisão", 1, 1000);
				//clicar("//option[contains(text(), 'Decisão')]");
				clicar("//select[contains(@id,' :selectMenuTipoDocumentoDecoration:selectMenuTipoDoc ')]");
				clicar("//option[contains(text(), 'Decisão')]");
				clicar("//div/div/div/div/div/div/select[contains(@id, ':selectMenuTipoDocumentoDecoration:selectMenuTipoDocumento')]");
				Thread.sleep(2000);
				//clicar("//*[@id,\"taskInstanceForm:minutaEmElaboracao-\"]");
				//clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration:selectModeloDocumento\"]/option[8]");
				
				digitar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:j_id203:homologadorEventoTreeParamPesquisaInput\"]", "suspeição");
				clicar("/html/body/div[5]/div/div[3]/form/div/div[2]/span[1]/div/div/div/div[2]/div/div[1]/div[2]/div/div[2]/div[2]/div/div[2]/span/div[1]/div[2]/div/div[2]/fieldset/input[2]", 8, 2000);
				clicar("/html/body/div[5]/div/div[3]/form/div/div[2]/span[1]/div/div/div/div[2]/div/div[1]/div[2]/div/div[2]/div[2]/div/div[2]/span/div[1]/div[2]/div/div[2]/fieldset/div/div[1]/div/div[2]/table/tbody/tr/td[3]/span");
				clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:homologadorEventoTreeSelectedEventsTable:0:homologadorEventoTreelinkComplementos\"]/i");
				digitar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:j_id5208:0:j_id5209:tipoComplementoLivre105Decoration:tipoComplementoLivre105\"]", "DESEMBARGADOR RAIMUNDO BOGÉA");
				clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:homologadorEventoTreebotaoGravarMovimento\"]");
				clicar("//*[@id=\"btnTransicoesTarefa\"]/i");
				clicar("//*[@id=\"frameTarefas\"]/div/div[2]/div[2]/ul/li[1]/a");
				
				criarLog(processo, "Procedimento realizado com sucesso!");


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

		}

		System.out.println("fim realizarTarefa....");
	}
	
	/**
	 * Método que devolve os processos constantes nas tarefas
	 * assinar desição, minutar desição urgência inicial, minutar desição urgência, 
	 * minutar desição, minutar despacho inicial, minutar despacho
	 * para a caixa (CIV) Assessoria - Análise de Assessoria (coonforme solicitação Des. Bogéa)
	 * 
	 * 
	 * @autor Pedro Victor de Sousa Dantas
	 * @ToadaLab
	 */
	protected void devolverParaAssessoria(Processo processo) throws AutomacaoException{
		System.out.println("realizarTarefa......" + processo.getNumeroProcessoFormatado());
		String campoPesquisa = "//input[@id='inputPesquisaTarefas']";
		System.out.println("Movendo processo para a caixa: (CIV) Assessoria - Análise da Assessoria...");
		try {
			//assinar decição
			alternarFrame(new String[] { "ngFrame" });
			if(existeElementoTexto("Assinar decisão ")
					|| existeElementoTexto("Minutar decisão de urgência inicial ")
					|| existeElementoTexto("Minutar decisão de urgência ")
					|| existeElementoTexto("Minutar decisão ")
					|| existeElementoTexto("Minutar despacho inicial ")
					|| existeElementoTexto("Minutar despacho ")) {
				
				limparDigitacao(campoPesquisa);
				
				digitar(campoPesquisa, processo.getNumeroProcessoFormatado(), 1, 10);
				clicar("//button[@title = 'Pesquisar']", 1, 20);
				Thread.sleep(1000);
				int qtdProcessos = obterQuantidadeElementos("//p-datalist/div/div/ul/li");
				fecharJanelaDetalhes();
				alternarFrame(new String[] { "ngFrame" });
				
				//Iniciando o procedimento após clicar no número do processo
				clicar("//span[text()[contains(.,'" + processo.getNumeroProcessoFormatado() + "')]]", 2, 2000);
				clicar("//*[@id=\"btnTransicoesTarefa\"]");
				clicar("/html/body/app-root/selector/div/div/div[2]/right-panel/div/processos-tarefa/div[2]/conteudo-tarefa/div[1]/div/div/div[2]/div[2]/ul/li[3]/a");
				
				Thread.sleep(1000);
				criarLog(processo, "Procedimento realizado com sucesso!");				
			}

		} catch (WebDriverException we) {
			we.printStackTrace();
			return;

		} catch (AutomacaoException ae) {
			throw ae;

		} catch (Exception e) {

			criarLog(processo, "\nOcorreu um erro: " + processo.getNumeroProcesso());

		} finally {
			fecharJanelaDetalhes();

		}

		System.out.println("fim realizarTarefa....");
	}
	
	/**
	 * Método que permite ao robo realizar o preenchimento de decisão de suspeição ou impedimento
	 * 
	 * @autor Pedro Victor de Sousa Dantas
	 * @ToadaLab
	 */
	protected void realizarSubmissaoDeImpedimento(Processo processo) throws AutomacaoException {
		System.out.println("realizarTarefa......" + processo.getNumeroProcessoFormatado());
		String campoPesquisa = "//input[@id='inputPesquisaTarefas']";

		try {
			alternarFrame(new String[] { "ngFrame" });

			limparDigitacao(campoPesquisa);

			digitar(campoPesquisa, processo.getNumeroProcessoFormatado(), 2, 2000);

			clicar("//button[@title = 'Pesquisar']", 2, 2000);
			Thread.sleep(2000);
			int qtdProcessos = obterQuantidadeElementos("//p-datalist/div/div/ul/li");


				fecharJanelaDetalhes();
				alternarFrame(new String[] { "ngFrame" });
				clicar("//span[text()[contains(.,'" + processo.getNumeroProcessoFormatado() + "')]]", 2, 2000);

				clicar("//*[@id=\"btnTransicoesTarefa\"]/i", 2,1000);
				
				
				clicar("/html/body/app-root/selector/div/div/div[2]/right-panel/div/processos-tarefa/div[2]/conteudo-tarefa/div[1]/div/div/div[2]/div[2]/ul/li[5]/a",2,1000);
				
				clicar("//select[@id[contains('taskInstanceForm:minutaEmElaboracao-')]]");
				clicar("/html/body/div[5]/div/div[3]/form/div/div[2]/span[1]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div[2]/select", 1, 1000);
				Thread.sleep(5000);
				System.out.println("AQUI");
				
				//clicar("//select/option[contains(text(), 'Decisão')]",1,1000);
				
				
//				List<WebElement> selectsDaTela = getDriver().findElements(By.xpath("/html/body/div[5]/div/div[3]/form/div/div[2]/span[1]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div[2]/select"));
//				
//				int i = 0;
//				for(WebElement e : selectsDaTela) {
//					System.out.println(i+"  -   "+ e.getAttribute("text")+" - "+ e.getAttribute("id")+" - "+e.getAttribute("name")+" - "+e.getText()+" - "+e.getLocation());
//					i++;
//				}
				
				//clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:selectMenuTipoDocumentoDecoration:selectMenuTipoDocumento\"]");
				//clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:selectMenuTipoDocumentoDecoration:selectMenuTipoDocumento\"]/option[1]");
				//clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration:selectModeloDocumento\"]/option[7]");
				
				//digitar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:j_id203:homologadorEventoTreeParamPesquisaInput\"]", "impedimento");
				//clicar("/html/body/div[5]/div/div[3]/form/div/div[2]/span[1]/div/div/div/div[2]/div/div[1]/div[2]/div/div[2]/div[2]/div/div[2]/span/div[1]/div[2]/div/div[2]/fieldset/input[2]", 8, 2000);
				//clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:j_id203:j_id215:j__id216:0:j__id216:1:j__id216:0::j_id217:text\"]/span");
				//clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:homologadorEventoTreeSelectedEventsTable:0:homologadorEventoTreelinkComplementos\"]/i");
				//digitar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:j_id5208:0:j_id5209:tipoComplementoLivre105Decoration:tipoComplementoLivre105\"]", "DESEMBARGADOR RAIMUNDO BOGÉA");
				//clicar("//*[@id=\"taskInstanceForm:minutaEmElaboracao-481979342:minutaEmElaboracao-481979342Decoration2:j_id194:homologadorEventoTreebotaoGravarMovimento\"]");
				//clicar("//*[@id=\"btnTransicoesTarefa\"]/i");
				//clicar("//*[@id=\"frameTarefas\"]/div/div[2]/div[2]/ul/li[1]/a");
				
				criarLog(processo, "Procedimento realizado com sucesso!");


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

		}

		System.out.println("fim realizarTarefa....");
	}

	public void executarProcedimento() throws AutomacaoException {
		try {

			List<Processo> nProcessos = new ArrayList<Processo>();
			if (getListaParticionada() != null && getListaParticionada().size() > 0) {
				nProcessos = getListaParticionada();

			} else {

				switch (getParametros().getFonteDeDados()) {
				case "ARQUIVO":
					nProcessos = carregarProcessosArquivo();
					break;

				case "TAREFA":
					nProcessos = obterProcessosTarefa();
					break;
				case "BD":
					nProcessos = obterDadosBanco();
					break;
				}
			}
			
			
			for (int i = nProcessos.size() - 1; i >= 0; i--) {

				try {
					String teste = nProcessos.get(i).getNumeroProcesso();

					escreverLog("\n(" + (i + 1) + "/" + nProcessos.size() + ")");
					escreverLog(" Iniciando procedimento do processo: " + teste + "\n");
					//realizarTarefa(nProcessos.get(i));
					//devolverParaAssessoria(nProcessos.get(i));
					realizarSubmissaoDeImpedimento(nProcessos.get(i));
					//Thread.sleep(5000);
					escreverLog("\n----------------------------------------------------\n");

				} catch (WebDriverException we) {
					printarTela(nProcessos.get(i).getNumeroProcessoFormatado(), "ERROS", we);
					we.printStackTrace();
					break;

				} catch (AutomacaoException ae) {
					printarTela(nProcessos.get(i).getNumeroProcessoFormatado(), "ERROS", ae);
					escreverLog("\nOcorreu um erro com o processo " + nProcessos.get(i).getNumeroProcessoFormatado()
							+ " \n" + ae.getMessage());
				} catch (Exception e) {
					printarTela(nProcessos.get(i).getNumeroProcessoFormatado(), "ERROS", e);

					escreverLog("\nException:Ocorreu um erro com o processo "
							+ nProcessos.get(i).getNumeroProcessoFormatado() + " \n" + e.getMessage());

				}
			}

		} catch (AutomacaoException ae) {
			criarLog("\nProcedimento Não realizado: " + ae.getMessage(), obterArquivoLog());

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Não foi possível obter lista de processos na tarefa!" + e.getMessage());

		} finally {
			salvarLog();
		}

	}

	public void finalizarDriver() throws AutomacaoException {
		System.out.println("FINALIZANDO ROBO!");
		
		pegarTempoFinalOpeRobo();
		
		tempoTotalOpeRobo();
		
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}

	protected void alternarJanela() throws AutomacaoException {
		// alternar Janela
		for (String winHandle : getDriver().getWindowHandles()) {
			getDriver().switchTo().window(winHandle);
		}
	}

	protected void alternarParaFramePrincipalTarefas() throws AutomacaoException {
		alternarFrame(new String[] { "ngFrame", "frame-tarefa" });

	}

	protected void alternarFrame(String[] frames) throws AutomacaoException {
		getDriver().switchTo().defaultContent();
		int i = 0;
		try {
			for (i = 0; i < frames.length; i++) {
				getDriver().switchTo().frame(frames[i]);

			}
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível alternar para o frame " + frames[i].toString());
		}

	}

	protected void navegateTo(String url) throws AutomacaoException {
		getDriver().navigate().to(url);
	}

	protected void escreverLogErro(String nomeArquivo, String conteudo) throws AutomacaoException {

		try {
			FileWriter log = new FileWriter(nomeArquivo);
			log.write(conteudo);

			if (log != null) {
				log.close();
			}

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível criar arquivo de log: " + nomeArquivo);
		}

	}

	protected void criarLog(String nomeArquivo) throws AutomacaoException {
		String user = System.getProperty("user.dir");

		try {

			if (!new File(user + "\\logs\\").exists()) {
				new File(user + "\\logs\\").mkdir();
			}

			logFile = new FileWriter(user + "\\logs\\" + nomeArquivo + " - " + getDateTime() + ".txt");
			escreverLog("Perfil: " + getParametros().getPerfil());
			escreverLog("URL: " + getParametros().getUrl());

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível criar arquivo de log: " + nomeArquivo);
		}

	}

	protected void criarLog() throws AutomacaoException {
		String user = System.getProperty("user.dir");

		String nomeArquivo = getParametros().getPerfil().replaceAll("�", "a");
		nomeArquivo = nomeArquivo.replaceAll("[^a-zA-Z0-9_-]", "_");
		try {

			if (!new File(user + "\\logs\\").exists()) {
				new File(user + "\\logs\\").mkdir();
			}

			logFile = new FileWriter(user + "\\logs\\" + nomeArquivo + " - " + getDateTime() + ".txt");
			escreverLog("Perfil: " + getParametros().getPerfil());
			escreverLog("URL: " + getParametros().getUrl());

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível criar arquivo de log: " + nomeArquivo);
		}

	}

	protected void escreverLog(String texto) {
		try {
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " "+texto);
			obterArquivoLog().write("\n" + sdf.format(Calendar.getInstance().getTime()) + "\t" + texto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void salvarLog() {
		try {
			if (obterArquivoLog() != null) {
				obterArquivoLog().close();
			}

		} catch (Exception e) {
			System.out.println("Não foi possível fechar arquivo: " + e.getMessage());
		}
	}

	protected void mouseOver(String xpath) throws AutomacaoException {

		try {

			Actions action = new Actions(getDriver());
			WebElement we = getDriver().findElement(By.xpath(xpath));
			action.moveToElement(we).build().perform();

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível passar o mouse no elemento: " + xpath);
		}
	}

	protected void clicar(String xpath, Integer tempoEspera, Integer sleepTimeMilis) throws AutomacaoException {

		try {
			
			if(ElementoClicavel(By.xpath(xpath))) {
				getDriver().findElement(By.xpath(xpath)).click();
			}else {
				esperarElemento(xpath, tempoEspera);
				Thread.sleep(sleepTimeMilis);
				getDriver().findElement(By.xpath(xpath)).click();
			}

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível clicar no elemento: " + xpath);
		}
	}
	
	protected boolean elementoSelecionado(String xpath, Integer tempoEspera) throws AutomacaoException {

		try {
			esperarElemento(xpath, tempoEspera);

			return getDriver().findElement(By.xpath(xpath)).isSelected();

		} catch (Exception e) {
			return false;
		}
	}

	protected void clicar(String xpath) throws AutomacaoException {
		try {
			getDriver().findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível clicar no elemento: " + xpath);
		}
	}

	protected void clicarEmLink(String frame, String texto, String separador, Integer tempoEspera,
			Integer sleepTimeMilis) throws AutomacaoException {

		getDriver().switchTo().defaultContent();
		getDriver().switchTo().frame(frame);
		clicarEmLink(texto, separador, tempoEspera, sleepTimeMilis);

	}

	protected void clicarEmLink(String texto, String separador, Integer tempoEspera, Integer sleepTimeMilis)
			throws AutomacaoException {
		System.out.println("clicarEmLink: " + texto);
		String[] palavras = texto.split(separador);

		StringBuffer xpath = new StringBuffer("//*[text()[");

		for (int i = 0; i < palavras.length; i++) {
			if (i > 0) {
				xpath.append(" and ");
			}
			xpath.append(" contains(.,'" + palavras[i] + "') ");

		}

		xpath.append("]]");

		try {

			Thread.sleep(sleepTimeMilis);

			esperarElemento(xpath.toString(), tempoEspera);

			getDriver().findElement(By.xpath(xpath.toString())).click();

			Thread.sleep(4000);

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível clicar no elemento: " + xpath.toString());
		}

		System.out.println("fim clicarEmLink");

	}

	protected void selecionar(String xpath, String label, Integer tempo, Integer sleepTimeMilis)
			throws AutomacaoException {

		try {
			
			if(elementoExiste(By.xpath(xpath))) {
				Thread.sleep(1000);
				Select selectPerfil = new Select(getDriver().findElement(By.xpath(xpath)));
				selectPerfil.selectByVisibleText(label);
			}else {
				Thread.sleep(sleepTimeMilis);
				esperarElemento(xpath, tempo);
				Select selectPerfil = new Select(getDriver().findElement(By.xpath(xpath)));
				selectPerfil.selectByVisibleText(label);
			}
			
			
		} catch (Exception e) {
			throw new AutomacaoException(" Não foi possível selecionar elemento: " + label + " - Xpath: " + xpath);
		}

	}

	protected void selecionar(String xpath, String label) throws AutomacaoException {

		try {

			Select selectPerfil = new Select(getDriver().findElement(By.xpath(xpath)));
			selectPerfil.selectByVisibleText(label);
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível selecionar elemento: " + label + " - Xpath: " + xpath);
		}

	}
	
	/**
	 * @author William Sodré
	 * 
	 * @param xpath
	 * @param label
	 * @throws AutomacaoException
	 */
	protected void selecionar(String xpath, int optionIndex) throws AutomacaoException {
		selecionar(xpath, optionIndex, 0, 0);
		
		try {
			Select selectPerfil = new Select(getDriver().findElement(By.xpath(xpath)));
			selectPerfil.selectByIndex(optionIndex);
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível selecionar elemento de índice: " + optionIndex + " - Xpath: " + xpath);
		}
	}
	
	protected void selecionar(String xpath, int optionIndex, Integer tempo, Integer sleepTimeMilis)
			throws AutomacaoException {
		
		try {
			Thread.sleep(sleepTimeMilis);
			if (tempo > 0) {
				esperarElemento(xpath, tempo);
			}
			Select selectPerfil = new Select(getDriver().findElement(By.xpath(xpath)));
			selectPerfil.selectByIndex(optionIndex);
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível selecionar elemento de índice: " + optionIndex + " - Xpath: " + xpath);
		}
		
	}

	protected void digitarDocumentoComMascara(String xpath, String documento, Integer wait, Integer sleepTimeMilis)
			throws AutomacaoException {
		try {

			Thread.sleep(sleepTimeMilis);
			esperarElemento(xpath, wait);

			List<WebElement> elementos = obterElementos(xpath);
			for (WebElement webElement : elementos) {
				webElement.clear();
				for (int i = documento.trim().length() - 1; i >= 0; i--) {
					webElement.sendKeys(String.valueOf(documento.charAt(i)));
					Thread.sleep(50);
					webElement.sendKeys(Keys.HOME);
				}
				break;
			}
		} catch (Exception e) {
			throw new AutomacaoException(
					"Não foi possível digitar o texto no campo de m�scara.  " + documento + " - Xpath: " + xpath);
		}
	}

	protected void digitar(String xpath, String texto, Integer wait, Integer sleepTimeMilis) throws AutomacaoException {
		try {
			Thread.sleep(sleepTimeMilis);
			esperarElemento(xpath, wait);
			getDriver().findElement(By.xpath(xpath)).sendKeys(texto);
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível digitar o texto " + texto + " - Xpath: " + xpath);
		}

	}

	protected void digitar(String xpath, String texto) throws AutomacaoException {
		try {

			getDriver().findElement(By.xpath(xpath)).sendKeys(texto);
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível digitar o texto " + texto + " - Xpath: " + xpath);
		}

	}

	protected void digitar(String xpath, Keys caracter, Integer wait, Integer sleepTimeMilis)
			throws AutomacaoException {
		try {
			Thread.sleep(sleepTimeMilis);
			esperarElemento(xpath, wait);
			getDriver().findElement(By.xpath(xpath)).sendKeys(caracter);
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível digitar o caracter " + caracter + " - Xpath: " + xpath);
		}
	}
	
	protected void substituirConteudoHTML(String xpath, String html, Integer wait, Integer sleepTimeMilis)
			throws AutomacaoException {
		try {
			Thread.sleep(sleepTimeMilis);
			esperarElemento(xpath, wait);
			
			WebElement element = getDriver().findElement(By.xpath(xpath));
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].innerHTML='" + StringEscapeUtils.escapeJavaScript(html) + "'", element);
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível substituir o conteúdo HTML - Xpath: " + xpath);
		}
	}

	protected boolean elementoExiste(By by) throws AutomacaoException {

		try {
			if (getDriver().findElements(by).size() != 0) {
				return true;
			}
			

		} catch (Exception e) {
			return false;
		}
		
		return false;

	}

	protected boolean ElementoClicavel(By by) throws AutomacaoException {

		WebDriverWait wait = new WebDriverWait(getDriver(), 5);
		wait.until(ExpectedConditions.elementToBeClickable(by));
		return true;

	}
	
	protected boolean ElementoSelecionavel(By by) throws AutomacaoException {

		WebDriverWait wait = new WebDriverWait(getDriver(), 5);
		wait.until(ExpectedConditions.elementToBeSelected(by));
		return true;

	}

	protected void ScrollAteElemento(By by) throws AutomacaoException {

		try {
			WebElement element = getDriver().findElement(by);
			Actions actions = new Actions(getDriver());
			actions.moveToElement(element);
			actions.perform();
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível navegar até elemento");
		}

	}

	protected String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmmss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	protected void limparDigitacao(String xpath, Integer wait, Integer sleepTimeMilis) throws AutomacaoException {
		try {
			Thread.sleep(sleepTimeMilis);
			esperarElemento(xpath, wait);
			getDriver().findElement(By.xpath(xpath)).clear();
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível limpar digitacao: " + xpath);
		}

	}

	protected void limparDigitacao(String xpath) throws AutomacaoException {
		try {
			getDriver().findElement(By.xpath(xpath)).clear();
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível limpar digitacao: " + xpath);
		}

	}

	protected void esperarElemento(String xpath, Integer tempo) throws AutomacaoException {

		try {

			WebDriverWait wait = new WebDriverWait(getDriver(), tempo);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível aguardar elemento: " + xpath);
		}

	}

	protected String obterTexto(String xpath) throws AutomacaoException {
		try {
			return getDriver().findElement(By.xpath(xpath)).getText().trim();
		} catch (Exception e) {
			return "";
		}

	}

	
	
	protected int obterQuantidadeElementos(String xpath) throws AutomacaoException {
		try {
			return getDriver().findElements(By.xpath(xpath)).size();
		} catch (Exception e) {
			return 0;
		}

	}

	protected List<WebElement> obterElementos(String xpath) throws AutomacaoException {
		try {

			return getDriver().findElements(By.xpath(xpath));
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível obter elementos: " + xpath);
		}

	}

	protected List<String> obterListaElementos(String xpath) throws AutomacaoException {
		try {
			List<String> lista = new ArrayList<String>();

			List<WebElement> elements = getDriver().findElements(By.xpath(xpath));
			for (WebElement webElement : elements) {
				lista.add(webElement.getText());
			}

			return lista;
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível obter elementos: " + xpath);
		}

	}

	protected FileWriter obterArquivoLog() {
		return logFile;
	}

	protected void closeNavegator() throws AutomacaoException {
		getDriver().close();
	}

	protected void criarLog(Processo processo, String mensagem) {
		try {
			if (logFile == null) {
				criarLog();
			}

			escreverLog("Processo " + processo.getNumeroProcessoFormatado() + " " + mensagem + "\n");
			//escreverLog(System.getProperty("line.separator"));
		} catch (Exception e) {
			System.out.println("1 Não foi possível no arquivo de log. " + e.getMessage());
		}
	}

	protected void criarLog(String mensagem, FileWriter file) {
		try {

			if (file == null) {
				criarLog();
			}

			escreverLog(mensagem);

		} catch (Exception e) {
			System.out.println("2 Não foi possível no arquivo de log. " + e.getMessage());
		}
	}

	protected void fecharJanelaDetalhes() {
		try {
			if (getDriver().getWindowHandles().size() > 1) {

				for (String winHandle : getDriver().getWindowHandles()) {
					getDriver().switchTo().window(winHandle);
				}

				getDriver().close();

				for (String winHandle : getDriver().getWindowHandles()) {
					getDriver().switchTo().window(winHandle);
					break;
				}

			}
		} catch (Exception e) {
// Não faz nada
			e.printStackTrace();
		}

	}

	protected void clicar(By by) throws AutomacaoException {

		getDriver().findElement(by).click();
	}

	protected int ObterTamanhoLista(By by) throws AutomacaoException {
		return getDriver().findElements(by).size();

	}

	protected void selecionarPorTexto(By by, String texto) throws AutomacaoException {

		Select select = new Select(getDriver().findElement(by));
		select.selectByVisibleText(texto);

	}

	protected void clicarPorTexto(String texto) throws AutomacaoException {
		clicar(By.xpath("//*[@text[contains(.,'" + texto + "')]]"));
	}

	protected void inserir(By by, String texto) throws AutomacaoException {
		getDriver().findElement(by).sendKeys(texto);
	}

	protected String obterTexto(By by) throws AutomacaoException {
		return getDriver().findElement(by).getText();
	}

	protected boolean isCheckMarcado(By by) throws AutomacaoException {
		return getDriver().findElement(by).getAttribute("checked").equals("true");
	}

	protected boolean existeElementoTexto(String texto) {
		try {
			getDriver().findElement(By.xpath("//*[text()[contains(.,'" + texto + "')]]"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected void atualizarPagina() throws AutomacaoException {
		getDriver().navigate().refresh();

	}

}
