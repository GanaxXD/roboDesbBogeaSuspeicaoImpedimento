package PAGE.pje21.justicaComum.CI;

public class AtoJudicialEtiqueta {

	private String tipoAto;
	private String codMovimento;
	private String movimento;
	private String modeloAto;
	private String etiqueta;
	private String assinar;
	private String[] expedicao;
	private String[] polos;
	public String[] getPolos() {
		return polos;
	}

	public void setPolos(String[] polos) {
		this.polos = polos;
	}

	private String vistaMP;
	private String vistaDP;
	private String prazo;
	private String tipo;
	
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getPrazo() {
		return prazo;
	}

	public void setPrazo(String prazo) {
		this.prazo = prazo;
	}

	public AtoJudicialEtiqueta(String etiqueta, String tipoAto, String modeloAto, String movimento, String codMovimento,
			String assinar, String[] expedicao) {
		super();
		this.tipoAto = tipoAto;
		this.codMovimento = codMovimento;
		this.movimento = movimento;
		this.modeloAto = modeloAto;
		this.etiqueta = etiqueta;
		this.assinar = assinar;
		this.expedicao = expedicao;

	}
	
	public String getVistaMP() {
		return vistaMP;
	}

	public void setVistaMP(String vistaMP) {
		this.vistaMP = vistaMP;
	}

	public String getVistaDP() {
		return vistaDP;
	}

	public void setVistaDP(String vistaDP) {
		this.vistaDP = vistaDP;
	}

	public String[] getExpedicao() {
		return expedicao;
	}

	public void setExpedicao(String[] expedicao) {
		this.expedicao = expedicao;
	}

	public String getAssinar() {
		return assinar;
	}

	public void setAssinar(String assinar) {
		this.assinar = assinar;
	}

	@Override
	public String toString() {
		return "AtoJudicial [tipoAto=" + tipoAto + ", codMovimento=" + codMovimento + ", movimento=" + movimento
				+ ", modeloAto=" + modeloAto + "]";
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getTipoAto() {
		return tipoAto;
	}

	public void setTipoAto(String tipoAto) {
		this.tipoAto = tipoAto;
	}

	public String getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(String codMovimento) {
		this.codMovimento = codMovimento;
	}

	public String getMovimento() {
		return movimento;
	}

	public void setMovimento(String movimento) {
		this.movimento = movimento;
	}

	public String getModeloAto() {
		return modeloAto;
	}

	public void setModeloAto(String modeloAto) {
		this.modeloAto = modeloAto;
	}

}
