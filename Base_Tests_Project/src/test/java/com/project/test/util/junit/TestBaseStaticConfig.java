package com.project.test.util.junit;

import com.project.test.util.OperationSystem;
import com.project.test.util.TestMode;
import com.project.testrail.enums.TestRailProjects;

public class TestBaseStaticConfig {
	
	protected static final TestMode mode;
    protected static final OperationSystem os;
    protected static String gridHost;
    protected static Integer gridPort;
    protected static String localDownloadFolderPath;
    protected static final String browser;
    protected static final String server; 
    protected static final int retry;
    protected static final boolean testrail;
    protected static final boolean preparedRun;
    protected static final String suiteName;
    protected static final boolean documentation;
    protected static final boolean skipPassedTests;
    protected static final boolean skipNonTestrailTests;
    protected static final boolean ignoreFailures;
    
    protected static final boolean checkForLongSteps;
    protected static int limitInSeconds;
    
    protected static boolean attachment = false;
    protected static boolean thumbnail = false;
    
    protected static final TestRailProjects testRailProject;
    protected static TestRailService testRailService;
    protected static List<TestInstance> testInstances;
    protected static int testRunId;
    protected int testCaseId;
    
    public static final Logger logger = LoggerFactory.getLogger(TestBaseStaticConfig.class);
    

    protected static boolean DEBUG_LOGGING;
    
    static {
        MDC.put("methodName", "TestBaseConfig-static{}");
        
        mode = getTestModeProperty(System.getProperty("mode"));
        os = getOperationSystemProperty(System.getProperty("os"));
        FilePathUtils.init(mode, os);
        browser = getBrowserTypeProperty(System.getProperty("browser"));
        server = getServerProperty(System.getProperty("SERVER_HOSTNAME"));
        retry = getRetryProperty(System.getProperty("retry"));
        testrail = getBooleanProperty("testrail");
        preparedRun = getBooleanProperty("preparedRun");
        suiteName = System.getProperty("suiteName");
        documentation = getBooleanProperty("documentation");
        skipPassedTests = getBooleanProperty("skip");
        skipNonTestrailTests = getBooleanProperty("skipNonTestrail");
        DEBUG_LOGGING = getBooleanProperty("debugLogging");
        checkForLongSteps = getBooleanProperty("checkForLongSteps");
        if (checkForLongSteps) {
            limitInSeconds = getIntegerProperty("limitInSeconds");
            if (limitInSeconds < 1) {
                logger.info("limitInSeconds needs to be at least 1, settintg limitInSeconds to 1");
                limitInSeconds = 1;
            }
        } else {
            limitInSeconds = 0;
        }
        if (System.getProperty("ignoreFailures") != null) {
            ignoreFailures = getBooleanProperty("ignoreFailures");
        } else {
            ignoreFailures = true;
        }
        testRailProject = getTestRailProjectProperty(System.getProperty("testrailproject"));
        
        if (testrail) {
            testRailService = new TestRailService();
            readFromPropoertiesFile("src/test/resources/testrail.properties");
            TestRunInfo.setAssignedToId(4);
            
            TestRunInfo.setProject(getTestRailService().getProject(testRailProject.getId()));
            
            logger.trace("nightly");
            boolean nightly = getBooleanProperty("nightlyRun");            
            if (nightly) {
                setUpTestRailId();
            } else {
                try {
                    logger.trace("Read test run ID");
                    testRunId = Integer.parseInt(System.getProperty("testRunId"));
                    logger.trace("test run id: " + testRunId);
                    
                    TestRunInfo.setTestRun(testRailService.getTestRun(testRunId));
                    if (preparedRun) {
                        testInstances = testRailService.getTests(testRunId);
                    }
                } catch (Exception e) {
                    logger.trace("Test run id failed");
                    TestBaseStaticConfig.createTestRun();
                }
            }
            
            attachment = getBooleanProperty("attachment");
            if (attachment) {
                thumbnail = getBooleanProperty("thumbnail");
            }
        }
        logger.info("Mode set to: " + mode);
        logger.info("Browser set to: " + browser);
        logger.info("Server set: " + server);
        logger.info("Retry set to: " + retry);
        logger.info("TestRail set to: " + testrail);
        logger.info("Documentation set to: " + documentation);
        logger.info("TestRail Project set to: " + testRailProject);
        
        MDC.remove("methodName");
    }

