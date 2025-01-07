package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class SettingsPage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"LOGOUT\"]")
    @iOSXCUITFindBy(accessibility = "test-LOGOUT")
    public WebElement logoutButton;

    BaseTest baseTest;
    public SettingsPage(){
        baseTest = new BaseTest();
        PageFactory.initElements(new AppiumFieldDecorator(baseTest.getDriver()), this);
    }

    public LoginPage pressLogoutButton(){
        baseTest.clickOnWebElement(logoutButton);
        return new LoginPage();
    }


}
