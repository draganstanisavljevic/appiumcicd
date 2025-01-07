package com.qa;

import com.qa.utils.TestUtils;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.ThreadContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class BaseTest2 {

    protected AppiumDriverLocalService appiumServer;
    protected TestUtils testUtils = new TestUtils();

    @BeforeSuite
    public void beforeSuite(){
        appiumServer = getAppiumServer();
        if(!checkIfAppiumServerIsRunning(4723)){
            appiumServer.start();
            appiumServer.clearOutPutStreams();
            ThreadContext.put("ROUTINGKEY", "ServerLogs");
            testUtils.log().info("Appium server started");
        }else{
            testUtils.log("Appium server already running");
        }

    }

    @AfterSuite
    public void afterSuite(){
        appiumServer.stop();
        testUtils.log().info("Appium server stopped");
    }

    private boolean checkIfAppiumServerIsRunning(int port){
        boolean isAppiumServerRunning = false;
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
            socket.close();
        } catch (IOException e) {
            System.out.println("1");
            isAppiumServerRunning = true;
            throw new RuntimeException(e);
        } finally {
            socket = null;
        }
        return isAppiumServerRunning;
    }

    private AppiumDriverLocalService getAppiumServer(){
        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder().
                usingDriverExecutable(new File("/usr/local/bin/node")).
                withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js")).
                usingPort(4723).
                withArgument(GeneralServerFlag.SESSION_OVERRIDE).
                withLogFile(new File("ServerLogs/server.log")));
    }
}
