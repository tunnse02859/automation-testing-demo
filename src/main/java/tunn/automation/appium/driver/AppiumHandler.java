package tunn.automation.appium.driver;

import io.appium.java_client.remote.MobilePlatform;
import tunn.automation.utility.PropertiesLoader;

public class AppiumHandler {

	public AppiumBaseDriver startDriver() throws Exception {

		AppiumBaseDriver driver; 
		
		String platform = PropertiesLoader.getPropertiesLoader().appium_appium_configuration.getProperty("appium.platform");
		String awsPlatform = System.getenv("DEVICEFARM_DEVICE_PLATFORM_NAME");
		if (awsPlatform != null) {	
			if (awsPlatform.equalsIgnoreCase(MobilePlatform.ANDROID)) {
				AppiumAndroidDriver android = new AppiumAndroidDriver();
				android.createAWSDriver();
				driver = android;
			} else if (awsPlatform.equalsIgnoreCase(MobilePlatform.IOS)) {	
				AppiumIOsDriver ios = new AppiumIOsDriver();
				ios.createAWSDriver();
				driver = ios;
			}
			else {
				throw new Exception(String.format("The platform [%s] is not supported", awsPlatform));
			}
		} else {
			if (platform.equalsIgnoreCase(MobilePlatform.ANDROID)) {
				AppiumAndroidDriver android = new AppiumAndroidDriver();
				android.createDriver();
				driver = android;
			} else if (platform.equalsIgnoreCase(MobilePlatform.IOS)) {
				AppiumIOsDriver ios = new AppiumIOsDriver();
				ios.createDriver();
				driver = ios;
			}
			else {
				throw new Exception(String.format("The platform [%s] is not supported", platform));
			}
		}
		driver.setDefaultImplicitWaitTime();
		return driver;
	}
}
