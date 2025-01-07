package com.qa.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.qa.BaseTest;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TestListener implements ITestListener{

    TestUtils testUtils = new TestUtils();

    @Override
    public void onTestStart(ITestResult result){
        BaseTest baseTest = new BaseTest();
        ExtentReport.startTest(result.getName(), result.getMethod().getDescription()).
                assignCategory(baseTest.getPlatform() + "_" + baseTest.getDeviceName()).
                assignAuthor("Dragan S.");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReport.getTest().log(Status.PASS, "Test Passed");

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReport.getTest().log(Status.SKIP, "Test Skipped");

    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReport.getReporter().flush();
    }

    public void onTestFailure(ITestResult result) {
        if(result.getThrowable() != null){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.getThrowable().printStackTrace();
            testUtils.log(pw.toString());
        }

        Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();
        BaseTest baseTest = new BaseTest();
        File file = baseTest.getDriver().getScreenshotAs(OutputType.FILE);
        byte[] encoded = null;
        try {
            encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String destinationPath = "screenshots" + "_" + params.get("platformName") +
                "_" + params.get("platformVersion") + "_" + params.get("deviceName") + File.separator +
                baseTest.getDateTime() + File.separator + result.getTestClass().getRealClass().getSimpleName() +
                File.separator + result.getName() + ".png";
        String completeImagePath = System.getProperty("use.dir") + File.separator + destinationPath;

        try {
            FileUtils.copyFile(file, new File(destinationPath));
            Reporter.log("This is a sample screenshot");
            Reporter.log("<a href = '" + completeImagePath + "'><img src ='" + completeImagePath +
                    "' height=100 width = 100/></a>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            ExtentReport.getTest().fail("Test Failed",
                    MediaEntityBuilder.createScreenCaptureFromPath(completeImagePath).build());
            ExtentReport.getTest().fail("Test Failed",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(new String(encoded, StandardCharsets.US_ASCII)).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ExtentReport.getTest().fail(result.getThrowable());
    }
}
