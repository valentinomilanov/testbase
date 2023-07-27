package com.project.test.pageobjects.base;

import com.project.test.enums.TypeOfServer;
import com.project.test.util.OperationSystem;

import org.openqa.selenium.WebDriver;

public class BaseInformations {

	final WebDriver driver;
    private final OperationSystem os;
    private TypeOfServer typeOfServer = TypeOfServer.INSTANCE_1;
    
    public BaseInformations(WebDriver driver, OperationSystem os) {
        super();
        this.driver = driver;
        this.os = os;
    }
    
    public BaseInformations(WebDriver driver, OperationSystem os, TypeOfServer typeOfServer) {
        super();
        this.driver = driver;
        this.os = os;
        this.typeOfServer = typeOfServer;
    }
    
    public WebDriver getDriver() {
        return driver;
    }
    
    public OperationSystem getOperationSystem() {
        return os;
    }
    
    public TypeOfServer getTypeOfServer() {
        return typeOfServer;
    }
    
    public void setTypeOfServer(TypeOfServer typeOfServer) {
        this.typeOfServer = typeOfServer;
    }
}
