package tjma.PAGE.pje215.justicaComum.CI;

import org.openqa.selenium.WebDriverException;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import tjma.PAGE.pje215.geral.CitacaoIntimacao_Page;

public class Intimacao_CI extends CitacaoIntimacao_Page {

	public Intimacao_CI(Parametros parametro) throws AutomacaoException {
		super(parametro);
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
			// int qtdProcessos = obterQuantidadeElementos("//p-datalist/div/div/ul/li");

			if (filtrouEtiqueta(getParametros().isFiltrarEtiqueta(), getParametros().getFiltrarEtiqueta())
					&& deveProsseguir(processo)) {

				fecharJanelaDetalhes();
				alternarFrame(new String[] { "ngFrame" });
				clicar("//span[text()[contains(.,'" + processo.getNumeroProcessoFormatado() + "')]]", 15, 2000);

				/* [TJMA] Código comentado porque disparava NullPointerException */
//				if (processo.getAtoJudicialEtiqueta().getEtiqueta().indexOf("ROBO") != -1) {
//					String nomeEtiquetaFinal = processo.getAtoJudicialEtiqueta().getEtiqueta().replace("ROBO",
//							getParametros().getAtribuirEtiqueta());
//					atribuirEtiqueta(nomeEtiquetaFinal, getParametros().atribuirEtiqueta(), processo);
//
//				} else {
//					atribuirEtiqueta(getParametros().getAtribuirEtiqueta(), getParametros().atribuirEtiqueta(),
//							processo);
//				}

				movimentar();

				executar(processo);

				if (getParametros().getFiltrarEtiqueta() != null && !getParametros().getFiltrarEtiqueta().equals("")) {
					removerEtiqueta(getParametros().getFiltrarEtiqueta(), processo);
				} else {
					removerEtiqueta(getParametros().getAtribuirEtiqueta(), processo);
				}

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
		}

		System.out.println("fim realizarTarefa....");

	}

}
