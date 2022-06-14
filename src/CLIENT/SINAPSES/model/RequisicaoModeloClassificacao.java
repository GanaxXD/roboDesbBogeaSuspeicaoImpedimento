package CLIENT.SINAPSES.model;

public class RequisicaoModeloClassificacao {

	private Mensagem mensagem;

	private int quantidadeClasses;

	public RequisicaoModeloClassificacao(Mensagem mensagem_, int quantidadeClasses_) {
		this.mensagem = mensagem_;
		this.quantidadeClasses = quantidadeClasses_;

	}

	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	public int getQuantidadeClasses() {
		return quantidadeClasses;
	}

	public void setQuantidadeClasses(int quantidadeClasses) {
		this.quantidadeClasses = quantidadeClasses;
	}

}
