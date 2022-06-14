package CLIENT.SINAPSES.model;

import java.util.List;

import com.google.gson.Gson;

public class RetornoModeloClassificacao {

	private Classe classeConvicto;
	private List<Resultado> resultados;

	public RetornoModeloClassificacao(Classe classeConvicto, List<Resultado> resultados) {
		this.classeConvicto = classeConvicto;
		this.resultados = resultados;
	}

	public Classe getClasseConvicto() {
		return classeConvicto;
	}

	public void setClasseConvicto(Classe classeConvicto) {
		this.classeConvicto = classeConvicto;
	}

	public List<Resultado> getResultados() {
		return resultados;
	}

	public void setResultados(List<Resultado> resultados) {
		this.resultados = resultados;
	}
	
	public String toString() {
		return new Gson().toJson(this);
	}
	
	

}
