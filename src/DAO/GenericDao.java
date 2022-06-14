package DAO;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import CLIENT.util.Util;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
/**
 * 
 * Classe gen�rica respons�vel pelo acesso ao banco de dados
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class GenericDao {

	private String urlDB;
	private String user;
	private String pass;

	public GenericDao(String urlDB, String user, String pass) {
		this.urlDB = urlDB;
		this.user = user;
		this.pass = pass;
	}

	public String getUrlDB() {
		return urlDB;
	}

	public void setUrlDB(String urlDB) {
		this.urlDB = urlDB;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	protected Connection getConexao() throws SQLException {
		Connection conn;
		Properties props = new Properties();
		props.setProperty("user", getUser());
		props.setProperty("password", getPass());
		conn = DriverManager.getConnection(getUrlDB(), props);
		return conn;
	}

	protected ArrayList<ProcessoDto> obterProcessos(String query, String parametro) throws AutomacaoException {
		Connection conn = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		ArrayList<ProcessoDto> processos = new ArrayList<ProcessoDto>();

		try {
			conn = getConexao();

			sqlStatement = conn.prepareStatement(query);
			if (parametro != null && !parametro.equals("")) {
				sqlStatement.setString(1, parametro);
			}

			resultSet = sqlStatement.executeQuery();
			processos = carregarDados(resultSet);

		} catch (Exception e) {
			throw new AutomacaoException(
					"obterProcessos: Ocorreu um erro no acesso � base de dados! " + e.getMessage());
		} finally {
			try {
				sqlStatement.close();
				resultSet.close();
				conn.close();
			} catch (Exception e2) {
				System.out.println("Erro ao fechar conexoes " + e2.getMessage());
			}

		}

		return processos;
	}
	
	
	protected ArrayList<ProcessoDto> obterProcessos(String query, String[] parametros) throws AutomacaoException {
		Connection conn = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		ArrayList<ProcessoDto> processos = new ArrayList<ProcessoDto>();

		try {
			conn = getConexao();

			sqlStatement = conn.prepareStatement(query);
			
			for (int i = 0; i < parametros.length; i++) {

				try {
					int parametro = new Integer(parametros[i]);
					sqlStatement.setInt(i + 1, parametro);
				} catch (NumberFormatException nfe) {
					sqlStatement.setString(i + 1, "Minutar relatório de voto"); //- alterar isso aqui
				}

			}

			
			resultSet = sqlStatement.executeQuery();
			processos = carregarDados(resultSet);

		} catch (Exception e) {
			throw new AutomacaoException(
					"obterProcessos: Ocorreu um erro no acesso a base de dados! " + e.getMessage());
		} finally {
			try {
				sqlStatement.close();
				resultSet.close();
				conn.close();
			} catch (Exception e2) {
				System.out.println("Erro ao fechar conexoes " + e2.getMessage());
			}

		}

		return processos;
	}

	/**
	 * M�todo que carrega todos os dados do resultset no ProcessoDto usando reflex�o
	 * 
	 * @param resultSet
	 * @return
	 * @throws AutomacaoException
	 */
	protected ArrayList<ProcessoDto> carregarDados(ResultSet resultSet) throws AutomacaoException {
		ArrayList<ProcessoDto> processos = new ArrayList<ProcessoDto>();
		try {

			ResultSetMetaData metadata = resultSet.getMetaData();
			int numberOfColumns = metadata.getColumnCount();
			while (resultSet.next()) {
				ProcessoDto processo = new ProcessoDto();

				for (int i = 1; i <= numberOfColumns; i++) {
					String nomeColuna = metadata.getColumnName(i).toLowerCase();
					String valorColuna = resultSet.getString(i);
					String nomeMetodo = "set" + nomeColuna.substring(0, 1).toUpperCase() + nomeColuna.substring(1);
					Method metodo = processo.getClass().getMethod(nomeMetodo, String.class);
					metodo.invoke(processo, valorColuna);

				}
				processos.add(processo);
			}

		} catch (Exception e) {
			throw new AutomacaoException(
					"Não foi possível carregar todos os dados da query.\nFavor verificar se todas as colunas estao no padrao de nomenclatura e se existe atributo correspondente na classe ProcessoDto.");
		}

		return processos;
	}

	public Map<String, Processo> carregarProcessos(Parametros parametros) throws AutomacaoException {

		Map<String, Processo> mapaProcessos = new HashMap<String, Processo>();
		try {

			String queryProcessos = Util.readTextFile(parametros.getQueryProcessos()).toString();

			ArrayList<ProcessoDto> listaProcessos = obterProcessos(queryProcessos, parametros.getParametroQuery());

			for (ProcessoDto processoDto : listaProcessos) {

				Processo proc = new Processo();
				Util.atribuirValores(proc, processoDto);
				mapaProcessos.put(proc.getNumeroProcesso(), proc);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Nao foi possivel carregar dados do banco de dados. " + e.getMessage());

		}

		return mapaProcessos;
	}

}
