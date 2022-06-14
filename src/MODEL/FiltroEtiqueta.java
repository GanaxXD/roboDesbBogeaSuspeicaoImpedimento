package MODEL;

public class FiltroEtiqueta {

	private String[] palavrasChave;
	private String operador;
	private Etiqueta etiqueta;

	public FiltroEtiqueta(Etiqueta etiqueta, String operador, String[] palavrasChave) {
		this.etiqueta = etiqueta;
		this.palavrasChave = palavrasChave;
		this.operador = operador;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String[] getPalavrasChave() {
		return palavrasChave;
	}

	public void setPalavrasChave(String[] palavrasChave) {
		this.palavrasChave = palavrasChave;
	}

}
