package CLIENT.TEST;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


public class Teste3 {

	
	public static Map<String, String> readTextFile(String filePath) {
		Map<String, String> mapa = new HashMap<String, String>();
		try {

			FileReader fileReader = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(fileReader);
			String data = null;
			while ((data = reader.readLine()) != null) {
				String[] dados = data.split(",");
				mapa.put(dados[0],data);
			}
			fileReader.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mapa;
	}
	
	public static void main(String[] args) {

		//Map<String, String> mapa = readTextFile("C:\\Users\\lerioliveira\\Desktop\\PJBA_AUTOMACOES\\PJE\\TURMA\\BAIXA_TRANSITO_JULGADO\\dados\\diario.csv");
		
		
		//System.out.println(mapa.get("134729033"));
		//System.out.println(mapa.get("xxxx"));
		
		String teste = "2ª VSJE DO CONSUMIDOR (VESPERTINO)".replaceAll(" ", "_").replaceAll("[!@#$%¨&*()~^{}áéíóúâêîôûã~e~iõ]", "");
		System.out.println(teste);
		
	}

}
