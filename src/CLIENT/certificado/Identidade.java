package CLIENT.certificado;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

/**
 * Esta classe tem a função de auxiliar na montagem do certificado digital gerado 
 * pelo sistema para seus usuários. Uma das funções da classe é retirar acentuação dos 
 * valores do usuário(nome, endereço etc), haja vista que dentro do certificado estes
 * valores ficarão sem acentuação, conforme padrão de certificação icp-brasil.
 * 
 */
public class Identidade {

	String senha,
	       csenha,
	       pais,
	       estado,
	       cidade,
	       nome,
	       email;
	
	boolean pessoaFisica = false;
	Date dataNascimento;
	String CPF;
	String NIS;
	String RG;
	String RGExpedidor;
	String RGExpedidorUF;
	String INSS;
	
	/**
	 * Método que valida os campos digitados pelo usuário para a geração 
	 * de um certificado digial
	 * @return Vector contendo mensagens para serem exibidas ao usuário, em caso de incoerência
	 * das informações. 
	 */
	public Vector validaCampos()  {
		Vector erros = new Vector();
		if(senha != null && senha.length() < 8) erros.add("A senha deve ter no mínimo 8 caracteres");
		if(!(senha != null && csenha != null && senha.equals(csenha))) erros.add("Senha não confere.");		
		return erros;							
	}

	/**
	 * Método que retorna a cidade, sem acentuação
	 * @return A cidade do usuário, sem acentuação
	 */
	public String getCidade() {
		return retiraAcentos(cidade);
	}

	/**
	 * Método que retorna a confirmação de senha digitada pelo usuário
	 * @return A confirmação de senha digitada pelo usuário
	 */
	public String getCsenha() {
		return csenha;
	}

	/**
	 * Método que retorna o e-mail do usuário
	 * @return O e-mail do usuário
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Método que retorna o Estado(UF) do usuário, sem acentuação 
	 * @return O Estado (UF) do usuário, sem acentuação 
	 */
	public String getEstado() {
		return retiraAcentos(estado);
	}

	/**
	 * Método que retorna o nome do usuário, sem acentuação
	 * @return O nome do usuário, sem acentuação
	 */
	public String getNome() {
		return retiraAcentos(nome);
	}

	/**
	 * Método que retorna o país do usuário, sem acentuação
	 * @return O país do usuário, sem acentuação
	 */
	public String getPais() {
		return retiraAcentos(pais);
	}

	/**
	 * Método que retorna a senha digitada pelo usuário.
	 * @return A senha digitada pelo usuário.
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * Método que seta a cidade
	 * @param string Cidade.
	 */
	public void setCidade(String string) {
		cidade = string;
	}

	/**
	 * Método que seta a confirmação de senha digitada pelo usuário
	 * @param string COnfirmação de senha
	 */
	public void setCsenha(String string) {
		csenha = string;
	}

	/**
	 * Método que seta o e-mail que irá para o certificado
	 * @param string O e-mail que ficará no certificado
	 */
	public void setEmail(String string) {
		email = string;
	}

	/**
	 * Método que seta o Estado (UF) que ficará no certificado
	 * @param string O Estado (UF) que ficará no certificado
	 */
	public void setEstado(String string) {
		estado = string;
	}

	/**
	 * Método que seta o nome que ficará no certificado
	 * @param string O nome que ficará no certificado
	 */
	public void setNome(String string) {
		nome = string;
	}

	/**
	 * Método que seta o país do usuário, que ficará no certificado 
	 * @param string O país do usuário, que ficará no certificado
	 */
	public void setPais(String string) {
		pais = string;
	}

	/**
	 * Método que seta a senha do usuário, que ficará no certificado de forma criptografada.
	 * @param string A senha digitada pelo usuário para a chave privada do certificado
	 */
	public void setSenha(String string) {
		senha = string;
	}

