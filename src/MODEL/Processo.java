package MODEL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import CLIENT.SINAPSES.model.RetornoModeloClassificacao;
import PAGE.pje21.justicaComum.CI.AtoJudicialEtiqueta;

/**
 * 
 * Classe que representa um processo judicial. Apensa cont�m alguns atributos
 * necess�rios para a execu��o dos rob�s.
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class Processo {

	private String idProcesso;
	private String numeroProcesso;
	private String linkProcesso;
	
	private ArrayList<Movimento> movimentos = new ArrayList<Movimento>();
	private ArrayList<Documento> documentos = new ArrayList<Documento>();

	private ArrayList<Parte> partes = new ArrayList<Parte>();
	private ArrayList<Parte> intimacoesPartes = new ArrayList<Parte>();

	private List<String> listaAdvogadosPoloAtivo = new ArrayList<String>();
	private List<String> listaAdvogadosPoloPassivo = new ArrayList<String>();
	private List<String> listaProcuradoriasPoloAtivo = new ArrayList<String>();
	private List<String> listaProcuradoriasPoloPassivo = new ArrayList<String>();
	private List<String> listaPartePoloAtivo = new ArrayList<String>();
	private List<String> listaPartePoloPassivo = new ArrayList<String>();

	private Enum acao;
	private Etiqueta etiquetaAutomacao;
	private Etiqueta etiquetaIA;
	private RetornoModeloClassificacao retornoSinapses;
	private AtoJudicialEtiqueta atoJudicialEtiqueta;

	private Set<String> etiquetasAutomacao = new HashSet<String>();
	private String etiquetaSinapses;
	private String documentoAto;
	private String jurisdicao;
	private String orgaoJulgador;
	
	private String classe;
	private String assunto;
	private String competencia;
	
	private String dataAutuacao;
	private String julgamento;

	private String conteudoDocumento;
	private String etiqueta;

	
	public String getJulgamento() {
		return julgamento;
	}

	public void setJulgamento(String julgamento) {
		this.julgamento = julgamento;
	}
	
	
	public String getOrgaoJulgador() {
		return orgaoJulgador;
	}
	
	public String getDataAutuacao() {
		return dataAutuacao;
	}

	public void setDataAutuacao(String dataAutuacao) {
		this.dataAutuacao = dataAutuacao;
	}

	public void setOrgaoJulgador(String orgaoJulgador) {
		this.orgaoJulgador = orgaoJulgador;
	}

	public String getLinkDetalheProcesso() {
		return "https://projudi.tjba.jus.br/projudi/listagens/DadosProcesso?numeroProcesso="+getIdProcesso();
	}
	
	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getLinkProcesso() {
		return linkProcesso;
	}

	public void setLinkProcesso(String linkProcesso) {
		this.linkProcesso = linkProcesso;
	}

	public String getConteudoDocumento() {
		return conteudoDocumento;
	}

	public void setConteudoDocumento(String conteudoDocumento) {
		this.conteudoDocumento = conteudoDocumento;
	}

	public Set<String> getEtiquetasAutomacao() {
		return etiquetasAutomacao;
	}

	public void setEtiquetasAutomacao(Set<String> etiquetasAutomacao) {
		this.etiquetasAutomacao = etiquetasAutomacao;
	}

	public String getEtiquetaSinapses() {
		return etiquetaSinapses;
	}

	public void setEtiquetaSinapses(String etiquetaSinapses) {

		if (etiquetaSinapses != null && etiquetaSinapses.equals("IA_TELEFONIA_COBRANCA_TERCEIROS")) {
			this.etiquetaSinapses = "IA_TELEFONIA_COBRANCA_INDEVIDA";
		}

		this.etiquetaSinapses = etiquetaSinapses;
	}

	public AtoJudicialEtiqueta getAtoJudicialEtiqueta() {
		return atoJudicialEtiqueta;
	}

	public void setAtoJudicialEtiqueta(AtoJudicialEtiqueta atoJudicialEtiqueta) {
		this.atoJudicialEtiqueta = atoJudicialEtiqueta;
	}

	public RetornoModeloClassificacao getRetornoSinapses() {
		return retornoSinapses;
	}

	public void setRetornoSinapses(RetornoModeloClassificacao retornoSinapses) {
		this.retornoSinapses = retornoSinapses;
	}

	public Etiqueta getEtiquetaAutomacao() {
		return etiquetaAutomacao;
	}

	public void setEtiquetaAutomacao(Etiqueta etiquetaAutomacao) {
		this.etiquetaAutomacao = etiquetaAutomacao;
	}

	public Etiqueta getEtiquetaIA() {
		return etiquetaIA;
	}

	public String getEtiquetaIAFormatada() {

		if (getEtiquetaIA() != null) {

			if (getEtiquetaIA().equals(Etiqueta.VICIO_DE_PRODUTO)) {

				return "IA_PRODUTO_ATRASO_ENTREGA";

			} else {

				return "IA_" + getEtiquetaIA().toString();
			}

		} else {
			return null;
		}

	}

	public void setEtiquetaIA(Etiqueta etiquetaIA) {
		this.etiquetaIA = etiquetaIA;
	}

	public ArrayList<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(ArrayList<Documento> documentos) {
		this.documentos = documentos;
	}

	public ArrayList<Parte> getIntimacoesPartes() {
		return intimacoesPartes;
	}

	public void setIntimacoesPartes(ArrayList<Parte> intimacoesPartes) {
		this.intimacoesPartes = intimacoesPartes;
	}

	public String getCompetencia() {
		return competencia;
	}

	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}

	public String getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(String idProcesso) {
		this.idProcesso = idProcesso;
	}

	public String getNumeroProcessoFormatado() {

		if (getNumeroProcesso() == null) {
			return getIdProcesso();
		} else {

			if (getNumeroProcesso().indexOf("/") != -1) {
				return getNumeroProcesso().substring(0, getNumeroProcesso().indexOf("/"));
			} else {

				return getNumeroProcesso();
			}
		}
	}
	
	
	public String getNumeroRecursoInterno() {

		String retorno = "";
		if (getNumeroProcesso() != null) {
			
			if (getNumeroProcesso().indexOf("/") != -1) {
				retorno = getNumeroProcesso().substring(getNumeroProcesso().indexOf("/"));
			} 
		}
		
		return retorno;
	}

	public String getJurisdicao() {
		return jurisdicao;
	}

	public void setJurisdicao(String jurisdicao) {
		this.jurisdicao = jurisdicao;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getDocumentoAto() {
		return documentoAto;
	}

	public void setDocumentoAto(String documentoAto) {
		this.documentoAto = documentoAto;
	}

	public List<String> getListaAdvogadosPoloAtivo() {
		return listaAdvogadosPoloAtivo;
	}

	public void setListaAdvogadosPoloAtivo(List<String> listaAdvogadosPoloAtivo) {
		this.listaAdvogadosPoloAtivo = listaAdvogadosPoloAtivo;
	}

	public List<String> getListaAdvogadosPoloPassivo() {
		return listaAdvogadosPoloPassivo;
	}

	public void setListaAdvogadosPoloPassivo(List<String> listaAdvogadosPoloPassivo) {
		this.listaAdvogadosPoloPassivo = listaAdvogadosPoloPassivo;
	}

	public List<String> getListaProcuradoriasPoloAtivo() {
		return listaProcuradoriasPoloAtivo;
	}

	public void setListaProcuradoriasPoloAtivo(List<String> listaProcuradoriasPoloAtivo) {
		this.listaProcuradoriasPoloAtivo = listaProcuradoriasPoloAtivo;
	}

	public List<String> getListaProcuradoriasPoloPassivo() {
		return listaProcuradoriasPoloPassivo;
	}

	public void setListaProcuradoriasPoloPassivo(List<String> listaProcuradoriasPoloPassivo) {
		this.listaProcuradoriasPoloPassivo = listaProcuradoriasPoloPassivo;
	}

	public List<String> getListaPartePoloAtivo() {
		return listaPartePoloAtivo;
	}

	public void setListaPartePoloAtivo(List<String> listaPartePoloAtivo) {
		this.listaPartePoloAtivo = listaPartePoloAtivo;
	}

	public List<String> getListaPartePoloPassivo() {
		return listaPartePoloPassivo;
	}

	public void setListaPartePoloPassivo(List<String> listaPartePoloPassivo) {
		this.listaPartePoloPassivo = listaPartePoloPassivo;
	}

	public Enum getAcao() {
		return acao;
	}

	public void setAcaoSeNulo(Enum acao) {
		if (this.acao == null) {
			this.acao = acao;
		}

	}

	public void setAcao(Enum acao) {
		this.acao = acao;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso.replaceAll("\"", "");
	}

	public ArrayList<Movimento> getMovimentos() {
		return movimentos;
	}

	public void setMovimentos(ArrayList<Movimento> movimentos) {
		this.movimentos = movimentos;
	}

	public ArrayList<Parte> getPartes() {
		return partes;
	}

	public void setPartes(ArrayList<Parte> partes) {
		this.partes = partes;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("\nProcesso:\t" + numeroProcesso);

		if (etiquetasAutomacao != null && etiquetasAutomacao.size() > 0) {
			StringBuffer str = new StringBuffer("");

			for (Iterator iterator = etiquetasAutomacao.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				str.append("\t" + string);
			}
			sb.append(str.toString());
		}

		if (etiquetaAutomacao != null)
			sb.append("\t" + etiquetaAutomacao);
		if (etiquetaIA != null)
			sb.append("\t" + etiquetaIA);

		if (movimentos.size() > 0)
			sb.append("\nmovimentos=\n" + movimentos);
		if (partes.size() > 0)
			sb.append("\npartes=\n" + partes);
		if (acao != null)
			sb.append("\nacao=" + acao);

		if (listaAdvogadosPoloAtivo.size() > 0)
			sb.append("\nlistaAdvogadosPoloAtivo=\n" + listaAdvogadosPoloAtivo);
		if (listaAdvogadosPoloPassivo.size() > 0)
			sb.append("\nlistaAdvogadosPoloPassivo=\n" + listaAdvogadosPoloPassivo);
		if (listaProcuradoriasPoloAtivo.size() > 0)
			sb.append("\nlistaProcuradoriasPoloAtivo=\n" + listaProcuradoriasPoloAtivo);
		if (listaProcuradoriasPoloPassivo.size() > 0)
			sb.append("\nlistaProcuradoriasPoloPassivo=\n" + listaProcuradoriasPoloPassivo);
		if (listaPartePoloAtivo.size() > 0)
			sb.append("\nlistaPartePoloAtivo=\n" + listaPartePoloAtivo);
		if (listaPartePoloPassivo.size() > 0)
			sb.append("\nlistaPartePoloPassivo=\n" + listaPartePoloPassivo);

		return sb.toString();
	}

	public String toString2() {
		return getNumeroProcesso();
	}

}
