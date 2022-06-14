package PAGE.projudi.turmasRecursais;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import DAO.BaixaProcessualDao;
import MODEL.Acao;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import RN.BaixaProcessualRN;

public class DispensarProcessosTurmaProjudi_Page extends AnaliseTransitoJulgado {

	public DispensarProcessosTurmaProjudi_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException {
		List<Processo> listaProcessos = new ArrayList<Processo>();
		try {
			carregarProcessos();

			criarLog(getMapaProcessos().toString(), obterArquivoLog());

			for (Processo processo : getMapaProcessos().values()) {

				if (processo.getAcao().equals(Acao.DISPENSAR_BAIXA_DEFINITIVA)
						|| processo.getAcao().equals(Acao.DISPENSAR_NAO_EXISTENCIA_ACORDAO)
						|| processo.getAcao().equals(Acao.DISPENSAR_BAIXA_DEFINITIVA)
						|| processo.getAcao().equals(Acao.DISPENSAR_NEM_TODAS_PARTES_TEM_DECURSO)) {

					// -Acao.HUMANO_ANALISAR
					listaProcessos.add(processo);
				}

			}

		} catch (Exception e) {
			throw new AutomacaoException("\nErro ao obterLista de Processos no banco: " + e.getMessage());
		}

		criarLog("\n\n\n\n\n******************************************************************************************"
				+ listaProcessos.toString(), obterArquivoLog());
		return listaProcessos;

	}

	protected void executar(Processo processo) throws AutomacaoException, InterruptedException {
		System.out.println("EXECUTAR>>> " + processo.getNumeroProcesso());

		Processo p = mapaProcessos.get(processo.getNumeroProcesso());

		int quantidadeLinhas = obterQuantidadeElementos("//form[@name='formIntimacoes']/table/tbody/tr");
		if (getParametros().getQtdLinhasGrid().equals(String.valueOf(quantidadeLinhas))) {
			clicar("//a[contains(text(),'Dispensar') and contains(@href, 'desnecessarioCertificarCartorio')]", 10,
					3000);
			Thread.sleep(2000);
			Alert alert = driver.switchTo().alert();
			alert.accept();

		} else {
			clicar("//input[@name='checkMarcaTodos']", 10, 3000);
			clicar("//input[@value='Dispensar para Marcados']", 10, 3000);
		}

		criarLog(processo, " Ação: " + p.getAcao() + " Procedimento realizado com sucesso!");

		Thread.sleep(5000);

		if (elementoExiste(
				By.xpath("//*[text()[contains(.,'Ocorreu um erro ao executar a funcionalidade solicitada')]]"))) {
			getDriver().navigate().back();
			Thread.sleep(2000);
			getDriver().navigate().back();
			Thread.sleep(10000);
		}

		System.out.println("FIM PROCEDIMENTO!!!! " + processo.getNumeroProcesso());

	}

	protected boolean deveProsseguir(Processo processo)
			throws InterruptedException, AutomacaoException {

		validaProcessoTela(processo);
		Processo p = mapaProcessos.get(processo.getNumeroProcesso());
		System.out.println("Processo: " + processo.getNumeroProcesso() + " Ação: " + p.getAcao());

		if (p.getAcao().equals(Acao.HUMANO_ANALISAR)) {

			if (elementoExiste(By.xpath("//*[text()[contains(.,'Movimentação não permitida')]]"))) {
				p.setAcao(Acao.DISPENSAR_MOVIMENTACAO_NAO_PERMITIDA);
				return true;
			}

		} else if (p.getAcao().equals(Acao.DISPENSAR_BAIXA_DEFINITIVA)
				|| p.getAcao().equals(Acao.DISPENSAR_NAO_EXISTENCIA_ACORDAO)
				|| p.getAcao().equals(Acao.DISPENSAR_BAIXA_DEFINITIVA)
				|| p.getAcao().equals(Acao.DISPENSAR_NEM_TODAS_PARTES_TEM_DECURSO)) {
			return true;

		}

		criarLog(processo, " Procedimento não realizado para o processo " + processo.getNumeroProcesso());
		return false;

	}

}
