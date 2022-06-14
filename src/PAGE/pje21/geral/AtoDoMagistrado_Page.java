package PAGE.pje21.geral;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * Classe responsável pela realização de ato de magistrado.
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class AtoDoMagistrado_Page extends AtoGenerico_Page {

	public AtoDoMagistrado_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		selecionarDocumento();

		salvarDocumento();

		movimentar("Assinatura do magistrado");

		assinarDocumento();

		preencherMovimentoProcessual();

		movimentar("Lançar movimentação processual");

	}

}
