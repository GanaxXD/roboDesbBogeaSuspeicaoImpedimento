package CLIENT.TEST;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import CLIENT.Robo;
import DAO.ProcessoDto;
import MODEL.Movimento;
import PAGE.PaginaBase;

public class Teste {

	
	public static void atribuirValores(Object destino, Object origem) {
		
		Map<String, Method> mapaMetodos = new HashMap<String, Method>();
		
		for (int i = 0; i < destino.getClass().getMethods().length; i++) {
			if (destino.getClass().getMethods()[i].getName().indexOf("set") != -1
					&& destino.getClass().getMethods()[i].getName().indexOf("Class") == -1) {
				Method metodoProcessoDto = destino.getClass().getMethods()[i];
				mapaMetodos.put(metodoProcessoDto.getName().toLowerCase(), metodoProcessoDto);
			}
		}
		
		for (int i = 0; i < origem.getClass().getMethods().length; i++) {
			Method metodoProcessoDto = origem.getClass().getMethods()[i];
			
			if (metodoProcessoDto.getName().indexOf("get") != -1
					&& metodoProcessoDto.getName().indexOf("Class") == -1) {
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
	
	
	public static void main(String[] args) {
		
		String teste = "a;b;c;d;e; ";
		String[] str = teste.split(";");
		for (int i = 0; i < str.length; i++) {
			System.out.println(i+" ) "+str[i]);
		}
		
	}
	
	public static void main2(String[] args) {
		try {
		Movimento movimento = new Movimento();
		
		ProcessoDto processo = new ProcessoDto();
		throw new Exception("ERRO GENERICO");
		/*processo.setCodmovimentocnj("1");
		processo.setDatamovimentacao("01/01/2019");
		processo.setNomeparte("NOMEPARTE");
		processo.setNumeroprocesso("xxxxxxxxxxxxxxxxx");
		processo.setDescmovimentocnj("DESMOVIMENTO");
		atribuirValores(movimento, processo);	*/	
		
		}catch(Exception e) {
			 	StringWriter sw = new StringWriter();
	            PrintWriter pw = new PrintWriter(sw);
	            e.printStackTrace(pw);
	            System.out.println(sw.toString());
		}
	}
	
	
	public static void main4(String[] args) {
		try {
			Robo robo = new Robo();
			
			Class classe = Class.forName("PAGE.projudi.turmasRecursais.BaixaProcessosTurma_Page");
			Constructor construtor = classe.getDeclaredConstructor(String.class);
			robo.setPagina((PaginaBase)construtor.newInstance("properties/pje/intimarAcordao.json"));
			PaginaBase pagina = robo.getPagina();
			System.out.println(pagina.getParametros());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	private static String extrairIdentificacao(String destinatario) {
		String id = "";
		if (destinatario.indexOf("CNPJ") != -1) {
			id = destinatario.substring(destinatario.indexOf("CNPJ"));
			id = id.substring(0, id.indexOf("\n"));
			id = id.trim();

		} else if (destinatario.indexOf("CPF") != -1) {
			id = destinatario.substring(destinatario.indexOf("CPF"));
			id = id.substring(0, id.indexOf("\n"));
			id = id.trim();
		}
		return id;
	}
}
