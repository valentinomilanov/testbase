package com.project.test.pageobjects.base;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverWaitMethods {

	 protected static boolean DEBUG_LOGGING;
	    protected static final Logger logger = LoggerFactory.getLogger(WebDriverWaitMethods.class);
	    protected static final int DEFAULT_WAIT_TIME_IN_MILLIS = 15000;
	    protected static final int numberOfCiclesForWait = 75;
	    
	    static {
	        DEBUG_LOGGING = Boolean.parseBoolean(System.getProperty("debugLogging"));
	    }
	    
	    public int WAIT_TIME_IN_MILLIS;
	    
	    private WebDriver driver;
	    
	    public WebDriverWaitMethods(WebDriver driver) {
	        super();
	        this.driver = driver;
	    }
	    
	    public WebDriver getDriver() {
	        return driver;
	    }
	    
	    //is present    
	    public final boolean isElementPresent(By locator) {
	        return isElementPresent(driver.findElement(By.xpath("//html")), locator);
	    }
	    
	    public final boolean isElementPresent(WebElement element) {
	        try {
	            waitForElementToBePresent(element, 500);
	            return true;
	        } catch (Throwable e) {
	            return false;
	        }
	    }
	    
	    public final boolean isElementPresent(WebElement root, By childLocator) {
	        if (DEBUG_LOGGING) {
	            logger.trace("isElementPresent(WebElement root, By childLocator)");
	        }
	        driver.manage().timeouts().implicitlyWait(50, TimeUnit.MILLISECONDS);
	        try {
	            WebElement element = root.findElement(childLocator);
	            if (DEBUG_LOGGING) {
	                logger.trace("isSubElementPresent return true");
	            }
	            element.getText();
	            return true;
	        } catch (NoSuchElementException e) {
	            if (DEBUG_LOGGING) {
	                logger.trace("isSubElementPresent return false");
	            }
	            return false;
	        } catch (StaleElementReferenceException e) {
	            logger.trace("SERE");
	            return false;
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final boolean isElementPresent(By locator, int waitTimeInMillis) {
	        try {
	            waitForElementToBePresent(locator, waitTimeInMillis);
	            return true;
	        } catch (Throwable e) {
	            return false;
	        }
	    }
	    
	    public final boolean isElementPresent(WebElement element, int waitTimeInMillis) {
	        try {
	            waitForElementToBePresent(element, waitTimeInMillis);
	            return true;
	        } catch (Throwable e) {
	            return false;
	        }
	    }
	    
	    public final boolean isElementDisplayed(By locator) {
	        return isElementDisplayed(driver.findElement(By.xpath("//html")), locator);
	    }
	    
	    public final boolean isElementDisplayed(WebElement element) {
	        if (DEBUG_LOGGING) {
	            logger.trace("isElementDisplayed(WebElement element)");
	        }
	        driver.manage().timeouts().implicitlyWait(50, TimeUnit.MILLISECONDS);
	        try {
	            element.isDisplayed();
	            return true;
	        } catch (NoSuchElementException| StaleElementReferenceException e) {
	            return false;
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final boolean isElementDisplayed(WebElement root, By childLocator) {
	        if (DEBUG_LOGGING) {
	            logger.trace("isElementDisplayed(WebElement root, By childLocator)");
	        }
	        driver.manage().timeouts().implicitlyWait(50, TimeUnit.MILLISECONDS);
	        try {
	            WebElement element = root.findElement(childLocator);
	            if (DEBUG_LOGGING) {
	                logger.trace("isElementDisplayed return true");
	            }
	            return element.isDisplayed();
	        } catch (NoSuchElementException e) {
	            if (DEBUG_LOGGING) {
	                logger.trace("isElementDisplayed return false");
	            }
	            return false;
	        } catch (StaleElementReferenceException e) {
	            logger.trace("SERE");
	            return isElementPresent(root, childLocator);
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final boolean isElementDisplayed(By locator, int waitTimeInMillis) {
	        try {
	            waitForElementToBeDisplayed(locator, waitTimeInMillis);
	            return true;
	        } catch (Throwable e) {
	            return false;
	        }
	    }
	    
	    public final boolean isElementDisplayed(WebElement root, int waitTimeInMillis) {
	        try {
	            waitForElementToBeDisplayed(root, waitTimeInMillis);
	            return true;
	        } catch (Throwable e) {
	            return false;
	        }
	    }
	    
	    /**
	     * Waits for a given time
	     * 
	     * @param milis
	     * @deprecated<br>
	     * <b>Do not use this method unless there is no other option!<br>
	     * If u use it leave a comment why it is used!</b>
	     * 
	     */
	    @Deprecated
	    protected final static void waitFor(Integer milis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitFor " + milis);
	        }
	        try {
	            Thread.sleep(milis);
	        } catch (InterruptedException e1) {
	            
	        }
	    }
	    
	    public final WebDriverWait getWait(int waitTimeInMillis) {
	        WebDriverWait wait = new WebDriverWait(getDriver(), waitTimeInMillis / numberOfCiclesForWait);
	        wait.withTimeout(waitTimeInMillis / numberOfCiclesForWait, TimeUnit.MILLISECONDS);
	        return wait;
	    }
	    
	    public final void setTimeoutForWait(int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("setTimeoutForWait");
	            logger.trace("waitTimeInMillis / numberOfCiclesForWait: " + waitTimeInMillis / numberOfCiclesForWait);
	        }
	        driver.manage().timeouts().implicitlyWait(waitTimeInMillis / numberOfCiclesForWait, TimeUnit.MILLISECONDS);
	    }
	    
	    public final void setTimeoutToDefault(WebDriver driver) {
	        if (DEBUG_LOGGING) {
	            logger.trace("setTimeoutToDefault");
	            logger.trace("WAIT_TIME_IN_MILLIS / numberOfCiclesForWait: " + WAIT_TIME_IN_MILLIS / numberOfCiclesForWait);
	        }
	        driver.manage().timeouts().implicitlyWait(WAIT_TIME_IN_MILLIS / numberOfCiclesForWait, TimeUnit.MILLISECONDS);
	    }
	    
	    public final void shortWait(int waitTimeInMillis) {
	        waitFor(waitTimeInMillis / numberOfCiclesForWait);
	    }
	    
	    //waits

	    public final WebElement waitForElementToBePresent(By locator) {
	        return waitForElementToBePresent(locator, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final WebElement waitForElementToBePresent(By by, int waitTimeInMillis) {
	        return waitForElementToBePresent(driver.findElement(By.xpath("//html")), by, waitTimeInMillis);
	    }
	    
	    public final WebElement waitForElementToBePresent(WebElement element) {
	        return waitForElementToBePresent(element, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final WebElement waitForElementToBePresent(WebElement element, int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForElementToBePresent(WebElement element, int waitTimeInMillis)");
	            logger.trace("element: " + element);
	            logger.trace("waitTimeInMillis: " + waitTimeInMillis);
	        }
	        setTimeoutForWait(waitTimeInMillis);
	        try {
	            int count = 0;
	            while (count < numberOfCiclesForWait) {
	                try {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("wait until");
	                    }
	                    element.getText();
	                    return element;
	                } catch (StaleElementReferenceException e) {
	                    throw new AssertionError("SERE with fix element");
	                } catch (NoSuchElementException e) {
	                    count = count + 1;
	                    shortWait(waitTimeInMillis);
	                }
	            }
	            throw new AssertionError("Element did not appear: " + element);
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final WebElement waitForElementToBePresent(WebElement parentElement, By childLocator) {
	        return waitForElementToBePresent(parentElement, childLocator, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final WebElement waitForElementToBePresent(WebElement parentElement, By childLocator,
	            int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForElementToBePresent(WebElement parentElement, By childLocator, int waitTimeInMillis)");
	            logger.trace("parentElement: " + parentElement);
	            logger.trace("childLocator: " + childLocator);
	            logger.trace("waitTimeInMillis: " + waitTimeInMillis);
	        }
	        setTimeoutForWait(waitTimeInMillis);
	        try {
	            int count = 0;
	            WebDriverWait wait = getWait(waitTimeInMillis);
	            while (count < numberOfCiclesForWait) {
	                try {
	                    return wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentElement, childLocator));
	                } catch (StaleElementReferenceException e) {
	                    count = count + 1;
	                    shortWait(waitTimeInMillis);
	                } catch (TimeoutException e) {
	                    count = count + 1;
	                } catch (NoSuchElementException e) {
	                    count = count + 1;
	                    shortWait(waitTimeInMillis);
	                }
	            }
	            throw new AssertionError("Element did not appear: " + childLocator);
	        } finally {
	            if (DEBUG_LOGGING) {
	                logger.trace("waitForChildElementToAppear(WebElement parentElement, By childLocator, int waitTimeInSecond) out");
	            }
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final WebElement waitForElementToBeDisplayed(By locator) {
	        return waitForElementToBeDisplayed(locator, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final WebElement waitForElementToBeDisplayed(By by, int waitTimeInMillis) {
	        return waitForElementToBeDisplayed(driver.findElement(By.xpath("//html")), by, waitTimeInMillis);
	    }
	    
	    public final WebElement waitForElementToBeDisplayed(WebElement element) {
	        return waitForElementToBeDisplayed(element, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final WebElement waitForElementToBeDisplayed(WebElement element, int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForElementToAppear(WebElement element, int waitTimeInMillis)");
	            logger.trace("element: " + element);
	            logger.trace("waitTimeInMillis: " + waitTimeInMillis);
	            
	        }
	        setTimeoutForWait(waitTimeInMillis);
	        try {
	            int count = 0;
	            WebDriverWait wait = getWait(waitTimeInMillis);
	            while (count < numberOfCiclesForWait) {
	                try {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("wait until");
	                    }
	                    return wait.until(ExpectedConditions.visibilityOf(element));
	                } catch (StaleElementReferenceException e) {
	                    throw new AssertionError("SERE with fix element");
	                } catch (TimeoutException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("timeout");
	                    }
	                    count = count + 1;
	                } catch (NoSuchElementException e) {
	                    count = count + 1;
	                    shortWait(waitTimeInMillis);
	                }
	            }
	            throw new AssertionError("Element did not appear: " + element);
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final WebElement waitForElementToBeDisplayed(WebElement parentElement, By childLocator) {
	        return waitForElementToBeDisplayed(parentElement, childLocator, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final WebElement waitForElementToBeDisplayed(WebElement parentElement, By childLocator,
	            int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForElementToAppear(WebElement parentElement, By childLocator, int waitTimeInMillis)");
	            logger.trace("parentElement: " + parentElement);
	            logger.trace("childLocator: " + childLocator);
	            logger.trace("waitTimeInMillis: " + waitTimeInMillis);
	        }
	        setTimeoutForWait(waitTimeInMillis);
	        try {
	            int count = 0;
	            WebDriverWait wait = getWait(waitTimeInMillis);
	            while (count < numberOfCiclesForWait) {
	                try {
	                    return wait.until(ExpectedConditions.visibilityOf(parentElement.findElement(childLocator)));
	                } catch (StaleElementReferenceException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("SERE: " + e.getMessage());
	                    }
	                    count = count + 1;
	                    shortWait(waitTimeInMillis);
	                } catch (TimeoutException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("Timeout");
	                    }
	                    count = count + 1;
	                } catch (NoSuchElementException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("NSEE");
	                    }
	                    count = count + 1;
	                    //shortWait(waitTimeInMillis);
	                }
	            }
	            throw new AssertionError("Element did not appear: " + childLocator);
	        } finally {
	            if (DEBUG_LOGGING) {
	                logger.trace("waitForChildElementToAppear(WebElement parentElement, By childLocator, int waitTimeInSecond) out");
	            }
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final List<WebElement> waitForAllElementsToBeDisplayed(By locator) {
	        return waitForAllElementsToBeDisplayed(driver.findElement(By.xpath("//html")), locator);
	    }
	    
	    public final List<WebElement> waitForAllElementsToBeDisplayed(WebElement root, By locator) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForAllElementsToAppear(By locator)");
	        }
	        setTimeoutForWait(WAIT_TIME_IN_MILLIS);
	        try {
	            int count = 0;
	            WebDriverWait wait = getWait(WAIT_TIME_IN_MILLIS);
	            while (count < numberOfCiclesForWait) {
	                try {
	                    return wait.until(ExpectedConditions.visibilityOfAllElements(root.findElements(locator)));
	                } catch (StaleElementReferenceException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("SERE: " + e.getMessage().split("\\n")[0]);
	                        e.printStackTrace();
	                        logger.trace("Trying to recover from a stale element reference ecxeption");
	                    }
	                    count = count + 1;
	                } catch (TimeoutException e) {
	                    count = count + 1;
	                }
	            }
	            throw new AssertionError("Elements did not appear located by: " + locator);
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final List<WebElement> waitForAllElementsToBeDisplayed(List<WebElement> webElements) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForAllElementsToAppear(List<WebElement> webElements)");
	        }
	        setTimeoutForWait(WAIT_TIME_IN_MILLIS);
	        try {
	            int count = 0;
	            WebDriverWait wait = getWait(WAIT_TIME_IN_MILLIS);
	            while (count < numberOfCiclesForWait) {
	                try {
	                    return wait.until(ExpectedConditions.visibilityOfAllElements(webElements));
	                } catch (StaleElementReferenceException e) {
	                    throw new AssertionError("SERE, with given WebElements it cannot be fixed");
	                } catch (TimeoutException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("TimeoutException, count: " + count);
	                    }
	                    count = count + 1;
	                }
	            }
	            throw new AssertionError("Elements did not appear: " + webElements);
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final boolean waitForElementToDisappearOnUI(By selector) {
	        return waitForElementToDisappearOnUI(selector, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final boolean waitForElementToDisappearOnUI(By selector, int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForElementToDisappearOnUI(By selector, int waitTimeInMillis) in");
	            logger.trace("by: " + selector.toString());
	        }
	        setTimeoutForWait(waitTimeInMillis);
	        try {
	            int count = 0;
	            boolean stillVisible = false;
	            WebDriverWait wait = getWait(waitTimeInMillis);
	            while (count < numberOfCiclesForWait) {
	                try {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("findElement");
	                    }
	                    driver.findElement(selector);
	                    if (DEBUG_LOGGING) {
	                        logger.trace("wait until");
	                    }
	                    if (wait.until(ExpectedConditions.invisibilityOfElementLocated(selector))) {
	                        return true;
	                    }
	                } catch (StaleElementReferenceException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("SERE: " + e.getMessage().split("\\n")[0]);
	                    }
	                    stillVisible = false;
	                    shortWait(waitTimeInMillis);
	                } catch (TimeoutException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("timeout: " + e.getMessage().split("\\n")[0]);
	                    }
	                    stillVisible = true;
	                } catch (WebDriverException e) {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("No such element");
	                    }
	                    return true;
	                }
	                count = count + 1;
	            }
	            return !stillVisible;
	        } finally {
	            if (DEBUG_LOGGING) {
	                logger.trace("waitForElementToDisappearOnUI(By selector, int waitTimeInMillis) out");
	            }
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final boolean waitForElementToDisappearOnUI(WebElement element) {
	        return waitForElementToDisappearOnUI(element, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final boolean waitForElementToDisappearOnUI(WebElement element, int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForElementToDisappearOnUI(WebElement element, int waitTimeInMillis)");
	        }
	        setTimeoutForWait(waitTimeInMillis);
	        try {
	            int count = 0;
	            WebDriverWait wait = getWait(waitTimeInMillis);
	            while (count < numberOfCiclesForWait) {
	                try {
	                    if (DEBUG_LOGGING) {
	                        logger.trace("wait until");
	                    }
	                    if (wait.until(ExpectedConditions.invisibilityOf(element))) {
	                        return true;
	                    }
	                } catch (StaleElementReferenceException e) {
	                    return true;
	                } catch (TimeoutException e) {
	                    count = count + 1;
	                }
	            }
	            return false;
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final boolean checkThatElementAppearsAndDisappears(By selector, int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("checkThatElementAppearsAndDisappears in");
	        }
	        try {
	            WebElement element = waitForElementToBeDisplayed(selector, waitTimeInMillis);
	            if (DEBUG_LOGGING) {
	                logger.trace("checkThatElementAppearsAndDisappears appeared");
	            }
	            return waitForElementToDisappearOnUI(element, waitTimeInMillis);
	        } catch (Throwable e) {
	            if (DEBUG_LOGGING) {
	                logger.trace("checkThatElementAppearsAndDisappears throwable");
	                logger.trace("Throwable: " + e.getMessage().split("\\n")[0]);
	            }
	            return false;
	        }
	    }
	    
	    public final void waitForElementToStopMove(WebElement element) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForElementToStopMove(WebElement element)");
	        }
	        int count = 0;
	        try {
	            Point newLocation = element.getLocation();
	            Point oldLocation;
	            
	            do {
	                shortWait(WAIT_TIME_IN_MILLIS);
	                oldLocation = newLocation;
	                newLocation = element.getLocation();
	                count++;
	            } while (!newLocation.equals(oldLocation) && count < numberOfCiclesForWait);
	            
	            if (!newLocation.equals(oldLocation)) {
	                throw new AssertionError("The move of the element: " + element + " didn't stop in 5 secound");
	            }
	        } catch (StaleElementReferenceException e) {
	            logger.error("The element is not present anymore");
	        }
	    }
	    
	    public final void waitForAttributeToContain(WebElement root, By locator, String attribute, String value) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForAttibuteToContain(WebElement root, By locator, String attribute, String value)");
	        }
	        setTimeoutForWait(WAIT_TIME_IN_MILLIS);
	        try {
	            int count = 0;
	            while (count < numberOfCiclesForWait) {
	                try {
	                    WebElement element = root.findElement(locator);
	                    if (!element.getAttribute(attribute).contains(value)) {
	                        throw new AssertionError("The attribute does not contain the require value, attribute value: " + element.getAttribute(attribute) + ", should contains: " + value);
	                    }
	                    return;
	                } catch (NullPointerException|NoSuchElementException|AssertionError e) {
	                    count++;
	                    if (count < numberOfCiclesForWait) {
	                        if (DEBUG_LOGGING) {
	                            logger.trace("waitForAttibuteToContain sleep");
	                        }
	                        shortWait(WAIT_TIME_IN_MILLIS);
	                    } else {
	                        throw e;
	                    }
	                }
	            }
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final void waitForAttributeToContain(WebElement root, String attribute, String value) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForAttibuteToContain(WebElement root, String attribute, String value)");
	        }
	        setTimeoutForWait(WAIT_TIME_IN_MILLIS);
	        try {
	            int count = 0;
	            while (count < numberOfCiclesForWait) {
	                try {
	                    if (!root.getAttribute(attribute).contains(value)) {
	                        throw new AssertionError("The attribut not contains the require value, attribute value: " + root.getAttribute(attribute) + ", should contains: " + value);
	                    }
	                    return;
	                } catch (NullPointerException|NoSuchElementException|AssertionError e) {
	                    count++;
	                    if (count < numberOfCiclesForWait ) {
	                        shortWait(WAIT_TIME_IN_MILLIS);
	                    } else {
	                        throw e;
	                    }
	                    
	                }
	            }
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	    }
	    
	    public final void waitForAttributeToDisappear(SearchContext root, By locator, String attribute, String value) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForAttibuteToDisappear(WebElement root, By locator, String attribute, String value)");
	        }
	        waitForAttributeToDisappear(root, locator, attribute, value, WAIT_TIME_IN_MILLIS);
	    }
	    
	    public final void waitForAttributeToDisappear(SearchContext root, By locator, String attribute, String value, int waitTimeInMillis) {
	        if (DEBUG_LOGGING) {
	            logger.trace("waitForAttibuteToDisappear(WebElement root, By locator, String attribute, String value, int waitTimeInMillis)");
	        }
	        setTimeoutForWait(waitTimeInMillis);
	        try {
	            int count = 0;
	            while (count < numberOfCiclesForWait) {
	                try {
	                    WebElement element = root.findElement(locator);
	                    if (element.getAttribute(attribute).contains(value)) {
	                        throw new AssertionError("The attribute contains the value, attribute value: " + element.getAttribute(attribute) + ", should not contain: " + value);
	                    }
	                    if (DEBUG_LOGGING) {
	                        logger.trace("waitForAttibuteToDisappear return");
	                    }
	                    return;
	                } catch (StaleElementReferenceException|NoSuchElementException|AssertionError e) {
	                    if (DEBUG_LOGGING) {
	                        e.printStackTrace();
	                    }
	                    count++;
	                    if (count < numberOfCiclesForWait ) {
	                        shortWait(waitTimeInMillis);
	                    } else {
	                        throw e;
	                    }
	                    
	                } catch (NullPointerException e) {
	                    return; 
	                }
	            }
	        } finally {
	            setTimeoutToDefault(driver);
	        }
	        
	    }
	    
	    public final void waitForOneToAppear(WebElement root, By... locators) {
	        int count = 0;        
	        while (count < numberOfCiclesForWait) {
	            for (By locator : locators) {
	                if (isElementPresent(root, locator)) {
	                    return;
	                }
	            }
	            count++;
	            shortWait(WAIT_TIME_IN_MILLIS);
	        }
	        
	        throw new AssertionError("None of the given elemetnts appeared");
	    }
	    
	    public static final boolean isAttribtuePresent(WebElement element, String attribute) {
	        try {
	            String value = element.getAttribute(attribute);
	            if (value != null) {
	                return true;
	            }
	        } catch (Exception e) {
	            if (DEBUG_LOGGING) {
	                logger.trace("isAttribtuePresent exceptoin");
	                logger.trace("exceptoin: " + e.getMessage().split("\\n")[0]);
	            }
	        }
	        return false;
	    }
	    
	    public static final boolean isAttributeContains(WebElement element, String attribute, String text) {
	        try {
	            String value = element.getAttribute(attribute);
	            if (value.contains(text)) {
	                return true;
	            }
	        } catch (Exception e) {
	            if (DEBUG_LOGGING) {
	                logger.trace("isAttributeContains exceptoin");
	                logger.trace("exceptoin: " + e.getMessage().split("\\n")[0]);
	            }
	        }
	        return false;
	    }
}
