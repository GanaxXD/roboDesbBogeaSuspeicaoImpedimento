package CLIENT.TEST;

import java.io.File;

public class Teste2 {

	public static String obterArquivoDownload(String diretorio, String processo, String formato) {
		File f = new File(diretorio);
		File[] arquivos = f.listFiles(); // retorna um array de Files

		String arquivoDownload = "";

		for (File arquivo : arquivos) {
			if (arquivo.getAbsolutePath().indexOf(processo) != -1 && arquivo.getAbsolutePath().endsWith(formato)) {
				arquivoDownload = arquivo.getAbsolutePath();
			}
		}

		return arquivoDownload;
		
	}

	public static void main(String[] args) {

		System.out.println(obterArquivoDownload("C:\\Users\\lerioliveira\\Downloads", "8013971-71.2018.8.05.0000", ".pdf"));

	}

}
