package CLIENT.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Util {
	
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
		StringBuffer fileContent = new StringBuffer();
		try {

			FileReader fileReader = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(fileReader);
			String data = null;
			while ((data = reader.readLine()) != null) {
				fileContent.append(data+ " \n");
			}
			fileReader.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileContent;
	}
	
	public static void atribuirValores(Object destino, Object origem) {
		
		Map<String, Method> mapaMetodos = new HashMap<String, Method>();
		
		for (int i = 0; i < destino.getClass().getMethods().length; i++) {
			if (destino.getClass().getMethods()[i].getName().indexOf("set") != -1
					&& destino.getClass().getMethods()[i].getName().indexOf("Object.getClass") == -1) {
				Method metodoProcessoDto = destino.getClass().getMethods()[i];
				mapaMetodos.put(metodoProcessoDto.getName().toLowerCase(), metodoProcessoDto);
			}
		}
		
		for (int i = 0; i < origem.getClass().getMethods().length; i++) {
			Method metodoProcessoDto = origem.getClass().getMethods()[i];
			
			if (metodoProcessoDto.getName().indexOf("get") != -1
					&& metodoProcessoDto.getName().indexOf("Object.getClass") == -1) {
				try {
					
					String nomeMetodo = "s" + metodoProcessoDto.getName().substring(1);
					Method metodo = mapaMetodos.get(nomeMetodo.toLowerCase());
					if(metodo!=null) {
						metodo.invoke(destino, metodoProcessoDto.invoke(origem));
					}
					
				}catch(Exception e) {
					System.out.println("Erro ao invocar metodo "+ e.getMessage());
				}
				
			}
		}
		
	}
}
