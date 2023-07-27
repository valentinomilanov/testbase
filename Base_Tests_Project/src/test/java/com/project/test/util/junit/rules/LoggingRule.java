package com.project.test.util.junit.rules;

import java.io.File;
import java.io.PrintWriter;

import org.joda.time.DateTime;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;

import com.project.test.util.junit.TestBase;

public class LoggingRule implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		MDC.put("methodName", context.getTestClass().get().getCanonicalName());
	}
	
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		//clear log
	    
	    File file = getLogFile(context);
	    
		PrintWriter writer = new PrintWriter(file);
		writer.print("");
		writer.close();
		
		MDC.remove("methodName");
		MDC.put("methodName", context.getTestClass().get().getCanonicalName() + "-" + context.getTestMethod().get().getName());

        MDC.remove("testId");
        MDC.put("testId", TestBase.getFileName(context));
		
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		if (context.getExecutionException().isPresent()) {
			TestBase.logger.error(context.getExecutionException().get().toString());
		}
		MDC.remove("methodName");
		MDC.put("methodName", context.getTestClass().get().getCanonicalName());

        MDC.remove("testId");
        MDC.put("testId", context.getTestClass().get().getCanonicalName());
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		MDC.remove("methodName");
		MDC.remove("testId");
	}

	public static File getLogFile(ExtensionContext context) {
	    String fileName = TestBase.getFileName(context);
	    String path = "logs" + File.separator + DateTime.now().toString("yyyy-MM-dd") + File.separator + fileName + ".log";
	    return new File(path);	    
	}
}
