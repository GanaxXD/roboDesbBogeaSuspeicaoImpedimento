package PAGE.pje21.geral;

import org.openqa.selenium.WebDriverException;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * Robô que realiza o etiquetamento simples dos processos
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class TriagemSimples_Page extends PainelTarefasPJE {

	public TriagemSimples_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		
		if(processo.getEtiqueta()!=null && !processo.getEtiqueta().equals("")) {
			atribuirEtiqueta(processo.getEtiqueta());
			
			criarLog("Etiqueta " + processo.getEtiqueta() + " atribuída ao processo "
					+ processo.getNumeroProcessoFormatado(), obterArquivoLog());
			
		}
		
		
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		return true;

	}

	protected void realizarTarefa(Processo processo) throws AutomacaoException {
		System.out.println("realizarTarefa......" + processo.getNumeroProcessoFormatado());
		String campoPesquisa = "//input[@id='inputPesquisaTarefas']";

		try {
			alternarFrame(new String[] { "ngFrame" });

			limparDigitacao(campoPesquisa);

			digitar(campoPesquisa, processo.getNumeroProcessoFormatado(), 10, 2000);

			clicar("//button[@title = 'Pesquisar']", 10, 2000);
			Thread.sleep(6000);
			int qtdProcessos = obterQuantidadeElementos("//p-datalist/div/div/ul/li");

			if (filtrouEtiqueta(getParametros().isFiltrarEtiqueta(), getParametros().getFiltrarEtiqueta())
					&& deveProsseguir(processo)) {

				fecharJanelaDetalhes();
				alternarFrame(new String[] { "ngFrame" });
				clicar("//span[text()[contains(.,'" + processo.getNumeroProcessoFormatado() + "')]]", 15, 2000);

				String etiqueta = getParametros().getAtribuirEtiqueta();
				if (qtdProcessos > 1 && !processo.getNumeroRecursoInterno().equals("")) {
					etiqueta = getParametros().getAtribuirEtiqueta() + processo.getNumeroRecursoInterno();
				}

				atribuirEtiqueta(etiqueta, getParametros().atribuirEtiqueta(), processo);

				movimentar();

				executar(processo);

				removerEtiqueta(getParametros().getAtribuirEtiqueta(), processo);

				criarLog(processo, "Procedimento realizado com sucesso!");

			}

			Thread.sleep(2000);

		} catch (WebDriverException we) {
			we.printStackTrace();
			return;

		} catch (AutomacaoException ae) {
			throw ae;

		} catch (Exception e) {

			criarLog(processo, "\nOcorreu um erro: " + processo.getNumeroProcesso());

		} finally {
			fecharJanelaDetalhes();

			// getDriver().switchTo().defaultContent();
			// limparDigitacao("//*[@id=\"inputPesquisaTarefas\"]", 10, 3000);

		}

		System.out.println("fim realizarTarefa....");

	}

}
