package CLIENT.SINAPSES;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.exec.util.StringUtils;

public class GeradorDeDataSets {

	public static String converterEmBase64(String texto) {

		try {
			String textoEmBase64 = Base64.getEncoder().encodeToString(texto.toString().getBytes("utf-8"));
			return textoEmBase64;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void salvarArquivo(String nomeArquivo, StringBuffer arquivo) {

		File arquivo2 = new File(nomeArquivo);

		try {
			arquivo2.createNewFile();
			FileWriter fileW = new FileWriter(arquivo2);
			BufferedWriter buffW = new BufferedWriter(fileW);
			buffW.write(arquivo.toString());
			buffW.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	public static void gerarDataset(String caminho) {
		List<String> dataset = new ArrayList<String>();

		File pastaPrincipal = new File(caminho);
		File classes[] = pastaPrincipal.listFiles();
		for (File classe : classes) {
			if (classe.isDirectory()) {

				String caminhoPastaClasse = classe.getAbsolutePath();
				String nomeClasse = caminhoPastaClasse.substring(caminhoPastaClasse.lastIndexOf("\\") + 1);
				// System.out.println("Pasta: " + caminhoPastaClasse);

				File pastaClasse = new File(caminhoPastaClasse);
				File exemplosClasse[] = pastaClasse.listFiles();
				for (File exemplo : exemplosClasse) {
					// System.out.println("Exemplo: " + exemplo);

					try {
						BufferedReader br = new BufferedReader(new FileReader(exemplo));
						StringBuffer conteudoExemplo = new StringBuffer();
						while (br.ready()) {
							String linha = br.readLine();
							conteudoExemplo.append(linha);
						}

						dataset.add(nomeClasse + "," + GeradorDeDataSets.converterEmBase64(conteudoExemplo.toString()));

					} catch (Exception e) {
						System.out.println("Erro ao ler arquivo " + exemplo + " - " + e.getMessage());
					}
				}
			}

		}

		StringBuffer sb = new StringBuffer();

		for (String string : dataset) {
			sb.append(string + "\n");
		}

		GeradorDeDataSets.salvarArquivo(caminho + "\\dataset.csv", sb);
	}

	public static void main(String[] args) {
		//GeradorDeDataSets.gerarDataset("C:\\sinapses\\dados\\MARVEL");
		GeradorDeDataSets.gerarDataset("C:\\sinapses\\dados\\COJE");
		

	}

}
