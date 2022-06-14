package PAGE.pje.fazendaPublica;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje.PainelTarefasPJE;

/**
 * 
 * Robo em constru��o.
 * 
 * O mesmo ir� realizar a intima��o do polo ativo quando o AR DIGITAL voltar com os motivos: Mudou-se ou Falecido
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class IntimarARNegativo_Page extends PainelTarefasPJE {

	public IntimarARNegativo_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo)
			throws InterruptedException, AutomacaoException {

		movimentar("Verificar exist�ncia de outros prazos");

		movimentar("Verificar provid�ncias a adotar");

		movimentar("Outras op��es");

		movimentar("(EF) Prepara ato de cart�rio");

		minutarIntimacao(processo);

		assinarIntimacao(processo);

	}

	protected boolean deveProsseguir(Processo processo)
			throws InterruptedException, AutomacaoException {

			return false;
	}

	public void minutarIntimacao(Processo processo) throws InterruptedException, AutomacaoException {

		alternarParaFramePrincipalTarefas();
		Thread.sleep(8000);

		selecionar("//select[contains(@id,'selectModeloDocumento')]", getParametros().getModeloAto());
		Thread.sleep(3000);

		digitar("//input[contains(@id, 'tarefaEventoTreeParamPesquisaInput')]", getParametros().getMovimento());
		Thread.sleep(3000);

		clicar("//input[@value='Pesquisar']");

		Thread.sleep(3000);

		clicar("//span[text()[contains(.,'" + getParametros().getMovimento() + "')]]");

		Thread.sleep(5000);

		movimentar("Enviar para Assinatura em Cart�rio");

		criarLog(processo, "Minuta da Intima��o realizada com sucesso! - " + processo.getNumeroProcesso());

		System.out.println("Minuta da Intima��o realizada com sucesso! - " + processo.getNumeroProcesso());

		Thread.sleep(10000);

	}

	public void assinarIntimacao(Processo processo) throws InterruptedException, AutomacaoException {

		alternarParaFramePrincipalTarefas();
		Thread.sleep(8000);

		clicar("//input[@value = 'Polo Ativo']");

		Thread.sleep(3000);

		limparDigitacao("//input[contains(@id, ':tableDestinatarios:prazoGeralInput')]");

		Thread.sleep(3000);

		digitar("//input[contains(@id, ':tableDestinatarios:prazoGeralInput')]", getParametros().getPrazo());

		Thread.sleep(3000);

		clicar("//th[text() = 'Sistema']");

		Thread.sleep(3000);

		clicar("//input[@value = 'Gravar dados do(s) expediente(s)']");

		Thread.sleep(3000);

		clicar("//input[@value = 'Assinar documento(s)']");

		Thread.sleep(10000);

		criarLog(processo, "Intima��o assinada com sucesso! - " + processo.getNumeroProcesso());

		System.out.println("Intima��o assinada com sucesso! -" + processo.getNumeroProcesso());

	}

	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		// TODO Auto-generated method stub
		
	}

	

}
