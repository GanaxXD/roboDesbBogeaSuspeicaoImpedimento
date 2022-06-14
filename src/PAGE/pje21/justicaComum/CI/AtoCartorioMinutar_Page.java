package PAGE.pje21.justicaComum.CI;

import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class AtoCartorioMinutar_Page extends AtoMagistradoMinutar_Page {

	public AtoCartorioMinutar_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}

	
	protected void encaminharParaAssinatura() throws AutomacaoException, InterruptedException {

		movimentar("Enviar para assinatura em cartório");

	}
	
}