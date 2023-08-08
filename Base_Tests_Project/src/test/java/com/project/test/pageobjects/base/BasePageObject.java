package com.project.test.pageobjects.base;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.project.test.util.OperationSystem;

/**
 * 
 * @author Valentino Milanov
 *
 * Base page object with all the methods for Page Object handling
 */
public class BasePageObject extends BasePageObjectInitializationAndWaits {

	protected String nameOfPageObject;
    protected String typeOfPageObject;
    
    public BasePageObject(BaseInformations baseInformations, boolean driverFactoryInitialize, Object... variables) {
        super(baseInformations, driverFactoryInitialize, variables);
    }
    
//	Text handling
    /**
     * Making String text xpath safe
     * @param origin
     * @return
     */
    protected final String xpathSafe(String origin) {
        return "concat('" + origin.replace("'", "', \"'\", '") + "', '')";
    }
    
    /**
     * Copy selected text
     */
    public final void copySelectedText() {
        logger.debug("Copy selected text with the hotkey (Ctrl + C)");
        Actions actions = new Actions(getDriver());
        switch (getBaseInformations().getOperationSystem()) {
            case WINDOWS:
                actions.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).build().perform();
                break;
            case MAC_IOS:
                actions.sendKeys(Keys.chord(Keys.COMMAND, "c"), "").build().perform();
                break;
            case ANDROID:
                actions.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).build().perform();
                break;
        }
    }
    
    /**
     * Get double value from String
     * @param number	String value
     * @return	Double value
     */
    public Double getDoubleFromString(String number) {
        Double multCoef = 1.0;
        if(number.contains("k"))
            multCoef = 1000.0;
        return Double.parseDouble(number.replaceAll("[A-Za-z]", "").trim()) * multCoef;
    }
    
    /**
     * Takes a parent element and strips out the textContent of all child elements and returns textNode content only
     * 
     * @param e the parent element
     * @return the text from the child textNodes
     */
    public static String getTextNode(WebElement e)
    {
        String text = e.getText().trim();
        List<WebElement> children = e.findElements(By.xpath("./*"));
        for (WebElement child : children)
        {
            text = text.replaceFirst(child.getText(), "").trim();
        }
        return text;
    }

// Element handling using Actions class
    
    /**
     * Moving mouse with Action class to a Web Element
     * @param element	Web Element where the mouse has to be moved
     */
    protected final void mouseMove(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("mouseMove(WebElement element)");
        }
        Actions act = new Actions(getDriver());
        act.moveToElement(element).build().perform();
    }
    
    /**
     * Moving mouse with Action class to a Web Element with offset
     * @param element	Web Element where the mouse has to be moved
     * @param xOffset	x-axis offset
     * @param yOffset	y-axis offset
     */
    protected final void mouseMoveWithOffsets(WebElement element, int xOffset, int yOffset) {
        if (DEBUG_LOGGING) {
            logger.trace("mouseMoveWithOffsets(WebElement element, int xOffset, int yOffset)");
        }
        Actions act = new Actions(getDriver());
        act.moveToElement(element, xOffset, yOffset).build().perform();
    }
    
    /**
     * Moving mouse with Action class to a Web Element and click on it
     * @param targetElement		Web Element that has to be clicked
     */
    protected final void moveAndClick(WebElement targetElement) {
        if (DEBUG_LOGGING) {
            logger.trace("moveAndClick(WebElement targetElement)");
        }
        Actions builder = new Actions(getDriver());
        builder.moveToElement(targetElement).click(targetElement).build().perform();
    }
    
    /**
     * Moving mouse with Action class to a Web Element and click on it
     * @param targetElement		Web Element that has to be clicked
     * @param xOffset	x-axis offset
     * @param yOffset	y-axis offset
     */
    protected final void clickWithOffset(WebElement targetElement, int xOffset, int yOffset) {
        if (DEBUG_LOGGING) {
            logger.trace("clickWithOffset(WebElement targetElement, int xOffset, int yOffset)");
        }
        Actions act = new Actions(getDriver());
        act.moveToElement(targetElement, xOffset, yOffset).click().build().perform();
    }
    
    /**
     * Control (ctrl) click on a web element, mostly used for selecting elements
     * @param targetElement		element that has to be selected
     */
    protected final void controlClick(WebElement targetElement) {
        if (DEBUG_LOGGING) {
            logger.trace("controlClick(WebElement targetElement)");
        }
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        
        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            Actions actions = new Actions(getDriver());
            actions.keyDown(Keys.COMMAND)
            .click(targetElement)
            .keyUp(Keys.COMMAND)
            .build()
            .perform();
        } else {
            Actions actions = new Actions(getDriver());
            actions.keyDown(Keys.CONTROL)
            .click(targetElement)
            .keyUp(Keys.CONTROL)
            .build()
            .perform();
        }
    }
    
    /**
     * Drag and drop element using Actions class
     * @param element
     * @param xOffset
     * @param yOffset
     */
    public void clickDragAndDrop(WebElement element, int xOffset, int yOffset) {
        new Actions(getDriver()).dragAndDropBy(element, xOffset, yOffset).build().perform();
    }
    
    /**
     * Drag end drop element to another element using Actions class
     * @param from	element that has to be dragged
     * @param to	element to which the dragging element the element should be dragged
     */
    public void dragAndDrop(WebElement from, WebElement to) {
        new Actions(getDriver()).dragAndDrop(from, to).build().perform();
    }
    
