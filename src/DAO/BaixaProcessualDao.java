package DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import CLIENT.util.Util;
import MODEL.Movimento;
import MODEL.Parte;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
/**
 * 
 * Classe de acesso a dados espec�fica para a leitura dos processos para an�lise de tr�nsito em julgado.
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class BaixaProcessualDao extends GenericDao {

	public BaixaProcessualDao(String urlDB, String user, String pass) {
		super(urlDB, user, pass);
	}

	private void consolidarMovimentacao(Map<String, Processo> mapaProcessos, ProcessoDto processoDto) {

		Processo processo = mapaProcessos.get(processoDto.getNumeroprocesso());
		if (processo == null) {
			processo = new Processo();
			Util.atribuirValores(processo, processoDto);
			adicionarMovimento(processoDto, processo);
			mapaProcessos.put(processo.getNumeroProcesso(), processo);

		} else {
			adicionarMovimento(processoDto, processo);
		}

	}

	private void adicionarMovimento(ProcessoDto processoDto, Processo processo) {

		Movimento movimento = new Movimento();
		Util.atribuirValores(movimento, processoDto);
		processo.getMovimentos().add(movimento);
	}
	
	private void consolidarPartes(Map<String, Processo> mapaProcessos, ProcessoDto processoDto) {

		Processo processo = mapaProcessos.get(processoDto.getNumeroprocesso());
		if (processo == null) {
			processo = new Processo();
			Util.atribuirValores(processo, processoDto);
			adicionarParte(processoDto, processo);
			mapaProcessos.put(processo.getNumeroProcesso(), processo);

		} else {
			adicionarParte(processoDto, processo);
		}

	}

	private void adicionarParte(ProcessoDto processoDto, Processo processo) {

		Parte parte = new Parte();
		Util.atribuirValores(parte, processoDto);
		processo.getPartes().add(parte);

	}

	/**
	 * M�todo sobrescrito para tratar de algo espec�fico do rob� de baixa
	 * processual. Duas consultas s�o utilizadas para aplicar a l�gica da baixa,
	 * conforme classe BaixaProcessualRN
	 */
	public Map<String, Processo> carregarProcessos(Parametros parametros) throws AutomacaoException {
		Map<String, Processo> mapaProcessos = new HashMap<String, Processo>();
		try {
			System.out.println("Iniciando metodo DAO.carregarProcessos...."+parametros.getTarefa());

			String queryProcessos = Util.readTextFile(parametros.getQueryProcessos()).toString();
			String queryPartes = Util.readTextFile(parametros.getQueryPartes()).toString();
			
			ArrayList<ProcessoDto> listaMovimentosProcesso = obterProcessos(queryProcessos,
					parametros.getParametroQuery());
			System.out.println("Retornou a primeira query...."+parametros.getTarefa());
			for (ProcessoDto processoDto : listaMovimentosProcesso) {

				consolidarMovimentacao(mapaProcessos, processoDto);

			}

			ArrayList<ProcessoDto> listaPartesProcesso = obterProcessos(queryPartes,
					parametros.getParametroQuery());
			System.out.println("Retornou a segunda query...."+parametros.getTarefa());
			for (ProcessoDto processoDto : listaPartesProcesso) {

				consolidarPartes(mapaProcessos, processoDto);

			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Nao foi possivel carregar dados do banco de dados. " + e.getMessage());

		}

		System.out.println("Finalizando m�todo DAO.carregarProcessos...."+parametros.getTarefa());
		
		return mapaProcessos;
	}

}
