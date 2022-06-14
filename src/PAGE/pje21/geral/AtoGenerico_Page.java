package PAGE.pje21.geral;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * Classe abstrata responsável pela realização de atos (cartório ou gabinete).
 * As classes filhas definem o que é específico para cada robô.
 *  
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public abstract class AtoGenerico_Page extends PainelTarefasPJE {

	/**
	 * // 1.0
	 * <span class="cke_button_icon cke_button__pjetipomodelodocumento_icon" style=
	 * "background-image:url('https://corregedoria.treinamento.pje.jus.br/js/ckeditor/plugins/pjetipomodelodocumento/icons/pjetipomodelodocumento.png?t=20200808');background-position:0px;background-size:16px;">&nbsp;</span>
	 * 
	 * // 1.1 <select id="cke_104_select" class="tipoDocumento //
	 * cke_dialog_ui_input_select" aria-labelledby="cke_105_label"><option //
	 * value="Selecione"> Selecione</option><option value="Decisão"> //
	 * Decisão</option><option value="Despacho"> Despacho</option></select>
	 * 
	 * // 1.2 <input type="text" id="modeloAutoComplete" //
	 * class="cke_dialog_ui_input_text" disabled="disabled" placeholder="Selecione
	 * // um tipo de documento" autocomplete="off" aria-autocomplete="list">
	 * 
	 * // 1.3 <mark>MODELO WEBINAR ROBOS DECISAO</mark>
	 * 
	 * 
	 * @throws AutomacaoException
	 */
	protected void selecionarDocumento() throws AutomacaoException {
		try {
			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });

			clicar("//span[contains(@style,'pjetipomodelodocumento')]", 15, 3000);

			selecionar("//select[@id ='cke_104_select']", getParametros().getTipoDocumento(), 15, 3000);

			digitar("//input[@id ='modeloAutoComplete']", getParametros().getModeloDocumento(), 15, 3000);

			clicar("//mark[text()='" + getParametros().getModeloDocumento() + "']", 15, 3000);


		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível selecionar o documento. " + e.getMessage());
		}
	}

	/**
	 * // <span class="cke_button_icon cke_button__pjesalvarsemassinar_icon" //
	 * style="background-image:url('https://corregedoria.treinamento.pje.jus.br/js/ckeditor/plugins/pjesalvarsemassinar/icons/pjesalvarsemassinar.png?t=20200808');background-position:0
	 * // 0px;background-size:16px;">&nbsp;</span>
	 * 
	 * @throws AutomacaoException
	 */
	protected void salvarDocumento() throws AutomacaoException {
		try {
			clicar("//span[contains(@style,'pjesalvarsemassinar')]", 15, 3000);

			System.out.println();
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível salvar o documento. " + e.getMessage());
		}

	}

	/**
	 * //<span class="cke_button_icon cke_button__pjeassinar_icon" style=
	 * "background-image:url('https://corregedoria.treinamento.pje.jus.br/js/ckeditor/plugins/pjeassinar/icons/pjeassinar.png?t=20200808');background-position:0
	 * 0px;background-size:16px;">&nbsp;</span>
	 * 
	 * @throws AutomacaoException
	 */
	protected void assinarDocumento() throws AutomacaoException {
		try {

			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });
			clicar("//span[contains(@style,'pjeassinar')]", 15, 3000);

		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível assinar o documento. " + e.getMessage());

		}

	}

	/**
	 * 
	 * @throws AutomacaoException
	 */
	protected void preencherMovimentoProcessual() throws AutomacaoException {
		try {
			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });
			digitar("//input[contains(@id, 'tarefaEventoTreeParamPesquisaInput')]", getParametros().getMovimento(), 15,
					5000);

			clicar("//input[@value='Pesquisar']", 15, 5000);

			clicar("//span[text()[contains(.,'(" + getParametros().getMovimento() + ")')]]", 15, 5000);

			clicar("//input[@value = 'Salvar']", 15, 5000);
		} catch (Exception e) {
			throw new AutomacaoException("Não foi possível preencher o movimento processual. " + e.getMessage());
		}

	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		return true;

	}

}
