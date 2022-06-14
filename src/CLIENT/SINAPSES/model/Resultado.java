package CLIENT.SINAPSES.model;

public class Resultado {
	private Classe classe;
	private float conviccao;
	
	public Resultado(Classe classe, float conviccao) {
		super();
		this.classe = classe;
		this.conviccao = conviccao;
	}
	
	public Classe getClasse() {
		return classe;
	}
	public void setClasse(Classe classe) {
		this.classe = classe;
	}
	public float getConviccao() {
		return conviccao;
	}
	public void setConviccao(float conviccao) {
		this.conviccao = conviccao;
	}

	@Override
	public String toString() {
		return "Resultado Sinapses[classe=" + classe + ", conviccao=" + conviccao + "]";
	}
	
	
	
}
