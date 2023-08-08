package com.project.test.enums;

/**
 * 
 * @author Valentino Milanov
 *
 * Enum with all users and their informations that are used in the project
 */
public enum UsersEnum {

	ADMIN("admin", "'o@$D4g&Y0@a=;}e", "sureclinicaltester1.wedoqa.co+admin@wedoqa.co", "SureClinical", "Support" ,"1111"),
	USER_FOR_AUTOTEST_93011_1("", "2HZ$9g!Myv~#8jV6", "qtester113+399909857478@gmail.com", "Autotest93011_1", "Autotest93011_1", ""),
	;
	
	private final String username;
	private final String password;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String pin;

	UsersEnum(String username, String password, String email, String firstName, String lastName, String pin) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.pin = pin;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPin() {
		return pin;
	}

	public String getFullName(){
		return firstName + " " + lastName;
	}
}
