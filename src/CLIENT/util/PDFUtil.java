package CLIENT.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;



public class PDFUtil {
	
	public static String getConteudo(File arquivo) {
		FileInputStream is = null;
		try {
			is = new FileInputStream(arquivo);
		} catch (IOException e) {
			System.out.println("ERRO: " + e.getMessage() + " - "+ arquivo.getAbsolutePath());
			return null;
		}

		PDDocument pdfDocument = null;
		try {
			PDFParser parser = new PDFParser(is);
			parser.parse();
			pdfDocument = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			String conteudo = stripper.getText(pdfDocument);
			System.out.println("Tamanho do arquivo "+ arquivo + " : "+ conteudo.length());
			return conteudo;
			
		} catch (Exception e) {
			System.out.println("Erro ao ler arquivo PDF "+ e.getMessage()+ " - "+ arquivo.getAbsolutePath());
		} finally {
			if (pdfDocument != null) {
				try {
					pdfDocument.close();
				} catch (IOException e) {
					
				}
			}
		}
		
		return "";
	}




}