// Element andling using JS
    /**
     * Click on Web Element using JS
     * @param element	element that has to be clicked
     */
    protected final void javaScriptClick(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("javaScriptClick(WebElement element)");
        }
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", element);
    }
    
    /**
     * waiting for element and pressing Delete key
     * @param element
     */
    protected final void pressDeleteKey(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("pressDeleteKey(WebElement element)");
        }
        waitForElementToBeDisplayed(element).sendKeys(Keys.DELETE);
    }
    
    /**
     * Waiting for Ajax Web Element to disappear
     */
    protected final void waitForAjaxWorkingToDisappear() {
        if (DEBUG_LOGGING) {
            logger.trace("waitForAjaxWorkingToDisappear()");
        }
        getDriver().manage().timeouts().implicitlyWait(getWebDriverWaitMethods().WAIT_TIME_IN_MILLIS / WebDriverWaitMethods.numberOfCiclesForWait, TimeUnit.MILLISECONDS);
        try {
            int count = 0;
            while (count < WebDriverWaitMethods.numberOfCiclesForWait) {
                WebDriverWait wdw = getWebDriverWaitMethods().getWait(getWebDriverWaitMethods().WAIT_TIME_IN_MILLIS);
                ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        try {
                            WebElement bodyElement = driver.findElement(By.cssSelector("body"));
                            if (bodyElement.getAttribute("class").equals("ajaxWorking"))
                                return false;
                            else
                                return true;
                        } catch (NullPointerException e) {
                            return true;
                        } catch (Exception e) {
                            if (DEBUG_LOGGING) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    }
                };
                
                try {
                    if (wdw.until(condition))
                        return;
                } catch (TimeoutException e) {
                    count++;
                    if (DEBUG_LOGGING) {
                        logger.trace("TimeoutException");
                    }
                }  catch (Exception e) {
                    count++;
                    getWebDriverWaitMethods().shortWait(getWebDriverWaitMethods().WAIT_TIME_IN_MILLIS);
                    if (DEBUG_LOGGING) {
                        logger.trace("Exception: " + e.getMessage());
                    }
                }
            }
            throw new AssertionError("After " + getWebDriverWaitMethods().WAIT_TIME_IN_MILLIS + " class atribute of body element"
                    + "still contains 'ajaxWorking' value.");
        } finally {
            getWebDriverWaitMethods().setTimeoutToDefault(getDriver());
        }
    }

