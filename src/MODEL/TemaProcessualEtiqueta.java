package MODEL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemaProcessualEtiqueta {
	
	private String etiqueta;
	private String confirmacaoSinapses;
	private List<String>[] palavrasChaveANDOR;
	private List<List<String>> listaPalavrasANDOR;



	private String[] operadorNOT;
	
	public TemaProcessualEtiqueta() {
		listaPalavrasANDOR = new ArrayList() {};
		operadorNOT = new String[] {};
		
	}
	
	public TemaProcessualEtiqueta(String etiqueta, String confirmacaoSinapses, List<String>[] palavrasChaveANDOR,
			List<List<String>> listaPalavrasANDOR, String[] operadorNOT) {
		super();
		this.etiqueta = etiqueta;
		this.confirmacaoSinapses = confirmacaoSinapses;
		this.palavrasChaveANDOR = palavrasChaveANDOR;
		this.listaPalavrasANDOR = listaPalavrasANDOR;
		this.operadorNOT = operadorNOT;
	}
	public List<List<String>> getListaPalavrasANDOR() {
		return listaPalavrasANDOR;
	}

	public void setListaPalavrasANDOR(List<List<String>> listaPalavrasANDOR) {
		this.listaPalavrasANDOR = listaPalavrasANDOR;
	}
	public TemaProcessualEtiqueta(String etiqueta, String confirmacaoSinapses, List<String>[] palavrasChaveANDOR,
			String[] operadorNOT) {
		super();
		this.etiqueta = etiqueta;
		this.confirmacaoSinapses = confirmacaoSinapses;
		this.palavrasChaveANDOR = palavrasChaveANDOR;
		this.operadorNOT = operadorNOT;
	}
	
	
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getConfirmacaoSinapses() {
		return confirmacaoSinapses;
	}
	public void setConfirmacaoSinapses(String confirmacaoSinapses) {
		this.confirmacaoSinapses = confirmacaoSinapses;
	}
	
	public String[] getOperadorNOT() {
		return operadorNOT;
	}
	public void setOperadorNOT(String[] operadorNOT) {
		this.operadorNOT = operadorNOT;
	}


	public List<String>[] getPalavrasChaveANDOR() {
		return palavrasChaveANDOR;
	}


	public void setPalavrasChaveANDOR(List<String>[] palavrasChaveANDOR) {
		this.palavrasChaveANDOR = palavrasChaveANDOR;
	}

	@Override
	public String toString() {
		return "\nTemaProcessualEtiqueta [\netiqueta=" + etiqueta + "\npalavrasChaveANDOR=" + Arrays.toString(palavrasChaveANDOR) + "\nlistaPalavrasANDOR="
				+ listaPalavrasANDOR + "\noperadorNOT=" + Arrays.toString(operadorNOT) + "]";
	}



	
	
}
