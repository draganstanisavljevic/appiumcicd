package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.*;
import com.qa.utils.TestUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class ProductsTest {

    static LoginPage loginPage;
    static ProductsPage productsPage;

    TestUtils testUtils = new TestUtils();

    SettingsPage settingsPage;

    ProductDetailPage productDetailPage;
    BaseTest baseTest;

    JSONObject loginUsers;

    @BeforeClass
    @Parameters({"emulator", "platformName", "platformVersion", "deviceName", "udid",
            "systemPort", "chromeDrivePort", "wdaLocalPort", "webkitDebugProxyPort"})
    public void beforeClass(String emulator, String platformName,
                            @Optional String platformVersion, String deviceName, String udid,
                            @Optional("androidOnly") String systemPort,
                            @Optional("androidOnly") String chromeDrivePort,
                            @Optional("iOSOnly") String wdaLocalPort,
                            @Optional("iOSOnly") String webkitDebugProxyPort) throws IOException {
        InputStream datais = null;
        try{
            String dataFileName = "data/loginUsers.json";
            datais = getClass().getClassLoader().getResourceAsStream(dataFileName);
            JSONTokener tokener = new JSONTokener(datais);
            loginUsers = new JSONObject(tokener);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            if(datais != null){
                datais.close();
            }
        }

        baseTest = new BaseTest();
        baseTest.beforeTest(emulator, platformName, platformVersion, deviceName, udid, systemPort,
                chromeDrivePort, wdaLocalPort, webkitDebugProxyPort);
        baseTest.closeApp();
        baseTest.lunchApp();
        //baseTest.initializeDriver("android", "13.0", "any");
    }

    @AfterClass
    public void afterClass(){
        baseTest.afterTest();
    }


    @BeforeMethod
    public void beforeMethod(Method m){
        baseTest.beforeMethod();
        testUtils.log("-------- Product test before method -------------");
        testUtils.log("\n" + "***** Starting test " + m.getName() + "\n");
        loginPage = new LoginPage();
        productsPage = loginPage.login(loginUsers.getJSONObject("validUser").getString("username"),
                loginUsers.getJSONObject("validUser").getString("password"));
    }

    @AfterMethod
    public void afterMethod(ITestResult result){
        baseTest.afterMethod(result);
        testUtils.log("-------- Product test after method -------------");
        MenuPage menuPage = new MenuPage();
        settingsPage = menuPage.pressSettingsButton();
        settingsPage.pressLogoutButton();
    }

    @Test
    public void testValidateProductsOnProductPage() {
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(productsPage.getSLBTitle(), baseTest.getStrings().get("products_page_slb_title"));
        softAssert.assertEquals(productsPage.getSLBPrice(), baseTest.getStrings().get("products_page_slb_price"));

        softAssert.assertAll();
    }

    @Test
    public void testValidateProductsOnProductDetailPage() {
        SoftAssert softAssert = new SoftAssert();

        ProductDetailPage productDetailPage = productsPage.pressSLBTitle();

        testUtils.log(productDetailPage.getProductTitle());
        testUtils.log(productDetailPage.getProductDescription());
        softAssert.assertEquals(productDetailPage.getProductTitle(),
                baseTest.getStrings().get("products_detail_title"));
        softAssert.assertEquals(productDetailPage.getProductDescription(),
                baseTest.getStrings().get("products_detail_description"));

        String price = productDetailPage.scrollToPriceAndGetPrice();
        softAssert.assertEquals(productDetailPage.getProductDescription(),
                baseTest.getStrings().get("products_detail_description"));

        softAssert.assertAll();

    }

    @Test
    public void testValidateProductsOnProductDetailPageIOS() {
        SoftAssert softAssert = new SoftAssert();

        ProductDetailPage productDetailPage = productsPage.pressSLBTitle();

        testUtils.log(productDetailPage.getProductTitle());
        testUtils.log(productDetailPage.getProductDescription());
        softAssert.assertEquals(productDetailPage.getProductTitle(),
                baseTest.getStrings().get("products_detail_title"));
        softAssert.assertEquals(productDetailPage.getProductDescription(),
                baseTest.getStrings().get("products_detail_description"));

        baseTest.scrollToElementIOS();;
        softAssert.assertTrue(productDetailPage.isAddToCartButtonVisible());

        softAssert.assertAll();

    }

}
