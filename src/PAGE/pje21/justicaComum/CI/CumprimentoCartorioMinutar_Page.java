package PAGE.pje21.justicaComum.CI;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class CumprimentoCartorioMinutar_Page extends AtoMagistradoMinutar_Page {

	public CumprimentoCartorioMinutar_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}

	@Override
	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
		alternarParaFramePrincipalTarefas();

		selecionarExpedicao(processo.getAtoJudicialEtiqueta());

		movimentar("Prosseguir nas tarefas selecionadas");
		
		alternarParaFramePrincipalTarefas();
		
		selecionarTipoAto(processo.getAtoJudicialEtiqueta().getTipoAto());

		selecionarModeloAto(processo.getAtoJudicialEtiqueta().getModeloAto());
		
		Thread.sleep(5000);
		
		salvar();

		selecionarMovimentoProcessual(processo.getAtoJudicialEtiqueta());

		preencherExpediente(processo.getAtoJudicialEtiqueta());

		movimentar("Encaminhar para assinatura");

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

			preencherComplementos(lAtoJudicialEtiqueta);

		} catch (Exception e) {

			throw new AutomacaoException(e.getMessage());
		}

	}

	protected void preencherComplementos(AtoJudicialEtiqueta lAtoJudicialEtiqueta)
			throws AutomacaoException, InterruptedException {
		try {

			System.out.println("preenchendo o complemento ...");

			clicar("//a[@title='Preencher complementos']", 20, 3000);

			selecionar(
					"//html/body/div[5]/div/div[4]/form/div[2]/div[2]/div/div[2]/table/tbody/tr[2]/td/div/div[1]/div[2]/div[1]/span/div/select",
					lAtoJudicialEtiqueta.getTipoAto(), 20, 3000);

			//clicar("//input[@value='OK']", 20, 3000);
			clicar("//html/body/div[5]/div/div[4]/form/div[2]/div[2]/div/div[2]/table/tbody/tr[2]/td/div/div[2]/input[1]", 20, 3000);
		} catch (Exception e) {

			throw new AutomacaoException(e.getMessage());
		}

	}

	private void preencherExpediente(AtoJudicialEtiqueta lAtoJudicialEtiqueta)
			throws InterruptedException, AutomacaoException {

		try {

			clicar("//html/body/div[5]/div/div[4]/form/div/div[2]/span[3]/div/div/div[1]", 20, 3000);

			String[] polos = lAtoJudicialEtiqueta.getPolos();
			for (int i = 0; i < polos.length; i++) {
				clicar("//input[@value='" + polos[i] + "']");
			}
			Thread.sleep(2000);
			
			int qtdPartes = obterQuantidadeElementos("//tbody[contains(@id, 'tableDestinatarios:tb')]/tr");
			if (qtdPartes > 1 || qtdPartes==0) {
				throw new AutomacaoException(
						"A quantidade de partes para expedição de mandado deverá ser apenas uma. Humano deverá verificar.");
			}else {
				clicar("//th[text() = 'Central de Mandados']", 20, 3000);
			}
			
			limparDigitacao("//input[contains(@id, ':tableDestinatarios:prazoGeralInput')]");

			digitar("//input[contains(@id, ':tableDestinatarios:prazoGeralInput')]", lAtoJudicialEtiqueta.getPrazo(), 20,
					3000);

			Thread.sleep(3000);

			selecionar("//select[@title='Do cumprimento da diligência']", lAtoJudicialEtiqueta.getTipo(), 20, 3000);

			clicar("//input[@value = 'Gravar dados do(s) expediente(s)']", 20, 3000);

		} catch (Exception e) {

			throw new AutomacaoException(e.getMessage());
		}
	}

}