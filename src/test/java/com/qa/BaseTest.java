package com.qa;

import com.aventstack.extentreports.Status;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterTest;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BaseTest {

    protected static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    protected static ThreadLocal<Properties> props = new ThreadLocal<>();
    public static ThreadLocal<HashMap<String, String>> strings = new ThreadLocal<>();
    protected static ThreadLocal<String> platform = new ThreadLocal<>();
    protected static ThreadLocal<String> dateTime = new ThreadLocal<>();

    //we need deviceName for logging threads
    protected static ThreadLocal<String> deviceName = new ThreadLocal<>();


    String propFileName = "config.properties";
//    InputStream inputStream;
//    InputStream stringsis;
    TestUtils testUtils = new TestUtils();

    public BaseTest(){

    }

    public void beforeMethod(){
        testUtils.log("-------- Super before method -------------");
        ((CanRecordScreen) getDriver()).startRecordingScreen();
    }

    public void afterMethod(ITestResult result){
        testUtils.log("-------- Super after method -------------");
        String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();
        if(result.getStatus() == 2){
            Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();
            String destinationPath = "videos" + "_" + params.get("platformName") +
                    "_" + params.get("platformVersion") + "_" + params.get("deviceName") + File.separator +
                    this.getDateTime() + File.separator + result.getTestClass().getRealClass().getSimpleName();
            File videoDir = new File(destinationPath);
            if(!videoDir.exists()){
                videoDir.mkdirs();
            }
            try {
                FileOutputStream stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4");
                stream.write(Base64.decodeBase64(media));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void beforeTest(String emulator, String platformName,
                           String platformVersion, String deviceName,
                           String udid, String systemPort, String chromeDrivePort, String wdaLocalPort,
                           String webkitDebugProxyPort ) throws IOException {

        setDateTime(TestUtils.dateTime());
        setPlatform(platformName);
        setDeviceName(deviceName);
        InputStream inputStream = null;
        InputStream stringsis = null;
        try{
            //props = new Properties();
            setProps(new Properties());
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            getProps().load(inputStream);
            String xmlFilePath = "strings/strings.xml";
            stringsis = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            //testUtils = new TestUtils();
            //strings = testUtils.parseXmlFile(stringsis);
            setStrings(testUtils.parseXmlFile(stringsis));
//          or use another way (code bellow)
//            String apkPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" +
//                    File.separator + "resources" + File.separator + "old.apk";

            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName(platformName)
                    .setDeviceName(deviceName);
            String appiumServerUrl = getProps().getProperty("appiumUrl");
            URL appiumUrl = new URL(appiumServerUrl);;
            switch(platformName){
                case "android":
                    //it will not work on windows, you must use
                    //System.getProperty("user.dir") + File.separator + "src" + File.separator ...
                    // see sarce code or lesson 158 minute 34
                    String androidAppLocation = getClass().getResource(getProps().getProperty("androidAppLocation")).getFile();
                    if(emulator.equalsIgnoreCase("true")){
                        options.setPlatformVersion(platformVersion);
                        options.setAvd("Pixel_5").setAvdLaunchTimeout(Duration.ofSeconds(200));
                    }else{
                        options.setUdid(udid);
                    }
                    if(!systemPort.equalsIgnoreCase("androidOnly")){
                        options.setSystemPort(Integer.parseInt(systemPort));

                    }
                    if(!chromeDrivePort.equalsIgnoreCase("androidOnly")){
                        options.setChromedriverPort(Integer.parseInt(chromeDrivePort));
                    }
                    options.setAutomationName(getProps().getProperty("androidAutomationName"))
                            .setNewCommandTimeout(Duration.ofMinutes(5))
                            .setNoReset(true)
//                            .setUnlockType("pin")
//                            .setUnlockKey("1208")
                            .setAppPackage(getProps().getProperty("androidAppPackage"))
                            .setAppActivity(getProps().getProperty("androidAppActivity"))
                            //setapp will always install app, if you already have it installed
                            //use set bundle id (from config file)
                            .setApp(androidAppLocation);
                    setDriver(new AndroidDriver(appiumUrl, options));
                    break;
                case "iOS":
                    String iOSAppLocation = getClass().getResource(getProps().getProperty("iOSAppLocation")).getFile();
                    options.setPlatformVersion(platformVersion);
                    options.setAutomationName(getProps().getProperty("iOSAutomationName"))
                            .setNewCommandTimeout(Duration.ofMinutes(5))
                            .setApp(iOSAppLocation);
                    if(!wdaLocalPort.equalsIgnoreCase("iOSOnly")){
                        options.setCapability("wdaLocalPort", wdaLocalPort);
                    }
                    if(!webkitDebugProxyPort.equalsIgnoreCase("iOSOnly")){
                        options.setCapability("webkitDebugProxyPort", webkitDebugProxyPort);
                    }
                    setDriver(new IOSDriver(appiumUrl, options));
                    break;
                default:
                    throw new Exception("Invalid platform " + platformName);
            }
            testUtils.log("Session id: " + getDriver().getSessionId());
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
            if(stringsis != null){
                stringsis.close();
            }
        }
    }

    public void waitForVisibility(WebElement element){
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void clickOnWebElement(WebElement e){
        waitForVisibility(e);
        e.click();
    }

    public void clickOnWebElement(WebElement e, String msg){
        waitForVisibility(e);
        testUtils.log().info(msg);
        ExtentReport.getTest().log(Status.INFO, msg);
        e.click();
    }

    public void clear(WebElement e){
        waitForVisibility(e);
        e.clear();
    }
    public void sendKeys(WebElement e, String keys){
        clear(e);
        waitForVisibility(e);
        e.sendKeys(keys);
    }

    public String getText(WebElement e){
        switch (getPlatform()){
            case "android":
                return getAttribute(e, "text");
            case "iOS":
                return getAttribute(e, "label");
            default:
                return null;
        }
    }
    public String getAttribute(WebElement e, String attribute){
        waitForVisibility(e);
        return e.getAttribute(attribute);
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        if(getDriver() != null){
            getDriver().quit();
        }
    }

    public void closeApp(){
        ((InteractsWithApps) getDriver()).terminateApp(getProps().getProperty("androidAppPackage"));
    }

    public void lunchApp() {
        switch (getPlatform()){
            case "android":
                ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("androidAppPackage"));
                break;
            case "iOS":
                ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("iOSBundleId"));
                break;
        }
    }

    public WebElement scrollToElementAndroid(){
        return getDriver().findElement(AppiumBy.androidUIAutomator("new UIScrollable(new UISelector()" +
                ".description(\"test-Inventory item page\")).scrollIntoView(" +
                "new UISelector.description(\"test-Price\"));"));
    }

    public void scrollToElementIOS(){
        RemoteWebElement element = (RemoteWebElement) getDriver().findElement(By.className("XCUIElementTypeScrollView"));
        String elementId = element.getId();
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("element", elementId);
        scrollObject.put("direction", "down");
        getDriver().executeScript("mobile:scroll", scrollObject);
    }

    public void setDriver(AppiumDriver driver){
        this.driver.set(driver);
    }

    public AppiumDriver getDriver(){
        return driver.get();
    }

    public Properties getProps(){
        return this.props.get();
    }

    public void setProps(Properties props){
        this.props.set(props);
    }

    public static HashMap<String, String> getStrings(){
        return strings.get();
    }

    public void setStrings(HashMap<String, String> strings){
        this.strings.set(strings);
    }

    public String getPlatform(){
        return this.platform.get();
    }

    public void setPlatform(String platform){
        this.platform.set(platform);
    }

    public String getDateTime(){
        return dateTime.get();
    }

    public void setDateTime(String dateTime){
        this.dateTime.set(dateTime);
    }

    public String getDeviceName(){
        return deviceName.get();
    }

    public void setDeviceName(String deviceName){
        this.deviceName.set(deviceName);
    }
}