// Scrolling
    /**
     * scroll to the top of the page (try to scroll as high that the given element is in the bottom of the screen) using locator
     * @param element	locator for Web Element that has to be on the bottom of the page
     * @return	Web Element
     */
    protected final WebElement scrollWithJavaScript(By element) {
        return scrollWithJavaScript(getBaseInformations().getDriver().findElement(element));
    }
    
    /**
     * scroll to the bottom of the page (try to scroll as low that the given element is in the top of the screen)
     * @param element	Web Element that has to be on the top of the page
     * @return	Web Element
     */
    protected final WebElement scrollWithJavaScriptToTop(WebElement element) {
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        return element;
    }
    
    /**
     * scroll to the page so that the element is in the middle of the page
     * @param element	Web Element that has to be in the middle of the page
     * @return	Web Element
     */
    protected final WebElement scrollWithJavaScriptToMiddle(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("scrollWithJavaScriptToMiddle(WebElement element)");
        }
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({\r\n"
                + "            behavior: 'auto',\r\n"
                + "            block: 'center',\r\n"
                + "            inline: 'center'\r\n"
                + "        });", element);
        return element;
    }
    
    /**
     * scroll to the bottom of the page (try to scroll as low that the given element is in the top of the screen) using locator
     * @param element	locator for Web Element that has to be on the top of the page
     * @return	Web Element
     */
    protected final WebElement scrollWithJavaScriptToTop(By element) {
        WebElement webElement = getBaseInformations().getDriver().findElement(element);
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        return webElement;
    }
    
    /**
     * scroll to the top of the page (try to scroll as high that the given element is in the bottom of the screen)
     * @param element	Web Element that has to be on the bottom of the page
     * @return	Web Element
     */
    protected final WebElement scrollWithJavaScript(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("scrollWithJavaScript(WebElement element)");
        }
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(false);", element);
        return element;
    }
    
    /**
     * Scroll with JS to element with offset
     * @param element	Web Element to scroll on
     * @param offset	y-axis offset
     * @return		Web Element
     */
    protected final WebElement scrollWithJavaScript(WebElement element, int offset) {
        if (DEBUG_LOGGING) {
            logger.trace("scrollWithJavaScript(WebElement element, int offset)");
        }
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(false);", element);
        ((JavascriptExecutor) getDriver()).executeScript(String.format("scrollBy(0, %s);", offset));
        return element;
    }
    
 //Alert handlig
    /**
     * Verification if alert message is present
     * @return
     */
    public boolean isAlertPresent() {
        WebDriverWait wait = new WebDriverWait(getDriver(), 10);
        try {
            if (wait.until(ExpectedConditions.alertIsPresent()) == null)
                return false;
            else
                return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }
    
    /**
     * Method for closing alert message
     */
    public void closeAlertMessage() {
        getBaseInformations().getDriver()
        .switchTo()
        .alert()
        .accept();
    }
    
    /**
     * Method for closing alert message if it is present
     */
    public void closeAlertMessageIfPresent() {
        if(isAlertPresent())
            closeAlertMessage();
    }
    
// Input fields
    /**
     * Metohod to type the text char by char if usual sendKeys method fails
     * @param element	input Web Element
     * @param text		text for typing
     */
    protected final void secureSendkeys(WebElement element, String text) {
        if (DEBUG_LOGGING) {
            logger.trace("secureSendkeys(WebElement element, String text)");
        }
        element.sendKeys(Keys.DELETE);
        element.sendKeys(text);
        String insertedValue = element.getAttribute("value");
        if (!insertedValue.equals(text)) {
            // Failed, must send characters one by one
            if (DEBUG_LOGGING) {
                logger.trace("sendkeys failed, expected: " + text + " found in inbox: " + insertedValue + ". Now we try to type text char by char");
            }
            element.clear();
            for(int i = 0; i < text.length(); i++) {
                element.sendKeys("" + text.charAt(i));
                waitFor(50);
            }
        }	
    }
    
    /**
     * Method for entering value to input field
     * @param element input field
     * @param value
     */
    public void enterValueToInputField(WebElement element, String value) {
        enterValueToInputField(element, value, true);
    }
    
    /**
     * Method for entering value to input field with check of input value after entering
     * @param element	input field
     * @param value
     * @param checkExpected
     */
    public void enterValueToInputField(WebElement element, String value, boolean checkExpected) {
        scrollWithJavaScript(element);
        try {
            waitForElementToBeDisplayed(element);
            element.click();
            element.clear();
        } catch (Exception e) {
            
        }
        if(getBaseInformations().getOperationSystem().equals(OperationSystem.ANDROID)) {
            waitFor(3000);
            element.click();
            element.clear();
        }
        try {
            waitForValueEqual(element, "");
        } catch (Exception e) {
            element.clear();
            if (!element.getAttribute("value").isEmpty()) {
                element.sendKeys(Keys.CONTROL + "a");
                element.sendKeys(Keys.DELETE);
                if (!element.getAttribute("value").isEmpty()) {
                    throw new Error("The input field default value is not deleted");
                }
            }
        }
        element.sendKeys(value);
        if (checkExpected) {
            waitForPageLoaded();
            waitForElementToBeDisplayed(element);
            waitForValueEqual(element, value);
        }
    }
    
//Wait methods    
    
    /**
     * Wait for 40 sec for the ajax to finish
     */
    protected final void waitForAjaxToFinish() {
        if (DEBUG_LOGGING) {
            logger.trace("waitForAjaxToFinish");
        }
        int timeout = 0;
        
        while (timeout < 120) {
            boolean ajaxWorking = (boolean) ((JavascriptExecutor) getDriver())
                    .executeScript("return !!jQuery && jQuery.active == 0");
            if (ajaxWorking)
                return;
            timeout++;
            //logger.trace("ajax...");
            waitFor(500);
        }
        
        throw new AssertionError("The ajax does not finish its job in 60 sec");
    }
    
    /**
     * Wait for page loading to finish
     */
    public final void waitForPageLoaded() {
        if (DEBUG_LOGGING) {
            logger.trace("waitForPageLoaded");
        }
        int timeout = 0;
        
        while (timeout < 2*60*5) {
            boolean ajaxWorking = (boolean) ((JavascriptExecutor) getDriver())
                    .executeScript("try {\n" +
                            "  if (document.readyState !== 'complete') {\n" +
                            "    return false; // Page not loaded yet\n" +
                            "  }\n" +
                            "  if (window.jQuery) {\n" +
                            "    if (window.jQuery.active) {\n" +
                            "      return false;\n" +
                            "    } else if (window.jQuery.ajax && window.jQuery.ajax.active) {\n" +
                            "      return false;\n" +
                            "    }\n" +
                            "  }\n" +
                            "  if (window.angular) {\n" +
                            "    if (!window.qa) {\n" +
                            "      // Used to track the render cycle finish after loading is complete\n" +
                            "      window.qa = {\n" +
                            "        doneRendering: false\n" +
                            "      };\n" +
                            "    }\n" +
                            "    // Get the angular injector for this app (change element if necessary)\n" +
                            "    var injector = window.angular.element('body').injector();\n" +
                            "    // Store providers to use for these checks\n" +
                            "    var $rootScope = injector.get('$rootScope');\n" +
                            "    var $http = injector.get('$http');\n" +
                            "    var $timeout = injector.get('$timeout');\n" +
                            "    // Check if digest\n" +
                            "    if ($rootScope.$$phase === '$apply' || $rootScope.$$phase === '$digest' || $http.pendingRequests.length !== 0) {\n" +
                            "      window.qa.doneRendering = false;\n" +
                            "      return false; // Angular digesting or loading data\n" +
                            "    }\n" +
                            "    if (!window.qa.doneRendering) {\n" +
                            "      // Set timeout to mark angular rendering as finished\n" +
                            "      $timeout(function() {\n" +
                            "        window.qa.doneRendering = true;\n" +
                            "      }, 0);\n" +
                            "      return false;\n" +
                            "    }\n" +
                            "  }\n" +
                            "  return true;\n" +
                            "} catch (ex) {\n" +
                            "  return false;\n" +
                            "}");
            if (ajaxWorking)
                return;
            timeout++;
            if (DEBUG_LOGGING) {
                logger.trace("page loaded?");
            }
            waitFor(200);
        }
        
        throw new AssertionError("The page load not finished in 120 sec");
    }
    
    /**
     * Wait 10 for alert dialog to appear
     */
    @SuppressWarnings("deprecation")
    protected final Alert waitForAlert() {
        if (DEBUG_LOGGING) {
            logger.trace("waitForAlert");
        }
        int timeout = 0;
        while (timeout < WebDriverWaitMethods.numberOfCiclesForWait) {
            try {
                waitFor(getWebDriverWaitMethods().WAIT_TIME_IN_MILLIS / WebDriverWaitMethods.numberOfCiclesForWait);
                return getDriver().switchTo().alert();
            } catch (NoAlertPresentException e) {
                timeout++;
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionError("Unexpected exception happened when" + " the test waited for an alert dialog");
            }
        }
        
        throw new AssertionError("The alert dialog did not appear in 10 seconds");
    }
    
    /**
     * Wait until JS animation is finished
     * @param cssLocator	animation
     */
    protected final void waitUntilAnimationIsDone(final String cssLocator) {
        if (DEBUG_LOGGING) {
            logger.trace("waitUntilAnimationIsDone(final String cssLocator)");
        }
        WebDriverWait wdw = new WebDriverWait(getDriver(), getWebDriverWaitMethods().WAIT_TIME_IN_MILLIS);
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                String temp = ((JavascriptExecutor) driver)
                        .executeScript("return jQuery(\"" + cssLocator + "\").is(':animated')").toString();
                return temp.equalsIgnoreCase("false");
            }
        };
        
        try {
            wdw.until(expectation);
        } catch (TimeoutException e) {
            throw new AssertionError("Animation of element didnt finished in time: " + cssLocator);
        }
    }
    
    /**
     * Waiting for loading circle to disappear with custom wait time
     * @param root	root element that has to be loaded
     * @param sec	waiting time in seconds
     */
    protected final void waitForLoadingCircleToDisappearWithCustomTime(WebElement root, int sec) {
        if (DEBUG_LOGGING) {
            logger.trace("LoadingCircle in");
        }
        //FIXME fix the loading circle locator
        By selector = By.cssSelector("progress-bar-circular");
        WebElement circleUnderRoot;
        try {
            circleUnderRoot = waitForElementToBeDisplayed(root, selector, sec * 1000);
        } catch (Throwable e) {
            if (DEBUG_LOGGING) {
                logger.trace("circle not appeared");
            }
            return;
        }
        for(int i = 0; i < sec; i++)
            if(waitForElementToDisappearOnUI(circleUnderRoot, 1000))
                return;
        throw new AssertionError("The loading circle still visible");
        
    }
    
    /**
     * Waiting for loading circle to disappear
     */
    protected final void waitForLoadingCircleToDisappear() {
        if (DEBUG_LOGGING) {
            logger.trace("LoadingCircle in");
        }
        By selector = By.cssSelector("progress-bar-circular");	 //FIXME fix the loading circle locator
        int count = 0;
        try {
            waitForElementToBeDisplayed(selector, 2 * 1000);
        } catch (Throwable e) {
            if (DEBUG_LOGGING) {
                logger.trace("circle not appeared");
            }
            return;
        }
        while (!waitForElementToDisappearOnUI(selector, 500) && count < 240) {
            count++;
        }
        if (count == 500) {
            throw new AssertionError("The loading circle still visible");
        }
        if (DEBUG_LOGGING) {
            logger.trace("LoadingCircle out");
        }
    }
    
    /**
     * Waiting for loading circle to disappear with root element
     * @param root	root element that has to be loaded
     */
    protected final void waitForLoadingCircleToDisappear(WebElement root) {
        By selector = By.cssSelector("progress-bar-circular");	//FIXME fix the loading circle locator
        int count = 0;
        try {
            waitForElementToBeDisplayed(root, selector, 500);
        } catch (Throwable e) {
            if (DEBUG_LOGGING) {
                logger.trace("circle not appeared");
            }
            return;
        }
        while (!waitForElementToDisappearOnUI(selector, 500) & count < 120) {
            count++;
            waitFor(1000);
        }
        if (count == 120) {
            throw new AssertionError("The loading circle still visible");
        }
    } 
    
    /**
     * Wait for value to be expected
     * @param element	Web Element
     * @param text	Expected value
     */
    public void waitForValueEqual(WebElement element, String text) {
        WebDriverWait myWait = new WebDriverWait(getDriver(), 5);
        myWait.until(ExpectedConditions.attributeToBe(element, "value", text));
    }

    /**
     * Wait for text to be the expected
     * @param element	Web Element
     * @param text	Expected text
     */
    public void waitForTextEqual(WebElement element, String text) {
        WebDriverWait myWait = new WebDriverWait(getDriver(), 5);
        myWait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }
    
    /**
     *	If there is a process/lading Dialog use this method to wait for it to Disappear
     */
    public boolean waitForProcessDialogToDisappear() {
        if (DEBUG_LOGGING) {
            logger.trace("waitForProcessDialogToDisappear in");
        }
        waitForPageLoaded();
        //FIXME fix the process/landing dialog locator
        if (isElementPresent(By.cssSelector("md-dialog"), 500)) {
            try {
                BaseDialog baseDialog = new BaseDialog(baseInformations);
                if (baseDialog.getDialogTitle().equals("Please wait...")) {
                    baseDialog.waitForDialogToDisappear();
                    return true;
                }
                return false;
            } catch (Throwable e) {
                if (DEBUG_LOGGING) {
                    logger.trace("The dialog disappeared in an instant, return true " + e.getMessage().split("\\n")[0]);
                }
                return true;
            }
        } else {
            if (DEBUG_LOGGING) {
                logger.trace("waitForProcessDialogToDisappear false");
            }
            return false;
        }
    }
    
