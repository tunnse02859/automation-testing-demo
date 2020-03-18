package tunn.automation.setup.selenium;

import java.lang.reflect.Method;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import tunn.automation.report.HtmlReporter;
import tunn.automation.selenium.WebDriverMethod;
import tunn.automation.utility.FilePaths;

public class WebTestBaseSetup {

	// Web driver
	public WebDriverMethod driver;
	// browser
	public String browserName;
	// platform
	public String platform;

	@BeforeSuite
	public void beforeSuite() throws Exception {
		FilePaths.initReportFolder();
		HtmlReporter.setReporter(FilePaths.getReportFilePath());
	}

	// @BeforeClass
	// public void beforeClass() throws Exception {
	//
	// driver = new WebDriverMethod(browserName,platform);
	// String description = platform + " - " + browserName;
	// HtmlReporter.createTest(this.getClass().getSimpleName() + " - " +
	// description,"On " + description);
	// }

	@BeforeClass
	public void beforeClass() throws Exception {
		//browserName = PropertiesLoader.getPropertiesLoader().selenium_configuration.getProperty("selenium.browser"); // chrome,firefox
		//platform = PropertiesLoader.getPropertiesLoader().selenium_configuration.getProperty("selenium.platform");

		String description = browserName + " - " + platform;
		HtmlReporter.createTest(this.getClass().getSimpleName() + " - " + description, "On " + description);
		
	}

	@BeforeMethod
	public void beforeMethod(Method method) throws Exception {
		driver = new WebDriverMethod(browserName, platform);
		HtmlReporter.createNode(this.getClass().getSimpleName(), method.getName(),
				// Common.getDataProviderString(data));
				"this is a test");
	}

	@AfterMethod
	public void afterMethod(ITestResult result) throws Exception {
		driver.closeDriver();
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() throws Exception {
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		HtmlReporter.flush();
	}
}
