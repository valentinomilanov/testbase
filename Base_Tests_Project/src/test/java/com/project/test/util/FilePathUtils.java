package com.project.test.util;

import java.io.File;

import org.openqa.selenium.remote.RemoteWebDriver;

import com.project.test.util.junit.TestBaseDriverSetupAndKill;
import com.project.test.util.junit.TestBaseStaticConfig;

public class FilePathUtils {

	private final TestMode testMode;
	private final OperationSystem os;
	
	private static FilePathUtils instance;
	
	public FilePathUtils(TestMode mode, OperationSystem os) {
		this.testMode = mode;
		this.os = os;
	}

	public static void init(TestMode mode, OperationSystem os){
		instance = new FilePathUtils(mode, os);
	}
	
	public static FilePathUtils getInstance() {
		return instance;
	}
	
	public String getUploadFilePath (String fileName) {
	    if (os.equals(OperationSystem.ANDROID)) {
	        return "//storage/emulated/0/Download/upload/" + fileName;
	    }
		return getWindowsUploadFilePath(fileName);
	}
	
	public String getWindowsUploadFilePath (String fileName) {
		switch (testMode) {
		case GRID:
			return "C:" + File.separator + "sc-test-data-upload" + File.separator + "upload" + File.separator + fileName;
		case LOCAL:
		case QA:
		default:
			return new File("." + File.separator + "src" + File.separator + "test" + File.separator +"resources" + File.separator + "upload" + File.separator + fileName).getAbsolutePath() ;
		}
	}
	
	public String getImportFilePath (String fileName) {
		switch (testMode) {
		case GRID:
			return "C:" + File.separator + "sc-test-data-upload" + File.separator + "import" + File.separator + fileName;
		case LOCAL:
		case QA:
		default:
			return new File("." + File.separator + "src" + File.separator + "test" + File.separator +"resources" + File.separator + "import" + File.separator + fileName).getAbsolutePath() ;
		}
	}

    public String getImagesFilePath (String fileName) {
        switch (testMode) {
            case GRID:
                return "C:" + File.separator + "sc-test-data-upload" + File.separator + "images" + File.separator + fileName;
            case LOCAL:
            case QA:
            default:
                return new File("." + File.separator + "src" + File.separator + "test" + File.separator +"resources" + File.separator + "images" + File.separator + fileName).getAbsolutePath() ;
        }
    }
	
	@SuppressWarnings("deprecation")
    public String getDownloadFilePath (TestBaseDriverSetupAndKill testInstance) {
		switch (testMode) {
		case GRID:
			RemoteWebDriver driver = (RemoteWebDriver) testInstance.getDriver();
			ActiveNodeDeterminer nodeDeterminer = new ActiveNodeDeterminer(TestBaseStaticConfig.getGridHost(), TestBaseStaticConfig.getGridPort());
			return "\\\\" + nodeDeterminer.getNodeInfoForSession(driver.getSessionId()).getNodeIp() + "\\" + testInstance.getDownloadPath().substring(testInstance.getDownloadPath().indexOf("sc"));
		case LOCAL:
		case QA:
		default:
			return testInstance.getDownloadPath();
		}
	}
}
