package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ProductsPage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"PRODUCTS\"]")
    private WebElement pageTitle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"test-Item title\" and @text=\"Sauce Labs Backpack\"]")
    private WebElement SLBTitle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"test-Price\" and @text=\"$29.99\"]")
    private WebElement SLBPrice;



    BaseTest baseTest;

    public ProductsPage(){
        baseTest = new BaseTest();
        PageFactory.initElements(new AppiumFieldDecorator(baseTest.getDriver()), this);
    }

    public String getTitle(){
        return baseTest.getText(pageTitle);
    }

    public String getSLBTitle(){
        return baseTest.getText(SLBTitle);
    }

    public String getSLBPrice(){
        return baseTest.getText(SLBPrice);
    }

    public ProductDetailPage pressSLBTitle(){
        baseTest.clickOnWebElement(SLBTitle);
        return new ProductDetailPage();
    }
}
