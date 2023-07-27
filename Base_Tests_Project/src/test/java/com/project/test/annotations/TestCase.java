package com.project.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Created by wedoqa
*
* id - Test Case id from TestRail (e.g. C23)
* name - Test Case name from TestRail
*
* Mark every test case implementation with this annotation to provide information about TestRail TestCase
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //can use in method only.
public @interface TestCase {
	public int id() default 0;
	//public int mobileId() default 0;		* this is only used if the same code is used for mobile and web app
    public String name() default "test";
}
