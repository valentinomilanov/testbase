package com.project.test.util.junit;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import com.project.test.util.junit.rules.BrowserConsoleLogRule;
import com.project.test.util.junit.rules.CloseDriverRule;
import com.project.test.util.junit.rules.LoggingRule;
import com.project.test.util.junit.rules.LongStepAlertRule;
import com.project.test.util.junit.rules.ScreenshotRule;

@ExtendWith(LongStepAlertRule.class)
@ExtendWith(LoggingRule.class)
@ExtendWith(CloseDriverRule.class)
@ExtendWith(BrowserConsoleLogRule.class)
@ExtendWith(ScreenshotRule.class)
@Timeout(2400)//40 min timeout

public class TestBaseRules extends TestBaseDriverSetupAndKill {

	public TestBaseRules() {
		super();
	}
}
