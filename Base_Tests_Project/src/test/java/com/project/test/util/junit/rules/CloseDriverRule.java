package com.project.test.util.junit.rules;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.project.test.pageobjects.base.BaseDialog;
import com.project.test.pageobjects.base.BasePageObjectWithRoot;
import com.project.test.util.FilePathUtils;
import com.project.test.util.junit.TestBase;
import com.project.test.util.junit.TestBaseDriverSetupAndKill;

public class CloseDriverRule implements AfterEachCallback, BeforeEachCallback {

	/**
	 * Closing all open dialogs on the current open page
	 * Logging out
	 * Closing the driver and all open windows
	 * Deleting created Download folders
	 */
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		TestBaseDriverSetupAndKill testBaseDriverSetupAndKill = (TestBaseDriverSetupAndKill) context.getTestInstance().get();

		int limit = 5;
		int i = 0;
		while (BasePageObjectWithRoot.isPresent(BaseDialog.class, testBaseDriverSetupAndKill.getBaseInformations()) && i < limit) {
	        if (Boolean.parseBoolean(System.getProperty("debugLogging"))) {
	            TestBaseDriverSetupAndKill.logger.trace("close dialog");
	        }
            BaseDialog dialog = new BaseDialog(testBaseDriverSetupAndKill.getBaseInformations());
            try {
                if (dialog.isXCloseButtonDisplayed()) {
                    if (Boolean.parseBoolean(System.getProperty("debugLogging"))) {
                        TestBaseDriverSetupAndKill.logger.trace("dialog x button was present");
                    }
                    dialog.closeDialog();
                } else {
                    if (dialog.isDialogCloseButtonDisplayed()) {
                        if (Boolean.parseBoolean(System.getProperty("debugLogging"))) {
                            TestBaseDriverSetupAndKill.logger.trace("dialog close button was present");
                        }
                        dialog.clickCloseButton();
                    }
                }
            } catch (Throwable e) {
                
            }
            i++;
        }

		try {
            if (Boolean.parseBoolean(System.getProperty("debugLogging"))) {
                TestBaseDriverSetupAndKill.logger.trace("logout");
            }
			TestBase.logout(testBaseDriverSetupAndKill.getBaseInformations());
		} catch (Throwable e) {
            if (Boolean.parseBoolean(System.getProperty("debugLogging"))) {
                TestBaseDriverSetupAndKill.logger.trace("logout failed");
            }
		}

		String downloadFolderPath = FilePathUtils.getInstance().getDownloadFilePath(testBaseDriverSetupAndKill);

        if (Boolean.parseBoolean(System.getProperty("debugLogging"))) {
            TestBaseDriverSetupAndKill.logger.trace("close driver");
        }
		testBaseDriverSetupAndKill.getDrivers().forEach(driver -> {
			try {
				if (!driver.toString().contains("(null)"))
					driver.quit();

			} catch (Throwable e) {
				TestBaseDriverSetupAndKill.logger.error("driver close failed " + e);
			}

		});		
		testBaseDriverSetupAndKill.deleteDownloadFolder(downloadFolderPath);
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		TestBaseDriverSetupAndKill testBaseDriverSetupAndKill = (TestBaseDriverSetupAndKill) context.getTestInstance().get();
        if (Boolean.parseBoolean(System.getProperty("debugLogging"))) {
            TestBaseDriverSetupAndKill.logger.trace("Creating a requiest for a driver");
        }
		testBaseDriverSetupAndKill.createDriverForTest(context.getTestMethod().get().getName());
		
	}
}
