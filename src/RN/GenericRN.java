package RN;

import java.util.Map;
import DAO.GenericDao;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;

public class GenericRN {
	private GenericDao dao;

	public GenericRN(GenericDao dao) {
		setDao(dao);
	}

	public GenericDao getDao() {
		return dao;
	}

	public void setDao(GenericDao dao) {
		this.dao = dao;
	}
	
	public Map<String, Processo> carregarProcessos(Parametros parametros) throws AutomacaoException {
		
		return dao.carregarProcessos(parametros);

	}

}
