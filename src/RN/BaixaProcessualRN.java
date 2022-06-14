package RN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DAO.BaixaProcessualDao;
import MODEL.Acao;
import MODEL.Movimento;
import MODEL.Parte;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
/**
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @TJBA
 */
public class BaixaProcessualRN {
	private BaixaProcessualDao dao;

	public BaixaProcessualRN(BaixaProcessualDao dao) {
		setDao(dao);
	}

	public BaixaProcessualDao getDao() {
		return dao;
	}

	public void setDao(BaixaProcessualDao dao) {
		this.dao = dao;
	}

	/**
	 * A regra do robo de baixa processual:
	 * 
	 * Processo que teve ac�rd�o proferido, seguido de intima��es e o prazo
	 * decorrido para todas as partes. N�o havendo peticionamento intermedi�rio
	 * neste intervalo, o processo deve ser baixado. No caso do PROJUDI, caso n�o
	 * haja peticionamento intermedi�rio, mas alguma parte ainda esteja no prazo, o
	 * robo devera dispensar"
	 * 
	 * Se Baixa definitiva ap�s acordao, dispensar Se n�o tiver ac�rd�o, dispenso
	 * Caso contr�rio, o Humano dever� analisar.
	 * 
	 * @param parametros
	 * @return
	 */
	public Map<String, Processo> carregarProcessos(Parametros parametros) throws AutomacaoException {
		try {
			System.out.println("Iniciando metodo RN.carregarProcessos....");
		Map<String, Processo> mapaProcessos = dao.carregarProcessos(parametros);

		
		for (Processo processo : mapaProcessos.values()) {
			
			
			Collections.sort(processo.getMovimentos(), Collections.reverseOrder());

			if (!verificaExistenciaAcordao(processo, parametros) || processo.getMovimentos().size() == 0) {
				processo.setAcao(Acao.DISPENSAR_NAO_EXISTENCIA_ACORDAO);
				continue;
			}

			if (verificaExistenciaBaixaDefinitiva(processo, parametros)) {
				processo.setAcao(Acao.DISPENSAR_BAIXA_DEFINITIVA);
				continue;
			}

			if (verificaMovimentacoesPermitidas(processo, parametros)) {

				if (verificaDecursoPrazoTodasPartes(processo, parametros)) {
					// - Baixa processo quando todas as movimenta��es pertencem a lista
					// listaMovimentosPermitidosBaixa e todas as
					// partes tiveram movimenta��o de decurso de prazo ap�s intima��o do ac�rd�o
					processo.setAcao(Acao.BAIXAR_PROCESSO);

				} else {
					
					processo.setAcaoSeNulo(Acao.DISPENSAR_NEM_TODAS_PARTES_TEM_DECURSO);	
				}
				continue;

			} else {
				processo.setAcao(Acao.HUMANO_ANALISAR);
				continue;
			}

		}
		
		System.out.println("Finalizando metodo RN.carregarProcessos....");
		System.out.println(mapaProcessos);
		return mapaProcessos;
		}catch(Exception e) {
			throw new AutomacaoException("Erro ao obter processos. "+ e.getMessage()+" " +parametros.getTarefa());
		}

	}


	/**
	 * Verifica se todas as partes do processo tiveram movimenta��o de decurso de
	 * prazo. Caso positivo, retorna true.
	 * 
	 * @param processo
	 */
	private boolean verificaDecursoPrazoTodasPartes(Processo processo, Parametros parametros) {
		Collection<Parte> partes = processo.getPartes();
		
		if(partes.size()==0) {
			return false;
		}
		
		for (Movimento movimento : processo.getMovimentos()) {

			if (movimento.getCodMovimentoCNJ().equals(parametros.getMovimentacaoDecursoDePrazo())) {

				for (Parte parte : partes) {
					if (movimento.getTextoFinalMovimento().indexOf(parte.getNomeParte()) != -1) {
						parte.setPrazoDecorrido("SIM");
						break;
					}
				}
			}

		}

		boolean decursoDePrazoProcesso = true;
		for (Parte parte : partes) {
			if (parte.getPrazoDecorrido() == null || !parte.getPrazoDecorrido().equals("SIM")) {
				decursoDePrazoProcesso = false;
				break;
			}

		}
		return decursoDePrazoProcesso;

	}

	/**
	 * Verifica se existe movimenta��o de baixa definitiva
	 * 
	 * @param processo
	 * @param parametros
	 * @return
	 */
	private boolean verificaExistenciaBaixaDefinitiva(Processo processo, Parametros parametros) {

		for (Movimento movimento : processo.getMovimentos()) {
			if (parametros.getMovimentacaoBaixaDefinitiva().equals(movimento.getCodMovimentoCNJ())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifica se a existe acordao movimentado
	 * 
	 * @param processo
	 * @param parametros
	 * @return
	 */
	private boolean verificaExistenciaAcordao(Processo processo, Parametros parametros) {
		List<String> lista = Arrays.asList(parametros.getMovimentosJulgamento());

		for (Movimento movimento : processo.getMovimentos()) {
			if (lista.contains(movimento.getCodMovimentoCNJ())) {
				if (movimento.getTipoDocumento()!=null && movimento.getTipoDocumento().equals("Acórdão")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Verifica se a existe alguma movimenta��o que n�o pertencente �s movimenta��es
	 * permitidas. Caso positivo, retorna false.
	 * 
	 * @param processo
	 * @param parametros
	 * @return
	 */
	private boolean verificaMovimentacoesPermitidas(Processo processo, Parametros parametros) {
		List<String> lista = Arrays.asList(parametros.getMovimentosPermitidosBaixa());

		for (Movimento movimento : processo.getMovimentos()) {
			if (!lista.contains(movimento.getCodMovimentoCNJ())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Verifica se a existe algum embargo de declara��o nas movimenta��es
	 * processuais. Caso positivo retorna true
	 * 
	 * @param processo
	 * @param parametros
	 * @return
	 */
	private boolean verificaEmbargosDeDeclacacao(Processo processo, Parametros parametros) {

		List<String> lista = Arrays.asList(parametros.getCodigosTipoDocumentoED());
		for (Movimento movimento : processo.getMovimentos()) {

			if (lista.contains(movimento.getTipoDocumento())) {
				return true;
			}

		}
		return false;

	}

}
