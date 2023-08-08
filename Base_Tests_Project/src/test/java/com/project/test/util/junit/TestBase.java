package com.project.test.util.junit;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.RandomStringGenerator;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.project.test.annotations.TestCase;
import com.project.test.constants.EmailAccount;
import com.project.test.enums.TypeOfServer;
import com.project.test.pageobjects.base.BaseInformations;
import com.project.test.pageobjects.base.BasePageObject;
import com.project.test.pageobjects.base.BasePageObjectWithRoot;
import com.project.test.util.EmailUtils;
import com.project.test.util.FilePathUtils;
import com.project.test.util.ImageUtils;

public class TestBase extends TestBaseRules {

	protected RandomStringGenerator randomNumericStringGenerator = new RandomStringGenerator.Builder()
            .withinRange('0', '9')
            .build();
	
	public static final String SPECIAL_CHARACTERS = "!@#$%^&*?_~";
	
	 protected String winHandleBefore;
	 protected Set<String> winHandlesBefore;
    
    public TestBase() {
        super();
    }
    
// Windows and Tabs handeling
    
    public void switchToTab(int numberOfTab) {
        switchToTab(numberOfTab, getBaseInformations().getDriver());
    }
    
    public void switchToTab(int numberOfTab, WebDriver driver) {
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(numberOfTab));
    }
    
    public void switchToOpenedWindowBefore() {
        winHandleBefore = getBaseInformations().getDriver().getWindowHandle();
        winHandlesBefore = getBaseInformations().getDriver().getWindowHandles();
    }
    
    public void switchToOpenedWindowAfter() {
        Set<String> handlesNow = getBaseInformations().getDriver().getWindowHandles();
        while(winHandlesBefore.containsAll(handlesNow)) {
            waitFor(100);
            handlesNow = getBaseInformations().getDriver().getWindowHandles();
        }
        handlesNow.removeAll(winHandlesBefore);
        getBaseInformations().getDriver().switchTo().window(handlesNow.iterator().next());
    }

    public String getCurrentWindowTitle(){
        return getBaseInformations().getDriver().getTitle();
    }
    
    public boolean isElementInCurrentWindowPresent(By selector){
        return getBaseInformations().getDriver().findElement(selector).isDisplayed();
    }

    public String getTextOfElementInCurrentWindow(By selector){
        return getBaseInformations().getDriver().findElement(selector).getText().trim();
    }
    
    public void closeOpenedWindow() {
        getBaseInformations().getDriver().close();
    }
    
    public void switchToDefaultWindow() {
        getBaseInformations().getDriver().switchTo().window(winHandleBefore);
    }
    
    public void navigateBack() {
    	logger.debug("Click on the browser's back button");
    	getBaseInformations().getDriver().navigate().back();
    }
    
    @SuppressWarnings("deprecation")
    public void pressAltF4() {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch(AWTException ex) {
            throw new AssertionError("Error with alt+f4 combination");
        }
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_ALT);
        waitFor(5000);
    }
    
// Random number and Name generation    
    public static int generateRandomIntIntRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    
    public String generateRandomName() {
        return "CreatedByTest" + getRunAndCaseNumberWithRandomDigits();
    }
    
    public String getRunAndCaseNumberWithRandomDigits() {
        return "n" + randomNumericStringGenerator.generate(3) 
            + "c" + getTestCaseId() ;
    }
    
    
// Image handeling
    public ImageUtils getImageUtils() {
        return getImageUtils(getBaseInformations());
    }

    public ImageUtils getImageUtils(BaseInformations baseInformations) {
        return new ImageUtils(baseInformations);
    }
    
// File handeling
    public static String getFileName(ExtensionContext context) {
        String fileName;
        Integer id = TestBase.getTestCaseId(context);
        if (id != null) {
            fileName = "" + id;
        } else {
            fileName = context.getTestClass().get().getCanonicalName() + "-"
                    + context.getTestMethod().get().getName();
        }
        return fileName;
    }
    
    protected File getFileFromResources(String fileName) {
        String filePath = FilePathUtils.getInstance().getUploadFilePath(fileName);
        return new File(filePath);
    }
    
//Email Handeling 
    //FIXME use this only if you have to use emails
    protected void openEmails(EmailAccount account, String expectedSubject) {
        openEmails(account, expectedSubject, 1);
    }
    
    protected void openEmails(EmailAccount account, String expectedSubject, int minCount) {
        openEmails(account, "", expectedSubject, minCount);
    }
    
    protected void openEmails(EmailAccount account, String expectedEmailAddress, String expectedSubject) {
        openEmails(account, expectedEmailAddress, expectedSubject, 1);
    }
    
    protected void openEmails(EmailAccount account, String expectedEmailAddress, String expectedSubject, int minCount) {
        if (expectedSubject == null)
            return;
        try {
            EmailUtils emailUtils = new EmailUtils(account);
            emailUtils.readMessages(expectedSubject, expectedEmailAddress, minCount);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }
    
    
    /**
     * Use this method ONLY when there is no option to paste file path to upload input field!
     * This method is used to select a file ftom the Windows file selector window.
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
    
    
    /**
     * Method for getting test case Id
     * @param context
     * @return Test Case Id or -1
     */
    public static Integer getTestCaseId(ExtensionContext context) {
        if (context.getTestMethod().get().isAnnotationPresent(TestCase.class)) {
            TestCase annotation = context.getTestMethod().get().getAnnotation(TestCase.class);
            switch (((TestBase) context.getTestInstance().get()).getBaseInformations().getOperationSystem()) {
                case ANDROID:
                    //return annotation.mobileId();
                case WINDOWS:
                    return annotation.id();
                default:
                    throw new Error("This operation system type is not supported");
            }
        } else {
            return -1;
        }
    }
    
    /**
     * Method for logging out user from application
     * @param baseInformations
     */
    public static void logout(BaseInformations baseInformations) {
        baseInformations.setTypeOfServer(TypeOfServer.INSTANCE_1);
        if (DEBUG_LOGGING) {
            logger.trace("logout");
        }
        if (BasePageObjectWithRoot.isPresent(BasePageObject.class, baseInformations)) {
            try {
                //FIXME add logout method
            } catch (Throwable t) {
                if (DEBUG_LOGGING) {
                    logger.trace("not present 1");
                }
                //FIXME Add methods to check if some steps needs to be done before login (e.g. 'open offside panel', 'close tooltip', etc.)
            } 
        } else {
            logger.error("It is not logged in");
            throw new Error("it is not logged in");
        }
    }
}
