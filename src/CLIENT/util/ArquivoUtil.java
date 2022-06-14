package CLIENT.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import MODEL.Processo;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

public class ArquivoUtil {
	
	public static String obterArquivoDownload(String diretorio, String processo, String formato) {
		File f = new File(diretorio);
		File[] arquivos = f.listFiles(); // retorna um array de Files

		String arquivoDownload = diretorio+processo+"_nao_encontrado.pdf";

		for (File arquivo : arquivos) {
			if (arquivo.getAbsolutePath().indexOf(processo) != -1 && arquivo.getAbsolutePath().endsWith(formato)) {
				arquivoDownload = arquivo.getAbsolutePath();
				break;
			}
		}

		return arquivoDownload;
		
	}
	
	public static List<String> obterArquivosDownload(String diretorio, String processo, String formato) {
		File f = new File(diretorio);
		File[] arquivos = f.listFiles(); // retorna um array de Files
		
		List<String> listaArquivos = new ArrayList<String>();
		
		for (File arquivo : arquivos) {
			if (arquivo.getAbsolutePath().indexOf(processo) != -1 && arquivo.getAbsolutePath().endsWith(formato)) {
				listaArquivos.add(arquivo.getAbsolutePath());
			}
		}

		return listaArquivos;
		
	}
	
	
	public static void salvarArquivo(String pasta, String nomeArquivo, StringBuffer conteudo) throws IOException {
//		String user = System.getProperty("user.dir");

	//	String pastaPrincipal = user + "\\conteudoDiario";

		try {
			criarPasta(pasta);
			
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			FileWriter arquivo = new FileWriter(pasta + "\\" + nomeArquivo);
			arquivo.write(conteudo.toString());
			if (arquivo != null) {
				arquivo.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Nao foi possivel criar arquivo:" + nomeArquivo);
		}

	}
	
	
	public static void salvarArquivo(String nomeArquivo, StringBuffer conteudo) throws IOException {

		System.out.println("Salvando arquivo "+ nomeArquivo);
		try {
			FileWriter arquivo = new FileWriter(nomeArquivo);
			arquivo.write(conteudo.toString());
			if (arquivo != null) {
				arquivo.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Nao foi possivel criar arquivo:" + nomeArquivo);
		}

	}

	public static void deletarArquivosPasta(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				file.delete();
			}
		}
	}

	private static void criarPasta(String caminho) {
		File folder = new File(caminho);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public static void copyFile(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	public static StringBuffer readTextFile(String filePath) {
		return readTextFile(filePath, false);
	}
	
	/**
	 * Retorna o conteúdo do arquivo linha a linha em formato String, com as linhas
	 * divididas pelo caractere '\n'.
	 * 
	 * @param filePath
	 * @param trimLine indica se os espaços sobressalentes no início, meio e final
	 *                 de cada linha devem ser removidos.
	 * @return
	 * 
	 * @author William Sodré @TJMA
	 */
	public static StringBuffer readTextFile(String filePath, boolean trimLine) {
		StringBuffer fileContent = new StringBuffer();
		try {

			FileReader fileReader = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(fileReader);
			String data = null;
			while ((data = reader.readLine()) != null) {
				if (trimLine) {
					data = data.trim().replaceAll(" +", " ");
				}
				fileContent.append(data + " \n");
			}
			fileReader.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileContent;
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

	public static String obterExtensao(byte[] arquivoOriginal) throws IOException {

		String extensao = "";
		if (arquivoOriginal != null && arquivoOriginal.length > 4 
				&& arquivoOriginal[0] == 0x25 && // %
				arquivoOriginal[1] == 0x50 && // P
				arquivoOriginal[2] == 0x44 && // D
				arquivoOriginal[3] == 0x46 // F
				) {

			return ".pdf";
		}

		try {
			InputStream is = new ByteArrayInputStream(arquivoOriginal);
			String mimeType = URLConnection.guessContentTypeFromStream(is);

			if (mimeType.indexOf("html") != -1) {
				extensao = ".htm";
			}

		} catch (Exception e) {

			extensao = ".doc";
		}

		return extensao;
	}
	
	public static File salvarArquivo(String caminho, File arquivo, byte[] arquivoOriginal, String extensao)
			throws FileNotFoundException, IOException {
		File arquivoDestino = new File(
				caminho + arquivo.getName().substring(0, arquivo.getName().indexOf(".p7s")) + extensao);

		FileOutputStream fos = new FileOutputStream(arquivoDestino);
		fos.write(arquivoOriginal);
		fos.close();
		return arquivoDestino;
	}
	
	public static File salvarArquivo(String caminho, File arquivo, Processo processo, byte[] arquivoOriginal, String extensao)
			throws FileNotFoundException, IOException {
		//File arquivoDestino = new File(caminho + arquivo.getName().substring(0, arquivo.getName().indexOf(".p7s")) + extensao);
		File arquivoDestino = null;
		try {
			arquivoDestino = new File(
					caminho + processo.getNumeroProcessoFormatado() + extensao);

			FileOutputStream fos = new FileOutputStream(arquivoDestino);
			fos.write(arquivoOriginal);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arquivoDestino;
	}
	
}
