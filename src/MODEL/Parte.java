package MODEL;

public class Parte {
	private String documentoParte;
	private String nomeParte;
	private String prazoDecorrido;
	private String tipoParte;
	
	private String representante;
	private String siglaParte;
	private String reciboDiario;
	
	public String getReciboDiario() {
		return reciboDiario;
	}

	public void setReciboDiario(String reciboDiario) {
		this.reciboDiario = reciboDiario;
	}

	public String getSiglaParte() {
		return siglaParte;
	}

	public void setSiglaParte(String siglaParte) {
		this.siglaParte = siglaParte;
	}

	public String getDocumentoParte() {
		return documentoParte;
	}

	public void setDocumentoParte(String documentoParte) {
		this.documentoParte = documentoParte;
	}
	
	public String getRepresentante() {
		return representante;
	}

	public void setRepresentante(String representante) {
		this.representante = representante;
	}

	public String getTipoParte() {
		return tipoParte;
	}

	public void setTipoParte(String tipoParte) {
		this.tipoParte = tipoParte;
	}

	

	public String getPrazoDecorrido() {
		return prazoDecorrido;
	}

	public void setPrazoDecorrido(String prazoDecorrido) {
		this.prazoDecorrido = prazoDecorrido;
	}

	public String getNomeParte() {
		return nomeParte;
	}

	public void setNomeParte(String nomeParte) {
		this.nomeParte = nomeParte;
	}

	@Override
	public String toString() {
		return "\n\t" + nomeParte;
	}
	
	
}
