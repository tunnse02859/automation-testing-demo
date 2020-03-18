package tunn.automation.setup.appium;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import tunn.automation.appium.driver.AppiumBaseDriver;
import tunn.automation.appium.driver.AppiumHandler;
import tunn.automation.report.HtmlReporter;
import tunn.automation.utility.FilePaths;

public class MobileTestBaseSetup {

	// Web driver
	public static AppiumBaseDriver driver;
	// hashmap contains device infor like: platform, deviceName, uuid,
	// browser...... etc
	public HashMap<String, String> deviceInfo;

	@BeforeSuite
	public void beforeSuite() throws Exception {
		/*********** Init Html reporter *************************/
		FilePaths.initReportFolder();
		HtmlReporter.setReporter(FilePaths.getReportFilePath());
		driver = new AppiumHandler().startDriver();
	}

	@BeforeClass
	public void beforeClass() throws Exception {
		HtmlReporter.createTest(this.getClass().getSimpleName(), "");
		Constant.currentTest = this.getClass().getSimpleName();
	}

	@BeforeMethod
	public void beforeMethod(Method method) throws Exception {
		HtmlReporter.createNode(this.getClass().getSimpleName(), method.getName(), "");
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestResult result) throws Exception {
		String mess = "";
		try {
			switch (result.getStatus()) {
				case ITestResult.SUCCESS:
					mess = String.format("The test [%s] is PASSED", result.getName());
					HtmlReporter.pass(mess);
					break;	
				case ITestResult.SKIP:
					mess = String.format("The test [%s] is PASSED", result.getName());
					HtmlReporter.pass(mess);
					break;
				
				case ITestResult.FAILURE:
					mess = String.format("The test [%s] is FAILED", result.getName());
					HtmlReporter.fail(mess, result.getThrowable(), driver.takeScreenshot());;
					break;		
				default:
					break;
			}
		} catch (Exception e) {
		}
		finally {
			driver.resetApp();
		}
		
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() throws Exception {
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() throws Exception {
		HtmlReporter.setSystemInfo("Platform", driver.getDriver().getCapabilities().getPlatform().toString());
		HtmlReporter.setSystemInfo("Platform Version", driver.getDriver().getCapabilities().getVersion());
		HtmlReporter.flush();
		driver.closeDriver();
	}
}
