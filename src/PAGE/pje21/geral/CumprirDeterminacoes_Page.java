package PAGE.pje21.geral;

import java.util.List;

import MODEL.Processo;
import MODEL.TemaProcessualEtiqueta;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * Robô que realiza citações e intimações de acordo com a configuração passada.
 * Apenas partes devidamente qualificadas são intimadas (Com Procuradorias ou
 * Advogados)
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class CumprirDeterminacoes_Page extends TriagemInicial_Page {

	public CumprirDeterminacoes_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
		try {
			carregarDocumentoProcesso(processo);

			atribuirEtiquetasProcesso(processo);

			super.executar(processo);

		} catch (Exception e) {
			throw new AutomacaoException(
					"Erro ao realizar o procedimento do processo  " + processo.getNumeroProcesso());
		}
	}

	protected void carregarDocumentoProcesso(Processo processo) throws InterruptedException, AutomacaoException {

		try {

			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });

			String textoAtoMagistrado = obterTexto("//div[@id='paginaInteira']");

			processo.setConteudoDocumento(textoAtoMagistrado.toLowerCase());

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Não foi possível carregar o documento do processo "
					+ getParametros().getDocumentoProcesso() + "\n" + e.getMessage());
		}

	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		return true;

	}

	protected void validarCamposObrigatorios() throws AutomacaoException {

	}

}
