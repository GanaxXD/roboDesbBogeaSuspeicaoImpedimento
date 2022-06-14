package DAO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import PAGE.AutomacaoException;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

public class JCRStorage {

	public static String getConteudoTexto(String pastaNFS, String jcrNumeroDocumentoStorage, String tipoArquivo)
			throws AutomacaoException {

		String conteudo = "";
		String caminhoArquivo = "";
		try {

			String parte1 = jcrNumeroDocumentoStorage.substring(0, 2);
			String parte2 = jcrNumeroDocumentoStorage.substring(2, 4);
			String parte3 = jcrNumeroDocumentoStorage.substring(4, 6);
			caminhoArquivo = pastaNFS + "\\" + parte1 + "\\" + parte2 + "\\" + parte3 + "\\"
					+ jcrNumeroDocumentoStorage;

			System.out.println(caminhoArquivo+" - ");
			if (tipoArquivo.indexOf("html") != -1) {
				conteudo = lerConteudoHTML(new File(caminhoArquivo));
			} else if (tipoArquivo.indexOf("pdf") != -1) {
				conteudo = lerConteudoPDF(new File(caminhoArquivo));
			}

		} catch (Throwable t) {
			throw new AutomacaoException("Erro ao ler o arquivo " + caminhoArquivo);

		}
		
		return conteudo;
	}