//Combo-box handling   
 
    /**
     * Selecting a random element from combo-box
     * @param root
     * @return	Name of selected element
     */
    protected final String selectRandomElementFromCombobox(WebElement root) {
        if (DEBUG_LOGGING) {
            logger.trace("selectRandomElementFromCombobox(WebElement root)");
        }
        root.click();
        //FIXME fix combo-box input locator
        waitForElementToBeDisplayed(root, By.cssSelector("input.ui-select-search"));
        //FIXME fix combo-box list locator
        waitForElementToBeDisplayed(root, By.xpath(".//li/div/span"));
        
        //FIXME fix combo-box list locator
        List<String> elements = root
                .findElements(By.xpath("//div[@ng-model='dialogController.selectedDocument']//li/div/span")).stream()
                .map(element -> element.getText().trim()).collect(Collectors.toList());
        
        String elementName = elements.get(ThreadLocalRandom.current().nextInt(0, elements.size()));
        //FIXME Fix combo-box element locator
        waitForElementToBeDisplayed(root,
                By.xpath(".//li/div/span[normalize-space(.)='" + elementName + "']"), 5).click();
        // TODO temporary fix
        root.click();
        waitForElementToBeDisplayed(root, By.cssSelector("input.ui-select-search")).sendKeys(Keys.ESCAPE);
        return elementName;
    }
    
    /**
     * Select element from combo-box by name
     * @param root	combo-box root
     * @param name	name of the combo-box element
     */
    protected void selectElementFromComboBox(WebElement root, String name) {
        if (DEBUG_LOGGING) {
            logger.trace("selectElementFromComboBox(WebElement root, String name)");
        }
        try {
            root.click();
        }
        catch (ElementNotInteractableException e) {
            waitForElementToBeDisplayed(root.findElement(By.cssSelector("span[aria-label='Select box activate']"))).click();
            //for mobile sometimes click does not work, it needs to 'fix' it
            if(baseInformations.getOperationSystem().equals(OperationSystem.ANDROID)) {
                try {
                	//FIXME fix combo-box input locator
                    waitForElementToBeDisplayed(By.cssSelector("input.ui-select-search"));
                } catch (AssertionError error) {
                	//FIXME fix alternativ combo-box input locator
                    waitForElementToBeDisplayed(root.findElement(By.cssSelector("span[aria-label='Select box activate']"))).click();
                }
            }
        }
        
        try {
        	//FIXME Fix combo-box element locator
            By locator = By.xpath(".//div[@role = 'option']//span[normalize-space()=" + xpathSafe(name) + "]");
            WebElement row = waitForElementToBePresent(root.findElement(locator), 2000);
            scrollWithJavaScript(row);
            row.click();
            waitForElementToDisappearOnUI(row);
        } catch (AssertionError | NoSuchElementException | ElementNotVisibleException e) {
            try {
            	//FIXME Fix combo-box element locator
                By locator = By.xpath(".//div[@role = 'option']//span[starts-with(normalize-space()," + xpathSafe(name) + ")]");
                WebElement row = waitForElementToBePresent(root.findElement(locator), 2000);
                scrollWithJavaScript(row);
                row.click();
                waitForElementToDisappearOnUI(row);
            } catch (AssertionError |NoSuchElementException | ElementNotVisibleException err) {
            	//FIXME Fix combo-box element locator
                By locator = By.xpath(".//div[@role = 'option']//span[contains(normalize-space(.)," + xpathSafe(name) + ")]");
                try {
                    WebElement row = waitForElementToBePresent(root.findElement(locator), 2000);
                    scrollWithJavaScript(row);
                    row.click();
                    waitForElementToDisappearOnUI(row);
                } catch (AssertionError | NoSuchElementException | ElementNotVisibleException ex) {
                	try {
                		//FIXME fix combo-box input locator
	                    waitForElementToBeDisplayed(root, By.cssSelector("input.ui-select-search")).sendKeys(name.toLowerCase());
	                    WebElement row = waitForElementToBePresent(root, locator, 50000);
	                    scrollWithJavaScript(row);
	                    row.click();
	                    waitForElementToDisappearOnUI(row);					
	                	} catch (AssertionError | NoSuchElementException | ElementNotVisibleException exc) {
		                	try {
		                		//FIXME Fix combo-box element locator
		                		By loc = By.xpath("//div[contains(@ng-attr-id, 'ui-select-choices')]//span[contains(normalize-space(), " + xpathSafe(name) + ")]");
		                    	WebElement el = waitForElementToBePresent(loc, 2000);
		                        scrollWithJavaScript(el);
		                        el.click();
		                        waitForElementToDisappearOnUI(el);					
		                	} catch (AssertionError | NoSuchElementException | ElementNotVisibleException exp) {
		                		//FIXME Fix combo-box element locator
		                		By loc = By.xpath("//div[contains(@ng-attr-id, 'ui-select-choices')]//span//span[contains(normalize-space(.)," + xpathSafe(name) + ")]");
		                    	WebElement el = waitForElementToBePresent(loc, 2000);
		                        scrollWithJavaScript(el);
		                        el.click();
		                        waitForElementToDisappearOnUI(el);
		                		}
	                	}
                }
            }
        }
    }
    
    /**
     * Select an element from suggested list by name
     * @param root	root input with suggested list
     * @param name	name of the suggested element
     */
    protected void selectElementFromSuggested(WebElement root, String name) {
        if (DEBUG_LOGGING) {
            logger.trace("selectElementFromSuggested(WebElement root, String name)");
        }
        try {
        	//FIXME Fix suggested element locator
            By locator = By.xpath(".//li[@role = 'option']//a[normalize-space()=" + xpathSafe(name) + "]");
            waitForElementToBeDisplayed(root.findElement(locator), 2).click();
        } catch (NoSuchElementException | ElementNotVisibleException e) {
        	//FIXME Fix suggested element locator
            By locator = By.xpath(".//li[@role = 'option']//a[starts-with(normalize-space()," + xpathSafe(name) + ")]");
            try {
                waitForElementToBeDisplayed(root.findElement(locator));
                waitForElementToBeDisplayed(root.findElement(locator)).click();
            } catch (NoSuchElementException | ElementNotVisibleException ex) {
                try {
                	//FIXME fix input field locator
                    waitForElementToBeDisplayed(root, By.cssSelector("input.ui-select-search")).sendKeys(name.toLowerCase());
                    waitForElementToBeDisplayed(root,	locator, 5).click();
                } catch (AssertionError | NoSuchElementException | ElementNotVisibleException exc) {
                	//FIXME Fix suggested element locator
                    locator = By.xpath(".//li[.//div[@role = 'option']]//span[normalize-space()=" + xpathSafe(name) + "]");
                    waitForElementToBeDisplayed(root.findElement(locator));
                    waitForElementToBeDisplayed(root.findElement(locator)).click();
                }
            }
        }
        
    }
    
    /**
     * Get text form web element
     * @param element Web Element
     * @return	Text from Web Element
     */
    protected final String getText(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("getText(WebElement element)");
        }
        return element.getAttribute("innerHTML").replaceAll("(<br>|<br\\/>)", " ").replaceAll("<[^>]*>", "").trim();
    }
    
    /**
     * Check if element is clickable, no element is on top of that element, and click will not be intercepted
     * Helping function which checks if the element (or its sub-element) is on Top
     * @param element	Web Element that has to be clickable
     * @return	boolean if the element is clickable
     */
    protected final boolean isClickable(WebElement element) {
        
        return (boolean) ((JavascriptExecutor) getDriver()).executeScript(
                "function isDescendant(parent, child) {" + 
                        "     var node = child.parentNode;" + 
                        "     while (node !== null) {" + 
                        "         if (node === parent) {" + 
                        "             return true;" + 
                        "         }" + 
                        "         node = node.parentNode;" + 
                        "     }" + 
                        "     return false;" + 
                        "}" +
                        "var elm = arguments[0];" + 
                        "var doc = elm.ownerDocument || document;" + 
                        "var rect = elm.getBoundingClientRect();" +
                        "var actualElement = doc.elementFromPoint(rect.left + (rect.width / 2), rect.top + (rect.height / 2));" +
                        "return elm === actualElement || (actualElement !== null && isDescendant(elm, actualElement))", element);
    }
    