    private static TestMode getTestModeProperty(String testMode) {
        TestMode result = null;
        
        if(testMode == null || testMode.isEmpty()){
            logger.info("Test mode was null. Setting LOCAL mode.");
            return TestMode.LOCAL;
        }
        
        switch (testMode) {
            case "QA":
                result = TestMode.QA;
                logger.debug("Home folder on QA:" + System.getProperty("user.home"));
                if (System.getProperty("user.home").contains("systemprofile")){
                    System.setProperty("user.home", "C:\\Users\\suredms");
                }
                break;
            case "LOCAL":
                result = TestMode.LOCAL;
                break;
            case "GRID":
                result = TestMode.GRID;
                
                gridHost = System.getProperty("gridHost");
                if (gridHost == null || gridHost.isEmpty()) {
                    throw new Error("The gridHost parameter is required for grid mode");
                }
                
                String gridPortString = System.getProperty("gridPort");
                if (gridPortString == null || gridPortString.isEmpty()) {
                    logger.trace("Set no grid port property found, seting to the default 4444");
                    gridPort = 4444;
                } else {
                    logger.trace("Grid port property found " + gridPortString);
                    try {
                        gridPort = Integer.parseInt(gridPortString);
                    } catch (NumberFormatException e) {
                        logger.trace("Grid port parsing failed, set to the default 4444");
                        gridPort = 4444;
                    }
                }
                
                String localDownloadFolderPathString = System.getProperty("localDownloadFolderPath");
                if (localDownloadFolderPathString == null || localDownloadFolderPathString.isEmpty()) {
                    logger.trace("No local download folder path found, use C:\\\\sc");
                    localDownloadFolderPath = "C:\\\\sc";
                } else {
                    logger.trace("Local download folder path  found " + localDownloadFolderPathString);
                    localDownloadFolderPath = localDownloadFolderPathString;
                }
                
                break;
            default:
                throw new InvalidParameterException("Unexpected test mode: " + mode);
        }
        
        logger.info("Test mode set to: " + result.toString());
        return result;
    }
    
    private static OperationSystem getOperationSystemProperty(String osProperty) {
        if(osProperty == null || osProperty.isEmpty()){
            logger.info("The os property was null. Setting Windows as OS.");
            return OperationSystem.WINDOWS;
        }
        for(OperationSystem os: OperationSystem.values()) {
            if (os.name().toLowerCase().equals(osProperty.toLowerCase())) {
                logger.info("os value found, set it to: " + os);
                if (os.equals(OperationSystem.ANDROID)) {
                    gridHost = System.getProperty("gridHost");
                    if (gridHost == null || gridHost.isEmpty()) {
                        throw new Error("The gridURL parameter is required for grid mode");
                    }
                    
                    String gridPortString = System.getProperty("gridPort");
                    if (gridPortString == null || gridPortString.isEmpty()) {
                        logger.trace("Set no grid port property found, setting to the default 4444");
                        gridPort = 4444;
                    } else {
                        logger.trace("Grid port property found " + gridPortString);
                        try {
                            gridPort = Integer.parseInt(gridPortString);
                        } catch (NumberFormatException e) {
                            logger.trace("Grid port parsing failed, set to the default 4444");
                            gridPort = 4444;
                        }
                    }
                }
                return os;
            }
        }
        logger.info("The os value does not match any known os: " + osProperty + ", set os to windows");
        return OperationSystem.WINDOWS;
    }
    
    private static String getBrowserTypeProperty(String browserProperty) {
        if(browserProperty == null || browserProperty.isEmpty()){
            logger.info("Browser was null. Setting chrome as browser.");
            return "chrome";
        }
        return StringUtils.lowerCase(browserProperty);
    }
    
