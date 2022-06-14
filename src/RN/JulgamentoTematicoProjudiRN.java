package RN;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import CLIENT.SINAPSES.SinapsesClient;
import CLIENT.SINAPSES.model.Resultado;
import CLIENT.SINAPSES.model.RetornoModeloClassificacao;
import CLIENT.certificado.CertificadoUtil;
import CLIENT.util.ArquivoUtil;
import CLIENT.util.PDFUtil;
import DAO.BaixaProcessualDao;
import DAO.JCRStorage;
import DAO.JulgamentoTematicoDao;
import MODEL.Documento;
import MODEL.Etiqueta;
import MODEL.FiltroEtiqueta;
import MODEL.Processo;
import MODEL.TemaProcessualEtiqueta;
import PAGE.AutomacaoException;
import PAGE.Parametros;

/**
 * Classe respons�vel pela
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @TJBA
 */
public class JulgamentoTematicoProjudiRN {
	private JulgamentoTematicoDao dao;

	public JulgamentoTematicoProjudiRN(JulgamentoTematicoDao dao) {
		setDao(dao);
	}

	public JulgamentoTematicoDao getDao() {
		return dao;
	}

	public void setDao(JulgamentoTematicoDao dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @param parametros
	 * @return
	 */
	public Map<String, Processo> carregarProcessos(Parametros parametros, SinapsesClient conexaoSinapses)
			throws AutomacaoException {
		try {
			System.out.println("Iniciando método RN.carregarProcessos.....");
			Map<String, Processo> mapaProcessos = dao.carregarProcessos(parametros);

			int count = 0;
			int qtdProcessosComEtiqueta = 0;
			for (Processo processo : mapaProcessos.values()) {

				System.out.println("(" + (++count) + "/" + mapaProcessos.size() + " == " + qtdProcessosComEtiqueta
						+ ") Processo: " + processo.getNumeroProcesso());
				analisarProcesso(parametros, conexaoSinapses, processo);
				if (processo.getEtiquetasAutomacao().size() > 0) {
					qtdProcessosComEtiqueta++;
				}
				
				if (processo.getEtiquetasAutomacao().size() > 1) {
					definirEtiquetaInteligenciaArtificial(processo, conexaoSinapses,
							parametros.getAcuraciaIA());
				}

			}
			
			System.out.println("Finalizando metodo RN.carregarProcessos....");
			return mapaProcessos;
		} catch (Exception e) {
			throw new AutomacaoException(
					"Erro ao carregar processos. " + e.getMessage() + " " + parametros.getTarefa());
		}

	}

	private void analisarProcesso(Parametros parametros, SinapsesClient conexaoSinapses, Processo processo)
			throws AutomacaoException {
		List<Documento> documentos = processo.getDocumentos();

		try {

			for (Documento documento : documentos) {

				if (documento.getDsTipoProcessoDocumento().indexOf(parametros.getDocumentoProcesso()) != -1) {

					carregarConteudoTexto(parametros, processo, documento);

					definirEtiquetaProcesso(parametros, processo, documento);

					processo.setDocumentoAto(documento.getConteudoTexto());

					//documento.setConteudoTexto(null);// - liberar memória

				}

			}

		} catch (AutomacaoException e) {
			processo.setEtiquetaAutomacao(Etiqueta.OUTROS);
			processo.setEtiquetaIA(Etiqueta.OUTROS);

		} catch (Exception e) {
			e.printStackTrace();
			throw new AutomacaoException(
					" Ocorreu um erro ao analisar o processo " + processo.getNumeroProcesso() + " - " + e.getMessage());
		}

	}

	private void definirEtiquetaProcesso(Parametros parametros, Processo processo, Documento documento)
			throws AutomacaoException {

		for (TemaProcessualEtiqueta tema : parametros.getTemas()) {

			atribuirEtiquetaAutomacao(processo, documento, tema);

		}

	}

	public void definirEtiquetaInteligenciaArtificial(Processo processo,
			SinapsesClient conexaoSinapses, float acuracia) throws AutomacaoException {

		if (processo.getDocumentoAto() != null) {

			RetornoModeloClassificacao retornoSinapses = conexaoSinapses.consultarModelo(processo.getDocumentoAto());
			if (retornoSinapses != null) {

				try {

					Resultado resultado = (Resultado) retornoSinapses.getResultados().toArray()[0];
					if (resultado.getConviccao() > new Float(acuracia)) {
						System.out.println("Processo: 	" + processo.getNumeroProcessoFormatado()
								+ "	Sugestão Sinapses: " + retornoSinapses.getClasseConvicto().getDescricao());
						processo.setEtiquetaSinapses(retornoSinapses.getClasseConvicto().getDescricao());
						processo.setRetornoSinapses(retornoSinapses);
						
						
					} else {
						System.out.println("Processo: 	" + processo.getNumeroProcessoFormatado()
								+ "	Sugestão Sinapses abaixo da média esperada: "
								+ retornoSinapses.getClasseConvicto().getDescricao()
								+ ". Portanto não será utilizado!");

					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new AutomacaoException("Erro ao consultar o sinapses. " + e.getMessage());
				}
			}

		}

	}

	private void atribuirEtiquetaAutomacao(Processo processo, Documento documento, TemaProcessualEtiqueta tema) {

		try {
			// System.out.println("\nAnalisando se o tema " + tema.getEtiqueta() + " pode
			// ser atribuído ao processo "+ processo.getNumeroProcessoFormatado());
			if (documento.getConteudoTexto() == null) {
				System.out.println(
						"O conteúdo do documento está vazio. Possivelmente problema na conversão do arquivo PDF para Texto");
				return;
			}
			if (documento.getConteudoTexto() != null) {

				if (tema.getOperadorNOT() != null) {
					for (int i = 0; i < tema.getOperadorNOT().length; i++) {

						if (documento.getConteudoTexto().indexOf(tema.getOperadorNOT()[i].toLowerCase()) != -1) {

							// - Significa que contém uma palavra que está na lista de palavras não
							// permitidas
							// System.out.println("------O tema " + tema.getEtiqueta() + " não foi atribuído
							// ao processo " + processo.getNumeroProcessoFormatado());
							// System.out.println("\t\tA palavra " +
							// tema.getOperadorNOT()[i].toLowerCase()+", pertencente a lista de palavras
							// proibidas da etiqueta " + tema.getEtiqueta()+ ", está presente na petição.");
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
							if (documento.getConteudoTexto().indexOf(palavra.toLowerCase()) != -1) {
								// System.out.println("\tA palavra '" + palavra.toLowerCase() + "' está contida
								// na petição.");
								passou = true;
								break;
							}
						}

						if (passou) {
							continue;
						} else {
							// System.out.println("------O tema " + tema.getEtiqueta() + " não foi atribuído
							// ao processo " + processo.getNumeroProcessoFormatado());
							// System.out.println("\tNenhuma das seguintes palavras estão contidas na
							// petição inicial: \n\t\t"+ palavras);
							return;
						}
					}

				}else if(tema.getListaPalavrasANDOR()!=null){
					
					List<List<String>> blocosPalavras = tema.getListaPalavrasANDOR();
					for (Iterator iterator = blocosPalavras.iterator(); iterator.hasNext();) {
						List<String> conjuntoPalavrasBloco = (List<String>) iterator.next();
						
						
						boolean passou = false;
						for (String string : conjuntoPalavrasBloco) {
							if (documento.getConteudoTexto().indexOf(string.toLowerCase()) != -1) {
								// System.out.println("\tA palavra '" + palavra.toLowerCase() + "' está contida
								// na petição.");
								passou = true;
								break;
							}
						}
						
						if (passou) {
							continue;
						} else {
							// System.out.println("------O tema " + tema.getEtiqueta() + " não foi atribuído
							// ao processo " + processo.getNumeroProcessoFormatado());
							// System.out.println("\tNenhuma das seguintes palavras estão contidas na
							// petição inicial: \n\t\t"+ palavras);
							return;
						}
						
						
					}
					
				}

				/*
				 * Se chegou neste ponto aqui significa atendeu
				 */
				if (tema.getConfirmacaoSinapses() != null && tema.getConfirmacaoSinapses().equalsIgnoreCase("sim")) {
					// - verificando se o sinapses classificou da mesma forma
					if (processo.getEtiquetaSinapses() != null
							&& processo.getEtiquetaSinapses().equalsIgnoreCase(tema.getEtiqueta())) {

						// System.out.println("\tO Sinapses classificou o processo da mesma forma: " +
						// tema.getEtiqueta());

						processo.getEtiquetasAutomacao().add(tema.getEtiqueta());
					} else {
						// System.out.println("\tA etiqueta não está de acordo com o sinapses " +
						// tema.getEtiqueta());

					}

				} else {

					// System.out.println("+++++++ O tema '" + tema.getEtiqueta() + "' será
					// atribuída ao processo " processo.getNumeroProcessoFormatado() + ". Não houve
					// confirmação do Sinapses");
					processo.getEtiquetasAutomacao().add(tema.getEtiqueta());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\nEtiqueta '" + tema.getEtiqueta() + ". \t Erro: " + e.getMessage());

		}

	}

	private void carregarConteudoTexto(Parametros parametros, Processo processo, Documento documento) {
		try {

			if (documento.getNrDocumentoStorage() != null) {
				documento.setConteudoTexto(
						obterConteudo(parametros.getJCRLocation() + documento.getNrDocumentoStorage(), processo));
			}

		} catch (Exception e) {
			e.printStackTrace();
			documento.setConteudoTexto(e.getMessage());

		}
	}

	public String obterConteudo(String caminhoFisicoArquivo, Processo processo) throws Exception {

		String conteudo = "";
		File arquivo = new File(caminhoFisicoArquivo);

		byte[] arquivoAssinado = FileUtils.readFileToByteArray(arquivo);
		byte[] arquivoOriginal = new CertificadoUtil().getConteudoOriginal(arquivoAssinado);

		String extensao = ArquivoUtil.obterExtensao(arquivoOriginal);

		File arquivoDestino = ArquivoUtil.salvarArquivo("arquivos\\", arquivo, processo, arquivoOriginal, extensao);

		if (extensao.indexOf("htm") != -1) {
			conteudo = ArquivoUtil.lerConteudoHTML(arquivoDestino);
		} else if (extensao.indexOf("pdf") != -1) {
			conteudo = PDFUtil.getConteudo(arquivoDestino);
		}
		arquivoDestino.delete();

		return conteudo.toLowerCase();

	}

}