	/**
	 * Método que retira acetuação de uma string
	 * @param stringOrigem String cuja acentuação se deseja retirar
	 * @return A string sem acentuação
	 */
	private String retiraAcentos(String stringOrigem) {
		String destino = stringOrigem.replaceAll("[áàâãª]", "a");
		destino = destino.replaceAll("[ÁÀÂÃ]","A");
		destino = destino.replaceAll("[éèê]", "e");
		destino = destino.replaceAll("[ÉÈÊ]","E");
		destino = destino.replaceAll("[óòôõº]", "o");
		destino = destino.replaceAll("[ÓÒÔÕ]","O");
		destino = destino.replaceAll("[úùû]", "u");
		destino = destino.replaceAll("[ÚÙÛ]","U");
		destino = destino.replaceAll("ç","c");
		destino = destino.replaceAll("Ç","C");
		return destino.toUpperCase();		
	}

	/**
	 * Método que retorna se se trata de pessoa física ou juríica  
	 * @return True caso se trate de pessoa física, false caso contrário
	 */
	public boolean ehPessoaFisica() {
		return pessoaFisica;
	}
	
	/**
	 * Método que seta que é pessoa física
	 *
	 */
	public void setEhPessoaFisica() {
		pessoaFisica = true;
	}

	/**
	 * Método que seta a data de nascimento do usuário da identificação
	 * @param date Data de nascimento do usuário  
	 */
	public void setDataNascimento(Date date) {
		this.dataNascimento = date;
	}

	/**
	 * Método que retorna a data de nascimento, atualizando os dados para o formato
	 * GragorianCalendar, através do qual o primeiro Mês tem código 0 ao invés de 1, e 
	 * o último mês têm código 11 ou invés de 12. 
	 * @return String dia+mes+ano da data de nascimento do usuário 
	 */
	public String getDataNascimento() {
		GregorianCalendar calenGreg = new GregorianCalendar();
		calenGreg.setTime(dataNascimento);
		String ano = ""+calenGreg.get(GregorianCalendar.YEAR);
		String mes = "" + (calenGreg.get(GregorianCalendar.MONTH) + 1);
		String dia = "" + calenGreg.get(GregorianCalendar.DAY_OF_MONTH);
		if(mes.length()==1) mes= "0"+mes;
		if(dia.length() == 1) dia = "0"+dia;
		
		String retorno = dia+mes+ano;
		if(retorno.length()!=8) throw new RuntimeException("Ocorreu um erro na geração da data de nascimento: "+retorno);
		return retorno;
	}

	/**
	 * Método que retorna o CPF do suário
	 * @return 
	 */
	public String getCPF() {
		if(CPF.length()!=11) throw new RuntimeException("Tamanho da string CPF incorreto.");		
		return CPF;
	}

	public void setCPF(String string) {
		if(string == null) throw new IllegalArgumentException("CPF não pode ser null");		
		CPF = string;
	}

	public String getNIS() {
		if(NIS==null) return "00000000000";
		String retorno = completaZerosEsquerda(NIS,11);
		if(retorno.length()!=11) throw new RuntimeException("Tamanho da string NIS incorreto.");
		return retorno;
	}

	public void setNIS(String string) {
		NIS = string;
	}
	
	public void setRG(String string) {
		if(string == null) throw new IllegalArgumentException("RG não pode ser null");
		RG = string;
	}

	public String getRG() {
		String retorno = completaZerosEsquerda(RG,11);
		if(retorno.length()!=11) throw new RuntimeException("Tamanho da string RG incorreto.");
		return retorno;
	}

	public void setRGExpedidor(String string) {
		if(string == null) throw new IllegalArgumentException("RGExpedidor não pode ser null");
		RGExpedidor = string;
	}

	public void setRGExpedidorUF(String string) {
		if(string == null) throw new IllegalArgumentException("RGExpedidorUF não pode ser null");
		RGExpedidorUF = string;
	}

	public String getRGOrgaoExpedidorUF() {
		String retorno = RGExpedidor + RGExpedidorUF;
		if(retorno.length()>6) throw new RuntimeException("Tamanho da string RG Expedidor incorreto.");
		return retorno;
	}

	public String getINSS() {
		if(INSS == null) return "000000000000";
		String retorno = completaZerosEsquerda(INSS,12);
		if(retorno.length()!=12) throw new RuntimeException("Tamanho da string INSS incorreto.");
		return retorno;
	}

	public void setINSS(String string) {
		INSS = string;
	}


	private String completaZerosEsquerda(String origem, int tam) {
		if(origem.length() >= tam) return origem;
		return completaZerosEsquerda("0"+origem,tam);
	}
}