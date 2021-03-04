package com.saucelabs.example;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Simple TestNG test which demonstrates being instantiated via a DataProvider in order to supply multiple browser combinations.
 * @author Bill Meyer
 */







public class TestBase
{
    protected static final boolean realDeviceTesting = true;
    protected static final String userName = System.getenv("SAUCE_USERNAME");
    protected static final String accessKey = System.getenv("SAUCE_ACCESS_KEY");

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<IOSDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return Two dimensional array of objects with browser, version, and platform information
     */
    @DataProvider(name = "hardCodedDevices", parallel = true)
    public static Object[][] sauceDeviceDataProvider(Method testMethod)
    {
        if (realDeviceTesting == true)
        {
            return new Object[][] {
                    new Object[]{"iOS", "iPhone_XR_POC178", "12.3.1"},
                    new Object []{"iOS", "iPad Air", "12.4.8"},
            };
        }
        else
        {
            return new Object[][]{
                    new Object[]{"iOS", "iPhone XS Simulator", "13.0"},
                    new Object[]{"iOS", "iPad Pro (12.9 inch) Simulator", "13.0"}
            };
        }
        // @formatter:on
    }

    protected void annotateJob(String text)
    {
        /**
         * Example of using the JavascriptExecutor to annotate the job execution as it runs
         *
         * @see https://wiki.saucelabs.com/display/DOCS/Annotating+Tests+with+Selenium%27s+JavaScript+Executor
         */

        driverThreadLocal.get().executeScript("sauce:context=" + text);
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the platformName,
     * platformVersion and deviceName parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the userName and access key populated by the authentication instance.
     *
     * @param platformName    Represents the platformName to be used as part of the test run.
     * @param platformVersion Represents the platformVersion of the platformName to be used as part of the test run.
     * @param deviceName      Represents the operating system to be used as part of the test run.
     * @param methodName      Represents the name of the test case that will be used to identify the test on Sauce.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    protected IOSDriver createDriver(String platformName, String platformVersion, String deviceName, String methodName)
    throws MalformedURLException
    {
        URL url = null;
        DesiredCapabilities caps = new DesiredCapabilities();

        // set desired capabilities to launch appropriate platformName on Sauce
        // For real device testing, connect to one URL using a certain set of credentials...
        url = new URL("https://" + userName + ":" + accessKey + "@ondemand.us-west-1.saucelabs.com/wd/hub");
        if (realDeviceTesting == true)
        {
            // iOS real devices use *.ipa files...
            //caps.setCapability("app", "sauce-storage:LoanCalc.ipa");
            caps.setCapability("app", "storage:filename=LoanCalc.ipa");
        }
        else
        {
            // iOS Simulators use *.app.zip files...
            caps.setCapability("app", "storage:a5f0f35e-f6c4-4a78-8428-81b2c9354a6b");
        }

        caps.setCapability("automationName", "XCUITest");
       // caps.setCapability("tunnelIdentifier", "demotunnel");
        caps.setCapability("platformName", platformName);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("name", methodName);
        caps.setCapability("build", "buildmobile");
      

        // Launch the remote platformName and set it as the current thread

        long start = System.currentTimeMillis();
        IOSDriver driver = new IOSDriver(url, caps);
        long stop = System.currentTimeMillis();
        info(driver, "Device allocation took %.2f secs\n", (stop - start) / 1000f);

        driverThreadLocal.set(driver);

        return driverThreadLocal.get();
    }

    /**
     * Method that gets invoked after test.
     * Sets the job status (PASS or FAIL) and closes the browser.
     */
    @AfterMethod
    public void tearDown(ITestResult result)
    throws Exception
    {
        AppiumDriver driver = driverThreadLocal.get();

        if (driver != null)
        {
            boolean success = result.isSuccess();

            reportSauceLabsMobileResult(driver, success);
            driver.quit();
        }
    }

    /**
     * Logs the given line in the job’s commands list. No spaces can be between sauce: and context.
     */
    public static void info(RemoteWebDriver driver, String format, Object... args)
    {
        System.out.printf(format, args);
        return; // Not currently implemented
//        String msg = String.format(format, args);
//        ((JavascriptExecutor) driver).executeScript("sauce:context=" + msg);
    }

    public static void reportSauceLabsMobileResult(RemoteWebDriver driver, boolean status)
    {
        ((JavascriptExecutor) driver).executeScript("sauce:job-result=" + (status ? "passed" : "false"));
    }
}
