package tjma.PAGE.pje215.geral;

import org.openqa.selenium.By;

import CLIENT.util.StringUtil;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import tjma.PAGE.pje215.PainelTarefasPJE;

/**
 * Robô que realiza a juntada de um documento em um ou mais processos.
 * 
 * @author William Sodré
 * @TJMA
 */
public class JuntarDocumentoProcesso_Page extends PainelTarefasPJE {

	public JuntarDocumentoProcesso_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}


	@Override
	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
		
		abrirDetalhesProcesso();
		
		if ( !temJuntadaRedistribuicao() ) {
			preencherJuntada(processo);
		} else {
			System.out.println("Processo ignorado! Não foi necessário juntar certidão de redistribuição, pois o mesmo já contém uma no último evento da sua linha do tempo.");
		}
		
	}


	private boolean temJuntadaRedistribuicao() throws AutomacaoException {
		String descricaoDocumento = getParametros().getDescricaoDocumento();
		if (StringUtil.isNotEmpty(descricaoDocumento)) {
			String xpathPrimeitoItemTimeline = "/html/body/div/div[2]/div[2]/table/tbody/tr[2]/td/table/tbody/tr/td/form[1]/div[2]/div[1]/div[3]/div[2]/div[2]/a[contains(@id, 'divTimeLine')]/span";
			if (elementoExiste(By.xpath(xpathPrimeitoItemTimeline))) {
				String textoCertidao = obterTexto(xpathPrimeitoItemTimeline);
				if (textoCertidao != null) {
					return textoCertidao.indexOf("(" + descricaoDocumento + ")") != -1;
				}
			}
		}
		
		return false;
	}


	private void preencherJuntada(Processo processo) throws AutomacaoException, InterruptedException {

		try {
			clicar("//a[@title='Menu']", 5, 2000);

			clicar("//a[@name='navbar:linkAbaIncluirPeticoes']", 5, 3000);

			selecionar("//select[@id='cbTDDecoration:cbTD']", getParametros().getTipoDocumento(), 5, 3000);

			limparDigitacao("//input[@id='ipDescDecoration:ipDesc']", 5, 3000);

			digitar("//input[@id='ipDescDecoration:ipDesc']", getParametros().getDescricaoDocumento(), 5, 3000);

			selecionar("//select[@id='modTDDecoration:modTD']", getParametros().getModeloDocumento(), 15, 5000);

			Thread.sleep(3000);
			
			clicar("//input[@value='Salvar']", 15, 5000);
			
			Thread.sleep(3000);

			clicar("//input[@value='Assinar documento(s)']", 15, 5000);

			Thread.sleep(10000);
			criarLog("Processo " + processo.getNumeroProcesso() + " teve o documento juntado!", obterArquivoLog());

		} catch (Exception e) {
			throw new AutomacaoException(
					" Erro ao preencher a juntada no processo " + processo.getNumeroProcessoFormatado() + "\n");
		}
	}


	@Override
	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return true;
	}

}
