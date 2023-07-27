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

public class BasePageObject extends BasePageObjectInitializationAndWaits {

	protected String nameOfPageObject;
    protected String typeOfPageObject;
    
    public BasePageObject(BaseInformations baseInformations, boolean driverFactoryInitialize, Object... variables) {
        super(baseInformations, driverFactoryInitialize, variables);
    }
    
    protected final String xpathSafe(String origin) {
        return "concat('" + origin.replace("'", "', \"'\", '") + "', '')";
    }
    
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
        
    protected final void mouseMove(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("mouseMove(WebElement element)");
        }
        Actions act = new Actions(getDriver());
        act.moveToElement(element).build().perform();
    }
    
    protected final void mouseMoveWithOffsets(WebElement element, int xOffset, int yOffset) {
        if (DEBUG_LOGGING) {
            logger.trace("mouseMoveWithOffsets(WebElement element, int xOffset, int yOffset)");
        }
        Actions act = new Actions(getDriver());
        act.moveToElement(element, xOffset, yOffset).build().perform();
    }
    
    protected final void moveAndClick(WebElement targetElement) {
        if (DEBUG_LOGGING) {
            logger.trace("moveAndClick(WebElement targetElement)");
        }
        Actions builder = new Actions(getDriver());
        builder.moveToElement(targetElement).click(targetElement).build().perform();
    }
    
    protected final void clickWithOffset(WebElement targetElement, int xOffset, int yOffset) {
        if (DEBUG_LOGGING) {
            logger.trace("clickWithOffset(WebElement targetElement, int xOffset, int yOffset)");
        }
        Actions act = new Actions(getDriver());
        act.moveToElement(targetElement, xOffset, yOffset).click().build().perform();
    }
    
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
    
    protected final void javaScriptClick(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("javaScriptClick(WebElement element)");
        }
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", element);
    }
    
    protected final void pressDeleteKey(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("pressDeleteKey(WebElement element)");
        }
        waitForElementToBeDisplayed(element).sendKeys(Keys.DELETE);
    }
    
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
    
    //scroll to the top of the page (try to scroll as high that the given element is in the bottom of the screen)
    protected final WebElement scrollWithJavaScript(By element) {
        return scrollWithJavaScript(getBaseInformations().getDriver().findElement(element));
    }
    
    //scroll to the bottom of the page (try to scroll as high that the given element is in the bottom of the screen)
    protected final WebElement scrollWithJavaScriptToTop(WebElement element) {
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        return element;
    }
    
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
    
    protected final WebElement scrollWithJavaScriptToTop(By element) {
        WebElement webElement = getBaseInformations().getDriver().findElement(element);
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        return webElement;
    }
    
    //scroll to the top of the page (try to scroll as high that the given element is in the bottom of the screen)
    protected final WebElement scrollWithJavaScript(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("scrollWithJavaScript(WebElement element)");
        }
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(false);", element);
        return element;
    }
    
    protected final WebElement scrollWithJavaScript(WebElement element, int offset) {
        if (DEBUG_LOGGING) {
            logger.trace("scrollWithJavaScript(WebElement element, int offset)");
        }
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(false);", element);
        ((JavascriptExecutor) getDriver()).executeScript(String.format("scrollBy(0, %s);", offset));
        return element;
    }
    
    //type the text char by char if usual sendkeys fails
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
    
    public final void hardRefresh() {
        if (DEBUG_LOGGING) {
            logger.trace("Hard refresh (Crtl + F5)");
        }
        ((JavascriptExecutor) getDriver()).executeScript("location.reload(true);");
        waitForPageLoaded();
    }
    
    /**
     * 
     * If there is a process/lading Dialog use this method to wait for it to Disappear
     *
     *Make sure that the locator for the Dialog is correct
     *
    public boolean waitForProcessDialogToDisappear() {
        if (DEBUG_LOGGING) {
            logger.trace("waitForProcessDialogToDisappear in");
        }
        waitForPageLoaded();
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
    */
    
    protected final void waitForLoadingCircleToDisappearWithCustomTime(WebElement root, int sec) {
        if (DEBUG_LOGGING) {
            logger.trace("LoadingCircle in");
        }
        By selector = By.cssSelector("sure-progress-bar-circular");
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
    
    protected final void waitForLoadingCircleToDisappear() {
        if (DEBUG_LOGGING) {
            logger.trace("LoadingCircle in");
        }
        By selector = By.cssSelector("sure-progress-bar-circular");
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
    
    protected final void waitForLoadingCircleToDisappear(WebElement root) {
        By selector = By.cssSelector("sure-progress-bar-circular");
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
    
    protected final String selectRandomElementFromCombobox(WebElement root) {
        if (DEBUG_LOGGING) {
            logger.trace("selectRandomElementFromCombobox(WebElement root)");
        }
        root.click();
        waitForElementToBeDisplayed(root, By.cssSelector("input.ui-select-search"));
        waitForElementToBeDisplayed(root, By.xpath(".//li/div/span"));
        
        List<String> elements = root
                .findElements(By.xpath("//div[@ng-model='dialogController.selectedDocument']//li/div/span")).stream()
                .map(element -> element.getText().trim()).collect(Collectors.toList());
        
        String elementName = elements.get(ThreadLocalRandom.current().nextInt(0, elements.size()));
        waitForElementToBeDisplayed(root,
                By.xpath(".//li/div/span[normalize-space(.)='" + elementName + "']"), 5).click();
        // TODO temporary fix
        root.click();
        waitForElementToBeDisplayed(root, By.cssSelector("input.ui-select-search")).sendKeys(Keys.ESCAPE);
        return elementName;
    }
    
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
                    waitForElementToBeDisplayed(By.cssSelector("input.ui-select-search"));
                } catch (AssertionError error) {
                    waitForElementToBeDisplayed(root.findElement(By.cssSelector("span[aria-label='Select box activate']"))).click();
                }
            }
        }
        
        try {
            By locator = By.xpath(".//div[@role = 'option']//span[normalize-space()=" + xpathSafe(name) + "]");
            WebElement row = waitForElementToBePresent(root.findElement(locator), 2000);
            scrollWithJavaScript(row);
            row.click();
            waitForElementToDisappearOnUI(row);
        } catch (AssertionError | NoSuchElementException | ElementNotVisibleException e) {
            try {
                By locator = By.xpath(".//div[@role = 'option']//span[starts-with(normalize-space()," + xpathSafe(name) + ")]");
                WebElement row = waitForElementToBePresent(root.findElement(locator), 2000);
                scrollWithJavaScript(row);
                row.click();
                waitForElementToDisappearOnUI(row);
            } catch (AssertionError |NoSuchElementException | ElementNotVisibleException err) {
                By locator = By.xpath(".//div[@role = 'option']//span[contains(normalize-space(.)," + xpathSafe(name) + ")]");
                try {
                    WebElement row = waitForElementToBePresent(root.findElement(locator), 2000);
                    scrollWithJavaScript(row);
                    row.click();
                    waitForElementToDisappearOnUI(row);
                } catch (AssertionError | NoSuchElementException | ElementNotVisibleException ex) {
                	try {
                    waitForElementToBeDisplayed(root, By.cssSelector("input.ui-select-search")).sendKeys(name.toLowerCase());
                    WebElement row = waitForElementToBePresent(root, locator, 50000);
                    scrollWithJavaScript(row);
                    row.click();
                    waitForElementToDisappearOnUI(row);					
                	} catch (AssertionError | NoSuchElementException | ElementNotVisibleException exc) {
	                	try {
	                		By loc = By.xpath("//div[contains(@ng-attr-id, 'ui-select-choices')]//span[contains(normalize-space(), " + xpathSafe(name) + ")]");
	                    	WebElement el = waitForElementToBePresent(loc, 2000);
	                        scrollWithJavaScript(el);
	                        el.click();
	                        waitForElementToDisappearOnUI(el);					
	                	} catch (AssertionError | NoSuchElementException | ElementNotVisibleException exp) {
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
    
    protected void selectElementFromSuggested(WebElement root, String name) {
        if (DEBUG_LOGGING) {
            logger.trace("selectElementFromSuggested(WebElement root, String name)");
        }
        try {
            By locator = By.xpath(".//li[@role = 'option']//a[normalize-space()=" + xpathSafe(name) + "]");
            waitForElementToBeDisplayed(root.findElement(locator), 2).click();
        } catch (NoSuchElementException | ElementNotVisibleException e) {
            By locator = By.xpath(".//li[@role = 'option']//a[starts-with(normalize-space()," + xpathSafe(name) + ")]");
            try {
                waitForElementToBeDisplayed(root.findElement(locator));
                waitForElementToBeDisplayed(root.findElement(locator)).click();
            } catch (NoSuchElementException | ElementNotVisibleException ex) {
                try {
                    waitForElementToBeDisplayed(root, By.cssSelector("input.ui-select-search")).sendKeys(name.toLowerCase());
                    waitForElementToBeDisplayed(root,	locator, 5).click();
                } catch (AssertionError | NoSuchElementException | ElementNotVisibleException exc) {
                    locator = By.xpath(".//li[.//div[@role = 'option']]//span[normalize-space()=" + xpathSafe(name) + "]");
                    waitForElementToBeDisplayed(root.findElement(locator));
                    waitForElementToBeDisplayed(root.findElement(locator)).click();
                }
            }
        }
        
    }
    
    protected final String getText(WebElement element) {
        if (DEBUG_LOGGING) {
            logger.trace("getText(WebElement element)");
        }
        return element.getAttribute("innerHTML").replaceAll("(<br>|<br\\/>)", " ").replaceAll("<[^>]*>", "").trim();
    }
    
    /**
     * Helping function which checks if the element (or its subelement) is on Top<
     * 
     * @param element
     * @return
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
    
    //Toast message
    private static final By toast = By.cssSelector("md-toast div");
    
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
        return waitForElementToBeDisplayed(getDriver().findElement(toast), By.cssSelector(".ace-toast-message")).getText();
    }
    
    public final void closeToastMessage() {
        waitForPageLoaded();
        try{
            waitForElementToBeDisplayed(getDriver().findElement(toast), By.cssSelector("button")).click();
        } catch (WebDriverException|AssertionError e) {
            //the toast message disappearing now, click has been caught with other element and error has been thrown 
        }
        try {
            waitForElementToDisappearOnUI(toast, 2000);
        } catch (AssertionError e) {
            //still present, most likely another toast
            try {
                waitForElementToBeDisplayed(getDriver().findElement(toast), By.cssSelector("button")).click();
            } catch (WebDriverException|AssertionError e2) {
                //the toast message disappearing now, click has been caught with other element and error has been thrown 
            }
        }
    }
    
    public final boolean isCloseButtonPresent() {
        return isElementPresent(toast) && isElementPresent(getDriver().findElement(toast), By.cssSelector("button[ng-click*='toastController.closeToast()']"));
    }
    
    public final void waitForToastDisappear() {
        waitForElementToDisappearOnUI(toast);
    }
    
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
    
    public final void verifyAndCloseToastMessage(String message) {
        waitForToastToAppear();
        if (!getToastMessage().equals(message))
            throw new AssertionError("The toast message is not the expected");
        closeToastMessage();
    }
    
    public void clickDragAndDrop(WebElement element, int xOffset, int yOffset) {
        new Actions(getDriver()).dragAndDropBy(element, xOffset, yOffset).build().perform();
    }
    
    public boolean isElementEnabled(WebElement element) {
        String disabled = element.getAttribute("disabled");
        if (disabled == null)
            return true;
        if (disabled.equals("true"))
            return false;
        logger.trace("Disabled value: " + disabled);
        throw new AssertionError("Unexpected state of the button");
    }
    
    public boolean isButtonEnabledWithWait(WebElement element) {
        for(int i = 0; i < 10; i++) {
            if(isElementEnabled(element))
                return true;
            waitFor(100);
        }
        return false;
    }
    
    public void openNewTab() {
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("window.open()");
    }
    
    public void switchToTab(int numberOfTab) {
        switchToTab(numberOfTab, getDriver());
    }
    
    public void switchToTab(int numberOfTab, WebDriver driver) {
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(numberOfTab));
    }
    
    public boolean isCheckboxChecked(WebElement checkbox) {
        return checkbox.getAttribute("class").contains("md-checked");
    }
        
    public void dragAndDrop(WebElement from, WebElement to) {
        new Actions(getDriver()).dragAndDrop(from, to).build().perform();
    }
    
    public void enterValueToInputField(WebElement element, String value) {
        enterValueToInputField(element, value, true);
    }
    
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
    
    public void waitForValueEqual(WebElement element, String text) {
        WebDriverWait myWait = new WebDriverWait(getDriver(), 5);
        myWait.until(ExpectedConditions.attributeToBe(element, "value", text));
    }

    public void waitForTextEqual(WebElement element, String text) {
        WebDriverWait myWait = new WebDriverWait(getDriver(), 5);
        myWait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }
    
    private boolean isPageObjectNameAndPageObjectTypePresent() {
        return nameOfPageObject != null && typeOfPageObject != null;
    }
    
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
    
    public void logClick(String nameOfElement, String elementType) {
        baseLog(
                String.format("Click on the \"%s\" %s on the \"%s\" %s", nameOfElement, elementType, nameOfPageObject, typeOfPageObject),
                String.format("Click on the \"%s\" %s", nameOfElement, elementType),
                null,
                null);
    }
    
    public void logClickButton(String nameOfButton) {
        logClick(nameOfButton, "button");
    }
    
    public void logSelectValue(String nameOfSelector, String value) {
        baseLog(
                String.format("Select \"%s\" on the \"%s\" %s", nameOfSelector, nameOfPageObject, typeOfPageObject),
                String.format("Select \"%s\"", nameOfSelector),
                String.format("Select \"%s\" from \"%s\" on the \"%s\" %s", value, nameOfSelector, nameOfPageObject, typeOfPageObject),
                String.format("Select \"%s\" from \"%s\"", value, nameOfSelector)
                );
    }
    
    public void logEnterValueToField(String nameOfField, String value) {
        baseLog(
                String.format("Enter value to \"%s\" field on the \"%s\" %s", nameOfField, nameOfPageObject, typeOfPageObject),
                String.format("Enter value to \"%s\" field", nameOfField),
                String.format("Enter value \"%s\" into \"%s\" on the \"%s\" %s", value, nameOfField, nameOfPageObject, typeOfPageObject),
                String.format("Enter value \"%s\" into \"%s\"", value, nameOfField)
                );
    }
    
    public void logEnterValueToTextArea(String nameOfTextArea, String value) {
        baseLog(
                String.format("Enter value to \"%s\" text area on the \"%s\" %s", nameOfTextArea, nameOfPageObject, typeOfPageObject),
                String.format("Enter value to \"%s\" text area", nameOfTextArea),
                String.format("Enter value \"%s\" into \"%s\" on the \"%s\" %s", value, nameOfTextArea, nameOfPageObject, typeOfPageObject),
                String.format("Enter value \"%s\" into \"%s\"", value, nameOfTextArea)
                );
    }
    
    public void logClickCheckbox(String nameOfCheckbox) {
        logClick(nameOfCheckbox, "checkbox");
    }
    
    public void logClickRadioButton(String nameOfRadioButton) {
        logClick(nameOfRadioButton, "radio button");
    }
    
    public void logClickSwitcher(String nameOfSwitcher) {
        logClick(nameOfSwitcher, "switcher");
    }
    
    public void logClickLink(String nameOfLink) {
        logClick(nameOfLink, "link");
    }
    
    public void logClickIcon(String nameOfLink) {
        logClick(nameOfLink, "icon");
    }
    
    public void logClickTab(String nameOfLink) {
        logClick(nameOfLink, "tab");
    }
    
    public void logClickField(String nameOfField) {
        logClick(nameOfField, "field");
    }
    
    public void logFindElemntFromAnypage(String typeOfElement) {
        logger.debug(String.format("Find \"%s\" from any page on the \"%s\" %s", typeOfElement, nameOfPageObject, typeOfPageObject));
    }
    
    public void handleDowloadPermissions() {
        waitForElementToBeDisplayed(By.xpath("//android.widget.Button[@text='Download']")).click();
    }
    
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
    
    public void closeAlertMessage() {
        getBaseInformations().getDriver()
        .switchTo()
        .alert()
        .accept();
    }
    
    public void closeAlertMessageIfPresent() {
        if(isAlertPresent())
            closeAlertMessage();
    }
    
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
    
}
