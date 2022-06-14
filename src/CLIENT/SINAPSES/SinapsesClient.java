package CLIENT.SINAPSES;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import CLIENT.SINAPSES.model.Mensagem;
import CLIENT.SINAPSES.model.RequisicaoChat;
import CLIENT.SINAPSES.model.RequisicaoModeloClassificacao;
import CLIENT.SINAPSES.model.RetornoChat;
import CLIENT.SINAPSES.model.RetornoModeloClassificacao;
import CLIENT.SINAPSES.model.Similaridade;
import CLIENT.SINAPSES.model.RequestResponseModeloSimilaridade;
import CLIENT.util.ArquivoUtil;
import CLIENT.util.Util;

public class SinapsesClient {

	private String url;
	private String usuario;
	private String senha;

	public SinapsesClient(String url, String usuario, String senha) {
		this.url = url;
		this.usuario = usuario;
		this.senha = senha;
	}

	private final class BasicAuthenticator extends Authenticator {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(getUsuario(), getSenha().toCharArray());
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public RetornoModeloClassificacao consultarTema(SinapsesClient sinapses, String arquivo) throws Exception {
		String requisicao = sinapses.gerarRequisicaoJSON("TEXTO", 3, Util.readTextFile(arquivo).toString());

		String retornoSinapses = sinapses.realizarChamada(requisicao);
		RetornoModeloClassificacao retornoModelo = new Gson().fromJson(retornoSinapses,
				RetornoModeloClassificacao.class);
		System.out.println(retornoModelo);
		return retornoModelo;
	}

