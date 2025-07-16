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
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bonigarcia.wdm.WebDriverManager;
import rahulshettyacademy.pageobjects.LandingPage;

public class BaseTest {

    protected ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    public LandingPage landingPage;
    public ExtentTest test;
    public static ExtentReports extent;

    public WebDriver getDriver() {
        return driver.get();
    }

    public WebDriver initializeDriver() throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")
                + "//src//main//java//rahulshettyacademy//resources//GlobalData.properties");
        prop.load(fis);
        
        String browserName = System.getProperty("browser") != null ? System.getProperty("browser") : prop.getProperty("browser");

        if (browserName.contains("chrome")) {
            ChromeOptions options = new ChromeOptions();
            
            // Create unique user data directory for each thread
            String userDataDir = System.getProperty("java.io.tmpdir") + "chrome-data-" + UUID.randomUUID();
            options.addArguments("--user-data-dir=" + userDataDir);
            
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            
            if (browserName.contains("headless")) {
                options.addArguments("--headless");
                options.addArguments("--window-size=1920,1080");
            }
            
            WebDriverManager.chromedriver().setup();
            driver.set(new ChromeDriver(options));
            getDriver().manage().window().setSize(new Dimension(1920, 1080));
        } 
        else if (browserName.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            if (browserName.contains("headless")) {
                options.addArguments("--headless");
            }
            driver.set(new FirefoxDriver(options));
        } 
        else if (browserName.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            if (browserName.contains("headless")) {
                options.addArguments("--headless");
            }
            driver.set(new EdgeDriver(options));
        }

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        getDriver().manage().window().maximize();
        return getDriver();
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
