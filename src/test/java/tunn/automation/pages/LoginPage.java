package tunn.automation.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import tunn.automation.appium.driver.AppiumBaseDriver;

import static tunn.automation.utility.Assertion.*;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

	public AppiumBaseDriver driver;

	@AndroidFindBy(id = "au.com.finstro.finstropay:id/snackbar_text")
	@iOSXCUITFindBy(iOSClassChain = "name BEGINSWITH 'ERROR'")
	private WebElement errorMessage;
	@AndroidFindBy(id = "au.com.finstro.finstropay:id/snackbar_action")
	private WebElement errorType;

	@AndroidFindBy(id = "au.com.finstro.finstropay:id/login_email_edt")
	@iOSXCUITFindBy(accessibility = "email")
	private WebElement emailAddress;

	@AndroidFindBy(id = "au.com.finstro.finstropay:id/login_access_code_edt")
	@iOSXCUITFindBy(accessibility = "accessCode")
	private WebElement accessCode;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().resourceId(\"au.com.finstro.finstropay:id/btnSubmit\"))")
	@iOSXCUITFindBy(accessibility = "submit")
	private WebElement submit;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().resourceId(\"au.com.finstro.finstropay:id/sign_up_link\"))")
	@iOSXCUITFindBy(accessibility = "signup")
	private WebElement registerPageLink;

	@AndroidFindBy(uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().resourceId(\"au.com.finstro.finstropay:id/forgot_access_code\"))")
	@iOSXCUITFindBy(accessibility = "forgotAccesscode")
	private WebElement forgotAccessCodePageLink;

	@iOSXCUITFindBy(accessibility = "Maybe Later")
	private WebElement touchID_DontSave;

	@iOSXCUITFindBy(accessibility = "Not Now")
	private WebElement savePass_NotNow;

	public LoginPage(AppiumBaseDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver.getDriver()), this);
	}

	public boolean isActive() {
		return driver.waitForElementDisplayed(emailAddress, 10);
	}

	public void toForgotAccessCodePage() throws Exception {
		driver.clickByPosition(forgotAccessCodePageLink, "right");
	}

	public void toRegisterPage() throws Exception {
		driver.clickByPosition(registerPageLink, "right");
	}

	public void login(String email, String code) throws Exception {
		driver.inputTextWithClear(emailAddress, email);
		driver.inputTextWithClear(accessCode, code);
		driver.click(submit);
	}

	public void doSuccessLogin(String email, String code) throws Exception {
		login(email, code);
		if (driver.isIOSDriver()) {
			driver.setImplicitWaitTime(10);
			if (driver.isElementDisplayed(savePass_NotNow)) {
				driver.click(savePass_NotNow);
			}
			if (driver.isElementDisplayed(touchID_DontSave)) {
				driver.click(touchID_DontSave);
			}
			driver.setDefaultImplicitWaitTime();
		}
	}
}