    private static String getServerProperty(String serverProperty) {
        if(serverProperty == null || serverProperty.isEmpty()){
            logger.info("Server was null. Setting " + ServerURL.QA_LOGIN_URL + " as server.");
            return ServerURL.QA_LOGIN_URL;
        }
        return StringUtils.lowerCase(serverProperty);
    }
    
    /**
     * 
     * @param retryProperty - it can have values 0 or above. -1 and 0 mean no retry but the test will be re-run the given amount of times in any other way
     * @return
     * @throws ConfigurationException
     */
    private static int getRetryProperty(String retryProperty) {
        if(retryProperty == null || retryProperty.isEmpty()){
            logger.info("Retry was null. Setting retry number to 0.");
            return 0;
        }
        
        Integer retryValue = null;		
        try {
            retryValue = Integer.parseInt(retryProperty);
            
            if(retryValue < 1){
                logger.warn("The retryProperty has a less than 1 value: " + retryProperty + ". It will be set to 0.");
                return 0;
            }
            
            logger.info("Retry value found!. Setting retry number to: " + retryValue.intValue());
        } catch (NumberFormatException e) {
            logger.error("Unable to parse retryProperty with value: " + retryProperty);
            throw new InvalidParameterException("The parsing of retryValue is not possible! Its value cannot be converted to integer: " + retryProperty);
        }
        
        return retryValue;
    }
    
    private static boolean getBooleanProperty(String property) {
        String value = System.getProperty(property);
        if(value == null || value.isEmpty()){
            logger.info(property + " was null. Setting " + property + " to false.");
            return false;
        }
        
        Boolean booleanValue = Boolean.parseBoolean(value);
        logger.info(property + " value found!. Setting " + property + " to: " + booleanValue);
        return booleanValue;
    }
    
    private static int getIntegerProperty(String property) {
        String value = System.getProperty(property);
        if(value == null || value.isEmpty()){
            logger.info(property + " was null. Setting " + property + " to -1.");
            return -1;
        }
        int intValue = -1;
        try {
            intValue  = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.info(property + " was failed to be parsed to int. Setting " + property + " to -1.");
            return -1;
        }
        logger.info(property + " value found!. Setting " + property + " to: " + intValue);
        return intValue;
    }
    
    private static TestRailProjects getTestRailProjectProperty(String testRailProject) {
        if(testRailProject == null || testRailProject.isEmpty()){
            switch (os) {
                case WINDOWS:
                    logger.info("TestRail Project was null. Setting TestRail Project to \"SureClinical - Web\"");
                    return TestRailProjects.SC_WEB;
                case ANDROID:
                    logger.info("TestRail Project was null. Setting TestRail Project to \"SureClinical - Mobile\"");
                    return TestRailProjects.SC_MOBILE;
                default:
                    throw new Error("No default project for this os: " + os);
            }
        }
        TestRailProjects project = TestRailProjects.getById(Integer.parseInt(testRailProject));
        logger.info("TestRail Project value found!. Setting TestRail Project to: " + project);
        return project;
    }
    
    private static void setUpTestRailId() {
        TestRailService testRailService = TestBaseStaticConfig.getTestRailService();
        List<TestRun> testRuns = testRailService.getTestRuns(TestBaseStaticConfig.getTestRailProject().getId());
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy"); 
        String expectedTestRunName = "Nightly Test Run "+ dtf.print(now) + " - " + dtf.print(now.plusDays(1));
        logger.trace("expectedTestRunName: " + expectedTestRunName);
        
        boolean testRunFound = false;
        
        for (TestRun testRun: testRuns) {
            if (testRun.getName().equals(expectedTestRunName)) {
                TestBaseStaticConfig.setTestRunId(testRun.getId());
                TestRunInfo.setTestRun(testRun);
                testRunFound = true;
                break;
            }
        }
        
        if (!testRunFound) {
            TestBaseStaticConfig.createTestRun(expectedTestRunName);
        }
    }
    
    public static void createTestRun() {
        createTestRun(ServerURL.VERSION + (suiteName!=null?" " + suiteName:"") + " " + new LocalDateTime());
    }
    
