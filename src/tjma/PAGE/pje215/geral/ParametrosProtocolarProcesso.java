package tjma.PAGE.pje215.geral;

import PAGE.Parametros;

/**
 * Classe que representa os parâmetros de configuração do robô que protocola processos.
 * 
 * @author William Sodré
 * @TJMA
 */
public class ParametrosProtocolarProcesso extends Parametros {

	private String manterNaoProtocolado;

	public String getManterNaoProtocolado() {
		return manterNaoProtocolado;
	}

	public void setManterNaoProtocolado(String manterNaoProtocolado) {
		this.manterNaoProtocolado = manterNaoProtocolado;
	}
	
}