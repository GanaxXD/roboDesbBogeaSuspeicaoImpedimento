package tjma.PAGE.pje215.geral;

import java.util.Iterator;
import java.util.List;

import CLIENT.util.ArquivoUtil;
import CLIENT.util.PDFUtil;
import MODEL.Processo;
import MODEL.TemaProcessualEtiqueta;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import tjma.PAGE.pje215.PainelTarefasPJE;

/**
 * Robô que realiza citações e intimações de acordo com a configuração passada.
 * Apenas partes devidamente qualificadas são intimadas (Com Procuradorias ou
 * Advogados)
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class TriagemInicial_Page extends PainelTarefasPJE {

	public TriagemInicial_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	protected void executar(Processo processo) throws InterruptedException, AutomacaoException {

		try {

			for (String etiqueta : processo.getEtiquetasAutomacao()) {

				alternarFrame(new String[] { "ngFrame" });

				if (!existeEtiqueta(etiqueta)) {

					atribuirEtiqueta(etiqueta);

					criarLog("Etiqueta " + etiqueta + " atribuída ao processo " + processo.getNumeroProcessoFormatado(),
							obterArquivoLog());
					
					contadorProcessosEtiquetados();
					
					/*
					 * System.out.println("Verificando Suspeição no processo "+processo.
					 * getNumeroProcesso()); //Aplicando a suspeição
					 * if(existeElementoTexto("ToadaRobô06-suspeição Gustavo S. de Oliveira") ||
					 * existeElementoTexto("ToadaRobô06-suspeição Haroldo G. S. Filho") ||
					 * existeElementoTexto("ToadaRobô06-suspeição G S Adv. Ass.") ||
					 * existeElementoTexto("Elaine")) { realizarSubmissaoDeSuspeicao(processo);
					 * System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
					 * System.out.println("Suspeição aplicada ao processo "+processo.
					 * getNumeroProcesso()); } else { System.out.
					 * println("Não foram encontradas etiquetas de suspeição no processo "+processo.
					 * getNumeroProcesso()); }
					 * 
					 * //aplicando o impedimento
					 * System.out.println("Verificando Impedimento no processo "+processo.
					 * getNumeroProcesso());
					 * if(existeElementoTexto("ToadaRobô06-impedimento Ulisses C. M. de Sousa") ||
					 * existeElementoTexto("ToadaRobô06-impedimento Marcos L. B. R. Simões") ||
					 * existeElementoTexto("ToadaRobô06-impedimento Antônio A. J. Canovas") ||
					 * existeElementoTexto("ToadaRobô06-impedimento Catarina S. Bogéa") ||
					 * existeElementoTexto("ToadaRobô06-impedimento Ulisses S. Adv. Ass.") ||
					 * existeElementoTexto("Codó")) { realizarSubmissaoDeImpedimento(processo);
					 * System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
					 * System.out.println("Impedimento aplicado no processo "+processo.
					 * getNumeroProcesso()); } else { System.out.
					 * println("Não foram encontradas etiquetas de impedimento no processo "
					 * +processo.getNumeroProcesso()); }
					 * 
					 * //Devolvendo para a análise da assessoria devolverParaAssessoria(processo);
					 */
					
				} else {
					criarLog(
							"Etiqueta " + etiqueta + " já existia no processo " + processo.getNumeroProcessoFormatado(),
							obterArquivoLog());

				}

			}

		} catch (Exception e) {
			throw new AutomacaoException(
					"Erro ao realizar o procedimento do processo  " + processo.getNumeroProcesso());
		}
	}

	protected void carregarDocumentoProcesso(Processo processo) throws InterruptedException, AutomacaoException {

		try {

			clicar("//button[@title='Abrir autos']", 20, 2000);

			Thread.sleep(2000);

			alternarParaDetalhes();

			System.out.println("Realizando o download dos documentos para análise das palavras chaves");

			clicar("//a[@title='Download autos do processo']", 20, 2000);

			String[] documentos = getParametros().getDocumentosProcesso();
			for (String doc : documentos) {

				try {
					selecionar("//select[@id='navbar:cbTipoDocumento']", doc, 10, 2000);

					clicar("//input[@id='navbar:downloadProcesso']", 10, 2000);

					Thread.sleep(3000);

					getDriver().switchTo().alert().accept();
				} catch (Exception e) {
					System.out.println(
							">>>>>>Não encontrou o documento '" + doc + "' no processo " + processo.getNumeroProcesso());
				}

			}
			
			lerArquivo(processo, 1, 30);
			

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException("Não foi possível baixar o documento do processo "
					+ getParametros().getDocumentoProcesso() + "\n" + e.getMessage());
		}
		
		deletarArquivos(processo);


	}

	private void lerArquivo(Processo processo, int contador, int tentativas) {
		
		try {
			criarLog("TENTATIVA DE LEITURA: "+contador, obterArquivoLog());
			Thread.sleep(1000);

			List<String> arquivos = ArquivoUtil.obterArquivosDownload(getParametros().getCaminhoDownload(),
					processo.getNumeroProcessoFormatado(), ".pdf");
	
			
			StringBuffer conteudoArquivos = new StringBuffer();
			
			for (String arquivo : arquivos) {
				
				criarLog("\t Lendo o arquivo salvo: " + arquivo, obterArquivoLog());
				conteudoArquivos.append(PDFUtil.getConteudo(new java.io.File(arquivo))+ " ");
				
			}
			
			
			String conteudo = conteudoArquivos.toString().replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", " ");
			
			String[] tokens = conteudo.split(" ");
			String valorFinal = ""; 
			for (String string : tokens) {
				if( !string.equals("") && !string.equals(" ")) {
					valorFinal+=string.trim()+" ";	
				}
				
			}
			System.out.println(" Tamanho do arquivo do processo "+processo.getNumeroProcesso()+" : "+ valorFinal.length());
			if(valorFinal.length()==0) {
				throw new AutomacaoException("O download do arquivo ainda está pendente");
			}
			processo.setConteudoDocumento(valorFinal.toLowerCase());
		
		}catch(Exception e) {
			if((++contador)<tentativas) {
				lerArquivo(processo, contador, tentativas);
			}
		}

	}
	
	
	private void deletarArquivos(Processo processo) {

		List<String> arquivos = ArquivoUtil.obterArquivosDownload(getParametros().getCaminhoDownload(),
				processo.getNumeroProcessoFormatado(), ".pdf");

		if (getParametros().getDeletarArquivo() == null
				|| getParametros().getDeletarArquivo().equalsIgnoreCase("sim")) {

			for (String arquivo : arquivos) {
				new java.io.File(arquivo).delete();
			}

		}
	}

	protected void atribuirEtiquetasProcesso(Processo processo) throws InterruptedException, AutomacaoException {
		//System.out.println("Conteúdo documento: \n\n"+ processo.getConteudoDocumento()+ " \n\n\n");
		for (TemaProcessualEtiqueta tema : getParametros().getTemas()) {

			if (processo.getConteudoDocumento() != null) {

				verificarEtiquetaProcesso(processo, tema);

			}else {
				criarLog("O conteúdo do documento está vazio. ", obterArquivoLog());
			}
		}

	}

	protected void verificarEtiquetaProcesso(Processo processo, TemaProcessualEtiqueta tema) {
		System.out.println("Verificando o tema "+ tema.getEtiqueta());
		
		if (processo.getConteudoDocumento() == null) {
			//System.out.println("O conteúdo do documento está vazio. ");
			criarLog("O conteúdo do documento está vazio. ", obterArquivoLog());
			return;
		}

		if (tema.getOperadorNOT() != null) {
			for (int i = 0; i < tema.getOperadorNOT().length; i++) {

				if (processo.getConteudoDocumento().indexOf(tema.getOperadorNOT()[i].toLowerCase()) != -1) {
					criarLog("------A etiqueta " + tema.getEtiqueta() + " não foi atribuído ao processo "
							+ processo.getNumeroProcessoFormatado() + "\n\t\tA palavra "
							+ tema.getOperadorNOT()[i].toLowerCase()
							+ ", pertencente a lista de palavras proibidas da etiqueta " + tema.getEtiqueta()
							+ ", está presente no documento.", obterArquivoLog());
					return;
				}
			}
		}

		if (tema.getPalavrasChaveANDOR() != null) {
			for (int i = 0; i < tema.getPalavrasChaveANDOR().length; i++) {
				/*
				 * Cada registro aqui significa uma lista de palavras chaves que deve ser
				 * satisfeito pelo menos 1 elemento da lista ou seja, se tem 10 listas de
				 * palavras chaves cada um elemento com 5 palavras, pelo menos uma palavra das
				 * 10 listas deve estar presentes no conteúdo do arquivo.
				 */

				boolean passou = false;
				List<String> palavras = tema.getPalavrasChaveANDOR()[i];
				for (String palavra : palavras) {
					if (processo.getConteudoDocumento().indexOf(palavra.toLowerCase()) != -1) {
						criarLog("\tA palavra " + palavra.toLowerCase() + " está contida na petição.",
								obterArquivoLog());

						passou = true;
						break;
					}
				}

				if (passou) {
					continue;
				} else {
					criarLog("------A etiqueta " + tema.getEtiqueta() + " não foi atribuído ao processo "
							+ processo.getNumeroProcessoFormatado()
							+ "\n\tNenhuma das seguintes palavras estão contidas na petição inicial: \n\t\t" + palavras,
							obterArquivoLog());

					return;
				}
			}
		} else if (tema.getListaPalavrasANDOR() != null) {

			List<List<String>> blocosPalavras = tema.getListaPalavrasANDOR();
			for (Iterator iterator = blocosPalavras.iterator(); iterator.hasNext();) {
				List<String> conjuntoPalavrasBloco = (List<String>) iterator.next();

				boolean passou = false;
				for (String string : conjuntoPalavrasBloco) {
					
					criarLog("\tTestando a palavra A palavra '" + string.toLowerCase() + "'", obterArquivoLog());
					
					if (processo.getConteudoDocumento().indexOf(string.toLowerCase()) != -1) {
						
						criarLog("\tA palavra '" + string.toLowerCase() + "' está contida na petição.", obterArquivoLog());
						passou = true;
						break;
					}else {
						criarLog("\tA palavra '" + string.toLowerCase() + "' NÃO estava contida na petição.", obterArquivoLog());
					}
				}

				if (passou) {
					
					continue;
					
				} else {

					criarLog("------O tema " + tema.getEtiqueta() + " não foi atribuído ao processo "
							+ processo.getNumeroProcessoFormatado(), obterArquivoLog());
					
					System.out.println("============================================================");
					
					return;
				}

			}

		}

		/*
		 * Se chegou neste ponto aqui significa atendeu
		 */
		criarLog(
				"+++++++ Todas as contições AND_OR e NOT foram satisfeitas. O processo "
						+ processo.getNumeroProcessoFormatado() + " ganhará a etiqueta " + tema.getEtiqueta(),
				obterArquivoLog());
		
		System.out.println("============================================================");

		processo.getEtiquetasAutomacao().add(tema.getEtiqueta());
		
		//contadorProcessosEtiquetados();
	}

	protected boolean deveProsseguir(Processo processo) throws InterruptedException, AutomacaoException {
		System.out.println();
		carregarDocumentoProcesso(processo);

		atribuirEtiquetasProcesso(processo);

		if (processo.getEtiquetasAutomacao() != null && processo.getEtiquetasAutomacao().size() > 0) {

			return true;

		} else {

			criarLog(processo, "Nenhuma etiqueta foi atribuída ao processo " + processo.getNumeroProcessoFormatado());

			return false;
		}

	}

	protected void validarCamposObrigatorios() throws AutomacaoException {

		if (isEmpty(getParametros().getDocumentoProcesso())&& getParametros().getDocumentosProcesso()==null) {
			throw new AutomacaoException(
					"É necessário informar o documento do processo a ser utilizado no procedimento.");
		}

		if (isEmpty(getParametros().getCaminhoDownload())) {
			throw new AutomacaoException(
					"É necessário informar o caminho para download da(o) " + getParametros().getDocumentoProcesso());
		}

		// testar este processo: 0041066-30.2009.8.05.0001. tem petição inicial com
		// assinatura manual
		// 0038741-63.2001.8.05.0001
		// https://medium.com/codestar-blog/tika-tika-getting-started-doing-ocr-with-apache-tika-andtesseract-from-the-jvm-f5d2bfe9b397
	}

}
