package PAGE.pje21.juizados;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.geral.CitacaoIntimacao_Page;

/**
 * Robo especifico para a citação em processos dos juizados da fazenda publica
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class IntimacaoJuizadosFazenda_Page extends CitacaoIntimacao_Page {

	public IntimacaoJuizadosFazenda_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}
	
	protected void executar(Processo processo)
			throws InterruptedException, AutomacaoException {

		super.executar(processo);
		getParametros().setAtribuirEtiqueta(getParametros().getFiltrarEtiqueta());
		atribuirEtiqueta(getParametros().getManterEtiqueta());

	}



}
