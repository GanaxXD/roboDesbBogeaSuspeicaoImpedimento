package PAGE.projudi.turmasRecursais;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DAO.BaixaProcessualDao;
import MODEL.Acao;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.projudi.PainelUsuarioPROJUDI;
import RN.BaixaProcessualRN;

public abstract class AnaliseTransitoJulgado extends PainelUsuarioPROJUDI {

	protected Map<String, Processo> mapaProcessos = new HashMap<String, Processo>();

	protected void carregarProcessos() throws AutomacaoException {
		try {
			BaixaProcessualRN rn = new BaixaProcessualRN(new BaixaProcessualDao(getParametros().getDbURL(),
					getParametros().getDbUser(), getParametros().getDbPass()));

			setMapaProcessos(rn.carregarProcessos(getParametros()));
			

		} catch (Exception e) {
			throw new AutomacaoException("\nErro ao obterLista de Processos no banco: " + e.getMessage());
		}
	}

	protected void validarCamposObrigatorios() throws AutomacaoException {
		super.validarCamposObrigatorios();
		if (parametros.getMovimentosJulgamento() == null || parametros.getMovimentosPermitidosBaixa() == null
				|| parametros.getMovimentacaoDecursoDePrazo() == null) {
			throw new AutomacaoException(
					"Necessário preencher os parametros MovimentosJulgamento, MovimentosPermitidosBaixa e MovimentacaoDecursoDePrazo");
		}
	}
	

	protected void validaProcessoTela(Processo processo) throws AutomacaoException {

		int quantidadeLinhas = obterQuantidadeElementos("//form[@name='formIntimacoes']/table/tbody/tr");
		
		if(quantidadeLinhas>= Integer.valueOf(getParametros().getQtdLinhasGrid())) {
		
		for (int i = Integer.valueOf(getParametros().getQtdLinhasGrid()); i <= quantidadeLinhas; i++) {
			String numeroProcesso = obterTexto("//form[@name='formIntimacoes']/table/tbody/tr[" + i + "]/td[2]/a");
			if (!numeroProcesso.equals(processo.getNumeroProcesso())) {
				// - Se algum processo da lista não for o processo que está sendo analisado,
				// passar pro próximo.
				throw new AutomacaoException("Todos os processos exibidos na tela devem ser " + processo.getNumeroProcesso());
			}
		}
		}else {
			throw new AutomacaoException("Consulta não retornou registros para o processo " + processo.getNumeroProcesso());
		}
	}

}
