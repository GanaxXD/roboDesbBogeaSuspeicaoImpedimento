package RN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import CLIENT.SINAPSES.SinapsesClient;
import CLIENT.SINAPSES.model.RetornoModeloClassificacao;
import DAO.BaixaProcessualDao;
import DAO.JCRStorage;
import DAO.JulgamentoTematicoDao;
import MODEL.Documento;
import MODEL.Etiqueta;
import MODEL.FiltroEtiqueta;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * Classe respons�vel pela
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @TJBA
 */
public class JulgamentoTematicoPJERN {
	private JulgamentoTematicoDao dao;

	public JulgamentoTematicoPJERN(JulgamentoTematicoDao dao) {
		setDao(dao);
	}

	public JulgamentoTematicoDao getDao() {
		return dao;
	}

	public void setDao(JulgamentoTematicoDao dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @param parametros
	 * @return
	 */
	public Map<String, Processo> carregarProcessos(Parametros parametros, SinapsesClient conexaoSinapses)
			throws AutomacaoException {
		try {
			System.out.println("Iniciando metodo RN.carregarProcessos.....");
			Map<String, Processo> mapaProcessos = dao.carregarProcessos(parametros);

			for (Processo processo : mapaProcessos.values()) {

				analisarProcesso(parametros, conexaoSinapses, processo);

			}
			System.out.println(mapaProcessos);
			System.out.println("Finalizando metodo RN.carregarProcessos....");
			
			return mapaProcessos;
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao carregar processos. " + e.getMessage() + " " + parametros.getTarefa());
		}

	}

	private void analisarProcesso(Parametros parametros, SinapsesClient conexaoSinapses, Processo processo)
			throws AutomacaoException {
		List<Documento> documentos = processo.getDocumentos();

		
		try {
		
				for (Documento documento : documentos) {
					
					if (documento.getDsTipoProcessoDocumento().toUpperCase()
							.indexOf(parametros.getDocumentoProcesso()) != -1) {
		
						carregarConteudoTexto(parametros, documento);
		
						definirEtiquetaProcesso(parametros, conexaoSinapses, processo, documento);
		
						if (processo.getEtiquetaAutomacao() != null || processo.getEtiquetaIA()!=null) {
							break;
						}
		
					}
		
				}
		
		}catch(AutomacaoException e) {
			processo.setEtiquetaAutomacao(Etiqueta.OUTROS);
			processo.setEtiquetaIA(Etiqueta.OUTROS);
				
		}catch(Exception e) {
			throw new AutomacaoException("Ocorreu um erro ao analisar o processo "+ processo.getNumeroProcesso() + " - "+ e.getMessage());
		}
		
		
	}

	private void definirEtiquetaProcesso(Parametros parametros, SinapsesClient conexaoSinapses, Processo processo,
			Documento documento) throws AutomacaoException {

		if (parametros.getSinapsesURL() != null) {
			definirEtiquetaInteligenciaArtificial(processo, documento, conexaoSinapses);
		
			for (FiltroEtiqueta filtro : parametros.getFiltrosEtiquetas()) {
				if(filtro.getEtiqueta().equals(processo.getEtiquetaIA())) {
					definirEtiquetaAutomacao(processo, documento, filtro);
				}
			}
		}

	}

	private void definirEtiquetaInteligenciaArtificial(Processo processo, Documento documento, SinapsesClient conexaoSinapses) throws AutomacaoException {

		if (documento.getConteudoTexto() != null) {

			RetornoModeloClassificacao retornoSinapses = conexaoSinapses.consultarModelo(documento.getConteudoTexto());
			if (retornoSinapses != null) {

				try {
					System.out.println("Retorno Sinapses: "+retornoSinapses.getClasseConvicto().getDescricao());
					processo.setEtiquetaIA(Etiqueta.valueOf(retornoSinapses.getClasseConvicto().getDescricao()));
					processo.setRetornoSinapses(retornoSinapses);
				} catch (Exception e) {
					processo.setEtiquetaIA(Etiqueta.OUTROS);
					//throw new AutomacaoException("Erro ao consultar o sinapses. "+ e.getMessage());
				}
			}

		}

	}

	private void definirEtiquetaAutomacao(Processo processo, Documento documento, FiltroEtiqueta filtro) {
		if (documento.getConteudoTexto() != null) {

			if (filtro.getOperador().equals("AND")) {

				boolean encontrouPalavra = false;
				for (int i = 0; i < filtro.getPalavrasChave().length; i++) {
					encontrouPalavra = (documento.getConteudoTexto().toUpperCase()
							.indexOf(filtro.getPalavrasChave()[i].toUpperCase()) != -1);

					if (!encontrouPalavra) {
						break;
					}
				}

				if (encontrouPalavra) {
					processo.setEtiquetaAutomacao(filtro.getEtiqueta());
				}

			} else if (filtro.getOperador().equals("OR")) {

				boolean encontrouPalavra = false;
				for (int i = 0; i < filtro.getPalavrasChave().length; i++) {
					encontrouPalavra = (documento.getConteudoTexto().toUpperCase()
							.indexOf(filtro.getPalavrasChave()[i].toUpperCase()) != -1);

					if (encontrouPalavra) {
						processo.setEtiquetaAutomacao(filtro.getEtiqueta());
						break;
					}
				}
			}
		}
	}

	private void carregarConteudoTexto(Parametros parametros, Documento documento) {
		try {

			if (documento.getDsModeloDocumento() != null) {
				documento.setConteudoTexto(JCRStorage.lerConteudoHTML(documento.getDsModeloDocumento(), documento.getProcesso().getNumeroProcesso()+".html"));
			} else if (documento.getNrDocumentoStorage() != null) {

				documento.setConteudoTexto(JCRStorage.getConteudoTexto(parametros.getJCRLocation(),
						documento.getNrDocumentoStorage(), documento.getDsExtensao()));
			}

			System.out.println(" ");
		} catch (Exception e) {
			documento.setConteudoTexto(e.getMessage());

		}
	}

}
