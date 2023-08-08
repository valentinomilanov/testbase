package com.project.test.pageobjects.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;

public class BasePageObjectWithRoot extends BasePageObject {

protected WebElement root;
    
    public BasePageObjectWithRoot(BaseInformations baseInformations, By root, Object... variables) {
        super(baseInformations, false, variables);
        initialize(waitForElementToBePresent(root, getWebDriverWaitMethods().WAIT_TIME_IN_MILLIS * 5), variables);
    }
    
    public BasePageObjectWithRoot(BaseInformations baseInformations, WebElement root, Object... variables) {
        super(baseInformations, false, variables);
        initialize(root, variables);
    }
    
    @Deprecated
    public final BasePageObjectWithRoot initialize(Object... variables) {
        throw new Error("Use it with root element");
    }
    
    public final BasePageObjectWithRoot initialize(WebElement root, Object... variables) {
        rootFactory = new DefaultElementLocatorFactory(root);
        PageFactory.initElements(rootFactory, this);
        initVariables(variables);
        this.root = root;
        BasePageObjectWithRoot returnValue = (BasePageObjectWithRoot) this.get();
        return returnValue;
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

    public WebElement getRoot() {
        return root;
    }
}
