package tunn.automation.setup.selenium;

import java.lang.reflect.Method;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import tunn.automation.excelhelper.ExcelHelper;
import tunn.automation.report.Log;
import tunn.automation.utility.FilePaths;
import tunn.automation.utility.PropertiesLoader;


public class WebTestSetup extends WebTestBaseSetup {

	public static String dataFilePath;
	public static String sheetName;
	
	
	public Object[][] getTestProvider(String filepPath, String sheetName) throws Exception {
		// return the data from excel file
		Object[][] data = ExcelHelper.getTableArray(filepPath,sheetName);
		return data;
	}
	
	@DataProvider(name = "configBrowser",parallel = true)
	public Object[][] configBrowser() throws Exception {
		// return the data from excel file
		//Object[][] data = new Object[1][2];
		//String browser = PropertiesLoader.getPropertiesLoader().selenium_configuration.getProperty("selenium.browser"); //chrome,firefox
		//String platform = PropertiesLoader.getPropertiesLoader().selenium_configuration.getProperty("selenium.platform"); 
		//data[0][0] = browser;
		//data[0][1] = platform;
		Object[][] data = ExcelHelper.getTableArray(FilePaths.getResourcePath("/config/selenium/browser_configuration.xlsx"), "Sheet1");
		return data;
	}

	@Override
	@BeforeSuite
	public void beforeSuite() throws Exception {
		super.beforeSuite();

	}

	@Override
	@BeforeClass
	public void beforeClass() throws Exception {
		super.beforeClass();
		Log.startTestCase(this.getClass().getName());
	}

	@Override
	@BeforeMethod
	public void beforeMethod(Method method) throws Exception {
		Log.info("+++++++++ Start testing: " + method.getName() + " ++++++++++++++");
		super.beforeMethod(method);
		driver.openUrl(PropertiesLoader.getPropertiesLoader().selenium_environment_variable.getProperty("default_url"));
		//mngr250783
		//UjapUmu
	}

	@Override
	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestResult result) throws Exception {
		super.afterMethod(result);
	}

	@Override
	@AfterClass(alwaysRun = true)
	public void afterClass() throws Exception {
		Log.endTestCase(this.getClass().getName());
		super.afterClass();
	}

	@Override
	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		super.afterSuite();
	}


	public Integer randomNumber() {
		int number = 0;
		return number;
	}





}
