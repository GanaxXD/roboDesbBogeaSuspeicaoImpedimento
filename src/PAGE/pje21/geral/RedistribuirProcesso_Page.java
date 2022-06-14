package PAGE.pje21.geral;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * Classe responsavel pela redistribuição de processos
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class RedistribuirProcesso_Page extends PainelTarefasPJE {
	public RedistribuirProcesso_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	@Override
	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
		// TODO Auto-generated method stub
		/*
		 * 1 - verificar a tarefa aonde se encontra o processo. e definir as
		 * movimentações 2 - selecionar o motivo da redistribuição (select) 3 -
		 * Selecionar a jurisdição (select) 4 - Selecionar a competência (select) 5 -
		 * Clicar em redistribuir 6 - Clicar em Fechar na Janela que aparece
		 * 
		 */
//		try {
		// processo no limbo: 8001516-54.2016.8.05.0191

		if (getParametros().getDadosRedistribuicao() != null) {

			alternarFrame(new String[] { "ngFrame", "frame-tarefa" });

			String competenciaAtualProcesso = obterTexto(
					"//html/body/div[5]/div/div[4]/form/div/div[2]/span/div/div/div/table/tbody/tr[3]/td");
			if (competenciaAtualProcesso.indexOf(getParametros().getDadosRedistribuicao().getCompetencia()) != -1) {
				// significa que o processo já se encontra na competência correta.
				escreverLog("O processo " + processo.getNumeroProcessoFormatado()
						+ " possui a mesma competência da vara destino ("
						+ getParametros().getDadosRedistribuicao().getCompetencia() + "). A redistribuição será por "
						+ getParametros().getDadosRedistribuicao().getMotivoAlternativo());

				selecionar("//select[contains(@id,'tipoRedistribuicao')]",
						getParametros().getDadosRedistribuicao().getMotivoAlternativo(), 20, 3000);

			} else {
				escreverLog("O processo " + processo.getNumeroProcessoFormatado()
						+ " possui competênci diversa da vara destino ("
						+ getParametros().getDadosRedistribuicao().getCompetencia() + "). A redistribuição será por "
						+ getParametros().getDadosRedistribuicao().getMotivo());
				selecionar("//select[contains(@id,'tipoRedistribuicao')]",
						getParametros().getDadosRedistribuicao().getMotivo(), 20, 3000);
				selecionar("//select[contains(@id,'jurisdicaoRedistribuicao')]",
						getParametros().getDadosRedistribuicao().getJurisdicao(), 10, 2000);

				selecionar("//select[contains(@id,'comboCompetenciaExclusiva')]",
						getParametros().getDadosRedistribuicao().getCompetencia(), 10, 2000);
			}

			clicar("//input[contains(@id,'btnGravarRedistribuicao')]", 10, 1000);

			alternarJanela();

			clicar("//input[contains(@id,'btnFecharResultadoProtocolacao')]", 10, 1000);

			alternarJanela();

			escreverLog("Processo" + processo.getNumeroProcessoFormatado() + " Redistribuído!!");

		} else {
			escreverLog("Processo" + processo.getNumeroProcessoFormatado()
					+ " movimentado para o cartório: Verificar Providências a adotar!!");

		}

		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		return true;

	}

}
