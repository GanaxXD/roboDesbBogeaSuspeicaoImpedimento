package MODEL;

public class Documento implements Comparable {

	private String idProcessoDocumento;
	private String dtJuntada;
	private String idTipoProcessoDocumento;
	private String dsTipoProcessoDocumento;
	private String nrDocumentoStorage;
	private String dsExtensao;
	private String dsModeloDocumento;
	private String nrTamanho;
	private String conteudoTexto;
	private Processo processo;

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public String getConteudoTexto() {
		return conteudoTexto;
	}

	public void setConteudoTexto(String conteudoTexto) {
		this.conteudoTexto = conteudoTexto;
	}

	public String getIdProcessoDocumento() {
		return idProcessoDocumento;
	}

	public void setIdProcessoDocumento(String idProcessoDocumento) {
		this.idProcessoDocumento = idProcessoDocumento;
	}

	public String getDtJuntada() {
		return dtJuntada;
	}

	public void setDtJuntada(String dtJuntada) {
		this.dtJuntada = dtJuntada;
	}

	public String getIdTipoProcessoDocumento() {
		return idTipoProcessoDocumento;
	}

	public void setIdTipoProcessoDocumento(String idTipoProcessoDocumento) {
		this.idTipoProcessoDocumento = idTipoProcessoDocumento;
	}

	public String getDsTipoProcessoDocumento() {
		return dsTipoProcessoDocumento;
	}

	public void setDsTipoProcessoDocumento(String dsTipoProcessoDocumento) {
		this.dsTipoProcessoDocumento = dsTipoProcessoDocumento;
	}

	public String getNrDocumentoStorage() {
		return nrDocumentoStorage;
	}

	public void setNrDocumentoStorage(String nrDocumentoStorage) {
		this.nrDocumentoStorage = nrDocumentoStorage;
	}

	public String getDsExtensao() {
		return dsExtensao;
	}

	public void setDsExtensao(String dsExtensao) {
		this.dsExtensao = dsExtensao;
	}

	public String getDsModeloDocumento() {
		return dsModeloDocumento;
	}

	public void setDsModeloDocumento(String dsModeloDocumento) {
		this.dsModeloDocumento = dsModeloDocumento;
	}

	public String getNrTamanho() {
		return nrTamanho;
	}

	public void setNrTamanho(String nrTamanho) {
		this.nrTamanho = nrTamanho;
	}
	
	public String getLinkDownload() {
		return "https://projudi.tjba.jus.br/projudi/listagens/DownloadArquivo?arquivo="+getIdProcessoDocumento();
	}

	public int compareTo(Object o) {
		return this.dtJuntada.compareTo(((Documento) o).getDtJuntada());
	}

}
