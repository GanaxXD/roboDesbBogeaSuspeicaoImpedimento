package CLIENT.util;

import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe com métodos utilitários para manipulação de <code>String</code>.
 * 
 * @author William Sodré @ TJMA
 *
 */
public class StringUtil {
	
	/**
	 * 
	 * Verifica se uma sequência de caracteres é nula ou está vazia.
	 * 
	 * @param charSequence
	 * @return
	 */
	public static boolean isNotEmpty(CharSequence charSequence) {
		return StringUtils.isNotEmpty(charSequence);
	}
	
	/**
	 * Retorna o texto desejado desconsiderando-se a acentuação - a string é
	 * retornada com os caracteres não-acentuados correspondentes.
	 * 
	 * @param string
	 * @return
	 */
	public static String unaccent(String string) {
		return Normalizer.normalize(string, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "");
	}
	
	/**
	 * Retorna o texto desejado com caracteres minúsculos e desconsiderando-se a
	 * acentuação - a string é retornada com os caracteres minúsculos não-acentuados
	 * correspondentes.
	 * 
	 * @param string
	 * @return
	 */
	public static String lowerCaseUnaccent(String string) {
		return Normalizer.normalize(string, Normalizer.Form.NFD)
				.toLowerCase()
				.replaceAll("\\p{M}", "");
	}
	
	/**
	 * Verifica se os valores textuais de dois objetos (dados pelos respectivos {@link #toString()} de cada um)
	 * são iguais, independente de acentuação e case (maiúsculas ou minúsculas) dos caracteres.
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static boolean equalsIgnoreCaseAccent(Object obj1, Object obj2) {		
		String strObj1 = lowerCaseUnaccent(obj1.toString());
		String strObj2 = lowerCaseUnaccent(obj2.toString());
		return strObj1.equals(strObj2);
	}
}
