package tunn.automation.pages.selenium;

import tunn.automation.selenium.WebDriverMethod;

public class DemoGuruLoginPage {
	
	private WebDriverMethod driver;
	
	String usernameTf = "xpath=//*[@name='uid'] --- Username textfield";
	String passwordTf = "xpath=//*[@name='password'] --- Password textfield";
	String submitBtn = "xpath=//*[@name='btnLogin'] --- Submit button";
	
	public DemoGuruLoginPage(WebDriverMethod driver) {
		this.driver = driver;
	}
	
	public void login(String user, String pass) throws Exception {
		driver.inputText(usernameTf, user);
		driver.inputText(passwordTf, pass);
		driver.click(submitBtn);
	}
		
}
