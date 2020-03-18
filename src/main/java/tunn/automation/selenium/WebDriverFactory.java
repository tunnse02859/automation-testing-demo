package tunn.automation.selenium;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import tunn.automation.report.HtmlReporter;
import tunn.automation.report.Log;
import tunn.automation.utility.FilePaths;
import tunn.automation.utility.PropertiesLoader;

public class WebDriverFactory {

	private static final int IMPLICIT_WAIT_TIME = 10;

	public static class BrowserType {
		public static final String IE = "IE";
		public static final String FIREFOX = "Firefox";
		public static final String CHROME = "Chrome";
		public static final String EDGE = "Edge";
		public static final String SAFARI = "Safari";
		public static final String REMOTE = "Remote";
	}

	protected WebDriver driver;

	public WebDriverFactory() throws Exception {
		/* setDriver(); */
	}

	public WebDriverFactory(String browserName, String platform) throws Exception {
		setDriver(browserName, platform);
	}

	/**
	 * This method is used to open a webdriver, it's used for selenium grid as well
	 * 
	 * @author Hanoi Automation team
	 * @param None
	 * @return None
	 * @throws Exception
	 *             The method throws an exeption when browser is invalid or can't
	 *             start webdriver
	 */
	private void setDriver(String browser, String platform) throws Exception {
		String isSeleniumGrid = PropertiesLoader.getPropertiesLoader().selenium_configuration
				.getProperty("selenium.grid.enable");
		String selenium_grid_hub = PropertiesLoader.getPropertiesLoader().selenium_configuration
				.getProperty("selenium.grid.hub");
		// String selenium_grid_brower =
		// PropertiesLoader.getPropertiesLoader().selenium_configuration.getProperty("RemoteBrowser");

		DesiredCapabilities capabilities = null;
		setExecutableDriver(browser, platform);
		try {

			if (isSeleniumGrid.equalsIgnoreCase("true")) {
				if (browser.equalsIgnoreCase(BrowserType.FIREFOX)) {
					capabilities = DesiredCapabilities.firefox();
					capabilities.setCapability("browserName", browser);
					capabilities.setCapability("platform", platform);
				} else if (browser.equalsIgnoreCase(BrowserType.CHROME)) {
					capabilities = DesiredCapabilities.chrome();
					capabilities.setCapability("browserName", browser);
					capabilities.setCapability("platform", platform);
				} else if (browser.equalsIgnoreCase(BrowserType.IE)) {
					capabilities = DesiredCapabilities.internetExplorer();
					capabilities.setCapability("browserName", browser);
					capabilities.setCapability("platform", platform);
				} else if (browser.equalsIgnoreCase(BrowserType.SAFARI)) {
					capabilities = DesiredCapabilities.safari();
					capabilities.setCapability("browserName", browser);
					capabilities.setCapability("platform", platform);
				} else if (browser.equalsIgnoreCase(BrowserType.EDGE)) {
					capabilities = DesiredCapabilities.edge();
					capabilities.setCapability("browserName", browser);
					capabilities.setCapability("platform", platform);
				} else {
					throw new Exception("The given Browser is not available  OS: " + platform + " ,"
							+ " Remote Browser: " + browser);
				}

				driver = new RemoteWebDriver(new URL(selenium_grid_hub + "/wd/hub"), capabilities);
				Log.info("Starting remote webdriver for: OS: " + platform + " ," + " Remote Browser: " + browser);

			} else {

				if (browser.equalsIgnoreCase(BrowserType.FIREFOX)) {
					FirefoxOptions options = new FirefoxOptions();
					options.addPreference("security.insecure_password.ui.enabled", false);
					options.addPreference("security.insecure_field_warning.contextual.enabled", false);
					driver = new FirefoxDriver(options);
				} else if (browser.equalsIgnoreCase(BrowserType.CHROME)) {
					HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
					chromePrefs.put("profile.default_content_settings.popups", 0);
					chromePrefs.put("download.prompt_for_download", "false");
					chromePrefs.put("safebrowsing.enabled", "true");
					ChromeOptions options = new ChromeOptions();
					options.setExperimentalOption("prefs", chromePrefs);
					options.addArguments("safebrowsing-disable-download-protection");
					options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
					driver = new ChromeDriver(options);
				} else if (browser.equalsIgnoreCase(BrowserType.IE)) {
					InternetExplorerOptions options = new InternetExplorerOptions();
					options.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
					driver = new InternetExplorerDriver(options);
				} else if (browser.equalsIgnoreCase(BrowserType.SAFARI)) {
					driver = new SafariDriver();
				} else if (browser.equalsIgnoreCase(BrowserType.EDGE)) {
					EdgeOptions options = new EdgeOptions();
					options.setPageLoadStrategy("eager");
					driver = new EdgeDriver(options);
				} else {
					throw new Exception("The given local browser is not available: " + browser);
				}
				Log.info("Starting Webdriver, Browser: " + browser);
			}
		} catch (Exception e) {
			Log.error("Can't start the webdriver for " + browser + "\n" + e);
			throw (e);
		}
		driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
		setBrowserSizeToMaximum();
	}

