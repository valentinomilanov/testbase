package com.project.test.util.junit.rules;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Alert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.test.util.junit.TestBase;
import com.project.test.annotations.TestCase;

public class ScreenshotRule implements AfterTestExecutionCallback, AfterEachCallback, BeforeEachCallback{

	protected static final Logger logger = LoggerFactory.getLogger(ScreenshotRule.class);

	private boolean screenshot = false;

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		TestBase.logger.trace("ScreenshotRule afterTestExecution");
		TestBase testBase = (TestBase) context.getTestInstance().get();
		if (context.getExecutionException().isPresent()) {
			//test failed
			screenshot = true;
			logger.trace("FAILURE TIME: " + LocalDateTime.now(DateTimeZone.UTC).toString());
			captureScreenshot(testBase, context);
		} else if (TestBase.isAttachment()) {
			//we need a screenshot anyway
			captureScreenshot(testBase, context);
		}
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		TestBase.logger.trace("ScreenshotRule afterEach");
		TestBase testBase = (TestBase) context.getTestInstance().get();
		if (context.getExecutionException().isPresent() && !screenshot) {
			//test failed
			logger.trace("FAILURE TIME: " + LocalDateTime.now(DateTimeZone.UTC).toString());
			captureScreenshot(testBase, context);
		}	
	}

	private void captureScreenshot(TestBase testBase, ExtensionContext context) {

		File screenshot;
		logger.trace("capturing screenshot");
		List<WebDriver> drivers = testBase.getDrivers();
		boolean moreThanOne = drivers.size() > 1;
		int i = 0;
		for (WebDriver driver : drivers) {
			try {
				if (testBase.isAlertPresent()) {
					Alert alert = testBase.waitForAlert();
					logger.trace("accept alert: " + alert.getText());
					alert.dismiss();
				}
				screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				String fileName = TestBase.getFileName(context);
				String filePath = "." + File.separator + "screenshots" + File.separator 
				            + fileName + ((moreThanOne) ? ("-" + ++i) : "") + ".png";
				FileUtils.copyFile(screenshot, new File(filePath));
			} catch (Exception e) {
				logger.trace("An exception appeared while creating screenshot:" + e.getMessage());
			}
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		screenshot = false;
	}
	
	public static File getScreenshotForTest(ExtensionContext context) {
	    String fileName = TestBase.getFileName(context);
	    String filePath = "." + File.separator + "screenshots" + File.separator 
	            + fileName + ".png";
	    if (!new File(filePath).exists()) {
	        filePath = "." + File.separator + "screenshots" + File.separator 
	                + fileName + "-1.png";
	    }
	    File file = new File(filePath);
	    if (file.exists()) {
	        return file;
	    } else {
	        return null; //throw new Error("No file found on path: " + file.getAbsolutePath());
	    }
	}
}
