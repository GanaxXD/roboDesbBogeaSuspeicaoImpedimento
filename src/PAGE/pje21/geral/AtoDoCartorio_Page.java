package PAGE.pje21.geral;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * Classe respons�vel pela realiza��o de ato de cart�rio
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class AtoDoCartorio_Page extends AtoGenerico_Page {

	public AtoDoCartorio_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
		
		selecionarExpedirOutrosDocumentos();
		
		selecionarDocumento();

		salvarDocumento();

		movimentar("Encaminhar para Encaminhar para assinatura da secretaria");

		assinarDocumento();

		preencherMovimentoProcessual();

		movimentar("Lançaar movimentação processual");

	}
	
	private void selecionarExpedirOutrosDocumentos() throws InterruptedException, AutomacaoException {
		try {

			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });
			if (obterElementos("//input[contains(@id,'ExpedirOutrosDocumentos')]").size() >= 1) {
				clicar("//input[contains(@id,'ExpedirOutrosDocumentos')]", 15, 5000);
				movimentar("01 - Cumprir opções selecionadas abaixo");
			}
		} catch (Exception e) {
			throw new AutomacaoException("Nao foi possivel selecionar Expedir Outros Documentos.");
		}
	}



}
