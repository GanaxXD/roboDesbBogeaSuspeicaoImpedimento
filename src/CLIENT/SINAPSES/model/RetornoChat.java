package CLIENT.SINAPSES.model;


import com.google.gson.Gson;

public class RetornoChat {

	private float acuracia;
	private String textoPredicao;

	public RetornoChat(float acuracia, String textoPredicao) {
		this.acuracia = acuracia;
		this.textoPredicao = textoPredicao;
	}

	
	public float getAcuracia() {
		return acuracia;
	}



	public void setAcuracia(float acuracia) {
		this.acuracia = acuracia;
	}



	public String getTextoPredicao() {
		return textoPredicao;
	}



	public void setTextoPredicao(String textoPredicao) {
		this.textoPredicao = textoPredicao;
	}



	public String toString() {
		return new Gson().toJson(this);
	}
	
	

}
