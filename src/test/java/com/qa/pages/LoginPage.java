package com.qa.pages;

import com.qa.BaseTest;
import com.qa.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(accessibility = "test-Username")
    private WebElement usernameTextField;

    @AndroidFindBy(accessibility = "test-Password")
    @iOSXCUITFindBy(accessibility = "test-Password")
    WebElement passwordTextField;

    @AndroidFindBy(accessibility = "test-LOGIN")
    @iOSXCUITFindBy(accessibility = "test-LOGIN")
    WebElement loginButton;

    @AndroidFindBy(accessibility = "test-Error message")
    WebElement errorMsg;

    TestUtils testUtils = new TestUtils();

    BaseTest baseTest;
    public LoginPage(){
        baseTest = new BaseTest();
        PageFactory.initElements(new AppiumFieldDecorator(baseTest.getDriver()), this);
    }

    public LoginPage enterUsername(String username){
        testUtils.log("Login with " + username);
        baseTest.sendKeys(usernameTextField, username);
        return this;
    }

    public LoginPage enterPassword(String password){
        testUtils.log("Password is " + password);
        baseTest.sendKeys(passwordTextField, password);
        return this;
    }

    public ProductsPage pressLoginButton(){
        baseTest.clickOnWebElement(loginButton, "Press login button");
        return new ProductsPage();
    }

    public String getErrorMessage(){
        return baseTest.getText(errorMsg);
    }

    public ProductsPage login(String username, String password){
        enterUsername(username);
        enterPassword(password);
        pressLoginButton();
        return new ProductsPage();
    }

}
