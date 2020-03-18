package tunn.automation.tests;

import tunn.automation.pages.LoginPage;
import tunn.automation.pages.RegisterPage;
import tunn.automation.setup.appium.Constant;
import tunn.automation.setup.appium.MobileTestSetup;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static tunn.automation.utility.Assertion.*;

import java.lang.reflect.Method;

public class Appium_LoginTest extends MobileTestSetup {
	private LoginPage loginPage;
	private RegisterPage registerPage;

	@BeforeMethod
	public void setupPage(Method method) throws Exception {
		registerPage = new RegisterPage(driver);
		assertTrue(registerPage.isActive(), "Register page didnt showed as default page in first installation",
				"Register page showed as default page");
	}

	@Test
	public void FPC_1290_VerifyUserLoginSuccessful() throws Exception {
		loginPage = registerPage.toLoginPage();
		loginPage.doSuccessLogin(Constant.LOGIN_NON_ONBOARDING_EMAIL_ADDRESS,
				Constant.LOGIN_NON_ONBOARDING_ACCESS_CODE);
	}

}
