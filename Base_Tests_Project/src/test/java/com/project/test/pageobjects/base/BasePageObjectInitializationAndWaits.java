package com.project.test.pageobjects.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePageObjectInitializationAndWaits extends LoadableComponent<BasePageObjectInitializationAndWaits> {

protected static final Logger logger = LoggerFactory.getLogger(WebDriverWaitMethods.class);
    
    protected static boolean DEBUG_LOGGING;
    private static boolean zeroWait = false;
    private WebDriverWaitMethods webDriverWaitMethods;
    
    protected BaseInformations baseInformations;
    protected ElementLocatorFactory rootFactory;
    
    static {
        DEBUG_LOGGING = Boolean.parseBoolean(System.getProperty("debugLogging"));
    }
    
    //constructor and init methods
    
    private BasePageObjectInitializationAndWaits(BaseInformations baseInformations) {
        init(baseInformations);
    }
    
    protected BasePageObjectInitializationAndWaits(BaseInformations baseInformations, boolean driverFactoryInitialize, Object... variables) {
        this(baseInformations);
        if (driverFactoryInitialize) {
            rootFactory = new DefaultElementLocatorFactory(getDriver());
            initialize(variables);
        }
    }
    
    protected void init(BaseInformations baseInformations) {
        this.baseInformations = baseInformations;
        webDriverWaitMethods = new WebDriverWaitMethods(getDriver());
        initWaitInMillis(this);
        webDriverWaitMethods.setTimeoutToDefault(baseInformations.getDriver());
    }
    
    private synchronized static final void initWaitInMillis(BasePageObjectInitializationAndWaits initObject) {
        if (DEBUG_LOGGING) {
            logger.trace("initWaitInMillis");
            logger.trace("zeroWait: " + zeroWait);
        }
        if (zeroWait) {
            initObject.webDriverWaitMethods.WAIT_TIME_IN_MILLIS = 0;
            zeroWait = false;
        } else {
            initObject.webDriverWaitMethods.WAIT_TIME_IN_MILLIS = WebDriverWaitMethods.DEFAULT_WAIT_TIME_IN_MILLIS;
        }
        if (DEBUG_LOGGING) {
            logger.trace("WAIT_TIME_IN_MILLIS: " + initObject.webDriverWaitMethods.WAIT_TIME_IN_MILLIS);
        }
    }
    
    protected final void initializeWithoutGet(Object... variables) {
        PageFactory.initElements(rootFactory, this);
        initVariables(variables);
    }
    
    public BasePageObjectInitializationAndWaits initialize(Object... variables) {
        PageFactory.initElements(rootFactory, this);
        initVariables(variables);
        return this.get();
    }
    
    /**
     * Will be executed before isLoaded methods
     * If a variable should be initialized before isLoaded(), override this method and initialize it in that
     */
    protected void initVariables(Object... variables) {
        
    }
    
    //is PO present
    
    /**
     * It will use the first matching constructor 
     */
    public static final boolean isPresent(Class<? extends BasePageObjectInitializationAndWaits> clazz, Object... parameters) {
        if (DEBUG_LOGGING) {
            logger.trace("isPresent");
        }
        ((BaseInformations)parameters[0]).getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);;
        BasePageObjectInitializationAndWaits basePageObject = getNewInstanceOrNull(clazz, parameters);
        return basePageObject != null;
    }
    
    public static final boolean isPresentWithWait(Class<? extends BasePageObjectInitializationAndWaits> clazz, Object... parameters) {
        if (DEBUG_LOGGING) {
            logger.trace("isPresentWithWait");
        }
        for(int i = 0; i < WebDriverWaitMethods.numberOfCiclesForWait; i++) {
            BasePageObjectInitializationAndWaits basePageObject = getNewInstanceOrNull(clazz, parameters);
            if(basePageObject == null) {
                waitFor(WebDriverWaitMethods.DEFAULT_WAIT_TIME_IN_MILLIS / WebDriverWaitMethods.numberOfCiclesForWait);
            } else {
                return true;
            }
            
        }
        return false;
    }
    
    /**
     * It will use the first matching constructor 
     */
    @SuppressWarnings("unchecked")
    public static final BasePageObjectInitializationAndWaits getNewInstanceOrNull(Class<? extends BasePageObjectInitializationAndWaits> clazz, Object... parameters) {
        try {
            Class<? extends Object>[] parametertyps = new Class[parameters.length];
            int i = 0;
            for (Object parameter: parameters) {
                parametertyps[i] = parameter.getClass();  
                i++;
            }
            Constructor<BasePageObjectInitializationAndWaits> constructor = getConstructorForArgs(clazz, parametertyps);
            return useConstuctorWithZeroWaitTime(constructor, parameters);
        } catch (Throwable e) {
            return null;
        }
    }
    
    /**
     * https://stackoverflow.com/a/9244175
     * returns the first match, or null when no match found
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final Constructor<BasePageObjectInitializationAndWaits> getConstructorForArgs(Class<? extends BasePageObjectInitializationAndWaits> klass, Class[] args) {
        for(Constructor<?> constructor : klass.getConstructors()) {
            Class<?>[] types = constructor.getParameterTypes();
            if(types.length == args.length) {
                boolean argumentsMatch = true;
                for(int i = 0; i < args.length; i++) {
                    if(!types[i].isAssignableFrom(args[i])) {
                        argumentsMatch = false;
                        break;
                    }
                }
                if (argumentsMatch) { //We found a matching constructor, return it
                    return (Constructor<BasePageObjectInitializationAndWaits>) constructor;
                }
            }
        }
        //No matching constructor
        logger.error("No constructor found in class: " + klass + " with parameter types: " + args);
        return null;
    }
    
    private synchronized static final BasePageObjectInitializationAndWaits useConstuctorWithZeroWaitTime(Constructor<BasePageObjectInitializationAndWaits> constructor, Object[] parameters) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        zeroWait = true;
        return constructor.newInstance(parameters);
    }
    
    //gets
    
    public final WebDriver getDriver() {
        return baseInformations.driver;
    }
    
    public final BaseInformations getBaseInformations() {
        return baseInformations;
    }
    
    public WebDriverWaitMethods getWebDriverWaitMethods() {
        return webDriverWaitMethods;
    }
    
    //loadable
    
    @Override
    protected void load() {
        logger.warn("load");
        //hardRefresh();
        PageFactory.initElements(rootFactory, this);
    }
    
    @Override
    protected void isLoaded() {
        
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
    protected static final void waitFor(Integer milis) {
        WebDriverWaitMethods.waitFor(milis);
    }
    

    //is present    
    protected final boolean isElementPresent(By locator) {
        return webDriverWaitMethods.isElementPresent(locator);
    }
    
    protected final boolean isElementPresent(WebElement element) {
        return webDriverWaitMethods.isElementPresent(element);
    }
    
    protected final boolean isElementPresent(WebElement root, By childLocator) {
        return webDriverWaitMethods.isElementPresent(root, childLocator);
    }
    
    protected final boolean isElementPresent(By locator, int waitTimeInMillis) {
        return webDriverWaitMethods.isElementPresent(locator, waitTimeInMillis);
    }
    
    protected final boolean isElementPresent(WebElement element, int waitTimeInMillis) {
        return webDriverWaitMethods.isElementPresent(element, waitTimeInMillis);
    }
    
    protected final boolean isElementDisplayed(By locator) {
        return webDriverWaitMethods.isElementDisplayed(locator);
    }
    
    protected final boolean isElementDisplayed(WebElement element) {
        return webDriverWaitMethods.isElementDisplayed(element);
    }
    
    protected final boolean isElementDisplayed(WebElement root, By childLocator) {
        return webDriverWaitMethods.isElementDisplayed(root, childLocator);
    }
    
    protected final boolean isElementDisplayed(By locator, int waitTimeInMillis) {
        return webDriverWaitMethods.isElementDisplayed(locator, waitTimeInMillis);
    }
    
    protected final boolean isElementDisplayed(WebElement element, int waitTimeInMillis) {
        return webDriverWaitMethods.isElementDisplayed(element, waitTimeInMillis);
    }
    
    //waits
    protected final WebElement waitForElementToBePresent(By locator) {
        return webDriverWaitMethods.waitForElementToBePresent(locator);
    }
    
    protected final WebElement waitForElementToBePresent(By by, int waitTimeInMillis) {
        return webDriverWaitMethods.waitForElementToBePresent(by, waitTimeInMillis);
    }
    
    protected final WebElement waitForElementToBePresent(WebElement element) {
        return webDriverWaitMethods.waitForElementToBePresent(element);
    }
    
    protected final WebElement waitForElementToBePresent(WebElement element, int waitTimeInMillis) {
        return webDriverWaitMethods.waitForElementToBePresent(element, waitTimeInMillis);
    }
    
    protected final WebElement waitForElementToBePresent(WebElement parentElement, By childLocator) {
        return webDriverWaitMethods.waitForElementToBePresent(parentElement, childLocator);
    }
    
    protected final WebElement waitForElementToBePresent(WebElement parentElement, By childLocator,
            int waitTimeInMillis) {
        return webDriverWaitMethods.waitForElementToBePresent(parentElement, childLocator, waitTimeInMillis);
    }
    
    protected final WebElement waitForElementToBeDisplayed(By locator) {
        return webDriverWaitMethods.waitForElementToBeDisplayed(locator);
    }
    
    protected final WebElement waitForElementToBeDisplayed(By by, int waitTimeInMillis) {
        return webDriverWaitMethods.waitForElementToBeDisplayed(by, waitTimeInMillis);
    }
    
    protected final WebElement waitForElementToBeDisplayed(WebElement element) {
        return webDriverWaitMethods.waitForElementToBeDisplayed(element);
    }
    
    protected final WebElement waitForElementToBeDisplayed(WebElement element, int waitTimeInMillis) {
        return webDriverWaitMethods.waitForElementToBeDisplayed(element, waitTimeInMillis);
    }
    
    protected final WebElement waitForElementToBeDisplayed(WebElement parentElement, By childLocator) {
        return webDriverWaitMethods.waitForElementToBeDisplayed(parentElement, childLocator);
    }
    
    protected final WebElement waitForElementToBeDisplayed(WebElement parentElement, By childLocator,
            int waitTimeInMillis) {
        return webDriverWaitMethods.waitForElementToBeDisplayed(parentElement, childLocator, waitTimeInMillis);
    }
    
    protected final List<WebElement> waitForAllElementsToBeDisplayed(WebElement root, By locator) {
        return webDriverWaitMethods.waitForAllElementsToBeDisplayed(root, locator);
    }
    
    protected final List<WebElement> waitForAllElementsToBeDisplayed(By locator) {
        return webDriverWaitMethods.waitForAllElementsToBeDisplayed(locator);
    }
    
    protected final List<WebElement> waitForAllElementsToBeDisplayed(List<WebElement> webElements) {
        return webDriverWaitMethods.waitForAllElementsToBeDisplayed(webElements);
    }
    
    protected final boolean waitForElementToDisappearOnUI(By selector) {
        return webDriverWaitMethods.waitForElementToDisappearOnUI(selector);
    }
    
    protected final boolean waitForElementToDisappearOnUI(By selector, int waitTimeInMillis) {
        return webDriverWaitMethods.waitForElementToDisappearOnUI(selector, waitTimeInMillis);
    }
    
    protected final boolean waitForElementToDisappearOnUI(WebElement element) {
        return webDriverWaitMethods.waitForElementToDisappearOnUI(element);
    }
    
    protected final boolean waitForElementToDisappearOnUI(WebElement element, int waitTimeInMillis) {
        return webDriverWaitMethods.waitForElementToDisappearOnUI(element, waitTimeInMillis);
    }
    
    protected final boolean checkThatElementAppearsAndDisappears(By selector, int waitTimeInMillis) {
        return webDriverWaitMethods.checkThatElementAppearsAndDisappears(selector, waitTimeInMillis);
    }
    
    protected final void waitForElementToStopMove(WebElement element) {
        webDriverWaitMethods.waitForElementToStopMove(element);
    }
    
    protected final void waitForAttributeToContain(WebElement root, By locator, String attribute, String value) {
        webDriverWaitMethods.waitForAttributeToContain(root, locator, attribute, value);
    }
    
    protected final void waitForAttributeToContain(WebElement root, String attribute, String value) {
        webDriverWaitMethods.waitForAttributeToContain(root, attribute, value);
    }
    
    protected final void waitForAttributeToDisappear(SearchContext root, By locator, String attribute, String value) {
        webDriverWaitMethods.waitForAttributeToDisappear(root, locator, attribute, value);
    }
    
    protected final void waitForAttributeToDisappear(SearchContext root, By locator, String attribute, String value, int waitTimeInMillis) {
        webDriverWaitMethods.waitForAttributeToDisappear(root, locator, attribute, value, waitTimeInMillis);
    }
    
    protected final void waitForOneToAppear(WebElement root, By... locators) {
        webDriverWaitMethods.waitForOneToAppear(root, locators);
    }
}