//Toast message handling
    private static final By toast = By.cssSelector("md-toast div");	//FIXME fix toast message or remove if needed
    
    /**
     * Wait for toast message to be present
     */
    public final void waitForToastToAppear() {
        waitForElementToBeDisplayed(toast, 50 * 1000);
    }
    
    /**
     * Verify that toast is present on UI or not
     * @return
     */
    public final boolean isToastMessagePresent() {
        try{
            return isElementPresent(toast, 500);
        } catch (AssertionError e) {
            return false;
        }
    }
    
    /**
     * Gets the text from the toast(status or info) message
     * @return
     */
    public final String getToastMessage() {
    	//FIXME fix toast message locator
        return waitForElementToBeDisplayed(getDriver().findElement(toast), By.cssSelector(".ace-toast-message")).getText();
    }
    
    /**
     * Closing toast message
     */
    public final void closeToastMessage() {
        waitForPageLoaded();
        try{
        	//FIXME fix close button locator
            waitForElementToBeDisplayed(getDriver().findElement(toast), By.cssSelector("button")).click();
        } catch (WebDriverException|AssertionError e) {
            //the toast message disappearing now, click has been caught with other element and error has been thrown 
        }
        try {
            waitForElementToDisappearOnUI(toast, 2000);
        } catch (AssertionError e) {
            //still present, most likely another toast
            try {
            	//FIXME fix close button locator
                waitForElementToBeDisplayed(getDriver().findElement(toast), By.cssSelector("button")).click();
            } catch (WebDriverException|AssertionError e2) {
                //the toast message disappearing now, click has been caught with other element and error has been thrown 
            }
        }
    }
    
    /**
     * Verify that close button is present on toast message
     * @return
     */
    public final boolean isCloseButtonPresent() {
        return isElementPresent(toast) && isElementPresent(getDriver().findElement(toast), 
        		By.cssSelector("button[ng-click*='toastController.closeToast()']"));	//FIXME fix close button locator
    }
    
    /**
     * Wait for toast message to disappear
     */
    public final void waitForToastDisappear() {
        waitForElementToDisappearOnUI(toast);
    }
    
    /**
     * Close toast message if it is present
     */
    public final void closeToastIfPresent() {
        try {
            if (isToastMessagePresent())
                if(isCloseButtonPresent()) {
                    closeToastMessage();
                    waitFor(100);
                } else {
                    waitForToastDisappear();
                }
        } catch (StaleElementReferenceException|NoSuchElementException e) {
            //ignore exception when the toast message disappeared in the meantime
        }
    }
    
    /**
     * Verify toast message and close it
     * @param message expected toast message
     */
    public final void verifyAndCloseToastMessage(String message) {
        waitForToastToAppear();
        if (!getToastMessage().equals(message))
            throw new AssertionError("The toast message is not the expected");
        closeToastMessage();
    }
    
