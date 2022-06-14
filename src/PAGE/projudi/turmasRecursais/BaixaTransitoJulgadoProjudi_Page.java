package PAGE.projudi.turmasRecursais;

import java.util.ArrayList;
import java.util.List;

import DAO.BaixaProcessualDao;
import MODEL.Acao;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import RN.BaixaProcessualRN;

/**
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class BaixaTransitoJulgadoProjudi_Page extends AnaliseTransitoJulgado {

	public BaixaTransitoJulgadoProjudi_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	
	}
	
	protected List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException {
		List<Processo> listaProcessos = new ArrayList<Processo>();
		try {
			carregarProcessos();

			criarLog(getMapaProcessos().toString(), obterArquivoLog());

			for (Processo processo : getMapaProcessos().values()) {

				if (processo.getAcao().equals(Acao.BAIXAR_PROCESSO)) {

					listaProcessos.add(processo);
				}

			}

		} catch (Exception e) {
			throw new AutomacaoException("\nErro ao obterLista de Processos no banco: " + e.getMessage());
		}

		
		return listaProcessos;

	}

	
	protected boolean deveProsseguir(Processo processo)
			throws InterruptedException, AutomacaoException {

		validaProcessoTela(processo);

		Processo p = mapaProcessos.get(processo);
		if (p.getAcao().equals(Acao.BAIXAR_PROCESSO)) {
			return true;
		}
		return false;

	}

	protected void executar(Processo processo) throws AutomacaoException, InterruptedException {
		System.out.println("EXECUTAR");
	}

	protected void baixar(Processo processo) throws AutomacaoException, InterruptedException {
		System.out.println("EXECUTAR>>> ." + processo.getNumeroProcesso());

		clicar("//a[contains(text(),'" + processo.getNumeroProcesso() + "')]", 10, 2000);

		clicar("//a[contains(text(),'Movimentar Recurso')]", 10, 2000);

		clicar("//input[@value='DigitarTexto']", 10, 2000);

		selecionar("//select[@id='codDescricao1']", getParametros().getTipoDocumento(), 10, 2000);

		selecionar("//select[@id='modelo']", getParametros().getModeloAto(), 10, 2000);

		clicarEmLink("Digitar Diretamente o Texto", " ", 10, 2000);

		Thread.sleep(5000);

		if (existeElementoTexto(getParametros().getTextoValidacao())) {

			clicar("//input[@name='Submeter']");

			clicar("//a[@onclick='assinar()']", 10, 4000);

			alternarFrame(new String[] { "mainFrame", "userMainFrame", "popupFrame" });

			digitar("//input[@id='senha']", getParametros().getSenhaToken(), 10, 5000);

			clicar("//img[contains(@src,'botoes/bot-assinar.gif')]", 10, 2000);

			alternarFrame(new String[] { "mainFrame", "userMainFrame" });

			digitar("//input[@id='seqCategoriaMovimentacao']", getParametros().getMovimento(), 10, 5000);

			clicar("//img[@id='btnBuscaMovimentacao']");

			clicar("//input[@id='enviaVara']", 10, 3000);

			clicar("//input[@name='Concluir']", 10, 3000);

			criarLog(processo, "Processo baixado com sucesso!!!!");

		} else {
			criarLog(processo, "Problema na seleção do modelo. Humano deve analisar!");

		}
	}

}