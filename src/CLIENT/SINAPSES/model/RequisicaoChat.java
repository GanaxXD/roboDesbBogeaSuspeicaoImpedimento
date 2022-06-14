package CLIENT.SINAPSES.model;

public class RequisicaoChat {

	private Mensagem mensagem;
	private String tema;
	public RequisicaoChat(Mensagem mensagem_, String tema_) {
		this.mensagem = mensagem_;
		this.tema = tema_;

	}

	public String getTema() {
		return tema;
	}

	public void setTema(String tema) {
		this.tema = tema;
	}



	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	

}
