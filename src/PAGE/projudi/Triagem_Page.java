package PAGE.projudi;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Alert;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;


/**
 * 
 * @autor Leonardo Ribeiro de Oliveira
 */
public class Triagem_Page extends PainelUsuarioPROJUDI {


	Map<String, StringBuffer> mapaEtiquetas = new HashMap<String, StringBuffer>();

	public Triagem_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);

	}

	protected void realizarTarefa(Processo processo) throws InterruptedException, AutomacaoException {

		System.out.println("realizarTarefa.......");
		try {

			Thread.sleep(2000);

			if (deveProsseguir(processo)) {

				executar(processo);

			} else {
				criarLog(processo,
						"Operacao não realizada!");
			}

			Thread.sleep(2000);

		} catch (AutomacaoException ae) {
			throw ae;

		} catch (Exception e) {
			e.printStackTrace();
			criarLog(processo, "\nOcorreu um erro:" + processo.getNumeroProcesso() + " >" + e.getMessage());

		}

		System.out.println("fim executar....");

	}

	protected void selecionarTarefa() throws AutomacaoException {
		try {
			buscarProcesso();
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a tarefa: " + getParametros().getTarefa());
		}

	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		System.out.println("Acessando o processo " + processo.getNumeroProcessoFormatado());

		navegateTo(processo.getLinkDetalheProcesso());

		clicar("//a/strong[text()[contains(.,'Modificar Dados')]]", 30, 3000);

		atribuirLocalizadorProjudi(processo, getParametros().getAtribuirEtiqueta());
		
		clicar("//input[@name = 'Modificar']", 20, 2000);
		
		criarLog("Procedimento realizado com sucesso!!\n", obterArquivoLog());

	}

	private void atribuirLocalizadorProjudi(Processo processo, String localizador) throws AutomacaoException {

		try {
			selecionar("//select[@id='codTipoLocalizador']", localizador, 20, 2000);
			
			//clicar("//input[@value = 'Adicionar']", 20, 2000);
			
			clicar("//a[@href = 'javascript: adicionaLocalizador();']", 20, 2000);
			
			try {
				Thread.sleep(2000);
				Alert alert = driver.switchTo().alert();
				alert.accept();
			}catch(Exception e) {
				//- não faz nada
			}
			
			criarLog("O localizador "+ localizador + " foi adicionado ao processo "+ processo.getNumeroProcessoFormatado(), obterArquivoLog());
			
			
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar o procedimento no processo " + processo.getNumeroProcesso());
		}
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		return true;
	}

}

