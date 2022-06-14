package PAGE.pje21.justicaComum.CI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class AtoMagistradoMinutar_Page extends PainelTarefasPJE {

	public AtoMagistradoMinutar_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	public void iniciar() throws AutomacaoException, InterruptedException {

		try {
			validarCamposObrigatorios();
			inicializarDriver();
			realizarLogin();
			selecionarPerfil();

			int count = 1;
			for (AtoJudicialEtiqueta lAtoJudicialEtiqueta : getParametros().getAtosJudiciais()) {

				try {
					criarLog(lAtoJudicialEtiqueta.getEtiqueta());
					escreverLog("Iniciando o procedimento com a etiqueta " + lAtoJudicialEtiqueta.getEtiqueta() + " - ("
							+ ((++count) + 1) + "/" + getParametros().getAtosJudiciais().length + " etiquetas)");

					selecionarTarefa(getParametros().getTarefa(), true, lAtoJudicialEtiqueta.getEtiqueta());
					executarProcedimento(lAtoJudicialEtiqueta);

				} catch (AutomacaoException ae) {

					printarTela(lAtoJudicialEtiqueta.getEtiqueta(), "ERROS", ae);
					criarLog("Ocorreu um erro durante o procedimento referente a etiqueta "
							+ lAtoJudicialEtiqueta.getEtiqueta() + " \n" + ae.getMessage(), obterArquivoLog());

				} catch (Exception e) {

					throw new AutomacaoException("Ocorreu um erro durante o procedimento referente a etiqueta "
							+ lAtoJudicialEtiqueta.getEtiqueta() + "\n" + e.getMessage());
				}

			}

		} catch (AutomacaoException ae) {
			criarLog("\nProcedimento nao realizado: " + ae.getMessage(), obterArquivoLog());

		} finally {
			salvarLog();
			finalizarDriver();
		}

	}
	/*
	 * protected void selecionarTarefa(AtoJudicialEtiqueta lAtoJudicialEtiqueta)
	 * throws AutomacaoException { try {
	 * 
	 * atualizarPagina();
	 * 
	 * clicar("(//a[@title='Tarefas'])|( //span[text()[contains(.,'Tarefas')]]/preceding-sibling::i)"
	 * , 30, 5000);
	 * 
	 * filtrarEtiqueta(true, lAtoJudicialEtiqueta.getEtiqueta());
	 * 
	 * clicar("//span[text()[contains(.,'" + getParametros().getTarefa() + "')]]",
	 * 30, 5000);
	 * 
	 * Thread.sleep(5000);
	 * 
	 * } catch (Exception e) {
	 * 
	 * throw new AutomacaoException("Nenhum processo foi encontrado na tarefa: " +
	 * getParametros().getTarefa() + " com a etiqueta " +
	 * lAtoJudicialEtiqueta.getEtiqueta() + ". \n" + e.getMessage());
	 * 
	 * }
	 * 
	 * }
	 */

	public void executarProcedimento(AtoJudicialEtiqueta lAtoJudicialEtiqueta) throws AutomacaoException {
		try {

			List<Processo> nProcessos = new ArrayList<Processo>();
			if (getListaParticionada() != null && getListaParticionada().size() > 0) {
				nProcessos = getListaParticionada();

			} else {
				nProcessos = obterProcessosTarefa();

			}

			for (int i = 0; i < nProcessos.size(); i++) {

				try {
					String teste = nProcessos.get(i).getNumeroProcesso();

					escreverLog("\n(" + (i + 1) + "/" + nProcessos.size() + ")");
					escreverLog(" Iniciando procedimento do processo: " + teste + " etiqueta "
							+ lAtoJudicialEtiqueta.getEtiqueta());
					nProcessos.get(i).setAtoJudicialEtiqueta(lAtoJudicialEtiqueta);
					realizarTarefa(nProcessos.get(i));
					criarLog(nProcessos.get(i),
							"cuja etiqueta atribuída pelo magistrado foi '" + lAtoJudicialEtiqueta.getEtiqueta()
									+ "' teve o modelo de minuta '" + lAtoJudicialEtiqueta.getModeloAto()
									+ "' atribuído juntamente com a movimentação processual '"
									+ lAtoJudicialEtiqueta.getMovimento() + " ("
									+ lAtoJudicialEtiqueta.getCodMovimento() + ")'");

					Thread.sleep(5000);

				} catch (WebDriverException we) {
					printarTela(nProcessos.get(i).getNumeroProcessoFormatado(), "ERROS", we);
					we.printStackTrace();
					break;

				} catch (AutomacaoException ae) {
					printarTela(nProcessos.get(i).getNumeroProcessoFormatado(), "ERROS", ae);
					escreverLog("\nOcorreu um erro com o processo " + nProcessos.get(i).getNumeroProcessoFormatado()
							+ " \n" + ae.getMessage());
				} catch (Exception e) {
					printarTela(nProcessos.get(i).getNumeroProcessoFormatado(), "ERROS", e);

					escreverLog("\nException:Ocorreu um erro com o processo "
							+ nProcessos.get(i).getNumeroProcessoFormatado() + " \n" + e.getMessage());

				}
			}

		} catch (AutomacaoException ae) {
			criarLog("\nProcedimento nao realizado: " + ae.getMessage(), obterArquivoLog());

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Nao foi possivel obter lista de processos na tarefa!" + e.getMessage());

		} finally {
			salvarLog();
		}

	}

	protected void realizarTarefa(Processo processo) throws AutomacaoException {
		try {
			alternarFrame(new String[] { "ngFrame" });
			String campoPesquisa = "//*[@id=\"inputPesquisaTarefas\"]";
			System.out.println();

			limparDigitacao(campoPesquisa);
			digitar(campoPesquisa, processo.getNumeroProcesso(), 10, 2001);

			clicar("//button[@title = 'Pesquisar']", 10, 2001);

			if (filtrouEtiqueta(true, processo.getAtoJudicialEtiqueta().getEtiqueta())) {

				clicar("//span[text()[contains(.,'" + processo.getNumeroProcesso() + "')]]", 15, 2000);

				String nomeEtiquetaFinal = processo.getAtoJudicialEtiqueta().getEtiqueta().replace("ROBO",
						getParametros().getAtribuirEtiqueta());

				atribuirEtiqueta(nomeEtiquetaFinal, getParametros().atribuirEtiqueta(), processo);

				movimentar();

				executar(processo);

				removerEtiqueta(processo.getAtoJudicialEtiqueta().getEtiqueta(), processo);

				assinar(processo.getAtoJudicialEtiqueta());
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
			getDriver().switchTo().defaultContent();
			alternarFrame(new String[] { "ngFrame" });
			limparDigitacao("//*[@id=\"inputPesquisaTarefas\"]", 10, 3001);

		}

		System.out.println("fim realizarTarefa....");

	}

	protected void selecionarExpedicao(AtoJudicialEtiqueta lAtoJudicialEtiqueta)
			throws AutomacaoException, InterruptedException {
		System.out.println("Selecionando a expedição...");
		try {
			alternarParaFramePrincipalTarefas();
			String[] expedicao = lAtoJudicialEtiqueta.getExpedicao();
			for (int i = 0; i < expedicao.length; i++) {
				// System.out.println(">>>>>"+obterQuantidadeElementos("//div/child::label[contains(text(),'"+expedicao[i]+"')]//parent::div//following-sibling::div//input"));
				clicar("//div/child::label[contains(text(),'" + expedicao[i]
						+ "')]//parent::div//following-sibling::div//input", 20, 2000);
				// clicar("//div/input//parent::div/preceding-sibling::div/child::label[contains(text(),'\"+expedicao[i]+\"')]",
				// 20, 2000);
			}

			if (lAtoJudicialEtiqueta.getVistaMP() != null && !lAtoJudicialEtiqueta.getVistaMP().equals("")) {
				limparDigitacao("//input[contains(@id, 'ci_paj_vistas_mpba_com_prazo')]", 15, 1000);
				digitar("//input[contains(@id, 'ci_paj_vistas_mpba_com_prazo')]", lAtoJudicialEtiqueta.getVistaMP(), 20,
						2000);

			}
			if (lAtoJudicialEtiqueta.getVistaDP() != null && !lAtoJudicialEtiqueta.getVistaDP().equals("")) {

				limparDigitacao("//input[contains(@id, 'ci_paj_vistas_dp_com_prazo')]", 15, 1000);
				digitar("//input[contains(@id, 'ci_paj_vistas_dp_com_prazo')]", lAtoJudicialEtiqueta.getVistaDP(), 20,
						2000);
			}

		} catch (Exception e) {

			throw new AutomacaoException(
					"Não foi possivel escolher a expedicao " + lAtoJudicialEtiqueta.getExpedicao());
		}
	}

	protected void selecionarMovimentoProcessual(AtoJudicialEtiqueta lAtoJudicialEtiqueta)
			throws AutomacaoException, InterruptedException {
		try {

			System.out.println("Selecionando a movimentação processual...");

			digitarMovimentacaoProcessual(lAtoJudicialEtiqueta.getCodMovimento());
			clicar("//input[@value='Pesquisar']", 15, 2000);

			String movimentacaoFormatada = lAtoJudicialEtiqueta.getMovimento() + " ("
					+ lAtoJudicialEtiqueta.getCodMovimento() + ")";
			clicar("//span[text()[contains(.,'" + movimentacaoFormatada + "')]]", 15, 2000);
		} catch (Exception e) {

			throw new AutomacaoException(e.getMessage());
		}
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		return true;
	}

	@Override
	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
		alternarParaFramePrincipalTarefas();

		selecionarTipoAto(processo.getAtoJudicialEtiqueta().getTipoAto());

		selecionarModeloAto(processo.getAtoJudicialEtiqueta().getModeloAto());

		salvar();

		selecionarExpedicao(processo.getAtoJudicialEtiqueta());

		selecionarMovimentoProcessual(processo.getAtoJudicialEtiqueta());

		removerEtiquetaPerfilAssessor(processo);

		encaminhar();

	}

	private void removerEtiquetaPerfilAssessor(Processo processo) throws AutomacaoException {

		if (getParametros().getPerfil() != null && getParametros().getPerfil().indexOf("Assessor") != -1) {
			removerEtiqueta(processo.getAtoJudicialEtiqueta().getEtiqueta(), processo);
		}

	}

	protected void encaminhar() throws InterruptedException, AutomacaoException {
		try {
			encaminharParaAssinatura();
		} catch (AutomacaoException ae) {

			if (getParametros().getMovimentarNoFinal() != null && getParametros().getMovimentarNoFinal().length > 0) {
				movimentar(getParametros().getMovimentarNoFinal()[0]);
			} else {
				movimentar("Encaminhar para validação do magistrado");
			}

		}
	}

	protected void assinar(AtoJudicialEtiqueta lAtoJudicialEtiqueta) throws AutomacaoException, InterruptedException {

		if (lAtoJudicialEtiqueta.getAssinar() != null && lAtoJudicialEtiqueta.getAssinar().equalsIgnoreCase("sim")) {
			super.assinar();
		}
	}

}