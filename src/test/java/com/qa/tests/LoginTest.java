package com.qa.tests;

import com.qa.BaseTest;
import com.qa.BaseTest2;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;


public class LoginTest extends BaseTest2 {

    static LoginPage loginPage;
    static ProductsPage productsPage;
    BaseTest baseTest;

    JSONObject loginUsers;


    @BeforeClass
    @Parameters({"emulator", "platformName", "platformVersion", "deviceName", "udid",
            "systemPort", "chromeDrivePort", "wdaLocalPort", "webkitDebugProxyPort"})
    public void beforeClass(@Optional("androidOnly") String emulator, String platformName,
                            @Optional String platformVersion, String deviceName, @Optional String udid,
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
        //baseTest.initializeDriver("android", "13.0", "any");
        baseTest.closeApp();
        baseTest.lunchApp();
    }

    @AfterClass
    public void afterClass(){
        baseTest.afterTest();
    }

    @BeforeMethod
    public void beforeMethod(Method m){
        baseTest.beforeMethod();
        testUtils.log("\n" + "***** Starting test " + m.getName() + "\n");
        loginPage = new LoginPage();
    }

    @AfterMethod
    public void afterMethod(ITestResult result){
        baseTest.afterMethod(result);
        testUtils.log("-------- Login test after method -------------");
    }







//    public AppiumDriverLocalService getAppiumDefaultServer(){
//        return AppiumDriverLocalService.buildDefaultService();
//    }



    @Test
    public void testInvalidUsername() {
        loginPage.enterUsername(loginUsers.getJSONObject("invalidUser").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"));
        loginPage.pressLoginButton();
        String errorMsg = loginPage.getErrorMessage();
        testUtils.log("Actual error message = " + errorMsg);
        String expectedMessage = BaseTest.getStrings().get("err_invalid_username_or_password");
        testUtils.log("expectedMessage = " + expectedMessage);
        Assert.assertEquals(errorMsg, expectedMessage);
    }

    @Test
    public void testInvalidPassword() {
        loginPage.enterUsername(loginUsers.getJSONObject("invalidPassword").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("invalidPassword").getString("password"));
        loginPage.pressLoginButton();
        String errorMsg = loginPage.getErrorMessage();
        testUtils.log("Actual error message = " + errorMsg);
        String expectedMessage = BaseTest.getStrings().get("err_invalid_username_or_password");
        testUtils.log("expectedMessage = " + expectedMessage);
        Assert.assertEquals(errorMsg, expectedMessage);
    }

    @Test
    public void testValidUser() {
        loginPage.enterUsername(loginUsers.getJSONObject("validUser").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("validUser").getString("password"));
        loginPage.pressLoginButton();

    }
}
