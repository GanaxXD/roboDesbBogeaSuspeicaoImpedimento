package PAGE.pje21.geral;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PaginaBasePJE;

/**
 * Robô que realiza a juntada de documentos no processo em lote
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 * 
 *       FIXME: esse robô não funciona caso seja configurado com o parâmetro
 *       "fonteDeDados: TAREFA". Isso ocorre porque, ao sobrescrever a seleção
 *       do perfil (no método local {@link #selecionarPerfil()}), ele direciona
 *       à página de consulta de processos, onde não é possível fazer a contagem do número
 *       de processos. Consequentemente, ao passar pelo método {@link #executarProcedimento()},
 *       dispara-se um {@link NullPointerException}. * 
 *       (William Sodré @TJMA)
 */
public class JuntarDocumentoProcesso_Page extends PaginaBasePJE {

	public JuntarDocumentoProcesso_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void realizarTarefa(Processo processo) throws InterruptedException, AutomacaoException {

		try {
			System.out.println(processo);
			alternarFrame(new String[] { "ngFrame", "frameConsultaProcessual" });

			limparProcesso();

			Thread.sleep(2000);

			preencherProcesso(processo);

			clicar("//input[contains(@id,'searchProcessos')]", 20, 2000);

			clicar("//a[text()[contains(.,'" + processo.getNumeroProcesso() + "')]]", 60, 2000);
			
			while(getDriver().getWindowHandles().size()==0) {
				Thread.sleep(1000);
					
			}
			alternarJanela();
			
			try {
				clicar("//a[contains(@id, 'divTimeLine')]//span[text()[contains(.,\"Petição Inicial\")]]", 20, 2000);
				// - Necessidade do codigo para resolver o problema do clique quando ultimo
				// documento juntado é pdf

			} catch (Exception e) {
				// - nao faz nada
				e.printStackTrace();
			}

			if (!verificaExistenciaCertidao()) {
				preencherJuntada(processo);

			} else {
				criarLog("Processo " + processo.getNumeroProcesso()
						+ " NaO teve a certidao juntada pois ja existia uma nos autos!", obterArquivoLog());
			}

		} catch (Exception e) {
			criarLog("Processo " + processo.getNumeroProcesso() + " NaO teve a certid�o juntada!", obterArquivoLog());
			throw new AutomacaoException(
					"Erro ao realizar procedimento de juntada de document " + getParametros().getTarefa());
		} finally {
			alternarJanela();
			fecharJanelaDetalhes();
			alternarFrame(new String[] { "ngFrame", "frameConsultaProcessual" });
			limparProcesso();
		}

	}

	private void preencherJuntada(Processo processo) throws AutomacaoException, InterruptedException {

		try {
			clicar("//a[@title='Menu']", 15, 3000);

			clicar("//a[@name='navbar:linkAbaIncluirPeticoes']", 15, 3000);

			selecionar("//select[@id='cbTDDecoration:cbTD']", getParametros().getTipoDocumento(), 15, 3000);

			limparDigitacao("//input[@id='ipDescDecoration:ipDesc']", 15, 3000);

			digitar("//input[@id='ipDescDecoration:ipDesc']", getParametros().getDescricaoDocumento(), 15, 3000);

			selecionar("//select[@id='modTDDecoration:modTD']", getParametros().getModeloDocumento(), 15, 3000);

			clicar("//input[@value='Salvar']", 15, 3000);

			clicar("//input[@value='Assinar documento(s)']", 15, 3000);

			Thread.sleep(10000);
			criarLog("Processo " + processo.getNumeroProcesso() + " teve a certidao juntada!", obterArquivoLog());

		} catch (Exception e) {
			throw new AutomacaoException(
					" Erro ao preencher a juntada no processo " + processo.getNumeroProcessoFormatado() + "\n");
		}
	}

	private boolean verificaExistenciaCertidao() throws InterruptedException, AutomacaoException {

		try {

			int qtdCitacoesSistema = obterQuantidadeElementos(
					"//a[contains(@id, 'divTimeLine')]//span[text()[contains(.,\""
							+ getParametros().getDescricaoDocumento() + "\")]]");

			if (qtdCitacoesSistema > 0) {
				criarLog("\nJa existe a certidao no processo!", obterArquivoLog());
				return true;
			}

		} catch (Exception e) {
				e.printStackTrace();
		}
		return false;

	}

	private void preencherProcesso(Processo processo_) throws AutomacaoException, InterruptedException {
		String processo = processo_.getNumeroProcesso();

		String sequencial = processo.substring(0, processo.indexOf('-'));
		String digitoVerificador = processo.substring(processo.indexOf('-') + 1, processo.indexOf('-') + 3);
		String ano = processo.substring(processo.indexOf('.') + 1, processo.indexOf('.') + 5);
		String tribunal = processo.substring(processo.indexOf('.') + 8, processo.indexOf('.') + 10);
		String orgao = processo.substring(processo.indexOf('.') + 11, processo.indexOf('.') + 15);

		digitar("//input[contains(@id,'numeroSequencial')]", sequencial, 15, 1000);
		digitar("//input[contains(@id,'numeroDigitoVerificador')]", digitoVerificador, 15, 1000);
		digitar("//input[contains(@id,'Ano')]", ano, 15, 1000);
		// digitar("//input[contains(@id,'labelTribunalRespectivo')]", tribunal);
		digitar("//input[contains(@id,'NumeroOrgaoJustica')]", orgao, 15, 1000);
	}

	private void limparProcesso() throws AutomacaoException, InterruptedException {
		limparDigitacao("//input[contains(@id,'numeroSequencial')]", 15, 1000);
		limparDigitacao("//input[contains(@id,'numeroDigitoVerificador')]", 15, 1000);
		limparDigitacao("//input[contains(@id,'Ano')]", 15, 1000);
		// limparDigitacao("//input[contains(@id,'labelTribunalRespectivo')]");
		// Thread.sleep(1000);
		limparDigitacao("//input[contains(@id,'NumeroOrgaoJustica')]", 15, 1000);
	}

	protected void selecionarPerfil() throws AutomacaoException {
		super.selecionarPerfil();
		alternarFrame(new String[] { "ngFrame" });
		try {

			clicar("//html/body/app-root/selector/div/div/div[1]/side-bar/nav/ul/li[9]/a/i", 15, 2000);

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a consulta avancada: " + e.getMessage());
		}

	}

	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		// TODO Auto-generated method stub

	}

}
