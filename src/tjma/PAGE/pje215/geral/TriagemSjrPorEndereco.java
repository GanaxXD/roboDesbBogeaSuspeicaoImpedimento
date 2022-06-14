package tjma.PAGE.pje215.geral;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;

import CLIENT.util.ArquivoUtil;
import CLIENT.util.StringUtil;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * Robô que realiza o etiquetamento simples dos processos.
 * 1ª Etapa do processo de SJR (coloca as etiquetas de 1 ou 2 juizado)
 * conforme bairros de origem do processo
 * 
 * @author William Sodré
 * @TJMA
 */
public class TriagemSjrPorEndereco extends TriagemSimples_Page {

	private Integer numeroJuizado = null;
	
	/**
	 * Possíveis termos considerados para identificar a parte autora do processo.
	 */
	private static final List<String> TERMOS_PARTE_AUTORA = Stream
			.of("AUTOR", "DEMANDANTE", "EXEQUENTE", "RECLAMANTE", "REQUERENTE", "VÍTIMA","ORDENANTE", "DEPRECANTE")
			.collect(Collectors.toList());

	/**
	 * Xpath do elemento HTML referente à parte autora de um processo (encontrado
	 * nos autos, ao expandir o painel de "Mais Detalhes")
	 */
	private static String XPATH_PARTE_AUTORA_PROCESSO;

	private String listaBairrosJuizado1;

	private String listaBairrosJuizado2;
	
	
	public TriagemSjrPorEndereco(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}
	
	/**
	 * Retorna o XPath do elemento HTML referente à parte autora de um processo
	 * (encontrado nos autos, ao expandir o painel de "Mais Detalhes")
	 * 
	 * @return
	 */
	private String getXPathParteAutoraProcesso() {
		if (XPATH_PARTE_AUTORA_PROCESSO == null) {
			String xPathTermo = "contains(text(), '(%s)')";
			String xPathTermosParteAutora = String.format(xPathTermo, TERMOS_PARTE_AUTORA.get(0));
			for (int i = 1; i < TERMOS_PARTE_AUTORA.size(); i++) {
				xPathTermosParteAutora = xPathTermosParteAutora
						.concat(" or ")
						.concat(String.format(xPathTermo, TERMOS_PARTE_AUTORA.get(i)));
			}
			
			XPATH_PARTE_AUTORA_PROCESSO = String.format(
					"//html/body/div/div[1]/div/form/ul/li/ul/li/div/table/tbody//a/span[%s]/parent::a", 
					xPathTermosParteAutora);
		}
		
		return XPATH_PARTE_AUTORA_PROCESSO;
	}

	@Override
	protected void validarCamposObrigatorios() throws AutomacaoException {
		super.validarCamposObrigatorios();
		
		boolean paramNaoInformado = Stream.of(getParametros().getArquivoBairrosJuizado1(),
				getParametros().getArquivoBairrosJuizado2(),
				getParametros().getAtribuirEtiquetaJuizado1(),
				getParametros().getAtribuirEtiquetaJuizado2() 
		).anyMatch(p -> p == null);
		
		if (paramNaoInformado) {
			throw new AutomacaoException("Todos os seguintes parâmetros devem ser informados nesta automação - "
					+ "arquivoBairrosJuizado1, atribuirEtiquetaJuizado1, arquivoBairrosJuizado2, atribuirEtiquetaJuizado2");
		}
		
		carregarBairros();
	}


	private void carregarBairros() {
		String arquivoBairrosJuizado1 = getParametros().getArquivoBairrosJuizado1();
		setListaBairrosJuizado1(ArquivoUtil.readTextFile(arquivoBairrosJuizado1, true).toString());
		setListaBairrosJuizado1(StringUtil.lowerCaseUnaccent(getListaBairrosJuizado1()).trim());
		
		String arquivoBairrosJuizado2 = getParametros().getArquivoBairrosJuizado2();
		setListaBairrosJuizado2(ArquivoUtil.readTextFile(arquivoBairrosJuizado2, true).toString());
		setListaBairrosJuizado2(StringUtil.lowerCaseUnaccent(getListaBairrosJuizado2()));
	}

