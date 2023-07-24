package com.project.test.constants;

public class ServerURL {

	public static final String VERSION = "3.0";
	
	public static final String QA_LOGIN_URL = ((null == System.getProperty("SERVER_HOSTNAME")) 
			|| System.getProperty("SERVER_HOSTNAME")
			.isEmpty()) ? "add full server URL e.g 'https://qa-web.project.com' " : System.getProperty("SERVER_HOSTNAME");
	
	public static final String QA_SERVER_SHIORT = ((null == System.getProperty("SERVER_SHORT")) 
			|| System.getProperty("SERVER_SHORT")
			.isEmpty()) ? "[add server extension e.g. 'qa-web' " : System.getProperty("SERVER_SHORT");
}
