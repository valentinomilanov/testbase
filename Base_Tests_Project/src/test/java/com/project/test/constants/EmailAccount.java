package com.project.test.constants;

public enum EmailAccount {

	//TODO add all information for your email accounts! 
	
	EMAIL_USER_1("[add email of User 1 withott host (e.g. without @gmail.com)]", "[add password for that email account]"),
    EMAIL_USER_2("[add email of User 2 withott host (e.g. without @gmail.com)]", "[add password for that email account]"),
    ;
    
    public static final String EMAIL_HOST = "add host extension, e.g. @gmail.com";
    
    private String password;
    private String shortEamil;
    
    private EmailAccount(String shortEamil, String password) {
        this.shortEamil = shortEamil;
        this.password = password;
    }

    public String getEmail() {
        return shortEamil + EMAIL_HOST;
    }

    public String getPassword() {
        return password;
    }

    public String getShortEamil() {
        return shortEamil;
    }
}
