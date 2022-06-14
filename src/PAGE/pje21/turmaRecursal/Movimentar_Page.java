package PAGE.pje21.turmaRecursal;

import java.util.Iterator;
import java.util.List;

import CLIENT.util.ArquivoUtil;
import CLIENT.util.PDFUtil;
import MODEL.Processo;
import MODEL.TemaProcessualEtiqueta;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;
import PAGE.pje21.geral.TriagemSimples_Page;

/**
 * Robô que realiza citações e intimações de acordo com a configuração passada.
 * Apenas partes devidamente qualificadas são intimadas (Com Procuradorias ou
 * Advogados)
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class Movimentar_Page extends TriagemSimples_Page {

	public Movimentar_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		try {
			
			super.executar(processo);
		
			movimentar("Ignorar e sair da tarefa");
			
			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });
			
			escolherTarefaCitarIntimar();
			
			movimentar("01 - Prosseguir na(s) tarefa(s) selecionada(s)");
			
			
			criarLog("Procedimento realizado com sucesso!",obterArquivoLog());
			

		} catch (Exception e) {
			throw new AutomacaoException(
					"Erro ao realizar o procedimento do processo  " + processo.getNumeroProcesso());
		}
	}
	

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		
			return true;

	}


}
