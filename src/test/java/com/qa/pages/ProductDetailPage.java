package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ProductDetailPage {

    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[1]")
    @iOSXCUITFindBy(accessibility = "Sauce Labs Backpack")
    WebElement productTitle;

    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[2]")
    @iOSXCUITFindBy(accessibility = "test-BACK TO PRODUCTS")
    WebElement productDescription;

    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-BACK TO PRODUCTS\"]")
    @iOSXCUITFindBy(accessibility = "test-BACK TO PRODUCTS")
    WebElement backToProductButton;

    @iOSXCUITFindBy(accessibility = "test-ADD TO CART")
    WebElement addToCartButton;

    BaseTest baseTest;

    public String getProductTitle() {
        return baseTest.getText(productTitle);
    }

    public String scrollToPriceAndGetPrice(){
        return baseTest.getText(baseTest.scrollToElementAndroid());
    }

    public void scrollPageIOS(){
        baseTest.scrollToElementIOS();
    }

    public String getProductDescription() {
        return baseTest.getText(productDescription);
    }

    public ProductDetailPage(){
        baseTest = new BaseTest();
        PageFactory.initElements(new AppiumFieldDecorator(baseTest.getDriver()), this);
    }

    public ProductsPage pressBackToProductsButton(){
        baseTest.clickOnWebElement(backToProductButton);
        return new ProductsPage();
    }

    public boolean isAddToCartButtonVisible(){
        return addToCartButton.isDisplayed();
    }

}