    public static void createTestRun(String name) {
        
        TestRunCreator testRunCreator = new TestRunCreator();
        testRunCreator.setName(name);
        testRunCreator.setIncludeAll(false);
        
        //set milestone for the test run
        String milestoneIdProperty = System.getProperty("milestoneId");
        if (milestoneIdProperty != null && !milestoneIdProperty.isEmpty()){
            logger.debug("milestoneId: " + milestoneIdProperty);
            try {
                Integer milestoneId = Integer.parseInt(milestoneIdProperty);
                testRunCreator.setMilestoneId(milestoneId);
            } catch (NumberFormatException e) {
                logger.debug("milestoneId was not a valid integer, continue without setting the milestone");
                e.printStackTrace();
            }
        } else {
            logger.debug("milestoneId not found, continue test run without setting the milestone");
        }
        
        
        
        TestRun testRun = getTestRailService().addTestRun(testRailProject.getId(), testRunCreator);
        TestRunInfo.setTestRun(testRun);
        logger.trace("test run id: " + testRun.getId());
        TestBaseStaticConfig.setTestRunId(testRun.getId());
        
    }
    
    /**
     *
     * Read data from properties file.
     * Set up TestRailService to be able to communicate with desired TestRail instance
     *
     * @param propertiesFile - file which holds the credentials and URL to the TestRail instance
     */
    protected static void readFromPropoertiesFile(String propertiesFile){
        Properties properties = new Properties();
        InputStream resource = null;
        try {
            try {
                resource = new FileInputStream(new File(propertiesFile));
                properties.load(resource);
            } catch (IOException e) {
                throw new InvalidParameterException("Properties file is not valid.");
            }
            
            //If clientId isn't in there or it's blank, look for api_endpoint. If it's there and not null, that means we have a "local" TestRail Instance.
            // Set the service's apiEndpoint accordingly for the remainder of the test.
            //If both properties are defined, api_endpoint will be ignored
            String clientId = properties.getProperty("clientId");
            if (null == clientId || clientId.isEmpty()) {
                String apiEndpoint = properties.getProperty("api_endpoint");
                try {
                    getTestRailService().setApiEndpoint(new URL(apiEndpoint));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                //If clientID wasn't null after all, set it, and use the "hosted" TestRail Instance code path
                getTestRailService().setClientId(clientId);
            }
            //Set up all the credentials
            getTestRailService().setUsername(properties.getProperty("username"));
            getTestRailService().setPassword(properties.getProperty("password"));
            TestRunInfo.setAssignedToId(Integer.parseInt(properties.getProperty("assignedToId")));
        } finally {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static TestMode getMode() {
        return mode;
    }
    
    public static OperationSystem getOs() {
        return os;
    }

    public static String getBrowser() {
        return browser;
    }
    
    public static String getServer() {
        return server;
    }
    
    public static int getRetry() {
        return retry;
    }
    
    public static boolean isTestrail() {
        return testrail;
    }
    
    public static TestRailService getTestRailService() {
        return testRailService;
    }
    
    public static TestRailProjects getTestRailProject() {
        return testRailProject;
    }
    
    public static int getTestRunId() {
        return testRunId;
    }
    
    public static void setTestRunId(int testRunId) {
        TestBaseStaticConfig.testRunId = testRunId;
    }
    
    public static boolean isDocumentation() {
        return documentation;
    }
    
    public static String getGridHost() {
        return gridHost;
    }
    
    public static Integer getGridPort() {
        return gridPort;
    }
    
    public static boolean isPassedSkip() {
        return skipPassedTests;
    }

    public static boolean isTestsWithoutTestrailSkip() {
        return skipNonTestrailTests;
    }
    
    public static boolean isIgnoreFailures() {
        return ignoreFailures;
    }
    
    public static boolean isAttachment() {
        return attachment;
    }
    
    public static boolean isThumbnail() {
        return thumbnail;
    }

    public static boolean isPreparedrun() {
        return preparedRun;
    }

    public static List<TestInstance> getTestInstances() {
        return testInstances;
    }

    public static boolean isCheckForLongSteps() {
        return checkForLongSteps;
    }
    
    public static int getLimitInSeconds() {
        return limitInSeconds;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

}