	public static String getConteudoTexto(String jcrUrl, String jcrUser, String jcrPassword,
			String jcrNumeroDocumentoStorage) {

		String conteudo = "";

		try {

			int maxHostConn = 100;
			String hash = jcrNumeroDocumentoStorage;

			HostConfiguration host = new HostConfiguration();
			host.setHost(jcrUrl);

			HttpConnectionManagerParams params = new HttpConnectionManagerParams();
			params.setMaxConnectionsPerHost(host, maxHostConn);
			HttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
			manager.setParams(params);

			Credentials cred = new UsernamePasswordCredentials(jcrUser, jcrPassword);
			HttpClient client = new HttpClient(manager);
			client.getState().setCredentials(AuthScope.ANY, cred);
			client.setHostConfiguration(host);
			client.getParams().setAuthenticationPreemptive(true);
			client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);

			GetMethod get = new GetMethod(String.format("%s/%s", new Object[] { jcrUrl, hash }));
			int resp = client.executeMethod(get);
			switch (resp) {
			case 200:
				conteudo = gerarConteudo(get.getResponseBodyAsStream());
				break;
			case 404:
				throw new HttpException(
						String.format("Rescurso nao encontrado %d", new Object[] { Integer.valueOf(resp) }));
			case 409:
				throw new HttpException(
						String.format("Estado do recurso invalido %d", new Object[] { Integer.valueOf(resp) }));
			case 500:
			case 503:
				throw new HttpException(String.format("Erro interno %d", new Object[] { Integer.valueOf(resp) }));
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return conteudo;
	}
	
	
	public static String lerConteudoHTML(String html, String nomeArquivo) throws IOException {
		try {
			File file = new File(new File("").getAbsolutePath() + "\\arquivos\\" +nomeArquivo);
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(html);
			out.close();
			
			//- DeLETAR
			String conteudo = new TextExtractor(new Source(file)).toString();
			return conteudo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String lerConteudoHTML(File arquivo) throws IOException {
		try {
			String conteudo = new TextExtractor(new Source(arquivo)).toString();
			return conteudo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String lerConteudoPDF(File arquivo) {
		FileInputStream is = null;
		try {
			is = new FileInputStream(arquivo);
		} catch (IOException e) {
			System.out.println("ERRO: " + e.getMessage());
			return null;
		}

		return gerarConteudo(is);
	}

	private static String gerarConteudo(InputStream is) {
		String conteudo = "";
		PDDocument pdfDocument = null;
		try {
			PDFParser parser = new PDFParser(is);
			parser.parse();
			pdfDocument = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			conteudo = stripper.getText(pdfDocument);

		} catch (Exception e) {
			System.out.println("Erro ao ler arquivo PDF " + e.getMessage());
		} finally {
			if (pdfDocument != null) {
				try {
					pdfDocument.close();
				} catch (IOException e) {

				}
			}
		}

		return conteudo;
	}

	public static byte[] getData(String jcrUrl, String jcrUser, String jcrPassword, String jcrNumeroDocumentoStorage) {

		byte[] byteArray = null;

		try {

			int maxHostConn = 100;
			String hash = jcrNumeroDocumentoStorage;

			HostConfiguration host = new HostConfiguration();
			host.setHost(jcrUrl);

			HttpConnectionManagerParams params = new HttpConnectionManagerParams();
			params.setMaxConnectionsPerHost(host, maxHostConn);
			HttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
			manager.setParams(params);

			Credentials cred = new UsernamePasswordCredentials(jcrUser, jcrPassword);
			HttpClient client = new HttpClient(manager);
			client.getState().setCredentials(AuthScope.ANY, cred);
			client.setHostConfiguration(host);
			client.getParams().setAuthenticationPreemptive(true);
			client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);

			GetMethod get = new GetMethod(String.format("%s/%s", new Object[] { jcrUrl, hash }));
			int resp = client.executeMethod(get);
			switch (resp) {
			case 200:
				byteArray = IOUtils.toByteArray(get.getResponseBodyAsStream());
				break;
			case 404:
				throw new HttpException(
						String.format("Rescurso nao encontrado %d", new Object[] { Integer.valueOf(resp) }));
			case 409:
				throw new HttpException(
						String.format("Estado do recurso invalido %d", new Object[] { Integer.valueOf(resp) }));
			case 500:
			case 503:
				throw new HttpException(String.format("Erro interno %d", new Object[] { Integer.valueOf(resp) }));
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return byteArray;
	}

	public static InputStream getInputStream(String jcrUrl, String jcrUser, String jcrPassword,
			String jcrNumeroDocumentoStorage) {

		InputStream is = null;

		try {

			int maxHostConn = 100;
			String hash = jcrNumeroDocumentoStorage;

			HostConfiguration host = new HostConfiguration();
			host.setHost(jcrUrl);

			HttpConnectionManagerParams params = new HttpConnectionManagerParams();
			params.setMaxConnectionsPerHost(host, maxHostConn);
			HttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
			manager.setParams(params);

			Credentials cred = new UsernamePasswordCredentials(jcrUser, jcrPassword);
			HttpClient client = new HttpClient(manager);
			client.getState().setCredentials(AuthScope.ANY, cred);
			client.setHostConfiguration(host);
			client.getParams().setAuthenticationPreemptive(true);
			client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);

			GetMethod get = new GetMethod(String.format("%s/%s", new Object[] { jcrUrl, hash }));
			int resp = client.executeMethod(get);
			switch (resp) {
			case 200:
				is = get.getResponseBodyAsStream();
				break;
			case 404:
				throw new HttpException(
						String.format("Rescurso nao encontrado %d", new Object[] { Integer.valueOf(resp) }));
			case 409:
				throw new HttpException(
						String.format("Estado do recurso invalido %d", new Object[] { Integer.valueOf(resp) }));
			case 500:
			case 503:
				throw new HttpException(String.format("Erro interno %d", new Object[] { Integer.valueOf(resp) }));
			}

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					System.out.println(
							"Erro ao tentar fechar o stream oriundo do storage: {0}" + e.getLocalizedMessage());
				}
			}
		}
		return is;
	}

	public static void main2(String args[]) throws IOException {

		byte[] byteArray = null;

		String[] numeroDocumentoStorage = new String[] { "d92859b74469ae6a80f7f901d5fadf33646b2d5e",
				"e6a1ed196c4da6ed029c41866bcccc994869a274"

		};

		File diretorio = new File("C:\\IA\\testes");
		diretorio.mkdir();

		for (int i = 0; i < numeroDocumentoStorage.length; i++) {

			byteArray = JCRStorage.getData("http://pjejcr03.tjba.jus.br:8080/jcr/documents", "admin", "admin",
					numeroDocumentoStorage[i]);
			String nomeArquivo = diretorio + "\\" + i + "-" + numeroDocumentoStorage[i] + ".pdf";
			File doc = new File(nomeArquivo);
			FileOutputStream fos = new FileOutputStream(doc);
			fos.write(byteArray);
			fos.close();

			// System.out.println(PDFUtil.getConteudo(new File(nomeArquivo)));

		}

	}//http://pjejcr03.tjba.jus.br:8080/jcr/documents/0b19660be727458a467496e963e819efa487ac15

	public static void main(String args[]) throws AutomacaoException {

		String[] numeroDocumentoStorage = new String[] { "d92859b74469ae6a80f7f901d5fadf33646b2d5e",
				"e6a1ed196c4da6ed029c41866bcccc994869a274"

		};

		for (int i = 0; i < numeroDocumentoStorage.length; i++) {

			String str = JCRStorage.getConteudoTexto("\\\\pjejcr03\\datastore", numeroDocumentoStorage[i], "pdf");
			System.out.println(str);

		}

	}

}
