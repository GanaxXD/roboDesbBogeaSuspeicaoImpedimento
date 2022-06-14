package tjma.PAGE.pje215.geral;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import tjma.PAGE.pje215.PainelTarefasPJE;

/**
 * Robô que realiza o etiquetamento simples dos processos.
 * 
 * @author William Sodré
 * @TJMA
 */
public class TriagemSimples_Page extends PainelTarefasPJE {

	public TriagemSimples_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
		
		if(processo.getEtiqueta()!=null && !processo.getEtiqueta().equals("")) {
			atribuirEtiqueta(processo.getEtiqueta());
			
			criarLog("Etiqueta " + processo.getEtiqueta() + " atribuída ao processo "
					+ processo.getNumeroProcessoFormatado(), obterArquivoLog());
		}
	}

	/**
	 * Antes de selecionar a tarefa, o robô dá a opção de renomear a etiqueta que será atribuída
	 * caso ela já exista.
	 */
	@Override
	protected void antesSelecionarTarefa() throws AutomacaoException {
		String etiquetaNovoNome = getParametros().getRenomearEtiquetaAntesAtribuir();
		if ( StringUtils.isNotEmpty(etiquetaNovoNome) ) {
			String etiquetaAtribuir = getParametros().getAtribuirEtiqueta();
			renomearEtiqueta(etiquetaAtribuir, etiquetaNovoNome);
		}
	}
	
	
	protected void renomearEtiqueta(String nomeAtual, String novoNome) throws AutomacaoException {
		super.antesSelecionarTarefa();
		
		String xpathBotaoEtiquetas = "//*[@id=\"liEtiquetas\"]/a";
		String xpathCampoPesquisa = "//input[@id=\"itPesquisarEtiquetas\"]";
		String xpathBotaoPesquisar = "//button[@title = 'Pesquisar']";
		String xpathEtiqueta = "//*[@id=\"divEtiquetas\"]//p-datalist//div[2]//span[text()=\"" + nomeAtual + "\"]";
		String xpathDropdownAcoesFechado = xpathEtiqueta + "/parent::span/parent::div/following-sibling::div/div[@class=\"acoes-etiqueta\"]"
				+ "/button[contains(@class,\"dropdown-toggle\")]";
		String xpathDropdownAcoesAberto = xpathEtiqueta + "/parent::span/parent::div/following-sibling::div/div[@class=\"acoes-etiqueta open\"]"
				+ "/button[contains(@class,\"dropdown-toggle\")]/following-sibling::ul//a[contains(text(), \"Editar\")]";
		String xpathCampoNomeEtiqueta = "//div[@id=\"rightPanel\"]//input[@id=\"itTitulo\"]";
		String xpathBotaoSalvar = "//*[@id=\"rightPanel\"]//button[@type=\"submit\" and text()=\"Salvar\"]";
		
		alternarFrame(new String[] { "ngFrame" });
		clicar(xpathBotaoEtiquetas);
		limparDigitacao(xpathCampoPesquisa);
		digitar(xpathCampoPesquisa, nomeAtual, 1, 1000);
		clicar(xpathBotaoPesquisar, 1, 1000);
		
		/*
		 * FIXME: esse método leva em consideração que a etiqueta buscada sempre
		 * aparecerá na primeira página, o que pode não ocorrer sempre. Deve-se
		 * melhorá-lo para percorrer as possíveis páginas até localizar o nome idêntico
		 * da etiqueta
		 */
		if (elementoExiste(By.xpath(xpathEtiqueta))) {
			clicar(xpathDropdownAcoesFechado, 1, 1000);
			clicar(xpathDropdownAcoesAberto, 1, 1000);
			limparDigitacao(xpathCampoNomeEtiqueta);
			digitar(xpathCampoNomeEtiqueta, novoNome);
			clicar(xpathBotaoSalvar, 1, 2000);
		} else {
			System.out.println( String.format(
					"PROCEDIMENTO DE RENOMEAR ETIQUETA IGNORADO - não foi encontrada nenhuma etiqueta com o nome: %s", nomeAtual) );
		}
		
		clicarBotaoHome();
	}
	
	

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return true;
	}

}
