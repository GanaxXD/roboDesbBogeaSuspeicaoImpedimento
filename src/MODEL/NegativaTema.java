package MODEL;

import java.util.List;

public class NegativaTema {
	private String tema;
	private String[] palavras;
	
	public String[] getPalavras() {
		return palavras;
	}

	public void setPalavras(String[] palavras) {
		this.palavras = palavras;
	}

	public NegativaTema(String tema, String[] palavras) {
		super();
		this.tema = tema;
		this.palavras = palavras;
	}

	public String getTema() {
		return tema;
	}

	public void setTema(String tema) {
		this.tema = tema;
	}


	
}