	@Override
	protected void antesExecutar(Processo processo) {
		super.antesExecutar(processo);
		setNumeroJuizado(null);
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		abrirDetalhesProcesso();
		
		abrirPainelMaisDetalhesProcesso();
		
		abrirDetalhesParteAutora();
		
		identificarEnderecoParteAutora();
		
		fecharDetalhesParteAutora();
		
		return getNumeroJuizado() != null;
	}


	protected void abrirDetalhesParteAutora() throws AutomacaoException, InterruptedException { 
		String xPathParteAutoraProcesso = getXPathParteAutoraProcesso();
		
		if (elementoExiste(By.xpath(xPathParteAutoraProcesso))) {
			System.out.println("Parte autora do processo encontrada. Acessando os dados de endereço...");
			clicar(xPathParteAutoraProcesso, 0, 0000);
			
		} else {
			StringBuilder msgErro = new StringBuilder(
					"------ A parte autora do processo não foi encontrada. Espera-se que ela esteja identificada no polo ativo por um dos seguintes termos:");
			
			TERMOS_PARTE_AUTORA.stream().findFirst().ifPresent(t -> msgErro.append(" " + t));
			TERMOS_PARTE_AUTORA.stream().skip(1).forEach(t -> msgErro.append(", " + t));
			msgErro.append(".");
			throw new AutomacaoException(msgErro.toString());
		}
		
		alternarParaDetalhes();
	}

	protected void fecharDetalhesParteAutora() throws AutomacaoException, InterruptedException {
		fecharJanelaDetalhes();
	}

	private void identificarEnderecoParteAutora() throws AutomacaoException {
		if (!isMunicipioSaoJoseRibamar()) {
			System.out.println("------ O município cadastrado no endereço não é \"São José de Ribamar\"");
			
		} else {
			String bairroEndereco = obterTexto("//table[@id=\"enderecoPessoaGridList\"]/tbody/tr/td[4]/span/div");
			if ( StringUtil.isNotEmpty(bairroEndereco) ) { 
				String bairroFormatado = StringUtil.lowerCaseUnaccent(bairroEndereco);
				
				if (isBairroJuizado(bairroFormatado, getListaBairrosJuizado1())) {
					setNumeroJuizado(1);
					getParametros().setAtribuirEtiqueta( getParametros().getAtribuirEtiquetaJuizado1() );
					System.out.println(String.format("++++++ O bairro de endereço (%s) está na lista do 1º Juizado", bairroEndereco));
				} else if (isBairroJuizado(bairroFormatado, getListaBairrosJuizado2())) {
					setNumeroJuizado(2);
					getParametros().setAtribuirEtiqueta( getParametros().getAtribuirEtiquetaJuizado2() );
					System.out.println(String.format("++++++ O bairro de endereço (%s) está na lista do 2º Juizado", bairroEndereco));
				} else {
					System.out.println(String.format("------ O bairro de endereço (%s) NÃO ESTÁ na lista de nenhum dos Juizados", bairroEndereco));
				}
			}
			
		}
	}

	
	private boolean isBairroJuizado(String bairro, String bairrosJuizado) {
		if (bairrosJuizado.indexOf(" \n" + bairro + " \n") != -1) {
			return true;
		} else {
			return false;
		}
	}


	protected boolean isMunicipioSaoJoseRibamar() throws AutomacaoException {
		String xPathMunicipio = "//table[@id=\"enderecoPessoaGridList\"]/tbody/tr/td[5]/span/div";
		String municipio = obterTexto(xPathMunicipio);
		
		boolean isSjr = false;
		if ( StringUtil.isNotEmpty(municipio) ) {
			String municSjr = "São José de Ribamar";
			if (StringUtil.equalsIgnoreCaseAccent(municSjr, municipio)) {
				isSjr = true;
			}
		}
		
		return isSjr;
	}

	public Integer getNumeroJuizado() {
		return numeroJuizado;
	}

	public void setNumeroJuizado(Integer numeroJuizado) {
		this.numeroJuizado = numeroJuizado;
	}

	public String getListaBairrosJuizado1() {
		return listaBairrosJuizado1;
	}

	public void setListaBairrosJuizado1(String listaBairrosJuizado1) {
		this.listaBairrosJuizado1 = listaBairrosJuizado1;
	}

	public String getListaBairrosJuizado2() {
		return listaBairrosJuizado2;
	}

	public void setListaBairrosJuizado2(String listaBairrosJuizado2) {
		this.listaBairrosJuizado2 = listaBairrosJuizado2;
	}
}
