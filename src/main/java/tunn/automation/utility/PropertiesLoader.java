package tunn.automation.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesLoader {


	private static PropertiesLoader _instance;
	public Properties apppium_android_configuration;
	public Properties appium_ios_configuration;
	public Properties appium_browser_configuration;
	public Properties appium_appium_configuration;
	
	public Properties selenium_configuration;
	public Properties selenium_environment_variable;
	public Properties general_configuration;
	public Properties test_variables;
	
	
	public PropertiesLoader() throws Exception{
		general_configuration = readResourceProperties("/config/config.properties");
		apppium_android_configuration = readResourceProperties("/config/appium/android.properties");
		appium_ios_configuration = readResourceProperties("/config/appium/ios.properties");
		appium_browser_configuration = readResourceProperties("/config/appium/browser.properties");
		appium_appium_configuration = readResourceProperties("/config/appium/appium.properties");
		
		selenium_configuration = readResourceProperties("/config/selenium/selenium.properties");
		String seleniumEnvironment = selenium_configuration.getProperty("selenium.environment");
		if(seleniumEnvironment != null && !seleniumEnvironment.equalsIgnoreCase("")) {
			selenium_environment_variable = readResourceProperties("/config/selenium/environment_properties/" + seleniumEnvironment + ".properties");
		}
		test_variables = new Properties();
	}
	
	public static PropertiesLoader getPropertiesLoader() throws Exception {
		if(_instance == null) {	
			_instance = new PropertiesLoader();
			return _instance;
		}
		else {
			return _instance;
		}
	}

	/**
	 * This method is used to read the configuration file
	 * 
	 * @param fileName
	 *            The path of property file located in project
	 * @return Properties set
	 * @throws Exception
	 */
	public static Properties readProperties(String path) throws Exception {
		Properties prop = new Properties();
		try {
			InputStream input = null;
			input = new FileInputStream(path);
			prop.load(input);
			for (String key : prop.stringPropertyNames()) {
				String configValue = System.getProperty(key);
				if (configValue != null) {
					prop.setProperty(key, configValue);
				}
			}
		} catch (Exception e) {
			System.out.print("Cannot read property file: [" + path + "]");
			throw e;
		}
		return prop;
	}

	/**
	 * This method is used to read the configuration file
	 * 
	 * @param filePath
	 *            The path of property file located in resource folder
	 * @return Properties set
	 * @throws IOException 
	 * @throws Exception
	 */
	public static Properties readResourceProperties(String filePath) throws Exception {
		Properties prop = new Properties();
		try (InputStream inputStream = PropertiesLoader.class.getResourceAsStream(filePath)) {
			prop.load(inputStream);
			for (String key : prop.stringPropertyNames()) {
				String configValue = System.getProperty(key);
				if (configValue != null) {
					prop.setProperty(key, configValue);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return prop;
	}

}
