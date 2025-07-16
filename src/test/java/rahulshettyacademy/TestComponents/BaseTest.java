package rahulshettyacademy.TestComponents;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import rahulshettyacademy.pageobjects.LandingPage;

public class BaseTest {

    public WebDriver driver;
    public LandingPage landingPage;

    @BeforeSuite(alwaysRun = true)
    public void killChromeProcesses() {
        try {
            // Kill any existing Chrome processes
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
            } else {
                Runtime.getRuntime().exec("pkill -f chrome");
                Runtime.getRuntime().exec("pkill -f chromedriver");
            }
        } catch (Exception e) {
            System.out.println("Error killing Chrome processes: " + e.getMessage());
        }
    }

    public WebDriver initializeDriver() throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")
                + "//src//main//java//rahulshettyacademy//resources//GlobalData.properties");
        prop.load(fis);
        
        String browserName = System.getProperty("browser") != null ? 
                System.getProperty("browser") : prop.getProperty("browser");

        if (browserName.contains("chrome")) {
            ChromeOptions options = new ChromeOptions();
            
            // Set Chrome binary path from properties or use default
            String chromeBinary = prop.getProperty("chrome.binary.path", "/usr/bin/google-chrome");
            options.setBinary(chromeBinary);
            
            // Common Chrome options
            options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--remote-allow-origins=*",
                "--disable-gpu",
                "--disable-software-rasterizer",
                "--disable-extensions",
                "--disable-infobars"
            );
            
            // Create a unique temp directory for each test run
            String tempDir = "/tmp/chrome-test-" + UUID.randomUUID();
            new File(tempDir).mkdirs();
            options.addArguments("--user-data-dir=" + tempDir);
            
            // Handle headless mode
            boolean isHeadless = Boolean.parseBoolean(prop.getProperty("chrome.headless", "true"));
            if (browserName.contains("headless") || isHeadless) {
                options.addArguments("--headless=new");
                String windowSize = prop.getProperty("chrome.window.size", "1920,1080");
                options.addArguments("--window-size=" + windowSize);
            }
            
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(options);
            
        } else if (browserName.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        } else if (browserName.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        return driver;
    }

    public List<HashMap<String, String>> getJsonDataToMap(String filePath) throws IOException {
        String jsonContent = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonContent, new TypeReference<List<HashMap<String, String>>>() {});
    }

    public String getScreenshot(String testCaseName, WebDriver driver) throws IOException {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        File file = new File(System.getProperty("user.dir") + "//reports//" + testCaseName + ".png");
        FileUtils.copyFile(source, file);
        return System.getProperty("user.dir") + "//reports//" + testCaseName + ".png";
    }

    @BeforeMethod(alwaysRun = true)
    public LandingPage launchApplication() throws IOException {
        driver = initializeDriver();
        landingPage = new LandingPage(driver);
        landingPage.goTo();
        return landingPage;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit(); // Use quit() instead of close() to ensure all windows are closed
            } catch (Exception e) {
                System.out.println("Error while closing driver: " + e.getMessage());
            }
        }
    }
}
