package com.project.test.util.junit.rules;

import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.Logs;

import com.project.test.util.OperationSystem;
import com.project.test.util.junit.TestBase;

public class BrowserConsoleLogRule implements AfterTestExecutionCallback {

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
        if (Boolean.parseBoolean(System.getProperty("debugLogging"))) {
            TestBase.logger.trace("BrowserConsoleLogRule afterTestExecution");
        }
		TestBase testBase = (TestBase) context.getTestInstance().get();
		if (context.getExecutionException().isPresent()) {
			//test failed
		    if (!TestBase.getOs().equals(OperationSystem.ANDROID)) {
		        printBrowserConsoleLogs(testBase);
		    }
		}		
	}
	
    private void printBrowserConsoleLogs(TestBase testBase){
		Logs logs = testBase.getBaseInformations().getDriver().manage().logs();
		for (String logType: testBase.getLogTypes()){
			LogEntries logEntries = logs.get(logType);
			if (!logEntries.getAll().isEmpty()){
				TestBase.logger.trace("Console output from browser:" );
				for (LogEntry logEntry : logEntries) {
					TestBase.logger.trace("JS: " + logEntry.getLevel() + " at: "+ DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSSS").print(logEntry.getTimestamp()) + " : " + logEntry.getMessage().replace("\n", ""));
				}
			}
		}
	}
}
