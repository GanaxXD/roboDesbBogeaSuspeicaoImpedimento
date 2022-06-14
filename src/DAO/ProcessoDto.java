package DAO;

/**
 * 
 * Classe usada para transfer�ncia de dados
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class ProcessoDto {

	/**
	 * Por conta de comportamento do banco postgres, tive que colocar as vari�veis
	 * com todas as letras em minusculo para possibilitar a sincronia das informa��es
	 * utilizando java.reflect
	 */
	private String idprocesso;
	private String numeroprocesso;
	private String linkprocesso;
		private String datamovimentacao;
	private String codmovimentocnj;
	private String descmovimentocnj;
	private String textofinalmovimento;
	private String nomeparte;
	private String descdocumento;
	private String tipodocumento;
	
	private String jurisdicao;
	private String dataautuacao;
	private String orgaojulgador;
	
	private String classe;
	private String assunto;
	private String documentoparte;
	private String prazodecorrido;
	private String tipoparte;
	private String representante;
	private String codnotificacao;
	private String siglaparte;
	
	private String recibodiario;
	
	private String idprocessodocumento; 
	private String dtjuntada;
	private String idtipoprocessodocumento;
	private String dstipoprocessodocumento;
	private String nrdocumentostorage;
	private String dsextensao;
	private String dsmodelodocumento;
	private String nrtamanho;
	private String julgamento;
	

	public String getJulgamento() {
		return julgamento;
	}
	public void setJulgamento(String julgamento) {
		this.julgamento = julgamento;
	}
	public String getDataautuacao() {
		return dataautuacao;
	}
	public void setDataautuacao(String dataautuacao) {
		this.dataautuacao = dataautuacao;
	}
	public String getOrgaojulgador() {
		return orgaojulgador;
	}
	public void setOrgaojulgador(String orgaojulgador) {
		this.orgaojulgador = orgaojulgador;
	}
	public String getLinkprocesso() {
		return linkprocesso;
	}
	public void setLinkprocesso(String linkprocesso) {
		this.linkprocesso = linkprocesso;
	}

	public String getIdprocessodocumento() {
		return idprocessodocumento;
	}
	public void setIdprocessodocumento(String idprocessodocumento) {
		this.idprocessodocumento = idprocessodocumento;
	}
	public String getDtjuntada() {
		return dtjuntada;
	}
	public void setDtjuntada(String dtjuntada) {
		this.dtjuntada = dtjuntada;
	}
	public String getIdtipoprocessodocumento() {
		return idtipoprocessodocumento;
	}
	public void setIdtipoprocessodocumento(String idtipoprocessodocumento) {
		this.idtipoprocessodocumento = idtipoprocessodocumento;
	}
	public String getDstipoprocessodocumento() {
		return dstipoprocessodocumento;
	}
	public void setDstipoprocessodocumento(String dstipoprocessodocumento) {
		this.dstipoprocessodocumento = dstipoprocessodocumento;
	}
	public String getNrdocumentostorage() {
		return nrdocumentostorage;
	}
	public void setNrdocumentostorage(String nrdocumentostorage) {
		this.nrdocumentostorage = nrdocumentostorage;
	}
	public String getDsextensao() {
		return dsextensao;
	}
	public void setDsextensao(String dsextensao) {
		this.dsextensao = dsextensao;
	}
	public String getDsmodelodocumento() {
		return dsmodelodocumento;
	}
	public void setDsmodelodocumento(String dsmodelodocumento) {
		this.dsmodelodocumento = dsmodelodocumento;
	}
	public String getNrtamanho() {
		return nrtamanho;
	}
	public void setNrtamanho(String nrtamanho) {
		this.nrtamanho = nrtamanho;
	}
	
	
	
	public String getRecibodiario() {
		return recibodiario;
	}
	public void setRecibodiario(String recibodiario) {
		this.recibodiario = recibodiario;
	}
	public String getSiglaparte() {
		return siglaparte;
	}
	public void setSiglaparte(String siglaparte) {
		this.siglaparte = siglaparte;
	}
	private String competencia;
	public String getCompetencia() {
		return competencia;
	}
	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}
	
	public String getIdprocesso() {
		return idprocesso;
	}
	public void setIdprocesso(String idprocesso) {
		this.idprocesso = idprocesso;
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
	public String getDocumentoparte() {
		return documentoparte;
	}
	public void setDocumentoparte(String documentoparte) {
		this.documentoparte = documentoparte;
	}
	public String getPrazodecorrido() {
		return prazodecorrido;
	}
	public void setPrazodecorrido(String prazodecorrido) {
		this.prazodecorrido = prazodecorrido;
	}
	public String getTipoparte() {
		return tipoparte;
	}
	public void setTipoparte(String tipoparte) {
		this.tipoparte = tipoparte;
	}
	public String getRepresentante() {
		return representante;
	}
	public void setRepresentante(String representante) {
		this.representante = representante;
	}
	public String getCodnotificacao() {
		return codnotificacao;
	}
	public void setCodnotificacao(String codnotificacao) {
		this.codnotificacao = codnotificacao;
	}
	public String getNumardigital() {
		return numardigital;
	}
	public void setNumardigital(String numardigital) {
		this.numardigital = numardigital;
	}
	private String numardigital;
	public String getNumeroprocesso() {
		return numeroprocesso;
	}
	public void setNumeroprocesso(String numeroprocesso) {
		this.numeroprocesso = numeroprocesso;
	}
	public String getDatamovimentacao() {
		return datamovimentacao;
	}
	public void setDatamovimentacao(String datamovimentacao) {
		this.datamovimentacao = datamovimentacao;
	}
	public String getCodmovimentocnj() {
		return codmovimentocnj;
	}
	public void setCodmovimentocnj(String codmovimentocnj) {
		this.codmovimentocnj = codmovimentocnj;
	}
	public String getDescmovimentocnj() {
		return descmovimentocnj;
	}
	public void setDescmovimentocnj(String descmovimentocnj) {
		this.descmovimentocnj = descmovimentocnj;
	}
	public String getTextofinalmovimento() {
		return textofinalmovimento;
	}
	public void setTextofinalmovimento(String textofinalmovimento) {
		this.textofinalmovimento = textofinalmovimento;
	}
	public String getNomeparte() {
		return nomeparte;
	}
	public void setNomeparte(String nomeparte) {
		this.nomeparte = nomeparte;
	}
	public String getDescdocumento() {
		return descdocumento;
	}
	public void setDescdocumento(String descdocumento) {
		this.descdocumento = descdocumento;
	}
	public String getTipodocumento() {
		return tipodocumento;
	}
	public void setTipodocumento(String tipodocumento) {
		this.tipodocumento = tipodocumento;
	}

	

}
