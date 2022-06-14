package PAGE.pje21.turmaRecursal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import DAO.BaixaProcessualDao;
import MODEL.Acao;
import MODEL.Movimento;
import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;
import RN.BaixaProcessualRN;

/**
 * 
 * Robo que realiza a baixa dos processos que tiveram tr�nsito em julgado.
 * 
 * Regra: Processos que tiveram acordao proferido, seguido de intimacoes e o
 * prazo decorrido para todas as partes deverao ser baixados caso nao haja
 * peticionamento intermediario neste intervalo.
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class BaixaTransitoJulgadoPJE_Page extends PainelTarefasPJE {

	public BaixaTransitoJulgadoPJE_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);

	}

	
	/**
	 * Tive que sobrescrever o metodo por conta do comportamento padrao de fechar aba de detalhes
	 * */
	protected void realizarTarefa(Processo processo) throws AutomacaoException {
		System.out.println("realizarTarefa......");

		try {
			alternarFrame(new String[] {"ngFrame"});
			String campoPesquisa = "//*[@id=\"inputPesquisaTarefas\"]";

			Thread.sleep(2000);
			limparDigitacao(campoPesquisa);
			digitar(campoPesquisa, processo.getNumeroProcesso(), 20, 2000);

			clicar("//button[@title = 'Pesquisar']", 10, 2000);

			if (filtrouEtiqueta(getParametros().isFiltrarEtiqueta(), getParametros().getFiltrarEtiqueta()) && deveProsseguir(processo)) {

				clicar("//span[text()[contains(.,'" + processo.getNumeroProcesso() + "')]]", 15, 2000);

				atribuirEtiqueta(getParametros().getAtribuirEtiqueta(),getParametros().atribuirEtiqueta(), processo);

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
			alternar(0);
			getDriver().switchTo().defaultContent();
			limparDigitacao("//*[@id=\"inputPesquisaTarefas\"]", 10, 3000);

		}

		System.out.println("fim realizarTarefa....");

	}
	

	protected List<Processo> obterDadosBanco() throws AutomacaoException, InterruptedException {
		List<Processo> listaProcessos = new ArrayList<Processo>();
		try {
			BaixaProcessualRN rn = new BaixaProcessualRN(new BaixaProcessualDao(getParametros().getDbURL(),
					getParametros().getDbUser(), getParametros().getDbPass()));

			setMapaProcessos(rn.carregarProcessos(getParametros()));

			for (Processo processo : getMapaProcessos().values()) {
				System.out.println(
						"processo : " + processo.getNumeroProcessoFormatado() + " Acao: " + processo.getAcao());

				for (int i = 0; i < getParametros().getAcoes().length; i++) {
					if (getParametros().getAcoes()[i].equals(processo.getAcao().toString())) {
						listaProcessos.add(processo);
						break;
					}
				}

			}

		} catch (Exception e) {
			throw new AutomacaoException("\nErro ao obterLista de Processos no banco: " + e.getMessage());
		}

		criarLog(
				"\n\n\n\n\n******************************************************************************************"
						+ listaProcessos.toString() + "\n\nQuantidade de Processos: " + listaProcessos.size() + "\n",
				obterArquivoLog());
		return listaProcessos;

	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {
//- colocar scroll to tarefa
		
		try {
			
			if (getParametros().getTarefa().equals("(CCPC) Assinar decurso de prazo")) {

				assinar();

				movimentar("06 - Encaminhar para o primeiro grau");

				encerrarExpedientes();

				remeter();

				System.out.println("FINALIZADO.." + processo.getNumeroProcesso());

				criarLog("\n\nO Processo " + processo.getNumeroProcesso()
						+ " foi certificado o transito em julgado e encaminhado para a instancia de origem!\n\n",
						obterArquivoLog());

			} else {
				
				
				if (getParametros().getTarefa().equals("(CCPC) Processos com prazos decorridos")
						|| getParametros().getTarefa().equals("(CCPC) Processos com prazo em curso")
						|| getParametros().getTarefa().equals("(CCPC) Análise da Secretaria")) 
				{

					alternarParaFramePrincipalTarefas();

					escolherTarefaCertificarDecurso();

					movimentar("01 - Prosseguir na(s) tarefa(s) selecionada(s)");

					alternarParaFramePrincipalTarefas();
	
					selecionarTipoAto();
	
					selecionarModeloAto();
	
					salvar();
	
					encaminharParaAssinatura();
	
					assinar();
	
					movimentar("06 - Encaminhar para o primeiro grau");
	
					encerrarExpedientes();

				
				}else 
				if (getParametros().getTarefa().equals("(CCPC) Assinar decurso de prazo")) {
					
					alternarParaFramePrincipalTarefas();
					
					assinar();
					
					movimentar("06 - Encaminhar para o primeiro grau");
	
					encerrarExpedientes();
					
				}else 
				if (getParametros().getTarefa().equals("(CCPC) Certificar decurso de prazo")) {
					
					alternarParaFramePrincipalTarefas();
					
					selecionarTipoAto();
	
					selecionarModeloAto();
	
					salvar();
	
					encaminharParaAssinatura();
	
					assinar();
	
					movimentar("06 - Encaminhar para o primeiro grau");
	
					encerrarExpedientes();
					
				}else 
				if (getParametros().getTarefa().equals("(CCPC) Fechar prazos dos expedientes")) {
					
					
					try {
						encerrarExpedientes();
						movimentar("Prosseguir");
					}catch(Exception e) {
						e.printStackTrace();
						//- não faz nada
					}
					
				}
				
				//remeter();

				System.out.println("FINALIZADO.." + processo.getNumeroProcesso());

				criarLog("\n\nO Processo " + processo.getNumeroProcesso()
						+ " foi certificado o transito em julgado e encaminhado para a instancia de origem!\n\n",
						obterArquivoLog());

			}

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao realizar o procedimento do processo " + processo.getNumeroProcesso());
		}
	}
	
	

	private void remeter() throws AutomacaoException {
		try {

			alternarParaFramePrincipalTarefas();

			esperarElemento("//input[contains(@id,'retornar')]", 600);// - 10 minutos

			clicar("//select[contains(@id,'comboClasseMotivoRemessa')]", 15, 4000);

			selecionar("//select[contains(@id,'comboClasseMotivoRemessa')]", getParametros().getMotivoRemessa(), 15,
					4000);

			clicar("//input[contains(@id,'retornar')]", 15, 4000);

			try {

				esperarElemento("//input[@value='Confirmar']", 15);
				clicar("//input[@value='Confirmar']", 10, 3000);

			} catch (Exception e) {
				// - confirmar nao existe
			}

			esperarElemento("//a[text()[contains(., 'Remetidos ao primeiro grau')]]", 60);
		} catch (Exception e) {
			criarLog("\n\nO Processo foi certificado o transito mas n�o foi remetido. \n\n", obterArquivoLog());

		}
	}

	protected void encerrarExpedientes() throws AutomacaoException, InterruptedException {
		try {
			alternarParaFramePrincipalTarefas();

			if (elementoExiste(By.xpath("//input[@value='Encerrar expedientes selecionados']"))) {

				clicar("//input[contains(@id,'fechadoHeader')]", 15, 5000);

				clicar("//input[@value='Encerrar expedientes selecionados']", 15, 5000);

				Thread.sleep(2000);
				Alert alert = driver.switchTo().alert();
				alert.accept();

				movimentar("Prosseguir");

			}
		} catch (Exception e) {
			throw new AutomacaoException("Erro ao encerrar expedientes. " + e.getMessage());
		}

	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {

		if (getParametros().getTarefa().equals("(CCPC) Assinar decurso de prazo")) {
			return true;

		} else {

			Processo p = getMapaProcessos().get(processo.getNumeroProcesso());

			if (p.getAcao().equals(Acao.BAIXAR_PROCESSO)) {
				criarLog("\n\nO Processo " + processo.getNumeroProcesso() + " foi analisado pelo robo. Acao: "
						+ processo.getAcao() + "\n\n", obterArquivoLog());

				return true;
			} else if (p.getAcao().equals(Acao.DISPENSAR_NEM_TODAS_PARTES_TEM_DECURSO)) {

				if (verificarPublicacaoIntimacaoDiario(processo)) {
					criarLog(processo, "\n\nAcordao foi publicado no diario. " + processo.getNumeroProcesso()
							+ ". Prosseguindo com a baixa. \n\n");

					return true;

				} else {
					criarLog(processo,
							"\n\nAcordao nAo teve publicacao no diario. " + processo.getNumeroProcesso() + "\n\n");

					return false;
				}

			} else {

				criarLog(processo, "\n\nProcedimento não realizado para o processo " + processo.getNumeroProcesso()
						+ ". Acao: " + processo.getAcao() + "\n\n");
				return false;
			}

		}
	}

	protected boolean verificarPublicacaoIntimacaoDiario(Processo processo)
			throws InterruptedException, AutomacaoException {

		try {

			alternar(1);
			
			
			
			navegateTo(getParametros().getUrlDiario());
			
			clicar("//input[@name='tmp.bntReset']", 2000, 10);

			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			Calendar c = Calendar.getInstance();

			List<Movimento> col = processo.getMovimentos();
			Collections.sort(col);

			String dataInicioFormatada = "";
			String dataFimFormatada = "";
			for (Movimento movimento : processo.getMovimentos()) {
				System.out.println(processo.getNumeroProcessoFormatado() + " >>> " + movimento.getDataMovimentacao());
				if (movimento.getCodMovimentoCNJ().equals(parametros.getMovimentacaoIntimacao())) {

					Date dataInicio = sdf1.parse(movimento.getDataMovimentacao());
					dataInicioFormatada = sdf2.format(dataInicio);

					c.setTime(dataInicio);
					c.add(Calendar.DAY_OF_MONTH, 10);
					Date dataFim = c.getTime();
					dataFimFormatada = sdf2.format(dataFim);
					break;
				}

			}

			
			criarLog("\n\tProcesso:  " + processo.getNumeroProcessoFormatado() + "\t DataInicio: " + dataInicioFormatada
					+ "\t DataFim: " + dataFimFormatada, obterArquivoLog());
			
			limparDigitacao("//input[@name = 'tmp.diario.dt_inicio']", 2000, 15);
			digitar("//input[@name = 'tmp.diario.dt_inicio']", dataInicioFormatada, 2000, 15);

			limparDigitacao("//input[@name = 'tmp.diario.dt_fim']", 2000, 15);
			digitar("//input[@name = 'tmp.diario.dt_fim']", dataFimFormatada, 2000, 15);

			limparDigitacao("//input[@name = 'tmp.diario.pal_chave']", 2000, 15);
			digitar("//input[@name = 'tmp.diario.pal_chave']", processo.getNumeroProcessoFormatado(), 2000, 15);

			clicar("//input[@name='tmp.bntEnviar']", 2000, 15);

			boolean existePublicacao = false;
			try {
				esperarElemento("//a[contains(text(),'" + getParametros().getSecaoDiario() + "')]", 30);
				existePublicacao = ElementoClicavel(
						By.xpath("//a[contains(text(),'" + getParametros().getSecaoDiario() + "')]"));
				Thread.sleep(3000);
				
				criarLog(processo, " Encontrou a publicacao no di�rio. ");

			} catch (Exception e) {
				criarLog(processo, " Nao Encontrou a publicacao no diario. ");
				existePublicacao = false;
			}

			alternar(0);

			return existePublicacao;

		} catch (Exception e) {
			criarLog(processo, "\n\nErro ao consultar diario. " + processo.getNumeroProcesso() + ". A��o: "
					+ processo.getAcao() + "\n\n");

		}
		return false;

	}

	protected void validarCamposObrigatorios() throws AutomacaoException {
		super.validarCamposObrigatorios();
		if (parametros.getMovimentosJulgamento() == null || parametros.getMovimentosPermitidosBaixa() == null
				|| parametros.getMovimentacaoDecursoDePrazo() == null) {
			throw new AutomacaoException(
					"Necess�rio preencher os parametros MovimentosJulgamento, MovimentosPermitidosBaixa e MovimentacaoDecursoDePrazo");
		}
	}

}