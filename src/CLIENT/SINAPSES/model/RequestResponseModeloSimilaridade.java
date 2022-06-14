package CLIENT.SINAPSES.model;

import java.util.List;

import com.google.gson.Gson;

public class RequestResponseModeloSimilaridade {

	private List<Similaridade> minutas;

	public RequestResponseModeloSimilaridade(List<Similaridade> minutas) {

		this.minutas = minutas;
	}

	public List<Similaridade> getMinutas() {
		return minutas;
	}

	public void setMinutas(List<Similaridade> minutas) {
		this.minutas = minutas;
	}

	public String toString() {
		return new Gson().toJson(this);
	}

}
