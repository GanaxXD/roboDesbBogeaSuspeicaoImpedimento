package CLIENT.TEST;

import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class GridTest {

	public static void main(String[] args) {
		try {
			WebDriver driver;
			String url = "https://corregedoria.treinamento.pje.jus.br/login.seam";
			String nodeUrl = "http://10.250.192.37:5566/wd/hub";
			DesiredCapabilities capability = DesiredCapabilities.chrome();
			capability.setBrowserName("chrome");
			capability.setPlatform(Platform.WIN10);
			
			ChromeOptions options = new ChromeOptions();
			driver = new RemoteWebDriver(options);
			
			driver.navigate().to(new URL(nodeUrl));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