	/**
	 * To configure the executable path corresponding to the environment
	 * 
	 * @throws Exception
	 */
	public void setExecutableDriver(String browserName, String platform) throws Exception {

		// String strLocalOS = System.getProperty("os.name");
		String strDriverPath = "";
		// Linux
		if (platform.contains("Linux")) {

			if (browserName.equalsIgnoreCase(BrowserType.CHROME)) {
				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/linux/chromedriver");
				System.setProperty("webdriver.chrome.driver", strDriverPath);

			} else if (browserName.equalsIgnoreCase(BrowserType.FIREFOX)) {

				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/linux/geckodriver");
				System.setProperty("webdriver.gecko.driver", strDriverPath);

			} else {
				throw new Exception("Linux doesn't support this browser [" + browserName + "]");
			}
		}
		// Windows
		else if (platform.contains("Windows")) {
			
			if (browserName.equalsIgnoreCase(BrowserType.CHROME)) {
				
				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/window/chromedriver.exe");
				System.out.println(strDriverPath);
				System.setProperty("webdriver.chrome.driver", strDriverPath);

			} else if (browserName.equalsIgnoreCase(BrowserType.FIREFOX)) {
				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/window/geckodriver.exe");
				System.setProperty("webdriver.gecko.driver", strDriverPath);

			} else if (browserName.equalsIgnoreCase(BrowserType.IE)) {

				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/window/IEDriverServer.exe");
				System.setProperty("webdriver.ie.driver", strDriverPath);

			} else if (browserName.equalsIgnoreCase(BrowserType.EDGE)) {

				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/window/MicrosoftWebDriver.exe");
				System.setProperty("webdriver.edge.driver", strDriverPath);

			} else {
				throw new Exception("Windows doesn't support this browser [" + browserName + "]");
			}
		}
		// MAC OS
		else if (platform.contains("mac")) {

			if (browserName.equalsIgnoreCase(BrowserType.SAFARI)) {
				System.setProperty("webdriver.safari.noinstall", "true");
				SafariOptions options = new SafariOptions();
				driver = new SafariDriver(options);
			} else {
				throw new Exception("MAC doesn't support this browser [" + browserName + "]");
			}
		}
		// Others
		else {
			throw new Exception("Selenium doesn't support this OS [" + platform + "]");
		}

	}

