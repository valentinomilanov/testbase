package com.project.test.constants;

/**
 * 
 * @author Valentino Milanov
 *
 * enum with information about email accounts used for the project
 */
public enum EmailAccount {

	//FIXME add all information for your email accounts! 
	
	EMAIL_USER_1("[add email of User 1 withott host (e.g. without @gmail.com)]", "[add password for that email account]"),
	//Exaple of email account:
    EMAIL_USER_2("tester1.wedoqa.co", ".zl#P~K4vuw5"),
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
