package tunn.automation.tests;

import tunn.automation.api.FinstroAPI;
import tunn.automation.report.Log;
import tunn.automation.setup.appium.Constant;
import tunn.automation.setup.appium.MobileTestSetup;
import tunn.automation.utility.Common;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class APICallExample extends MobileTestSetup {
	private FinstroAPI finstroAPI;

	@BeforeMethod
	public void setupPage(Method method) throws Exception {
	}

	@Test
	public void FPC_1292_VerifyUserLoginUnsuccessfulIfHeInputInvalidEmailAddress() throws Exception {
		//login
		finstroAPI = new FinstroAPI();
		finstroAPI.login(Constant.LOGIN_NON_ONBOARDING_EMAIL_ADDRESS, Constant.LOGIN_NON_ONBOARDING_ACCESS_CODE)
			.then()
				.verifyResponseCode(200)
				.extractJsonValue("accessToken").flush();
		finstroAPI.setAccessToken(Common.getTestVariable("accessToken",true));
		
		//call recovery data 
		finstroAPI.recoveryData()
			.then()
				.verifyResponseCode(200)
				.verifyJsonNodeEqual("creditApplicationStatus", "Incomplete")
				.extractJsonValue("myVariable", "id")
			.flush();
		
		Log.info("My Variable: [" + Common.getTestVariable("myVariable", true) + "]");
	}
}
