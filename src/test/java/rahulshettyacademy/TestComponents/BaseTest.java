package rahulshettyacademy.TestComponents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bonigarcia.wdm.WebDriverManager;
import rahulshettyacademy.pageobjects.LandingPage;

public class BaseTest {

	public WebDriver driver;
	public LandingPage landingPage;

	public WebDriver initializeDriver() throws IOException

	{
		// properties class

		 Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir")
				+ "//src//main//java//rahulshettyacademy//resources//GlobalData.properties");
		prop.load(fis);
		
		String browserName = System.getProperty("browser")!=null ? System.getProperty("browser") :prop.getProperty("browser");
		//prop.getProperty("browser");

		if (browserName.contains("chrome")) {
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			
			// Common Chrome options
			options.addArguments(
			    "--no-sandbox",
			    "--disable-dev-shm-usage",
			    "--disable-gpu",
			    "--remote-allow-origins=*",
			    "--user-data-dir=" + System.getProperty("java.io.tmpdir") + "/chrome-data-" + UUID.randomUUID()
			);
			
			if (browserName.contains("headless")) {
			    options.addArguments(
			        "--headless=new",
			        "--window-size=1440,900"
			    );
			}
			
			driver = new ChromeDriver(options);
			
			if (!browserName.contains("headless")) {
			    driver.manage().window().setSize(new Dimension(1440, 900));
			}

		} else if (browserName.equalsIgnoreCase("firefox")) {
		    WebDriverManager.firefoxdriver().setup();
		    FirefoxOptions options = new FirefoxOptions();
		    if (browserName.contains("headless")) {
		        options.addArguments("-headless");
		    }
		    driver = new FirefoxDriver(options);
		    
		} else if (browserName.equalsIgnoreCase("edge")) {
		    WebDriverManager.edgedriver().setup();
		    EdgeOptions options = new EdgeOptions();
		    if (browserName.contains("headless")) {
		        options.addArguments("--headless=new");
		    }
		    driver = new EdgeDriver(options);
		}

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().window().maximize();
		return driver;

	}
	
	public List<HashMap<String, String>> getJsonDataToMap(String filePath) throws IOException
	{
		//read json to string
	String jsonContent = 	FileUtils.readFileToString(new File(filePath), 
			StandardCharsets.UTF_8);
	
	//String to HashMap- Jackson Databind
	
	ObjectMapper mapper = new ObjectMapper();
	  List<HashMap<String, String>> data = mapper.readValue(jsonContent, new TypeReference<List<HashMap<String, String>>>() {
      });
	  return data;
	
	//{map, map}

	}
	
	public String getScreenshot(String testCaseName,WebDriver driver) throws IOException
	{
		TakesScreenshot ts = (TakesScreenshot)driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		File file = new File(System.getProperty("user.dir") + "//reports//" + testCaseName + ".png");
		FileUtils.copyFile(source, file);
		return System.getProperty("user.dir") + "//reports//" + testCaseName + ".png";
		
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public LandingPage launchApplication() throws IOException
	{
		
		 driver = initializeDriver();
		  landingPage = new LandingPage(driver);
		landingPage.goTo();
		return landingPage;
	
		
	}
	
	@AfterMethod(alwaysRun=true)
	public void tearDown()
	{
	    if (driver != null) {
	        try {
	            driver.quit();
	        } catch (Exception e) {
	            System.err.println("Error while closing the WebDriver: " + e.getMessage());
	        }
	    }
	}
}
