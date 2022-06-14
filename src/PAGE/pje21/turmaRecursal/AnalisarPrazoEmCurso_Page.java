package PAGE.pje21.turmaRecursal;

import org.openqa.selenium.By;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class AnalisarPrazoEmCurso_Page extends BaixaTransitoJulgadoPJE_Page {

	public AnalisarPrazoEmCurso_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);

	}

	public void iniciar() throws AutomacaoException, InterruptedException {

		validarCamposObrigatorios();
		inicializarDriver();

		navegateTo(getParametros().getUrlDiario());
		
		limparDigitacao("//input[@name = 'tmp.diario.dt_inicio']", 2000, 15);
		digitar("//input[@name = 'tmp.diario.dt_inicio']", "24/06/2020", 2000, 15);
		
		limparDigitacao("//input[@name = 'tmp.diario.dt_fim']", 2000, 15);
		digitar("//input[@name = 'tmp.diario.dt_fim']", "01/07/2020", 2000, 15);
		
		limparDigitacao("//input[@name = 'tmp.diario.pal_chave']", 2000, 15);
		digitar("//input[@name = 'tmp.diario.pal_chave']", "8000675-05.2019.8.05.0272", 2000, 15);
		
		clicar("//input[@name='tmp.bntEnviar']", 2000, 15);
		
		Thread.sleep(20000);
		
		boolean existePublicacao = false;
		try {
			existePublicacao = ElementoClicavel(
					By.xpath("//a[contains(text(),'"+getParametros().getSecaoDiario()+"')]"));

		} catch (Exception e) {
			existePublicacao = false;
		}
		
		if(existePublicacao) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> OK");
		} else {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> NaO ACHOU");
		}
		
		finalizarDriver();

	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		try {
			alternarParaFramePrincipalTarefas();

			escolherTarefaCertificarDecurso();

			movimentar("01 - Prosseguir na(s) tarefa(s) selecionada(s)");

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar o procedimento do processo " + processo.getNumeroProcesso());
		}
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		return true;
	}

}