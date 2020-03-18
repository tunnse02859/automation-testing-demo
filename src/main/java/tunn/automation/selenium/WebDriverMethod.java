package tunn.automation.selenium;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
//import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import tunn.automation.report.HtmlReporter;
import tunn.automation.report.Log;
import tunn.automation.utility.FilePaths;

public class WebDriverMethod extends WebDriverFactory {

	private static final String DEFAULT_ELEMENT_SEPARATOR = " --- ";
	private static final int DEFAULT_WAITTIME_SECONDS = 20;
	private static final int TIMEOUT_STEPS_SECONDS = 5;

	// some action will make browser load another page
	// adding delay for capture screen
	// private final int delayTime = 1000;

	public WebDriverMethod() throws Exception {
		super();
	}

	public WebDriverMethod(String browser, String platform) throws Exception {
		super(browser, platform);
	}

	public WebElement highlightElement(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].style.border='2px solid red'", element);
		return element;
	}

	public List<WebElement> highlightElement(List<WebElement> elements) {
		for (WebElement e : elements) {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].style.border='2px solid red'", e);
		}
		return elements;
	}

	/**
	 * This method is used to navigate the browser to the url
	 * 
	 * @author Hanoi Automation team
	 * @param url
	 *            the url of website
	 * @return None
	 * @throws Exception
	 *             The exception is thrown if the driver can't navigate to the url
	 */
	public void openUrl(String url) throws Exception {
		try {
			driver.get(url);
			HtmlReporter.pass("Navigated to the url : [" + url + "]", takeScreenshot());
		} catch (Exception e) {
			HtmlReporter.fail("Can't navigate to the url : [" + url + "]", e, takeScreenshot());
			throw (e);

		}
	}

	/**
	 * Set the time out to wait for page load
	 * 
	 * @param seconds
	 *            Wait time in seconds
	 */
	public void setPageLoadTimeout(int seconds) {
		try {
			// driver.manage().timeouts().pageLoadTimeout(seconds,
			// TimeUnit.SECONDS);
			driver.manage().timeouts().setScriptTimeout(seconds, TimeUnit.SECONDS);
		} catch (Exception e) {

		}
	}
	
	public String getCurrentURL() {
		String currentURL = driver.getCurrentUrl();
		HtmlReporter.pass("Currrent URL: ["+ currentURL +"]");
		return currentURL;
	}

	/**
	 * This method is used to wait for the page load
	 * 
	 * @author Hanoi Automation team
	 * @param
	 * @return None
	 * @throws Exception
	 */

	public void waitForPageLoad() {

		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAITTIME_SECONDS);

		// Wait for Javascript to load
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		// JQuery Wait
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return (Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0;
			}
		};

		// Angular Wait
		String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length";
		ExpectedCondition<Boolean> angularLoad = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return (Long) ((JavascriptExecutor) driver).executeScript(angularReadyScript) == 0;
			}
		};

		wait.until(jsLoad);
		// wait.until(jQueryLoad);
		// wait.until(angularLoad);
	}

	/**
	 * This method is used to send keys into a text box without cleaning before.
	 * 
	 * @author Hanoi Automation team
	 * @param elementName
	 *            The name of text box
	 * @param byWebElementObject
	 *            The by object of text box element
	 * @param keysToSend
	 *            The keys are sent
	 * @throws Exception
	 *             The exception is throws if sending keys not success
	 */
	public void sendkeys(String element, String keysToSend) throws Exception {
		String elementName = getElementName(element);
		try {
			findElement(element).sendKeys(keysToSend);
			HtmlReporter.pass("Keys [" + keysToSend + "] are sent to the element: [" + elementName + "]",
					takeScreenshot());
		} catch (Exception e) {
			HtmlReporter.fail("Can't sendkeys to the element: [" + elementName + "]", e, takeScreenshot());
			throw (e);

		}
	}

	/**
	 * This method is used to send keys into a text box.
	 * 
	 * @author Hanoi Automation team
	 * @param elementName
	 *            The name of text box
	 * @param byWebElementObject
	 *            The by object of text box element
	 * @param keysToSend
	 *            The keys are sent
	 * @throws Exception
	 *             The exception is throws if input text not success
	 */
	public void inputText(String element, String keysToSend) throws Exception {
		String elementName = getElementName(element);
		try {
			for (int time = 0; time < DEFAULT_WAITTIME_SECONDS; time += TIMEOUT_STEPS_SECONDS) {
				try {
					WebElement txtElement = findElement(element);
					txtElement.click();
					txtElement.clear();
					txtElement.sendKeys(keysToSend);
					break;
				} catch (StaleElementReferenceException e) {
					wait(TIMEOUT_STEPS_SECONDS);
				}
			}
			HtmlReporter.pass("Text [" + keysToSend + "] is inputted to the element: [" + elementName + "]",
					takeScreenshot());
		} catch (Exception e) {
			HtmlReporter.fail("Can't input text into the element: [" + elementName + "]", e, takeScreenshot());
			throw (e);
		}
	}

	/**
	 * Execute javascript. This method used to execute a javascript
	 * 
	 * @author Hanoi Automation team
	 * @param jsFunction
	 *            the js function
	 * @throws Exception
	 *             The exception is thrown if can't execute java script
	 */
	public void executeJavascript(String jsFunction) throws Exception {
		try {
			((JavascriptExecutor) driver).executeScript(jsFunction);
			Log.info("Excecuted the java script: [" + jsFunction + "]");
			HtmlReporter.pass("Excecuted the java script: [" + jsFunction + "]", takeScreenshot());
		} catch (Exception e) {
			Log.error("Can't excecute the java script: [" + jsFunction + "]");
			Log.error(e.getMessage());
			HtmlReporter.fail("Can't excecute the java script: [" + jsFunction + "]", e, takeScreenshot());
			throw (e);

		}
	}

	/**
	 * This method is used to execute a java script function for an object argument.
	 * 
	 * @author Hanoi Automation team
	 * @param jsFunction
	 *            The java script function
	 * @param object
	 *            The argument to execute script
	 * @throws Exception
	 *             The exception is thrown if object is invalid.
	 */
	public void executeJavascript(String jsFunction, Object object) throws Exception {
		try {
			((JavascriptExecutor) driver).executeScript(jsFunction, object);
			Log.info("Excecute the java script: [" + jsFunction + "] for the object: [" + object + "]");
			HtmlReporter.pass("Excecute the java script: [" + jsFunction + "] for the object: [" + object + "]",
					takeScreenshot());
		} catch (Exception e) {
			Log.error("Can't excecute the java script: [" + jsFunction + "] for the object: [" + object + "]");
			Log.error(e.getMessage());
			HtmlReporter.fail("Can't excecute the java script: [" + jsFunction + "] for the object: [" + object + "]",
					e, takeScreenshot());
			throw (e);

		}
	}

	/**
	 * Get the text of a web element
	 * 
	 * @param elementName
	 *            The name of web element
	 * @param byWebElementObject
	 *            The by object of web element
	 * @return The text of web element
	 * @throws Exception
	 *             The exception is thrown if can't get text successfully.
	 */
	public String getText(String element) throws Exception {
		String elementName = getElementName(element);
		try {

			String text = findElement(element).getText();

			if (text.equals("")) {
				text = getAttribute(element, "value");
			}

			Log.info("Got the text of element [" + elementName + "] is : [" + text + "]");
			HtmlReporter.pass("Got the text of element [" + elementName + "] is : [" + text + "]", takeScreenshot());
			return text;

		} catch (Exception e) {

			Log.error("Can't get text of element: [" + elementName + "]");
			HtmlReporter.fail("Can't get text of element: [" + elementName + "]", e, takeScreenshot());
			return "";

		}
	}

	/**
	 * Get the text of a selected option from a Dropdown list
	 * 
	 * @param elementName
	 *            The name of web element
	 * @param byWebElementObject
	 *            The by object of web element
	 * @return The text of selected element
	 * @throws Exception
	 *             The exception is thrown if can't get text successfully.
	 */
	public String getTextSelectedDDL(String element) throws Exception {
		String elementName = getElementName(element);
		try {
			String text = "";
			for (int time = 0; time < DEFAULT_WAITTIME_SECONDS; time += TIMEOUT_STEPS_SECONDS) {
				try {
					Select ddl = new Select(findElement(element));
					text = ddl.getFirstSelectedOption().getText();
					break;
				} catch (StaleElementReferenceException e) {
					wait(TIMEOUT_STEPS_SECONDS);
				}
			}

			Log.info("Got the text of Dropdown [" + elementName + "] is : [" + text + "]");
			HtmlReporter.pass("Got the text of Dropdown [" + elementName + "] is : [" + text + "]", takeScreenshot());
			return text;

		} catch (Exception e) {

			Log.error("Can't get text of Dropdown: [" + elementName + "]");
			HtmlReporter.fail("Can't get text of Dropdown: [" + elementName + "]", e, takeScreenshot());
			return "";

		}
	}

	/**
	 * Get the text of a Dropdown list
	 * 
	 * @param elementName
	 *            The name of web element
	 * @param byWebElementObject
	 *            The by object of web element
	 * @return The text of a dropdown list
	 * @throws Exception
	 *             The exception is thrown if can't get text successfully.
	 */
	public String getTextDDL(String element) throws Exception {
		String elementName = getElementName(element);
		try {
			String text = "";
			for (int time = 0; time < DEFAULT_WAITTIME_SECONDS; time += TIMEOUT_STEPS_SECONDS) {
				try {
					Select ddl = new Select(findElement(element));
					for (WebElement option : ddl.getOptions()) {
						text = text + option.getText();
					}
					break;
				} catch (StaleElementReferenceException e) {
					wait(TIMEOUT_STEPS_SECONDS);
				}
			}

			Log.info("Got the text of Dropdown [" + elementName + "] is : [" + text + "]");
			HtmlReporter.pass("Got the text of Dropdown [" + elementName + "] is : [" + text + "]", takeScreenshot());
			return text;

		} catch (Exception e) {

			Log.error("Can't get text of Dropdown: [" + elementName + "]");
			HtmlReporter.fail("Can't get text of Dropdown: [" + elementName + "]", e, takeScreenshot());
			return "";

		}
	}

	/**
	 * Get the attribute value of a web element
	 * 
	 * @param elementName
	 *            The name of element
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @param attribute
	 *            The attribute need to get value
	 * @return The attribute value as string
	 * @throws Exception
	 */
	public String getAttribute(String element, String attribute) throws Exception {
		String elementName = getElementName(element);
		try {

			String attributeValue = findElement(element).getAttribute(attribute);

			HtmlReporter.pass("getAttribute of the element [" + elementName + "]: [" + attributeValue + "]",
					takeScreenshot());
			return attributeValue;

		} catch (Exception e) {

			e.printStackTrace();
			HtmlReporter.fail("Can't get the attribute [" + attribute + "] of element: [" + elementName + "]", e,
					takeScreenshot());
			throw e;

		}
	}

	/**
	 * Click on a web element
	 * 
	 * @param elementName
	 *            The name of element
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @throws Exception
	 */
	public void click(String element) throws Exception {
		String elementName = getElementName(element);
		try {
			waitForElementToBeClickable(element, DEFAULT_WAITTIME_SECONDS).click();
			HtmlReporter.pass("Click on the element: [" + elementName + "]", takeScreenshot());
		} catch (Exception e) {
			HtmlReporter.fail("Can't click on the element: [" + elementName + "]", e, takeScreenshot());
			throw (e);
		}
	}

	/**
	 * Perform double click
	 * 
	 * @param by
	 *            The By locator object of element
	 * @param elementName
	 *            Name of element used to write
	 * @return
	 * @throws Exception
	 */

	public void doubleClick(String element) throws Exception {
		String elementName = getElementName(element);
		try {

			Actions action = new Actions(driver);
			action.moveToElement(findElement(element)).doubleClick().build().perform();
			Log.info("DoubleClick [" + elementName + "] successfully");
			HtmlReporter.pass("DoubleClick on the element: [" + elementName + "]", takeScreenshot());

		} catch (Exception e) {
			Log.error("DoubleClick [" + elementName + "] failed");
			HtmlReporter.fail("DoubleClick on the element: [" + elementName + "] failed", e, takeScreenshot());
			throw e;

		}
	}

	/**
	 * Click on a web element using javascript
	 * 
	 * @param elementName
	 *            The name of web element
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @throws Exception
	 */
	public void clickByJS(String element) throws Exception {
		String elementName = getElementName(element);
		try {

			executeJavascript("arguments[0].click();", findElement(element));

			Log.info("Click by JavaScript on the element: [" + elementName + "]");
			HtmlReporter.pass("Click by JavaScript on the element: [" + elementName + "]", takeScreenshot());

		} catch (Exception e) {

			Log.error("Can't click by Java Script on the element: [" + elementName + "]");
			HtmlReporter.fail("Can't click by Java Script on the element: [" + elementName + "]", e, takeScreenshot());
			throw (e);

		}
	}

	/**
	 * Move to the element then click
	 * 
	 * @param elementName
	 * @param elementName
	 *            The name of web element
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @throws Exception
	 */
	public void clickByAction(String element) throws Exception {
		String elementName = getElementName(element);
		try {

			Actions action = new Actions(driver);
			action.moveToElement(findElement(element)).click().build().perform();
			Log.info("Click by Actions on the element: [" + elementName + "]");
			HtmlReporter.pass("Click by Actions on the element: [" + elementName + "]", takeScreenshot());
		} catch (Exception e) {
			Log.error("Click by Actions on [" + elementName + "] failed");
			HtmlReporter.fail("Click by Actions on [" + elementName + "] failed", e, takeScreenshot());
			throw e;

		}
	}

	/**
	 * Select a radio button
	 * 
	 * @param elementName
	 *            The name of element
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @throws Exception
	 */
	public void selectRadioButton(String element) throws Exception {
		String elementName = getElementName(element);
		try {

			WebElement rbElement = findElement(element);

			if (!rbElement.isSelected()) {
				rbElement.click();
			}

			Log.info("Radio button element: [" + elementName + "] is selected.");
			HtmlReporter.pass("Radio button element: [" + elementName + "] is selected.", takeScreenshot());

		} catch (Exception e) {

			Log.error("Radio button element: [" + elementName + "] isn't selected.");
			HtmlReporter.fail("Radio button element: [" + elementName + "] isn't selected.", e, takeScreenshot());
			throw (e);
		}

	}

	/**
	 * Select a check box
	 * 
	 * @param elementName
	 *            The name of element
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @throws Exception
	 */
	public void selectCheckBox(String element) throws Exception {
		String elementName = getElementName(element);
		try {

			WebElement chkElement = findElement(element);

			if (!chkElement.isSelected()) {
				chkElement.click();
			}

			Log.info("Checkbox element: [" + elementName + "] is selected.");
			HtmlReporter.pass("Checkbox element: [" + elementName + "] is selected.", takeScreenshot());

		} catch (Exception e) {

			Log.error("Checkbox element: [" + elementName + "] isn't selected.");
			HtmlReporter.fail("Checkbox element: [" + elementName + "] isn't selected.", e, takeScreenshot());
			throw (e);
		}

	}

	/**
	 * De-select a check box
	 * 
	 * @param elementName
	 *            The name of element
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @throws Exception
	 */
	public void deselectCheckBox(String element) throws Exception {
		String elementName = getElementName(element);
		try {
			WebElement chkElement = findElement(element);

			if (chkElement.isSelected()) {
				chkElement.click();
			}

			Log.info("Checkbox element: " + elementName + " is deselected.");
			HtmlReporter.pass("Checkbox element: [" + elementName + "] is deselected.", takeScreenshot());

		} catch (Exception e) {

			Log.error("Checkbox element: " + elementName + " isn't deselected.");
			HtmlReporter.fail("Checkbox element: [" + elementName + "] isn't deselected.", e, takeScreenshot());
			throw (e);
		}

	}

	/**
	 * Verify Status of check box/selection box
	 * 
	 * @param elementName
	 *            The name of check box/selection box
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @throws Exception
	 */

	public void verifyStatusCheckbox(String element, boolean isSelected) throws Exception {
		String elementName = getElementName(element);
		try {
			WebElement checkbox = findElement(element);
			if (checkbox.isSelected() == isSelected) {
				Log.info("The status of Checkbox [" + elementName + "] is verified");
				HtmlReporter.pass("The status of Checkbox [" + elementName + "] is verified", takeScreenshot());
			} else {
				throw new Exception("The checkbox status is: [" + checkbox.isSelected() + "], but expectation is ["
						+ isSelected + "]");
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
			HtmlReporter.fail("Verify the status of checkbox element: [" + elementName + "] failed", e,
					takeScreenshot());

			throw (e);
		}
	}

	/**
	 * Select an option in the Drop Down list
	 * 
	 * @param elementName
	 *            The element name
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @param chosenOption
	 *            The option is chosen
	 * @throws Exception
	 */
	public void selectDDLByText(String element, String chosenOption) throws Exception {
		String elementName = getElementName(element);
		try {
			for (int time = 0; time < DEFAULT_WAITTIME_SECONDS; time += TIMEOUT_STEPS_SECONDS) {
				try {
					Select ddl = new Select(findElement(element));
					ddl.selectByVisibleText(chosenOption);
					break;
				} catch (StaleElementReferenceException e) {
					wait(TIMEOUT_STEPS_SECONDS);
				}
			}
			Log.info("Select option by Text: [" + chosenOption + "] from select box: [" + elementName + "]");
			HtmlReporter.pass("Select option by Text: [" + chosenOption + "] from select box: [" + elementName + "]",
					takeScreenshot());

		} catch (Exception e) {
			Log.error("Can't select option: [" + chosenOption + "] by Text from the select box: [" + elementName + "]");
			HtmlReporter

					.fail("Can't select option: [" + chosenOption + "] by Text from the select box: [" + elementName
							+ "]", e, takeScreenshot());

			throw (e);
		}
	}

	/**
	 * Select an option in the Drop Down list by value
	 * 
	 * @param elementName
	 *            The element name
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @param value
	 *            The value is chosen
	 * @throws Exception
	 */
	public void selectDDLByValue(String element, String value) throws Exception {
		String elementName = getElementName(element);
		try {

			Select ddl = new Select(findElement(element));
			ddl.selectByValue(value);
			Log.info("Select option by Value: [" + value + "] from select box: [" + elementName + "]");
			HtmlReporter.pass("Select option by Value: [" + value + "] from select box: [" + elementName + "]",
					takeScreenshot());

		} catch (Exception e) {
			Log.error("Can't select option: [" + value + "] by Value from the select box: [" + elementName + "]");
			HtmlReporter.fail(
					"Can't select option: [" + value + "] by Value from the select box: [" + elementName + "]", e,
					takeScreenshot());

			throw e;
		}
	}

	/**
	 * Verify the display of select DDL is correct
	 * 
	 * @param elementName
	 *            The name of element
	 * @param chosenOption
	 *            The text of options list that should be correct as
	 * @param byWebElementObject
	 *            The By of object
	 * @throws Exception
	 */

	public void verifyDisplayDDL(String element, String chosenOption) throws Exception {
		String elementName = getElementName(element);
		try {

			Select ddl = new Select(findElement(element));
			String txt = ddl.getFirstSelectedOption().getText();
			Assert.assertEquals(txt, chosenOption);

			Log.info("Option: [" + chosenOption + "] is displayed correctly on the select box: [" + elementName + "]");
			HtmlReporter.pass(
					"Option: [" + chosenOption + "] is displayed correctly on the select box: [" + elementName + "]",
					takeScreenshot());

		} catch (Exception e) {

			Log.error("Option: [" + chosenOption + "] is not displayed correctly on the select box : [" + elementName
					+ "]");
			HtmlReporter

					.fail("Option: [" + chosenOption + "] is not displayed correctly on the select box : ["
							+ elementName + "]", e, takeScreenshot());

			throw e;

		}
	}

	/**
	 * Wait for a time until VisibilityOfElementLocated
	 * 
	 * @param by
	 *            The by locator object of element
	 * @param time
	 *            Time to wait in seconds
	 * @throws Exception
	 */
	public WebElement waitForVisibilityOfElementLocated(String element, int time) throws Exception {
		String elementName = getElementName(element);
		WebDriverWait wait = new WebDriverWait(driver, time);
		try {
			WebElement e = findElement(element);
			return highlightElement(wait.until(ExpectedConditions.visibilityOf(e)));
		} catch (Exception e) {
			Log.error("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't visible. : " + e);
			HtmlReporter.fail("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't visible", e, takeScreenshot());
			throw e;
		}
	}

	/**
	 * Wait for a time until VisibilityOfElementLocated
	 * 
	 * @param by
	 *            The by locator object of element
	 * @param time
	 *            Time to wait in seconds
	 * @throws Exception
	 */
	public boolean isVisibilityOfElementLocated(String element, int time) throws Exception {
		String elementName = getElementName(element);
		WebDriverWait wait = new WebDriverWait(driver, time);
		try {
			WebElement e = findElement(element);
			highlightElement(wait.until(ExpectedConditions.visibilityOf(e)));
			return true;
		} catch (Exception e) {
			Log.error("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't visible. : " + e);
			HtmlReporter.fail("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't visible", e, takeScreenshot());
			return false;
		}
	}

	/**
	 * Wait for a time until invisibilityOfElementLocated
	 * 
	 * @param by
	 *            The by locator object of element
	 * @param time
	 *            Time to wait in seconds
	 * @throws Exception
	 */
	public void waitForInvisibilityOfElementLocated(String element, int time) throws Exception {
		String elementName = getElementName(element);
		WebDriverWait wait = new WebDriverWait(driver, time);
		WebElement ele = null;
		// findElement, if it throw exception -> no more element -> return
		try {
			ele = findElement(element);
		} catch (Exception e) {
			return;
		}
		// if still can find element -> wait until it invisibble
		try {
			wait.until(ExpectedConditions.invisibilityOf(ele));
		} catch (Exception e) {
			Log.error("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't invisible. : " + e);
			HtmlReporter.fail("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't invisible", e, takeScreenshot());
			throw e;
		}

	}

	/**
	 * Wait for a time until presenceOfElementLocated
	 * 
	 * @param by
	 *            The by locator object of element
	 * @param time
	 *            Time to wait in seconds
	 * @throws Exception
	 */
	public WebElement waitForPresenceOfElementLocated(String element, int time) throws Exception {
		String elementName = getElementName(element);
		// WebDriverWait wait = new WebDriverWait(driver, time);
		try {
			return findElement(element);
			// return wait.until(ExpectedConditions.presenceOf(e));
		} catch (Exception e) {
			Log.error("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't present. : " + e);
			HtmlReporter.fail("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't present", e, takeScreenshot());
			throw e;
		}

	}

	/**
	 * Wait for a time until elementToBeClickable
	 * 
	 * @param by
	 *            The by locator object of element
	 * @param time
	 *            Time to wait in seconds
	 * @throws Exception
	 */
	public WebElement waitForElementToBeClickable(String element, int time) throws Exception {
		String elementName = getElementName(element);
		WebDriverWait wait = new WebDriverWait(driver, time);
		try {
			WebElement e = findElement(element);
			return highlightElement(wait.until(ExpectedConditions.elementToBeClickable(e)));
		} catch (Exception e) {
			HtmlReporter.fail("The element : [" + elementName + "] located by : [" + getElemenLocator(element)
					+ "] isn't able to click", e, takeScreenshot());
			throw e;
		}

	}

	/**
	 * Checking a web element is present or not
	 * 
	 * @param by
	 *            The By locator object of element
	 * @return True if the element is present, False if the element is not present
	 * @throws Exception
	 */
	public boolean isElementPresent(String element) {
		try {
			findElement(element);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get a web element object
	 * 
	 * @param by
	 *            The By locator object of element
	 * @return The WebElement object
	 * @throws Exception
	 */
	public WebElement findElement(String elementInfo) throws Exception {
		WebElement element = null;
		String elementName = getElementName(elementInfo);
		String[] extract = getElemenLocator(elementInfo).split("=", 2);
		String by = extract[0];
		String value = extract[1];
		try {
			waitForPageLoad();
			if (by.equalsIgnoreCase("id")) {
				element = driver.findElement(By.id(value));
			} else if (by.equalsIgnoreCase("xpath")) {
				element = driver.findElement(By.xpath(value));
			} else if (by.equalsIgnoreCase("class")) {
				element = driver.findElement(By.className(value));
			} else if (by.equalsIgnoreCase("css")) {
				element = driver.findElement(By.cssSelector(value));
			} else if (by.equalsIgnoreCase("linkText")) {
				element = driver.findElement(By.linkText(value));
			} else if (by.equalsIgnoreCase("name")) {
				element = driver.findElement(By.name(value));
			} else if (by.equalsIgnoreCase("partialLinkText")) {
				element = driver.findElement(By.partialLinkText(value));
			} else if (by.equalsIgnoreCase("tag")) {
				element = driver.findElement(By.tagName(value));
			}
			highlightElement(element);
			// Log.info("The element : " + by + " is found.");
			// TestngLogger.writeLog("The element : " + by + " is found.");
		} catch (Exception e) {
			Log.error("The element : [" + elementName + "] located by : [" + getElemenLocator(elementInfo)
					+ "] isn't found. : " + e);
			HtmlReporter.fail("The element : [" + elementName + "] located by : [" + getElemenLocator(elementInfo)
					+ "] isn't found", e, takeScreenshot());
			throw (e);
		}
		return element;
	}

	/**
	 * Get a web element object
	 * 
	 * @param by
	 *            The By locator object of element
	 * @return The WebElement object
	 * @throws Exception
	 */
	public List<WebElement> findElements(String elementInfo) throws Exception {
		List<WebElement> listElement = null;
		String elementName = getElementName(elementInfo);
		String[] extract = getElemenLocator(elementInfo).split("=", 2);
		String by = extract[0];
		String value = extract[1];
		try {
			waitForPageLoad();
			if (by.equalsIgnoreCase("id")) {
				listElement = driver.findElements(By.id(value));
			} else if (by.equalsIgnoreCase("xpath")) {
				listElement = driver.findElements(By.xpath(value));
			} else if (by.equalsIgnoreCase("class")) {
				listElement = driver.findElements(By.className(value));
			} else if (by.equalsIgnoreCase("css")) {
				listElement = driver.findElements(By.cssSelector(value));
			} else if (by.equalsIgnoreCase("linkText")) {
				listElement = driver.findElements(By.linkText(value));
			} else if (by.equalsIgnoreCase("name")) {
				listElement = driver.findElements(By.name(value));
			} else if (by.equalsIgnoreCase("partialLinkText")) {
				listElement = driver.findElements(By.partialLinkText(value));
			} else if (by.equalsIgnoreCase("tag")) {
				listElement = driver.findElements(By.tagName(value));
			}
			highlightElement(listElement);
			// Log.info("The element : " + by + " is found.");
			// TestngLogger.writeLog("The element : " + by + " is found.");
		} catch (Exception e) {
			Log.error("The list element : [" + elementName + "] located by : [" + getElemenLocator(elementInfo)
					+ "] isn't found. : " + e);
			HtmlReporter.fail("The list element : [" + elementName + "] located by : [" + getElemenLocator(elementInfo)
					+ "] isn't found", e, takeScreenshot());
			throw (e);
		}
		return listElement;
	}

	/**
	 * Check correction of element text
	 * 
	 * @param by
	 *            The By locator object of element
	 * @return void
	 * @throws Exception
	 */
	public void verifyText(String element, String compareText) throws Exception {
		String elementName = getElementName(element);
		String actualText = "";
		try {

			actualText = getText(element);
			Assert.assertEquals(actualText, compareText);
			Log.info("[" + elementName + "] has correct text [" + actualText + "]");
			HtmlReporter.pass("[" + elementName + "] has correct text [" + actualText + "]", takeScreenshot());

		} catch (AssertionError e) {

			Log.error("The text of element [" + elementName + "] is [" + actualText + "], but expectation is ["
					+ compareText + "]");
			HtmlReporter.fail("The text of element [" + elementName + "] is [" + actualText + "], but expectation is ["
					+ compareText + "]", e, takeScreenshot());
			throw (e);

		}
	}

	/**
	 * Check correction of element text (Contain compare text)
	 * 
	 * @param by
	 *            The By locator object of element
	 * @return The WebElement object
	 * @throws Exception
	 */
	public void verifyNotEqualText(String element, String comparedText) throws Exception {
		String elementName = getElementName(element);
		try {
			String txt = getText(element);
			Assert.assertNotEquals(txt, comparedText);

			Log.info("verifyNotEqualText for the element [" + elementName + "]: Actual [" + txt + "], ComparedText ["
					+ comparedText + "]");
			HtmlReporter.pass("verifyNotEqualText for the element [" + elementName + "]: Actual [" + txt
					+ "], ComparedText [" + comparedText + "]", takeScreenshot());

		} catch (AssertionError e) {
			Log.error("Can't verifyNotEqualText of the element: [" + elementName + "] to the compared text ["
					+ comparedText + "]");
			HtmlReporter

					.fail("Can't verifyNotEqualText of the element: [" + elementName + "] to the compared text ["
							+ comparedText + "]", e, takeScreenshot());

			throw (e);

		}
	}

	/**
	 * Check correction of element text (not equal to compare text)
	 * 
	 * @param by
	 *            The By locator object of element
	 * @return The WebElement object
	 * @throws Exception
	 */
	public void verifyContainText(String element, String containText) throws Exception {
		String elementName = getElementName(element);
		String txt = "";
		try {
			txt = getText(element);
			if (!org.apache.commons.lang3.StringUtils.containsIgnoreCase(txt, containText))
				throw new Exception("Element doesnot contain expected text");

			Log.info("verifyContainText for the element [" + elementName + "]: Actual [" + txt + "], containText ["
					+ containText + "]");
			HtmlReporter.pass("verifyContainText for the element [" + elementName + "]: Actual [" + txt
					+ "], containText [" + containText + "]", takeScreenshot());

		} catch (Exception e) {
			Log.error("The text of element: [" + elementName + "] does not contain [" + containText + "]");
			HtmlReporter.fail("The text of element: [" + elementName + "]: Actual [" + txt + "] does not contain ["
					+ containText + "]", e, takeScreenshot());
			throw (e);
		}
	}

	/**
	 * Compare 2 object
	 * 
	 * @param by
	 *            The By locator object of element
	 * @return The WebElement object
	 * @throws Exception
	 */
	public void verifyEqual(Object actual, Object expected) throws Exception {

		try {

			Assert.assertEquals(actual, expected);
			Log.info("Actual object [" + actual.toString() + "] equals the expected object [" + expected.toString()
					+ "]");
			HtmlReporter.pass("Actual object [" + actual.toString() + "] equals the expected object ["
					+ expected.toString() + "]", takeScreenshot());

		} catch (AssertionError e) {
			Log.error("Actual object [" + actual.toString() + "] not equals the expected object [" + expected.toString()
					+ "]");
			HtmlReporter.fail("Actual object [" + actual.toString() + "] not equals the expected object ["
					+ expected.toString() + "]", e, takeScreenshot());
			throw (e);
		}
	}

	/**
	 * This method is used to verify the hidden text
	 * 
	 * @param elementName
	 *            The friendly name
	 * @param byWebElementObject
	 *            By object
	 * @param compareText
	 *            The expected text
	 * @throws Exception
	 */
	public void verifyHiddenText(String element, String compareText) throws Exception {
		String elementName = getElementName(element);
		try {

			String actualText = getAttribute(element, "textContent").trim().replaceAll("	", "");
			Assert.assertEquals(actualText, compareText);
			Log.info("Hidden element [" + elementName + "] has a correct text [" + actualText + "]");
			HtmlReporter.pass("Hidden element [" + elementName + "] has a correct text [" + actualText + "]",
					takeScreenshot());
		} catch (Exception e) {
			Log.error("The hidden element: [" + elementName + "] is not correct");
			HtmlReporter.fail("The hidden element: [" + elementName + "] is not correct ", e, takeScreenshot());
			throw (e);

		}
	}

	/**
	 * Check correction of title
	 * 
	 * @param elementName
	 *            The name of element
	 * @param compareText
	 *            The text that used to verify
	 * @throws Exception
	 */
	public void verifyTitle(String expectedTitle) throws Exception {

		String title = "";
		try {
			waitForPageLoad();
			title = driver.getTitle();
			Assert.assertEquals(title, expectedTitle);
			Log.info("The title is correct: [" + title + "]");
			HtmlReporter.pass("The title is correct: [" + title + "]", takeScreenshot());
		} catch (Exception e) {
			Log.error("The title [" + title + "] is incorrect");
			HtmlReporter.fail("The title [" + title + "] is incorrect", e, takeScreenshot());
			throw (e);
		}
	}
	/*
		*//**
			 * Upload file
			 * 
			 * @param elementName
			 *            The element name
			 * @param byWebElementObject
			 *            The button Browse
			 * @param url
			 *            Url to file upload
			 * @throws Exception
			 *//*
				 * public void uploadfile(String element, String url) throws Exception { String
				 * elementName = getElementName(element); try { url =
				 * FilePaths.correctPath(url); String strWinTitle = ""; String strBrowserType =
				 * getBrowserType(); if (strBrowserType.equalsIgnoreCase(BrowserType.FIREFOX)) {
				 * strWinTitle = "File Upload"; } else if
				 * (strBrowserType.equalsIgnoreCase(BrowserType.CHROME)) { strWinTitle = "Open";
				 * } else if (strBrowserType.equalsIgnoreCase(BrowserType.EDGE)) { strWinTitle =
				 * "Open"; } else if (strBrowserType.equalsIgnoreCase(BrowserType.IE)) {
				 * strWinTitle = "Choose File to Upload"; } else { // Need to implement on other
				 * browser types } click(element); AutoItX autoit = new AutoItX();
				 * autoit.winWait(strWinTitle); autoit.controlFocus(strWinTitle, "", "Edit1");
				 * autoit.sleep(1000); autoit.ControlSetText(strWinTitle, "", "Edit1", url);
				 * autoit.controlClick(strWinTitle, "", "Button1");
				 * 
				 * HtmlReporter.pass("Upload file [" + url + "] to the element [" + elementName
				 * + "]", takeScreenshot());
				 * 
				 * } catch (Exception e) { Log.error(elementName + " uploaded fail ");
				 * HtmlReporter.fail(elementName + " uploaded fail ", e, takeScreenshot());
				 * throw (e); } }
				 */
	/**
	 * Upload file using autoit exe file
	 * 
	 * @param btnBrowse
	 *            The button Browse
	 * @param strFilePath
	 *            The path to file uploaded
	 * @throws Exception
	 *//*
		 * public void uploadfile2(String element, String strFilePath) throws Exception
		 * { try { strFilePath = FilePaths.correctPath(strFilePath); String strWinTitle
		 * = ""; String strBrowserType = getBrowserType(); if
		 * (strBrowserType.equalsIgnoreCase(BrowserType.FIREFOX)) { strWinTitle =
		 * "File Upload"; } else if
		 * (strBrowserType.equalsIgnoreCase(BrowserType.CHROME)) { strWinTitle = "Open";
		 * } else if (strBrowserType.equalsIgnoreCase(BrowserType.EDGE)) { strWinTitle =
		 * "Open"; } else if (strBrowserType.equalsIgnoreCase(BrowserType.IE)) {
		 * strWinTitle = "Choose File to Upload"; } else { // Need to implement on other
		 * browser types } // Start the executable script to wait for the Upload window
		 * appears new ProcessBuilder(FilePaths.correctPath(
		 * "src\\main\\resource\\AutoIT\\UploadFile.exe"), strWinTitle,
		 * strFilePath).start(); // Click the button Browse click(element);
		 * 
		 * HtmlReporter.pass("Upload file [" + strFilePath + "]", takeScreenshot());
		 * 
		 * } catch (Exception e) { Log.error("Uploaded fail ");
		 * HtmlReporter.fail("Uploaded fail ", e, takeScreenshot());
		 * 
		 * throw (e); } }
		 */

	/**
	 * Upload file using Robot
	 * 
	 * @param filePath
	 *            Url to file upload
	 * @return The WebElement object
	 * @throws Exception
	 */
	public void uploadfile(String filePath) throws Exception {

		try {
			filePath = FilePaths.correctPath(filePath);
			String strBrowserType = getBrowserType();

			StringSelection selection = new StringSelection(filePath);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
			Robot robot = new Robot();
			if (strBrowserType.equalsIgnoreCase(BrowserType.SAFARI)) {
				// Cmd + Tab is needed since it launches a Java app and the
				// browser looses focus
				robot.keyPress(KeyEvent.VK_META);
				robot.keyPress(KeyEvent.VK_TAB);

				robot.keyRelease(KeyEvent.VK_META);
				robot.keyRelease(KeyEvent.VK_TAB);
				robot.delay(500);

				// Open Goto window
				robot.keyPress(KeyEvent.VK_META);
				robot.keyPress(KeyEvent.VK_SHIFT);
				robot.keyPress(KeyEvent.VK_G);

				robot.keyRelease(KeyEvent.VK_META);
				robot.keyRelease(KeyEvent.VK_SHIFT);
				robot.keyRelease(KeyEvent.VK_G);
				robot.delay(500);

				// Paste the clipboard value
				robot.keyPress(KeyEvent.VK_META);
				robot.keyPress(KeyEvent.VK_V);

				robot.keyRelease(KeyEvent.VK_META);
				robot.keyRelease(KeyEvent.VK_V);
				robot.delay(500);

				// Press Enter key to close the Goto window and Upload window
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				robot.delay(500);

				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);

			} else if (strBrowserType.equalsIgnoreCase(BrowserType.EDGE)) {

				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_CONTROL);
				robot.delay(1000);
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);

			} else {

				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_CONTROL);
				robot.delay(1000);
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
			}

			HtmlReporter.pass("Upload file [" + filePath + "]", takeScreenshot());

		} catch (Exception e) {
			Log.error("uploaded fail ");
			HtmlReporter.fail("Uploaded fail ", e, takeScreenshot());

			throw (e);
		}
	}

	/**
	 * Open url in new tab
	 * 
	 * @param url
	 *            Url to of new tab *
	 * @throws Exception
	 */
	public void openNewTab(String url) throws Exception {
		try {
			// Open tab 2 using CTRL + t keys.
			driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
			// Open URL In 2nd tab.
			driver.get(url);
			// Switch to current selected tab's content.
			driver.switchTo().defaultContent();

			HtmlReporter.pass("Open new tab", takeScreenshot());

		} catch (Exception e) {
			Log.error("Open tab failed ");
			HtmlReporter.fail("Open tab failed", e, takeScreenshot());

			throw (e);

		}
	}

	/**
	 * Get the attribute value of a web element
	 * 
	 * @param elementName
	 *            The name of element
	 * @param byWebElementObject
	 *            The By locator object of element
	 * @param attribute
	 *            The attribute need to get value
	 * @param verifyAttribute
	 *            The attribute value used to compare
	 * @return The attribute value as string
	 * @throws Exception
	 */
	public void verifyAttribute(String element, String attribute, String verifyAttribute) throws Exception {
		String elementName = getElementName(element);
		try {

			String attributeValue = getAttribute(element, attribute);
			Assert.assertEquals(attributeValue, verifyAttribute);

			Log.info("The attribute [" + attribute + "] of element: [" + elementName + "] is verified, Actual ["
					+ attributeValue + "], Expected [" + verifyAttribute + "]");
			HtmlReporter.pass("The attribute [" + attribute + "] of element: [" + elementName
					+ "] is verified, Actual [" + attributeValue + "], Expected [" + verifyAttribute + "]",
					takeScreenshot());

		} catch (Exception e) {
			Log.error("Can't verify the attribute [" + attribute + "] of element: [" + elementName + "]");
			HtmlReporter

					.fail("Can't verify the attribute [" + attribute + "] of element: [" + elementName + "]", e,
							takeScreenshot());

			throw e;
		}
	}

	/**
	 * Verify the css value of an element as expectation
	 * 
	 * @param elementName
	 *            The friendly name of element
	 * @param byWebElementObject
	 *            By locator
	 * @param cssAttribute
	 *            css attribute
	 * @param verifyAttribute
	 *            Expected value
	 * @return
	 * @throws Exception
	 */
	public void verifyCSS(String element, String cssAttribute, String verifyAttribute) throws Exception {
		String elementName = getElementName(element);
		try {
			String attributeValue = findElement(element).getCssValue(cssAttribute);
			Assert.assertEquals(attributeValue, verifyAttribute);

			Log.info("The css [" + cssAttribute + "] of element: [" + elementName + "] is verified, Actual ["
					+ attributeValue + "], Expected [" + verifyAttribute + "]");
			HtmlReporter.pass("The css [" + cssAttribute + "] of element: [" + elementName + "] is verified, Actual ["
					+ attributeValue + "], Expected [" + verifyAttribute + "]", takeScreenshot());
		} catch (Exception e) {
			Log.error("Can't verify the css value [" + cssAttribute + "] of element: [" + elementName + "]");
			HtmlReporter.fail("Can't verify the css value [" + cssAttribute + "] of element: [" + elementName + "]", e,
					takeScreenshot());

			throw e;
		}
	}

	/**
	 * Verify the present of an alert
	 * 
	 * @return
	 */
	public boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (Exception Ex) {
			return false;
		}
	}

	/**
	 * Accept an Alert
	 * 
	 * @throws Exception
	 */
	public void acceptAlert() throws Exception {
		try {
			if (isAlertPresent()) {
				driver.switchTo().alert().accept();
				HtmlReporter.pass("Accept Alert", takeScreenshot());
			}
		} catch (Exception e) {
			Log.error("Can't accept Alert");
			HtmlReporter.fail("Can't accept Alert", e, takeScreenshot());

			throw (e);
		}
	}

	/**
	 * Check an element displayed or not
	 * 
	 * @param by
	 *            By Locator
	 * @return
	 * @throws Exception
	 */
	public boolean displayedElement(String element) throws Exception {
		boolean check = false;
		try {
			waitForVisibilityOfElementLocated(element, DEFAULT_WAITTIME_SECONDS);
			check = findElement(element).isDisplayed();
		} catch (Exception e) {
			check = false;
		}
		return check;
	}

	/**
	 * Hide an element by javascript
	 * 
	 * @param by
	 *            By locator
	 * @throws Exception
	 */
	public void hideElement(By by) throws Exception {
		try {
			WebElement element = driver.findElement(by);
			executeJavascript("arguments[0].style.visibility='hidden'", element);
			waitForPageLoad();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Perform mouse hover action
	 * 
	 * @param by
	 *            The By locator object of element
	 * @param elementName
	 *            Name of element used to write
	 * @return
	 * @throws Exception
	 */

	public void mouseHover(String element) throws Exception {
		String elementName = getElementName(element);
		try {
			Actions action = new Actions(driver);
			action.moveToElement(findElement(element)).perform();
			Log.info("mouseHover [" + elementName + "] successfully");
			HtmlReporter.pass("mouseHover [" + elementName + "] successfully", takeScreenshot());
		} catch (Exception e) {
			Log.error("mouseHover [" + elementName + "] failed");
			HtmlReporter.fail("mouseHover [" + elementName + "] failed", e, takeScreenshot());

			throw e;
		}
	}

	/**
	 * Scroll the web page to the element
	 * 
	 * @param by
	 *            The By locator object of element
	 * @return The WebElement object
	 * @throws Exception
	 */
	public void scrollIntoView(String element) throws Exception {
		String elementName = getElementName(element);
		try {

			executeJavascript("arguments[0].scrollIntoView(true);", findElement(element));

			Log.info("Scroll into [" + elementName + "] successfully");
			HtmlReporter.pass("Scroll into [" + elementName + "] successfully", takeScreenshot());

		} catch (Exception e) {
			Log.error("Can not scroll into [" + elementName + "]");
			HtmlReporter.fail("Can not scroll into [" + elementName + "]", e, takeScreenshot());

			throw (e);

		}
	}

	/**
	 * This method is used to capture a screenshot then write to the TestNG Logger
	 * 
	 * @author Hanoi Automation team
	 * 
	 * @return A html tag that reference to the image, it's attached to the
	 *         report.html
	 * @throws Exception
	 */
	public String takeScreenshot() {

		String failureImageFileName = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss.SSS")
				.format(new GregorianCalendar().getTime()) + ".jpg";
		try {

			if (driver != null) {
				File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				String screenShotDirector = FilePaths.getScreenshotFolder();
				FileUtils.copyFile(scrFile, new File(screenShotDirector + File.separator + failureImageFileName));
				return screenShotDirector + File.separator + failureImageFileName;
			}
			return "";
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * This method is used to capture a screenshot
	 * 
	 * @author Hanoi Automation team
	 * 
	 * @return A html tag that reference to the image, it's attached to the
	 *         report.html
	 * @throws Exception
	 */
	public String takeScreenshot(String filename) throws Exception {

		String screenShotDirector = FilePaths.getScreenshotFolder();
		String screenshotFile = FilePaths.correctPath(screenShotDirector + filename);

		try {
			if (driver != null) {
				File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile, new File(screenshotFile));
				return screenshotFile;

			} else {
				return "";
			}
		} catch (Exception e) {
			Log.error("Can't capture the screenshot");
			Log.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * This method is used to capture a screenshot with Ashot
	 * 
	 * @author Hanoi Automation team
	 * @param filename
	 * @return The screenshot path
	 * @throws Exception
	 */
	public String takeScreenshotWithAshot(String fileDir) throws Exception {
		fileDir = FilePaths.correctPath(fileDir);
		try {

			if (driver != null) {
				Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
						.takeScreenshot(driver);
				ImageIO.write(screenshot.getImage(), "jpg", new File(fileDir));
			} else {
				fileDir = "";
			}

		} catch (Exception e) {
			Log.error("Can't capture the screenshot");
			Log.error(e.getMessage());
			throw e;
		}
		return fileDir;
	}

	/**
	 * This method is used to capture an element's screenshot with Ashot
	 * 
	 * @author Hanoi Automation team
	 * @param filename
	 * @return The screenshot path
	 * @throws Exception
	 */
	public String takeScreenshotWithAshot(String fileDir, String element) throws Exception {
		fileDir = FilePaths.correctPath(fileDir);
		try {

			if (driver != null) {
				WebElement e = findElement(element);
				Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
						.takeScreenshot(driver, e);
				ImageIO.write(screenshot.getImage(), "jpg", new File(fileDir));
			}

		} catch (Exception e) {
			Log.error("Can't capture the screenshot");
			Log.error(e.getMessage());
			throw e;
		}
		return fileDir;

	}

	/**
	 * To compare the layout of a web page with baseline
	 * 
	 * @param filename
	 *            The name of screenshot
	 * @throws Exception
	 */
/*	public void compareScreenshot(String filename) throws Exception {
		String screenshotFileName = filename + "." + Common.constants.getProperty("SCREENSHOT_FORMAT");
		String baseLineImage = HtmlReporter.strBaseLineScreenshotFolder + screenshotFileName;
		String actualImage = HtmlReporter.strActualScreenshotFolder + screenshotFileName;
		// String diffImage = Common.strWebDiffScreenshotFolder + screenshotFileName;

		try {
			waitForPageLoad();
			if (!Common.pathExist(baseLineImage)) {
				takeScreenshotWithAshot(baseLineImage);
			} else {
				takeScreenshotWithAshot(actualImage);
				ImageCompare imageComparitor = new ImageCompare();
				BufferedImage diffBuff = imageComparitor.diffImages(baseLineImage, actualImage, 30, 10);
				if (diffBuff == null) {
					Log.info("The actual screenshot of page [" + filename + "] matches with the baseline");
				} else {
					Log.error("The actual screenshot of page [" + filename + "] doesn't match with the baseline");
					ImageIO.write(diffBuff, Common.constants.getProperty("SCREENSHOT_FORMAT"),
							new File(HtmlReporter.strDiffScreenshotFolder, screenshotFileName));
					throw new Exception("The actual screenshot doesn't match with the baseline");
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}*/

	/**
	 * To compare the layout of a web element with baseline
	 * 
	 * @param filename
	 *            The name of screenshot
	 * @throws Exception
	 */
/*	public void compareScreenshot(String filename, String element) throws Exception {
		String screenshotFileName = filename + "." + Common.constants.getProperty("SCREENSHOT_FORMAT");
		String baseLineImage = HtmlReporter.strBaseLineScreenshotFolder + screenshotFileName;
		String actualImage = HtmlReporter.strActualScreenshotFolder + screenshotFileName;
		// String diffImage = Common.strWebDiffScreenshotFolder + screenshotFileName;

		try {
			waitForPageLoad();
			if (!Common.pathExist(baseLineImage)) {
				takeScreenshotWithAshot(baseLineImage, element);
			} else {
				takeScreenshotWithAshot(actualImage, element);
				ImageCompare imageComparitor = new ImageCompare();
				BufferedImage diffBuff = imageComparitor.diffImages(baseLineImage, actualImage, 30, 10);
				if (diffBuff == null) {
					Log.info("The actual screenshot of element [" + filename + "] matches with the baseline");
				} else {
					Log.error("The actual screenshot of element [" + filename + "] doesn't match with the baseline");
					ImageIO.write(diffBuff, Common.constants.getProperty("SCREENSHOT_FORMAT"),
							new File(HtmlReporter.strDiffScreenshotFolder, screenshotFileName));
					throw new Exception(
							"The actual screenshot of element [" + filename + "] doesn't match with the baseline");
				}

			}
		} catch (Exception e) {
			throw e;
		}
	}*/

	public void switchWindowByTitle(String title) throws Exception {
		try {
			boolean switched = false;
			Set<String> allWindows = driver.getWindowHandles();
			for (String windowHandle : allWindows) {
				driver.switchTo().window(windowHandle);
				if (driver.getTitle().equals(title)) {
					HtmlReporter.pass("Switched to Window with title:" + title);
					Log.info("Switched to Window with title:" + title);
					switched = true;
					break;
				}
			}
			if (!switched) {
				HtmlReporter.fail("Cannot find any window with title: " + title + " to switch", takeScreenshot());
				Log.error("Cannot find any window with title: " + title + " to switch");
			}
		} catch (Exception e) {
			HtmlReporter.fail("Switched to Window with title:" + title, takeScreenshot());
			Log.error("Switched to Window with title:" + title + "\n" + e);
		}
	}

	public String getElementName(String element) {
		return element.split(DEFAULT_ELEMENT_SEPARATOR)[1];
	}

	public String getElemenLocator(String element) {
		return element.split(DEFAULT_ELEMENT_SEPARATOR)[0];
	}

	public void wait(int second) throws InterruptedException {
		Thread.sleep(second * 1000);
	}

}
