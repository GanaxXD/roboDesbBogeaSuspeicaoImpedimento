package tjma.PAGE.pje215.geral;

import org.openqa.selenium.By;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import tjma.PAGE.pje215.PaginaBasePJE;

/**
 * Robô que realiza o protocolamento de um processo.
 * 
 * @autor William Sodré
 * @TJMA
 */
public class ProtocolarProcesso_Page extends PaginaBasePJE {
	
	private static final int QTD_MAX_PROCESSOS = 10;
	
	private Integer qtdProcessos;
	
	public ProtocolarProcesso_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return true;
	}
	
	@Override
	protected void validarCamposObrigatorios() throws AutomacaoException {
		super.validarCamposObrigatorios();
		
		try {
			setQtdProcessos( Integer.valueOf(getParametros().getQtdProcessos()) );
		} catch (NumberFormatException e) {
			throw new AutomacaoException("O parâmetro {qtdProcessos} deve ser um número inteiro válido!");
		}
		if (getQtdProcessos() == null || getQtdProcessos() < 0) {
			System.out.println("Parâmetro {qtdProcessos} ajustado automaticamente para o valor 1.");
			setQtdProcessos(1);
		}
		if (getQtdProcessos() > QTD_MAX_PROCESSOS) {
			throw new AutomacaoException( 
				String.format("Por motivo de segurança, o parâmetro {qtdProcessos} não pode ser maior que %d.", QTD_MAX_PROCESSOS));
		}
	}

	@Override
	public void executarProcedimento() throws AutomacaoException {
		
		// FIXME: eliminar try catch pra lançar pra fora
		try {
			
			for (int i = 0; i < getQtdProcessos(); i++) {
				
				/* Seleção da opção no menu lateral */
				clicar("//a[@class=\"botao-menu\"]", 1, 1000);
				clicar("//div[@class=\"nivel nivel-aberto\"]/ul[1]/li[2]", 1, 1000);
				clicar("//div[@class=\"nivel nivel-aberto\"]/ul[1]/li[1]", 1, 1000);
				
				/* Dados iniciais */
				selecionar("//select[@id='processoTrfForm:classeJudicial:classeJudicialjurisdicaoComboDecoration:classeJudicialjurisdicaoCombo']",
						parametros.getSecao(), 1, 1000);
				selecionar("//select[@id='processoTrfForm:classeJudicial:classeJudicialComboClasseJudicialDecoration:classeJudicialComboClasseJudicial']",
						parametros.getClasseJudicial(), 1, 2000);
				clicar("//input[@id=\"processoTrfForm:save\"]", 1, 1000);
				
				/* Assuntos */
				clicar("//a[starts-with(@id, 'r_processoAssuntoListList:0:')]", 1, 5000);
				
				/* Adição das partes do polo ativo e passivo */
				clicar("//td[@id=\"tabPartes_lbl\"]", 1, 2000);
				
				clicar("//span[@id=\"addParteA\"]", 1, 2000);
				String poloAtivoTipo = getParametros().getPoloAtivoTipo();
				if (poloAtivoTipo != null) {
					selecionar("//div[@id=\"divTipoPartePolo\"]//select[1]", poloAtivoTipo, 1, 2000);
				}
				digitar("//input[@id=\"preCadastroPessoaFisicaForm:preCadastroPessoaFisica_nrCPFDecoration:preCadastroPessoaFisica_nrCPF\"]", 
						getParametros().getPoloAtivoCpf(), 1, 2000);
				clicar("//input[@id=\"preCadastroPessoaFisicaForm:pesquisarDocumentoPrincipal\"]");
				clicar("//input[@id=\"preCadastroPessoaFisicaForm:btnConfirmarCadastro\"]", 1, 5000);
				clicar("//input[@id=\"formInserirParteProcesso:btnInserirParteProcesso\"]", 1, 5000);
	
				clicar("//span[@id=\"addParteP\"]", 1, 2000);
				String poloPassivoTipo = getParametros().getPoloPassivoTipo();
				if (poloPassivoTipo != null) {
					selecionar("//div[@id=\"divTipoPartePolo\"]//select[1]", poloPassivoTipo, 1, 2000);
				}
				digitar("//input[@id=\"preCadastroPessoaFisicaForm:preCadastroPessoaFisica_nrCPFDecoration:preCadastroPessoaFisica_nrCPF\"]", 
						getParametros().getPoloPassivoCpf(), 1, 2000);
				clicar("//input[@id=\"preCadastroPessoaFisicaForm:pesquisarDocumentoPrincipal\"]");
				clicar("//input[@id=\"preCadastroPessoaFisicaForm:btnConfirmarCadastro\"]", 1, 5000);
				clicar("//input[@id=\"formInserirParteProcesso:btnInserirParteProcesso\"]", 1, 5000);
				
				/* Inclusão de petições e documentos */ 
				clicar("//td[@id=\"novoAnexo_lbl\"]");
				clicar("//input[@value=\"Salvar\"]", 1, 3000);
				clicar("//input[@id=\"btn-assinador\"]", 1, 3000);
				
				/* Protocolar inicial */
				clicar("//td[@id=\"informativo_lbl\"]", 1, 2000);
				String competencia = getParametros().getCompetencia();
				if (competencia != null) {
					selecionar("//select[contains(@id, ':comboConflitoCompetencia')]", competencia, 1, 2000);
				} else {
					selecionar("//select[contains(@id, ':comboConflitoCompetencia')]", 1, 1, 2000);
				}
				clicar("//input[starts-with(@id, 'formBotoesAcao:btnProtocolar')]", 1, 1000);
				
				/* Um clique adicional, caso apareça um modal de confirmação para casos especiais
				 * (plantão judiciário, etc) */
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					throw new AutomacaoException(e.getMessage());
				}
				if (elementoExiste(By.xpath("//input[@id=\"formBotoesAcao:btnProsseguir\"]"))) {
					clicar("//input[@id=\"formBotoesAcao:btnProsseguir\"]", 1, 1000);
				}
			}
			
		} catch (AutomacaoException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
	}

	@Override
	protected void realizarTarefa(Processo processo) throws InterruptedException, AutomacaoException {
	}

	public Integer getQtdProcessos() {
		return qtdProcessos;
	}

	public void setQtdProcessos(Integer qtdProcessos) {
		this.qtdProcessos = qtdProcessos;
	}
}
