package CLIENT;
/***
 * Classe Thread de Rob�s
 * 
 * @author Leonardo Ribeiro de Oliveira @ TJBA
 *
 */
public class RoboThread implements Runnable {

	private Robo robo;

	public RoboThread(Robo robo_) {
		this.robo = robo_;
	}

	@Override
	public void run() {
		try {
			System.out.println("Iniciando... ");
			robo.iniciar();
			Thread.sleep(5000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