	public RetornoModeloClassificacao consultarModelo(String decisao) {
		// System.out.println("CONSULTANDO O SINAPSES: ");
		RetornoModeloClassificacao retornoModelo = null;
		try {
			String requisicao = gerarRequisicaoJSON("TEXTO", 3, decisao);

			System.out.println("REQUISICAO SINAPSES: \n\n" + requisicao);

			String retornoSinapses = realizarChamada(requisicao);

			System.out.println("=======================================================");
			System.out.println("\n\nRETORNO SINAPSES:\n\n " + retornoSinapses);

			retornoModelo = new Gson().fromJson(retornoSinapses, RetornoModeloClassificacao.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return retornoModelo;

	}

	private String realizarChamada(String json) {
		StringBuilder response = new StringBuilder();

		try {
			Authenticator.setDefault(new BasicAuthenticator());

			URL url = new URL(getUrl());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);

			try (OutputStream os = urlConnection.getOutputStream()) {
				byte[] input = json.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			try (

					BufferedReader br = new BufferedReader(
							new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {

				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return response.toString();
	}

	private String gerarRequisicaoSofia(String tipo, String tema, String conteudoTexto) throws Exception {

		return new Gson().toJson(new RequisicaoChat(new Mensagem(tipo, conteudoTexto), tema));

	}

	private String gerarRequisicaoJSON(String tipo, int quantidadeClasses, String conteudoTexto) throws Exception {

		String textoEmBase64 = Base64.getEncoder().encodeToString(conteudoTexto.toString().getBytes("utf-8"));
		return new Gson()
				.toJson(new RequisicaoModeloClassificacao(new Mensagem(tipo, textoEmBase64), quantidadeClasses));

	}
	
	
	public static void main2(String[] args) throws Exception {

		SinapsesClient sinapses = new SinapsesClient(
				"https://sinapses-backend.ia.pje.jus.br/rest/modelo/executarServico/-tjba-coje/CLS_CLASS_SCR_1/1",
				"tjba_coje_custas", "YmQ3MDcyM2Y0NGE2MTgwYjQxOTYyNzQx");

		RetornoModeloClassificacao retorno = testeSinapses(sinapses,
				"C:\\COJE\\IA\\EXTRACTOR\\arquivos\\PJE-1-SEM_PENDENCIA_DE_CUSTAS.txt");
		System.out.println(">>>>>> " + retorno.getClasseConvicto());

	}

	public static void main3(String[] args) throws Exception {

		SinapsesClient sinapsesCustas = new SinapsesClient(
				"https://sinapses-backend.ia.pje.jus.br/rest/modelo/executarServico/-tjba-coje/CLS_CLASS_SCR_1/1",
				"tjba_coje_custas", "YmQ3MDcyM2Y0NGE2MTgwYjQxOTYyNzQx");

		RetornoModeloClassificacao retorno = testeSinapses(sinapsesCustas,
				"C:\\COJE\\IA\\EXTRACTOR\\arquivos\\PJE-1-SEM_PENDENCIA_DE_CUSTAS.txt");
		System.out.println("OK >>>>>> " + retorno.getClasseConvicto() + "\n\n");

	}

	public static void main(String[] args) throws Exception {

		SinapsesClient sinapsesSimilaridade = new SinapsesClient(
				"https://sinapses-backend.ia.pje.jus.br/rest/modelo/executarServico/-tjba-coje/GEN_SIMILARIDADE_PECAS/1",
				"tjba_coje_custas", "YmQ3MDcyM2Y0NGE2MTgwYjQxOTYyNzQx");

		//RequestResponseModeloSimilaridade retorno = testeSimilaridade(sinapsesSimilaridade,
		//		"C:\\COJE\\IA\\EXTRACTOR\\datasets\\INVESTIGACAO_FRAUDES_2G\\similaridades\\2G_Similaridade_2.json");
		

		for (int i = 4; i < 10; i++) {
			RequestResponseModeloSimilaridade retorno = testeSimilaridade(sinapsesSimilaridade,
					"C:\\COJE\\IA\\EXTRACTOR\\datasets\\INVESTIGACAO_FRAUDES_SALVADOR\\similaridades\\Salvador_Similaridade_"+i+".json");
			
			ArquivoUtil.salvarArquivo("datasets", "Salvador_Similaridade_"+i+"_resultado.json", new StringBuffer(retorno.toString()));	
		}
		
		
		
		

	}

	private static RetornoChat testeSinapsesSofia(SinapsesClient sinapses, String pergunta, String tema)
			throws Exception {
		String requisicao = sinapses.gerarRequisicaoSofia("texto", tema, pergunta);
		System.out.println("Requisicao: " + requisicao);
		String retornoSinapses = sinapses.realizarChamada(requisicao);
		RetornoChat retornoModelo = new Gson().fromJson(retornoSinapses, RetornoChat.class);
		System.out.println(retornoModelo);
		return retornoModelo;
	}

	private static RetornoModeloClassificacao testeSinapses(SinapsesClient sinapses, String arquivo) throws Exception {
		String requisicao = sinapses.gerarRequisicaoJSON("TEXTO", 3, Util.readTextFile(arquivo).toString());

		String retornoSinapses = sinapses.realizarChamada(requisicao);
		RetornoModeloClassificacao retornoModelo = new Gson().fromJson(retornoSinapses,
				RetornoModeloClassificacao.class);
		System.out.println(retornoModelo);
		return retornoModelo;
	}
	
	
	private static void agrupar(Similaridade similaridade, Map<String, List<Similaridade>> agrupamentos) {
	
		if(agrupamentos.containsKey(similaridade.getCluster())) {
			List<Similaridade> lista = agrupamentos.get(similaridade.getCluster());
			lista.add(similaridade);
		}else {
			List<Similaridade> lista = new ArrayList<Similaridade>();
			lista.add(similaridade);
			agrupamentos.put(similaridade.getCluster(), lista);
		}
		
	}

	private static RequestResponseModeloSimilaridade testeSimilaridade(SinapsesClient sinapses, String arquivo)
			throws Exception {
		String requisicao = Util.readTextFile(arquivo).toString();
		String retornoSinapses = sinapses.realizarChamada(requisicao);
		RequestResponseModeloSimilaridade retornoModelo = new Gson().fromJson(retornoSinapses,
				RequestResponseModeloSimilaridade.class);
		
		return retornoModelo;
	}
	
	
	private static RequestResponseModeloSimilaridade testeSimilaridade2(SinapsesClient sinapses, String arquivo)
			throws Exception {
		String requisicao = Util.readTextFile(arquivo).toString();
		String retornoSinapses = sinapses.realizarChamada(requisicao);
		RequestResponseModeloSimilaridade retornoModelo = new Gson().fromJson(retornoSinapses,
				RequestResponseModeloSimilaridade.class);
		
		Map<String, List<Similaridade>> agrupamentos = new HashMap<String, List<Similaridade>>();
		
		
		List<Similaridade> minutas = retornoModelo.getMinutas();
		for (Similaridade minuta : minutas) {
			
			agrupar(minuta, agrupamentos);
			
			String cluster = minuta.getCluster();
			String processo = minuta.getId();
			LinkedTreeMap<String, Double> similaridade = (LinkedTreeMap<String, Double>)minuta.getSimilaridade();
			//System.out.println("=======================================");
			//System.out.println("Agrupamento: "+ cluster);
			//System.out.println("Processo referência: "+ processo);
			
			
			for(Map.Entry<String,Double> entry : similaridade.entrySet()) {
				  String key = entry.getKey();
				  Double value = entry.getValue();
				  //System.out.println("\t"+key + " => " + value);
			}
			
		}
		
		return retornoModelo;
	}

}