// Windows/Tab handling
    /**
     * Open new tab
     */
    public void openNewTab() {
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("window.open()");
    }
    
    /**
     * Switch to tab by index
     * @param numberOfTab	index of tab
     */
    public void switchToTab(int numberOfTab) {
        switchToTab(numberOfTab, getDriver());
    }
    
    /**
     * Switch to tab by index and Web Driver
     * @param numberOfTab	index of tab
     * @param driver	Web Driver
     */
    public void switchToTab(int numberOfTab, WebDriver driver) {
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(numberOfTab));
    }

//Checkbox handling    
    
    /**
     * Verify if checkbox is checked
     * @param checkbox	Web Element checkbox for verification
     * @return	verification if checkbox is checked
     */
    public boolean isCheckboxChecked(WebElement checkbox) {
        return checkbox.getAttribute("class").contains("md-checked");	//FIXME fix locator for checked 
    }
    
// Logging steps   
    
    /**
     * Base method for logging different type of steps
     * @param fullLog	full log with name of element, element type, name of page object, type of page object
     * @param shortLog	short log with name of element and element type
     * @param traceFullLog	full console trace with name of element, element type, name of page object, type of page object
     * @param traceShortLog	short console with name of element and element type
     */
    private void baseLog(String fullLog, String shortLog, String traceFullLog, String traceShortLog) {
        String debugString;
        String traceString;
        if(isPageObjectNameAndPageObjectTypePresent()) {
            debugString = fullLog.trim();
            traceString = traceFullLog;
        } else {
            debugString = shortLog.trim();
            traceString = traceShortLog;
        }
        logger.debug(debugString);
        if (traceString != null) {
            logger.trace(traceString.trim());
        }
    }
    
    /**
     * Logging method for 'click on element' step
     * @param nameOfElement
     * @param elementType
     */
    public void logClick(String nameOfElement, String elementType) {
        baseLog(
                String.format("Click on the \"%s\" %s on the \"%s\" %s", nameOfElement, elementType, nameOfPageObject, typeOfPageObject),
                String.format("Click on the \"%s\" %s", nameOfElement, elementType),
                null,
                null);
    }
    
    /**
     * Logging method for 'click on button' step
     * @param nameOfButton
     */
    public void logClickButton(String nameOfButton) {
        logClick(nameOfButton, "button");
    }
    
    /**
     * Logging method for 'select value' step
     * @param nameOfSelector
     * @param value
     */
    public void logSelectValue(String nameOfSelector, String value) {
        baseLog(
                String.format("Select \"%s\" on the \"%s\" %s", nameOfSelector, nameOfPageObject, typeOfPageObject),
                String.format("Select \"%s\"", nameOfSelector),
                String.format("Select \"%s\" from \"%s\" on the \"%s\" %s", value, nameOfSelector, nameOfPageObject, typeOfPageObject),
                String.format("Select \"%s\" from \"%s\"", value, nameOfSelector)
                );
    }
    
    /**
     * Logging method for 'enter value to input field' step
     * @param nameOfField
     * @param value
     */
    public void logEnterValueToField(String nameOfField, String value) {
        baseLog(
                String.format("Enter value to \"%s\" field on the \"%s\" %s", nameOfField, nameOfPageObject, typeOfPageObject),
                String.format("Enter value to \"%s\" field", nameOfField),
                String.format("Enter value \"%s\" into \"%s\" on the \"%s\" %s", value, nameOfField, nameOfPageObject, typeOfPageObject),
                String.format("Enter value \"%s\" into \"%s\"", value, nameOfField)
                );
    }
    
    /**
     * Logging method for 'enter value to text area' step
     * @param nameOfTextArea
     * @param value
     */
    public void logEnterValueToTextArea(String nameOfTextArea, String value) {
        baseLog(
                String.format("Enter value to \"%s\" text area on the \"%s\" %s", nameOfTextArea, nameOfPageObject, typeOfPageObject),
                String.format("Enter value to \"%s\" text area", nameOfTextArea),
                String.format("Enter value \"%s\" into \"%s\" on the \"%s\" %s", value, nameOfTextArea, nameOfPageObject, typeOfPageObject),
                String.format("Enter value \"%s\" into \"%s\"", value, nameOfTextArea)
                );
    }
    
    /**
     * Logging method for 'click on checkbox' step
     * @param nameOfCheckbox
     */
    public void logClickCheckbox(String nameOfCheckbox) {
        logClick(nameOfCheckbox, "checkbox");
    }
    
    /**
     * Logging method for 'click on radio button' step
     * @param nameOfRadioButton
     */
    public void logClickRadioButton(String nameOfRadioButton) {
        logClick(nameOfRadioButton, "radio button");
    }
    
    /**
     * Logging method for 'click switcher' step
     * @param nameOfSwitcher
     */
    public void logClickSwitcher(String nameOfSwitcher) {
        logClick(nameOfSwitcher, "switcher");
    }
    
    /**
     * Logging method for 'click on link' step
     * @param nameOfLink
     */
    public void logClickLink(String nameOfLink) {
        logClick(nameOfLink, "link");
    }
    
    /**
     * Logging method for 'click on icon' step
     * @param nameOfLink
     */
    public void logClickIcon(String nameOfLink) {
        logClick(nameOfLink, "icon");
    }
    
    /**
     * Logging method for 'click on tab' step
     * @param nameOfLink
     */
    public void logClickTab(String nameOfLink) {
        logClick(nameOfLink, "tab");
    }
    
    /**
     * Logging method for 'click on field' step
     * @param nameOfField
     */
    public void logClickField(String nameOfField) {
        logClick(nameOfField, "field");
    }
    
    /**
     * Logging method for 'find element on any page' step
     * @param typeOfElement
     */
    public void logFindElemntFromAnypage(String typeOfElement) {
        logger.debug(String.format("Find \"%s\" from any page on the \"%s\" %s", typeOfElement, nameOfPageObject, typeOfPageObject));
    }
    
