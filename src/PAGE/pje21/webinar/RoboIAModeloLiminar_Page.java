package PAGE.pje21.webinar;

import CLIENT.SINAPSES.model.RetornoModeloClassificacao;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * Robo que utiliza inteligência artificial para realização de uma tarefa de cartório.
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class RoboIAModeloLiminar_Page extends PainelTarefasPJE {

	public RoboIAModeloLiminar_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
		
		System.out.println("Executar outros procedimentos caso necessário..... ");
		
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		try {
			clicar("//span[text()[contains(.,'" + processo.getNumeroProcesso() + "')]]", 15, 2000);
			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });

			String textoAtoMagistrado = obterTexto("//div[@id='paginaInteira']");

			RetornoModeloClassificacao retornoSinapses = obterConexaoSinapses().consultarModelo(textoAtoMagistrado);
			if (retornoSinapses != null) {
				if (retornoSinapses.getClasseConvicto().getCodigo().equalsIgnoreCase("COM")) {
					criarLog("\nSinapses retornou COM convicção.. \n"+ retornoSinapses+"\n\n", obterArquivoLog());
					return true;
				}else {
					
					criarLog("\nSinapses retornou SEM convicção!!. \n"+ retornoSinapses+"\n\n", obterArquivoLog());
					return false;
				}
			} else {
				criarLog("\nSinapses não retornou. ", obterArquivoLog());
			}
		} catch (Exception e) {
			criarLog("\nSinapses não retornou. ", obterArquivoLog());
		}

		return false;
	}

}
