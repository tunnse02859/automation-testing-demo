package tunn.automation.tests;

import tunn.automation.excelhelper.ExcelHelper;
import tunn.automation.pages.selenium.DemoGuruLoginPage;
import tunn.automation.setup.selenium.WebTestSetup;
import tunn.automation.utility.FilePaths;

import static tunn.automation.utility.Assertion.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;


import java.lang.reflect.Method;

public class Selenium_LoginTest extends WebTestSetup {
	
	private DemoGuruLoginPage loginPage;
	
	public Selenium_LoginTest() {}
	
	@Factory(dataProvider = "configBrowser")
	public Selenium_LoginTest(String browser, String platform) {
		this.browserName = browser;
		this.platform = platform;
	}
	
	
	@BeforeMethod
	public void setupPage(Method method) throws Exception {
		loginPage = new DemoGuruLoginPage(driver);
	}
	
	@DataProvider(name="data")
	public Object[][] loginData() throws Exception{
		return ExcelHelper.getTableArray(FilePaths.getResourcePath("/dataprovider/login_data.xlsx"), "Sheet1");
	}

	@Test(dataProvider = "data")
	public void FPC_1290_VerifyUserLoginSuccessful(String username,String pass) throws Exception {
		loginPage.login(username, pass);
		assertEquals(driver.getCurrentURL(), "http://demo.guru99.com/v4/manager/Managerhomepage.php", "URL isn't correct", "URL is correct");
	}

}
