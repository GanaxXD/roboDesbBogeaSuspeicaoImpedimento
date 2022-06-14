package PAGE.pje.justicaComum;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje.geral.CitacaoIntimacao_Page;

/**
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class AtoCartorioIntimar_Page extends CitacaoIntimacao_Page {

	public AtoCartorioIntimar_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}

	protected void executar(Processo processo)
			throws InterruptedException, AutomacaoException {

		minutarAto();
		movimentar("Enviar para assinatura em cartório");
		assinar();
		movimentar("Preparar ato de comunicação");
		movimentar("Expedir usando o PAC");
		super.executar(processo);

	}

	protected void minutarAto() throws InterruptedException, AutomacaoException {

		alternarParaFramePrincipalTarefas();
		esperarElemento("//select[contains(@id,'tipoProcessoDocumento')]", 15);

		selecionarTipoAto();
		
		selecionarModeloAto();
		
		digitarMovimentacaoProcessual();
		
		clicar("//input[@value='Pesquisar']", 10, 2000);
		clicar("//span[text()[contains(.,'" + getParametros().getMovimento() + "')]]", 10, 2000);
		salvar();

	}

	protected void validarCamposObrigatorios() throws AutomacaoException {
		// TODO Auto-generated method stub
		super.validarCamposObrigatorios();

		if (isEmpty(getParametros().getTipoAto())) {
			throw new AutomacaoException("É necessário informar o tipoAto para realizar o procedimento!");
		}
	}

}
