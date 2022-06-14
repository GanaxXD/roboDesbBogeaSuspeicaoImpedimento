package tjma.time;

import tjma.time.contador.ContadorProcessosEtiquetados;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/*
 * Classe responsável por pegar o tempo em que o robô esteve em operação.
 * 
 * @author Derick de Jesus
 * @Toada.lab @TJMA 
 */

public class TempoRoboProcessamento extends ContadorProcessosEtiquetados {
	
	private String tempoInicialOpeRobo;
	private String tempoFinalOpeRobo;
	
	public void pegarTempoInicialOpeRobo() {
		
		DateTimeFormatter dtfInicial = DateTimeFormatter.ofPattern("HH:mm:ss");
		this.tempoInicialOpeRobo = dtfInicial.format(LocalDateTime.now());
		
		System.out.println("O ROBÔ INICIOU SEU PROCESSAMENTO ÀS " +tempoInicialOpeRobo);
	}
	
	public void pegarTempoFinalOpeRobo() {
		
		DateTimeFormatter dtfFinal = DateTimeFormatter.ofPattern("HH:mm:ss");
		this.tempoFinalOpeRobo = dtfFinal.format(LocalDateTime.now());
		
		System.out.println("O ROBÔ ENCERROU SEU PROCESSAMENTO ÀS " +tempoFinalOpeRobo);
	}
	
	public void tempoTotalOpeRobo() {
		
		LocalTime lt1 = LocalTime.parse(tempoInicialOpeRobo);
		LocalTime lt2 = LocalTime.parse(tempoFinalOpeRobo);
		
		System.out.println(lt1);
		System.out.println(lt2);
		
		
		long emHoras = lt1.until(lt2, ChronoUnit.HOURS);
		long emMinutos = lt1.until(lt2, ChronoUnit.MINUTES);
		long emSegundos = lt1.until(lt2, ChronoUnit.SECONDS);
		long emSegundosFormatados = emSegundos / 60; 
		
		System.out.println("O ROBÔ ETIQUETOU " + quantProcessosEtiquetados + " PROCESSOS EM " +emHoras+ " Horas, " +emMinutos+ " Minutos e " +emSegundosFormatados+ " Segundos");
	}
	
	
	
	
	/*
	public String getTempoInicialOpeRobo() {
		return tempoInicialOpeRobo;
	}
	public void setTempoInicialOpeRobo(String tempoInicialOpeRobo) {
		this.tempoInicialOpeRobo = tempoInicialOpeRobo;
	}
	public String getTempoFinalOpeRobo() {
		return tempoFinalOpeRobo;
	}
	public void setTempoFinalOpeRobo(String tempoFinalOpeRobo) {
		this.tempoFinalOpeRobo = tempoFinalOpeRobo;
	}
	*/
}