	/**
	 * To configure the executable path corresponding to the environment
	 * 
	 * @throws Exception
	 */
	public void setExecutableDriver() throws Exception {

		String strLocalOS = PropertiesLoader.getPropertiesLoader().selenium_configuration
				.getProperty("selenium.local.platform");
		String strLocalBrowser = PropertiesLoader.getPropertiesLoader().selenium_configuration
				.getProperty("selenium.local.browser");
		String strDriverPath = "";

		// Linux
		if (strLocalOS.contains("Linux")) {

			if (strLocalBrowser.equalsIgnoreCase(BrowserType.CHROME)) {
				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/linux/chromedriver");
				System.setProperty("webdriver.chrome.driver", strDriverPath);

			} else if (strLocalBrowser.equalsIgnoreCase(BrowserType.FIREFOX)) {

				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/linux/geckodriver");
				System.setProperty("webdriver.gecko.driver", strDriverPath);

			} else {
				throw new Exception("Linux doesn't support this browser [" + strLocalBrowser + "]");
			}
		}
		// Windows
		else if (strLocalOS.contains("Windows")) {

			if (strLocalBrowser.equalsIgnoreCase(BrowserType.CHROME)) {

				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/window/chromedriver.exe");
				System.setProperty("webdriver.chrome.driver", strDriverPath);

			} else if (strLocalBrowser.equalsIgnoreCase(BrowserType.FIREFOX)) {
				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/window/geckodriver.exe");
				System.setProperty("webdriver.gecko.driver", strDriverPath);

			} else if (strLocalBrowser.equalsIgnoreCase(BrowserType.IE)) {

				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/window/IEDriverServer.exe");
				System.setProperty("webdriver.ie.driver", strDriverPath);

			} else if (strLocalBrowser.equalsIgnoreCase(BrowserType.EDGE)) {

				strDriverPath = FilePaths.getResourcePath("/config/selenium/driver/window/MicrosoftWebDriver.exe");
				System.setProperty("webdriver.edge.driver", strDriverPath);

			} else {
				throw new Exception("Windows doesn't support this browser [" + strLocalBrowser + "]");
			}
		}
		// MAC OS
		else if (strLocalOS.contains("mac")) {

			if (strLocalBrowser.equalsIgnoreCase(BrowserType.SAFARI)) {
				System.setProperty("webdriver.safari.noinstall", "true");
				SafariOptions options = new SafariOptions();
				driver = new SafariDriver(options);
			} else {
				throw new Exception("MAC doesn't support this browser [" + strLocalBrowser + "]");
			}
		}
		// Others
		else {
			throw new Exception("Selenium doesn't support this OS [" + strLocalOS + "]");
		}
	}

	/**
	 * To set the Browser size depending on Testing Scope
	 */
	public void setBrowserSizeToMaximum() {
		try {
			driver.manage().window().maximize();
		} catch (Exception e) {
		}
	}

	/**
	 * To get driver instance
	 * 
	 * @return
	 */
	public WebDriver getDriver() {
		return this.driver;
	}

	/**
	 * This method is used to close a webdriver
	 * 
	 * @author Hanoi Automation team
	 * @param None
	 * @return None
	 * @throws Exception
	 *             The exception is thrown when can't close the webdriver.
	 */
	public void closeDriver() throws Exception {

		try {

			if (driver != null) {
				driver.quit();
			}
		} catch (Exception e) {

			Log.error("The webdriver is not closed!!! " + e.getMessage());
			HtmlReporter.getTest().fail("The webdriver is not closed!!!").fail(e);
			throw (e);

		}
	}

	/**
	 * Get Browser Type
	 * 
	 * @return Browser Type
	 */
	public String getBrowserType() {

		String strBrowserType = "";

		if (driver instanceof InternetExplorerDriver) {
			strBrowserType = BrowserType.IE;
		} else if (driver instanceof FirefoxDriver) {
			strBrowserType = BrowserType.FIREFOX;
		} else if (driver instanceof ChromeDriver) {
			strBrowserType = BrowserType.CHROME;
		} else if (driver instanceof EdgeDriver) {
			strBrowserType = BrowserType.EDGE;
		} else if (driver instanceof SafariDriver) {
			strBrowserType = BrowserType.SAFARI;
		} else if (driver instanceof RemoteWebDriver) {
			strBrowserType = BrowserType.REMOTE;
		} else {
			strBrowserType = "";
		}

		return strBrowserType;
	}

}
