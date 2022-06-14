package MODEL;

public class Movimento implements Comparable {
	
	private String dataMovimentacao;
	private String codMovimentoCNJ;
	private String descMovimentoCNJ;
	private String textoFinalMovimento;

	private String descMovimento;
	private String tipoDocumento;

	public String getDescMovimento() {
		return descMovimento;
	}

	public void setDescMovimento(String descMovimento) {
		this.descMovimento = descMovimento;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getDataMovimentacao() {
		return dataMovimentacao;
	}

	public void setDataMovimentacao(String dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}

	public String getCodMovimentoCNJ() {
		return codMovimentoCNJ;
	}

	public void setCodMovimentoCNJ(String codMovimentoCNJ) {
		this.codMovimentoCNJ = codMovimentoCNJ;
	}

	public String getDescMovimentoCNJ() {
		return descMovimentoCNJ;
	}

	public void setDescMovimentoCNJ(String descMovimentoCNJ) {
		this.descMovimentoCNJ = descMovimentoCNJ;
	}

	public String getTextoFinalMovimento() {
		return textoFinalMovimento;
	}

	public void setTextoFinalMovimento(String textoFinalMovimento) {
		this.textoFinalMovimento = textoFinalMovimento;
	}

	@Override
	public String toString() {
		return "\n\t" + dataMovimentacao + "\t(" + codMovimentoCNJ + ")\t" + descMovimentoCNJ + "\t"
				+ textoFinalMovimento + "\t" + tipoDocumento + "\t" + descMovimento;
	}

	@Override
	public int compareTo(Object o) {
		return dataMovimentacao.compareTo(((Movimento) o).getDataMovimentacao());
	}

}
