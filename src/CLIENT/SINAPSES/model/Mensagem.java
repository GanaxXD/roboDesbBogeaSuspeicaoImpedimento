package CLIENT.SINAPSES.model;

public class Mensagem {

	private String tipo;
	private String conteudo;

	public Mensagem(String tipo_, String conteudo_) {
		this.tipo = tipo_;
		this.conteudo = conteudo_;

	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

}
