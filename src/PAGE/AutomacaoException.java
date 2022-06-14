package PAGE;

/*
 * @autor Leonardo Ribeiro de Oliveira
 */
public class AutomacaoException extends Exception {

	private String mensagem;
	
	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public AutomacaoException (String mensagem) {
		super(mensagem);
		setMensagem(mensagem);
		System.out.println(mensagem);
	}

}
