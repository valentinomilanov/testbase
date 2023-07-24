package com.project.test.util.junit;

import com.project.test.constants.TypeOfServer;
import com.project.test.pageobjects.base.BaseInformations;
import com.project.test.util.junit.mobile.Devices;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class TestBaseDriverSetupAndKill extends TestBaseStaticConfig {

	private List<WebDriver> drivers;
	private TypeOfServer typeOfServer = TypeOfServer.INSTANCE_1;
	private Devices device;
	private String downloadPath;
	private List<String> logTypes;

	private int testRunCount = 1;

	static {
		String driverPath = new File("." + File.separator + "src" + File.separator + "test" + File.separator +"resources" + File.separator + "drivers" + File.separator + "chromedriver.exe").getAbsolutePath();
        System.setProperty("webdriver.chrome.driver", driverPath);
	}

	public TestBaseDriverSetupAndKill() {
		super();
	}
	
	public void createDriverForTest(String testName) {
		drivers = new ArrayList<WebDriver>();
		switch (mode) {
		case QA:
		case LOCAL:
			downloadPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "SC" + File.separator 
				+ this.getClass().getSimpleName() + "-" + testName;
			break;
		case GRID:
			downloadPath = localDownloadFolderPath + File.separator + "sc" + File.separator 
					+ this.getClass().getSimpleName() + "-" + testName;
			break;
		}
		
		logger.trace("Download path:" + downloadPath);
		addNewDriver();
	}
	
	public void deleteDownloadFolder(String downloadFolderPath) {
		//delete download folder
		try {
			logger.trace("deleting the download folder");
			FileUtils.deleteDirectory(new File(downloadFolderPath));
		} catch (IOException e) {
			logger.trace("deleting the download folder failed");
			e.printStackTrace();
		}
	}

	private WebDriver getWebDriver() {	
	    switch (os) {
	        case WINDOWS:
	            ChromeOptions chromeOptions = new ChromeOptions(); 
	            WebDriver driverLocal = null;

	            logTypes = new ArrayList<String>();
	            logTypes.add(LogType.BROWSER);

	            LoggingPreferences logs = new LoggingPreferences();

	            for (String logType: logTypes){
	                logs.enable(logType, Level.ALL);
	            }

	            chromeOptions.setCapability(CapabilityType.LOGGING_PREFS, logs);

	            Map<String, Object> prefs = new HashMap<String, Object>(); 

	            prefs.put("download.default_directory", downloadPath);
				prefs.put("profile.default_content_setting_values.notifications", 2); // 1- allow, 2-block
	            chromeOptions.addArguments("--enable-simple-cache-backend");
				chromeOptions.addArguments("use-fake-device-for-media-stream");
				chromeOptions.addArguments("use-fake-ui-for-media-stream");
				chromeOptions.addArguments("--mute-audio");
	            chromeOptions.setExperimentalOption("prefs", prefs);
	            switch (mode) {
	            case LOCAL:
	            case QA:
	                driverLocal = new ChromeDriver(chromeOptions);
	                driverLocal.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);
	                break;
	            case GRID:
	                URL url = null;
	                try {
	                    url = new URL("http://" + getGridHost() + ":" + getGridPort() + "/wd/hub");
	                    driverLocal = new RemoteWebDriver(url, chromeOptions);
	                } catch (MalformedURLException e) {
	                    e.printStackTrace();
	                    throw new Error("The grid URL is not valid: " + url);
	                }
	                driverLocal.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
	                break;
	            }

	            maximizeDriver(driverLocal);
	            return driverLocal;
	        case ANDROID:
	            DesiredCapabilities capabilities = new DesiredCapabilities();
	            capabilities.setCapability("platformName", "ANDROID");
	            capabilities.setCapability("browserName", "chrome");
	            capabilities.setCapability("newCommandTimeout", 1200);
                capabilities.setCapability("deviceName", "Android Emulator");
                capabilities.setCapability("autoGrantPermissions", "true");
                capabilities.setCapability("autoAcceptAlerts", "true");
	            driverLocal = null;
	            URL url = null;
                try {
                    url = new URL("http://" + getGridHost() + ":" + getGridPort() + "/wd/hub");
                    driverLocal = new RemoteWebDriver(url, capabilities);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    throw new Error("The grid URL is not valid: " + url);
                }
                driverLocal.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
                return driverLocal;
            default:
                throw new Error("Not supported OS: " + os);
	    }
		
	}

	public static void maximizeDriver(WebDriver driver) {
			driver.manage().window().maximize();
	}

	public WebDriver addNewDriver() {
		WebDriver driver = getWebDriver(); 
		drivers.add(driver);
		return driver;
	}
	
	public void closeDriver(WebDriver driver) { 
		driver.quit();
		drivers.remove(driver);
	}
	
	public BaseInformations getBaseInformations() {
	    return new BaseInformations(getDriver(), os, typeOfServer);
	}
	
	/**
	 * use getBaseInformations()
	 * @return
	 */
	@Deprecated
	public WebDriver getDriver() {
	    if (drivers != null && drivers.size() > 0) {
	        return drivers.get(0);
	    } else {
	        return null;
	    }
	}
	
	public Devices getDevice() {
	    return device;
	}

	public List<WebDriver> getDrivers(){
		return drivers;
	}

	public String getDownloadPath() {
		return downloadPath;
	}
	
	public String getCurrentUrl() {
	    return getDriver().getCurrentUrl();
	}
	
	/**
	 * Refreshes the page.
	 */
	public void refresh(){
		getDriver().navigate().refresh();
	}
		
	public void goToURL(String url, String log){
        goToURL(url, getBaseInformations(), log);
    }

	public void goToURL(String url, BaseInformations baseInformations, String log){
	    logger.debug(log);
        logger.trace("url: " + url);
        baseInformations.getDriver().navigate().to("https://www.google.com/");
        baseInformations.getDriver().navigate().to(url);
        if (isAlertPresent()){
            Alert alert = waitForAlert();
            logger.trace("accept alert: " + alert.getText());
            alert.accept();

            waitFor(500);

            baseInformations.getDriver().get(url);
        }
	}

	public List<String> getLogTypes() {
		return logTypes;
	}

	public int getTestRunCount() {
		return testRunCount;
	}

	public void incTestRunCount() {
		testRunCount++;
	}
	
	/**
	 * Wait 10 for alert dialog to appear
	 */
	public Alert waitForAlert() {
		int timeout = 0;
		while (timeout < 20) {
			try {
				waitFor(500);
				return getDriver().switchTo().alert();
			} catch (NoAlertPresentException e) {
				timeout++;
			} catch (Exception e) {
				e.printStackTrace();
				throw new AssertionError("Unexpected exception happened when" + " the test waited for an alert dialog");
			}
		}

		throw new AssertionError("The alert dialog did not appear in 10 seconds");
	}
	
		public boolean isAlertPresent() {
			try {
				getBaseInformations().getDriver().switchTo().alert();
				return true;
			} catch (NoAlertPresentException e) {
				return false;
			}
		}
		
		/**
		 * Waits for a given time
		 * 
		 * @param milis
		 * @deprecated<br>
		 * <b>Do not use this method unless there is no other option!<br>
		 * If u use it leave a comment why it is used!</b>
		 * 
		 */
		@Deprecated
		protected void waitFor(Integer milis) {
		    if (DEBUG_LOGGING) {
		        logger.trace("waitFor " + milis + "ms");
		    }
			try {
				Thread.sleep(milis);
			} catch (InterruptedException e1) {

			}
		}
}
