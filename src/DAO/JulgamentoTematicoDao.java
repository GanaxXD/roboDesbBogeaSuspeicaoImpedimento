package DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import CLIENT.util.Util;
import MODEL.Documento;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * 
 * Classe de acesso a dados espec�fica para a leitura dos processos para
 * promover o julgamento tem�tico
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class JulgamentoTematicoDao extends GenericDao {

	public JulgamentoTematicoDao(String urlDB, String user, String pass) {
		super(urlDB, user, pass);
	}

	private void consolidarDocumentos(Map<String, Processo> mapaProcessos, ProcessoDto processoDto,
			Parametros parametros) throws AutomacaoException{

		Processo processo = mapaProcessos.get(processoDto.getNumeroprocesso());
		if (processo == null) {
			processo = new Processo();
			Util.atribuirValores(processo, processoDto);
			adicionarDocumento(processoDto, processo, parametros);
			mapaProcessos.put(processo.getNumeroProcesso(), processo);

		} else {
			adicionarDocumento(processoDto, processo, parametros);
		}

	}

	private void adicionarDocumento(ProcessoDto processoDto, Processo processo, Parametros parametros)
			throws AutomacaoException {

		Documento documento = new Documento();
		Util.atribuirValores(documento, processoDto);
		processo.getDocumentos().add(documento);
		documento.setProcesso(processo);

	}

	/**
	 * Metodo sobrescrito para tratar de algo específico do robo de baixa
	 * processual. Duas consultas sao utilizadas para aplicar a lógica da baixa,
	 * conforme classe BaixaProcessualRN
	 */
	public Map<String, Processo> carregarProcessos(Parametros parametros) throws AutomacaoException {
		Map<String, Processo> mapaProcessos = new HashMap<String, Processo>();
		try {
			System.out.println("Iniciando método DAO.carregarProcessos....");

			String queryProcessos = Util.readTextFile(parametros.getQueryProcessos()).toString();

			System.out.println(queryProcessos);
			
			ArrayList<ProcessoDto> listaProcessosDocumentos = obterProcessos(queryProcessos,
					parametros.getParametrosQuery());
			
			for (ProcessoDto processoDto : listaProcessosDocumentos) {

				consolidarDocumentos(mapaProcessos, processoDto, parametros);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Não foi possivel carregar dados do banco de dados. " + e.getMessage());

		}

		System.out.println("Quantidade de processos retornados: "+ mapaProcessos.size());

		return mapaProcessos;
	}

}