// Other
    
    /**
     * Verify if Page Object name and Type is present
     * @return
     */
    private boolean isPageObjectNameAndPageObjectTypePresent() {
        return nameOfPageObject != null && typeOfPageObject != null;
    }
    
    /**
     * Verify if Web Element is enabled
     * @param element Web Element for verification
     * @return boolean if element is enabled
     */
    public boolean isElementEnabled(WebElement element) {
        String disabled = element.getAttribute("disabled");
        if (disabled == null)
            return true;
        if (disabled.equals("true"))
            return false;
        logger.trace("Disabled value: " + disabled);
        throw new AssertionError("Unexpected state of the button");
    }
    
    /**
     * Verify if Web Element is enabled with wait time for element to be enabled
     * @param element	Web Element for verificatio
     * @return	boolean if element is enabled
     */
    public boolean isButtonEnabledWithWait(WebElement element) {
        for(int i = 0; i < 10; i++) {
            if(isElementEnabled(element))
                return true;
            waitFor(100);
        }
        return false;
    }
    
    /**
     * Using Robot class to pick a file from Windows file selector
     * @param filePath
     * @throws AWTException
     */
    public void useRobot(String filePath) throws AWTException {
        Robot rb = new Robot();
        rb.delay(1000);
        StringSelection str = new StringSelection(filePath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);

        rb.keyPress(KeyEvent.VK_CONTROL);
        rb.keyPress(KeyEvent.VK_V);
        rb.delay(500);
        rb.keyRelease(KeyEvent.VK_CONTROL);
        rb.keyRelease(KeyEvent.VK_V);
        rb.delay(500);
        rb.keyPress(KeyEvent.VK_ENTER);
        rb.delay(500);
        rb.keyRelease(KeyEvent.VK_ENTER);
    }
    
    /*
     * Hard refresh of page using Ctrl + F5
     */
    public final void hardRefresh() {
        if (DEBUG_LOGGING) {
            logger.trace("Hard refresh (Crtl + F5)");
        }
        ((JavascriptExecutor) getDriver()).executeScript("location.reload(true);");
        waitForPageLoaded();
    }
    
//Mobile 
    /**
     * Method for allowing download of files on mobile phones
     */
    public void handleDowloadPermissions() {
        waitForElementToBeDisplayed(By.xpath("//android.widget.Button[@text='Download']")).click();
    }
    
}
