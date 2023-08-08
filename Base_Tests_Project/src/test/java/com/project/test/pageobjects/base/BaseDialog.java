package com.project.test.pageobjects.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;

import com.project.test.pageobjects.base.BaseInformations;
import com.project.test.pageobjects.base.BasePageObjectWithRoot;

/**
 * 
 * @author Valentino Milanov
 *
 * Base page object for all pop-up dialogs on the web application
 */
public class BaseDialog extends BasePageObjectWithRoot {

	//FIXME fix all the locators for the Base Dialog web element and root
	public static final By DIALOG_ROOT = By.cssSelector("md-dialog");
	
    @FindBy(css = "button[aria-label='Close']")
    protected WebElement closeButton;
    
    @FindBy(css = "button[aria-label='Cancel']")
    protected WebElement cancelButton;

    @FindBy(css = "[Locator for the dialog title]")
    protected WebElement dialogTitle;

	@FindBy(css = "[Locator for close icon ('X' icon)]")
	protected WebElement closeIcon;
	
    public BaseDialog(BaseInformations baseInformations) {
        super(baseInformations, DIALOG_ROOT, DIALOG_ROOT);
    }
    
    public BaseDialog(BaseInformations baseInformations, By rootLocator, Object... variables) {
        super(baseInformations, rootLocator, rootLocator, variables);
    }
    
    @Override
	protected void initVariables(Object... variables) {
		super.initVariables(variables);
	}

	@Override
    protected void isLoaded() {
        //waitForPageLoaded();
    }
    
    @Override
	protected void load() {
		logger.trace("load");
		waitForPageLoaded();
		rootFactory = new DefaultElementLocatorFactory(getDriver().findElement(DIALOG_ROOT));
		PageFactory.initElements(rootFactory, this);
        initVariables(DIALOG_ROOT);
	}

	public String getDialogTitle(){
        return waitForElementToBeDisplayed(dialogTitle).getText().trim();
    }

    public boolean isDialogTitleDisplayed(){
    	logger.trace("Checking if dialog title is displayed");
        return isElementPresent(dialogTitle);
    }

    public void clickCloseButton(){
    	logClickButton("Close");
        waitForElementToBeDisplayed(closeButton).click();
        waitForDialogToDisappear();
        closeToastIfPresent();
    }
    
    public void clickCancelButton(){
    	logClickButton("Cancel");
        waitForElementToBeDisplayed(cancelButton).click();        
        waitForDialogToDisappear();
        waitForPageLoaded();
    }

    public boolean isDialogCloseButtonDisplayed(){
    	logger.trace("Checking if dialog close button is displayed");
        return isElementPresent(closeButton);
    }
    
	public void waitForDialogToDisappear() {
		waitForElementToDisappearOnUI(root);
	}
	
	public void closeDialog() {
		logClickIcon("Close dialog");
		waitForElementToBeDisplayed(closeIcon).click();
        waitForDialogToDisappear();
	}
	
	public boolean isXCloseButtonDisplayed(){
        logger.trace("Checking if dialog x close button is displayed");
        return isElementPresent(closeIcon);
    }
}
