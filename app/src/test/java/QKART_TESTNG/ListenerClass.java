 package QKART_TESTNG;
 import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;;

public class ListenerClass implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test Started: " + result.getName());
        QKART_Tests.takeScreenshot("StartTest", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test Passed: " + result.getName());
        QKART_Tests.takeScreenshot("Success", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed: " + result.getName());
        QKART_Tests.takeScreenshot("EndTest", result.getName());
    }

   
}
