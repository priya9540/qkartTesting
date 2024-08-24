package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class QKART_Tests {

        static RemoteWebDriver driver;
        public static String lastGeneratedUserName;

        @BeforeSuite(alwaysRun = true)
        public static void createDriver() throws MalformedURLException {
                // Launch Browser using Zalenium
                final DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName(BrowserType.CHROME);
                driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
                System.out.println("createDriver()");
        }

        /*
         * Testcase01: Verify a new user can successfully register
         */

        @Test(groups = {"Sanity_test"}, priority = 1,
                        description = "Verify registration happens correctly.")
        @Parameters({"username", "password"})
        public void TestCase01(String username, String password) throws InterruptedException {
                Boolean status;
                logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
                // takeScreenshot(driver, "StartTestCase", "TestCase1");

                // Visit the Registration page and register a new user
                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser(username, password, true);
                // status = registration.registerUser("testUser", "abc@123", true);
                Assert.assertTrue(status, "Failed to register new user");

                // Save the last generated username
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Visit the login page and login with the previuosly registered user
                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, password);
                logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
                Assert.assertTrue(status, "Failed to login with registered user");

                // Visit the home page and log out the logged in user
                Home home = new Home(driver);
                status = home.PerformLogout();

                logStatus("End TestCase", "Test Case 1: Verify user Registration : ",
                                status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "EndTestCase", "TestCase1");
        }


        @Test(groups = {"Sanity_test"}, priority = 2,
                        description = "Verify re-registering an already registered user fails")
        public void TestCase02() throws InterruptedException {
                Boolean status;
                logStatus("Start Testcase",
                                "Test Case 2: Verify User Registration with an existing username ",
                                "DONE");

                // Visit the Registration page and register a new user
                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);
                logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
                Assert.assertTrue(status, "Failed to register new user");
                // if (!status) {
                // logStatus("End TestCase", "Test Case 2: Verify user Registration : ",
                // status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "EndTestCase", "TestCase2");

                // }

                // Save the last generated username
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Visit the Registration page and try to register using the previously
                // registered user's credentials
                registration.navigateToRegisterPage();
                status = registration.registerUser(lastGeneratedUserName, "abc@123", false);
                Assert.assertFalse(status, "Re-registeration successfull");
                // If status is true, then registration succeeded, else registration has
                // failed. In this case registration failure means Success
                logStatus("End TestCase", "Test Case 2: Verify user Registration : ",
                                status ? "FAIL" : "PASS");
                // takeScreenshot(driver, "EndTestCase", "TestCase2");

        }

        @Test(groups = {"Sanity_test"}, priority = 3,
                        description = "Verify the functionality of search text box")
        public void TestCase03() throws InterruptedException {
                logStatus("TestCase 3", "Start test case : Verify functionality of search box ",
                                "DONE");
                boolean status=false;

                // Visit the home page
                Home homePage = new Home(driver);
                homePage.navigateToHome();

                // Search for the "yonex" product
                status = homePage.searchForProduct("YONEX");
                // if (!status) {
                // logStatus("TestCase 3", "Test Case Failure. Unable to search for given product",
                // "FAIL");
                // // takeScreenshot(driver, "EndTestCase", "TestCase3");
                // }
                Assert.assertTrue(status, "Test Case Failure. Unable to search for given product");
                // Fetch the search results
                List<WebElement> searchResults = homePage.getSearchResults();

                // Verify the search results are available
                if (searchResults.size() == 0) {
                        // logStatus("TestCase 3",
                        // "Test Case Failure. There were no results for the given search string",
                        // "FAIL");
                        // takeScreenshot(driver, "EndTestCase", "TestCase3");
                        status=false;
                        Assert.assertTrue(status,
                                        "Test Case Failure. There were no results for the given search string");
                }

                for (WebElement webElement : searchResults) {
                        // Create a SearchResult object from the parent element
                        SearchResult resultelement = new SearchResult(webElement);

                        // Verify that all results contain the searched text
                        String elementText = resultelement.getTitleofResult();
                        if (!elementText.toUpperCase().contains("YONEX")) {
                                // logStatus("TestCase 3",
                                // "Test Case Failure. Test Results contains un-expected values: "
                                // + elementText,
                                // "FAIL");
                                // takeScreenshot(driver, "EndTestCase", "TestCase3");
                                status = false;
                                
                        }
                        Assert.assertTrue(status,
                                                "Test Case Failure. Test Results contains un-expected values: ");
                }

                logStatus("Step Success", "Successfully validated the search results ", "PASS");

                // Search for product
                status = homePage.searchForProduct("Gesundheit");
                // if (!status) {
                // logStatus("TestCase 3", "Test Case Failure. Invalid keyword returned results",
                // "FAIL");
                // // takeScreenshot(driver, "EndTestCase", "TestCase03");
                // }
                Assert.assertFalse(status, "Invalid keyword returned results");
              //  System.out.println(status);
                searchResults = homePage.getSearchResults();
                if (searchResults.size() == 0) {
                        if (homePage.isNoResultFound()) {
                                logStatus("Step Success",
                                                "Successfully validated that no products found message is displayed",
                                                "PASS");
                        }
                        logStatus("TestCase 3",
                                        "Test Case PASS. Verified that no search results were found for the given text",
                                        "PASS");
                } else {
                        // logStatus("TestCase 3",
                        // "Test Case Fail. Expected: no results , actual: Results were available",
                        // "FAIL");
                        // takeScreenshot(driver, "EndTestCase", "TestCase03");
                        status = false;
                        Assert.assertTrue(status,
                                        "Expected: no results , actual: Results were available");

                }
        }

        @Test(groups = {"Regression_Test"}, priority = 4,
                        description = "Verify the existence of size chart for certain items and validate contents of size chart")
        public void TestCase04() throws InterruptedException {
                logStatus("TestCase 4", "Start test case : Verify the presence of size Chart",
                                "DONE");
                boolean status = false;

                // Visit home page
                Home homePage = new Home(driver);
                homePage.navigateToHome();

                // Search for product and get card content element of search results
                status = homePage.searchForProduct("Running Shoes");
                Assert.assertTrue(status, "Failure to search for running Shoes");
                List<WebElement> searchResults = homePage.getSearchResults();

                // Create expected values
                List<String> expectedTableHeaders =
                                Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
                List<List<String>> expectedTableBody =
                                Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                                                Arrays.asList("7", "7", "41", "10.2"),
                                                Arrays.asList("8", "8", "42", "10.6"),
                                                Arrays.asList("9", "9", "43", "11"),
                                                Arrays.asList("10", "10", "44", "11.5"),
                                                Arrays.asList("11", "11", "45", "12.2"),
                                                Arrays.asList("12", "12", "46", "12.6"));

                // Verify size chart presence and content matching for each search result
                for (WebElement webElement : searchResults) {
                        SearchResult result = new SearchResult(webElement);

                        // Verify if the size chart exists for the search result
                        if (result.verifySizeChartExists()) {
                                logStatus("Step Success",
                                                "Successfully validated presence of Size Chart Link",
                                                "PASS");

                                // Verify if size dropdown exists
                                status = result.verifyExistenceofSizeDropdown(driver);
                                logStatus("Step Success", "Validated presence of drop down",
                                                status ? "PASS" : "FAIL");

                                // Open the size chart
                                if (result.openSizechart()) {
                                        // Verify if the size chart contents matches the expected
                                        // values
                                        if (result.validateSizeChartContents(expectedTableHeaders,
                                                        expectedTableBody, driver)) {
                                                logStatus("Step Success",
                                                                "Successfully validated contents of Size Chart Link",
                                                                "PASS");
                                        } else {
                                                logStatus("Step Failure",
                                                                "Failure while validating contents of Size Chart Link",
                                                                "FAIL");
                                                status = false;

                                        }

                                        // Close the size chart modal
                                        status = result.closeSizeChart(driver);
                                        Assert.assertTrue(status, "size chart did not close");

                                } else {
                                        logStatus("TestCase 4",
                                                        "Test Case Fail. Failure to open Size Chart",
                                                        "FAIL");
                                        status = false;

                                }

                        } else {
                                logStatus("TestCase 4",
                                                "Test Case Fail. Size Chart Link does not exist",
                                                "FAIL");
                                status = false;
                        }
                }
                Assert.assertTrue(status, "Failure to validate size chart details");
                logStatus("TestCase 4", "End Test Case: Validated Size Chart Details",
                                status ? "PASS" : "FAIL");

        }

        /*
         * Verify the complete flow of checking out and placing order for products is working
         * correctly
         */
        @Test(groups = {"Sanity_test"}, priority = 5,
                        description = "Verify that a new user can add multiple products in to the cart and Checkout")
        @Parameters({"YONEX", "Tan","Address"})
        public void TestCase05(String YONEX, String Tan,String Address) throws InterruptedException {
                Boolean status;
                logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products",
                                "DONE");

                // Go to the Register page
                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
        
                // Register a new user
                status = registration.registerUser("testUser", "abc@123", true);
                // }
                Assert.assertTrue(status, "user perform registeration failed");

                // Save the username of the newly registered user
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Go to the login page
               

                // Go to the login page
                Login login = new Login(driver);
                login.navigateToLoginPage();
        
                // Login with the newly registered user's credentials
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                Assert.assertTrue(status, "user perform login failed");

                // Go to the home page
                Home homePage = new Home(driver);
                homePage.navigateToHome();

                // Find required products by searching and add them to the user's cart
                status = homePage.searchForProduct(YONEX);
                homePage.addProductToCart(YONEX);
                status = homePage.searchForProduct(Tan);
                Assert.assertTrue(status, "could not search for product");
                homePage.addProductToCart(Tan);

                // Click on the checkout button
                homePage.clickCheckout();

                // Add a new address on the Checkout page and select it
                Checkout checkoutPage = new Checkout(driver);
                checkoutPage.addNewAddress(Address);
                checkoutPage.selectAddress(Address);

                // Place the order
                checkoutPage.placeOrder();

                WebDriverWait wait = new WebDriverWait(driver, 30);
                wait.until(ExpectedConditions
                                .urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

                // Check if placing order redirected to the Thansk page
                status = driver.getCurrentUrl().endsWith("/thanks");
                Assert.assertTrue(status, "placeOrder failed");

                // Go to the home page
                homePage.navigateToHome();

                // Log out the user
                homePage.PerformLogout();

                logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ",
                                status ? "PASS" : "FAIL");

        }

        /*
         * Verify the quantity of items in cart can be updated
         * 
         */
        
        @Test(groups = {"Regression_Test"}, priority = 6,
                        description = "Verify that the contents of the cart can be edited")
        @Parameters({"Xtend", "Yarine"})
        public void TestCase06(String Xtend, String Yarine) throws InterruptedException {
                Boolean status;
                logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
               
                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
        
                // Register a new user
                status = registration.registerUser("testUser", "abc@123", true);
                // }
                Assert.assertTrue(status, "user perform registeration failed");

                // Save the username of the newly registered user
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Go to the login page
               

                // Go to the login page
                Login login = new Login(driver);
                login.navigateToLoginPage();
        
                // Login with the newly registered user's credentials
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                Assert.assertTrue(status, "user perform login failed");

        
       
                Home homePage = new Home(driver);
                homePage.navigateToHome();
                status = homePage.searchForProduct(Xtend);
                Assert.assertTrue(status, "failed to search for product (Xtend)");
                homePage.addProductToCart(Xtend);

                status = homePage.searchForProduct(Yarine);
                Assert.assertTrue(status, "failed to search for product (yarine)");
                homePage.addProductToCart(Yarine);

                // update watch quantity to 2
                homePage.changeProductQuantityinCart(Xtend, 2);

                // update table lamp quantity to 0
                homePage.changeProductQuantityinCart(Yarine, 0);

                // update watch quantity again to 1
                homePage.changeProductQuantityinCart(Xtend, 1);

                homePage.clickCheckout();

                Checkout checkoutPage = new Checkout(driver);
                checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
                checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

                checkoutPage.placeOrder();

                try {
                        WebDriverWait wait = new WebDriverWait(driver, 30);
                        wait.until(ExpectedConditions.urlToBe(
                                        "https://crio-qkart-frontend-qa.vercel.app/thanks"));
                } catch (TimeoutException e) {
                        System.out.println("Error while placing order in: " + e.getMessage());


                }

                status = driver.getCurrentUrl().endsWith("/thanks");
                Assert.assertTrue(status, "place order failed");

                homePage.navigateToHome();
                homePage.PerformLogout();


                logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ",
                                status ? "PASS" : "FAIL");

        }

        @Test(groups = {"Sanity_test"}, priority = 7,
                        description = "Verify that insufficient balance error is thrown when the wallet balance is not enough")
        @Parameters({"Stylecon"})
        public void TestCase07(String Stylecon) throws InterruptedException {
                Boolean status;
                logStatus("Start TestCase",
                                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough",
                                "DONE");

                // Register registration = new Register(driver);
                // registration.navigateToRegisterPage();
                // status = registration.registerUser("testUser", "abc@123", true);
                // // if (!status) {
                // // logStatus("Step Failure", "User Perform Registration Failed", status ? "PASS" :
                // // "FAIL");
                // // logStatus("End TestCase",
                // // "Test Case 7: Verify that insufficient balance error is thrown when the wallet
                // // balance is
                // // not enough: ",
                // // status ? "PASS" : "FAIL");
                // // return false;
                // // }
                // Assert.assertTrue(status, "reistration failed");
                // lastGeneratedUserName = registration.lastGeneratedUsername;

                // Login login = new Login(driver);
                // login.navigateToLoginPage();
                // status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                // // if (!status) {
                // // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
                // // logStatus("End TestCase",
                // // "Test Case 7: Verify that insufficient balance error is thrown when the wallet
                // // balance is
                // // not enough: ",
                // // status ? "PASS" : "FAIL");
                // // return false;
                // // }
                // Assert.assertTrue(status, "User perform login failed");
                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
        
                // Register a new user
                status = registration.registerUser("testUser", "abc@123", true);
                // }
                Assert.assertTrue(status, "user perform registeration failed");

                // Save the username of the newly registered user
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Go to the login page
               

                // Go to the login page
                Login login = new Login(driver);
                login.navigateToLoginPage();
        
                // Login with the newly registered user's credentials
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                Thread.sleep(3000);

               Assert.assertTrue(status, "user perform login failed");
                Home homePage = new Home(driver);
                homePage.navigateToHome();
                status = homePage.searchForProduct(Stylecon);
                homePage.addProductToCart(Stylecon);

                homePage.changeProductQuantityinCart(Stylecon, 10);

                homePage.clickCheckout();

                Checkout checkoutPage = new Checkout(driver);
                checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
                checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

                checkoutPage.placeOrder();
                Thread.sleep(3000);

                status = checkoutPage.verifyInsufficientBalanceMessage();
                Assert.assertTrue(status, "InsufficientBalanceMessage did not display");

                logStatus("End TestCase",
                                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
                                status ? "PASS" : "FAIL");


        }

        @Test(groups = {"Regression_Test"}, priority = 8,
                        description = "Verify that a product added to a cart is available when a new tab is added")
        public void TestCase08() throws InterruptedException {
                Boolean status = false;

                logStatus("Start TestCase",
                                "Test Case 8: Verify that product added to cart is available when a new tab is opened",
                                "DONE");
                // takeScreenshot(driver, "StartTestCase", "TestCase09");

                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);
                // if (!status) {
                // logStatus("TestCase 8",
                // "Test Case Failure. Verify that product added to cart is available when a new tab
                // is
                // opened",
                // "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase09");
                // }
                Assert.assertTrue(status, "registeration failed");
                lastGeneratedUserName = registration.lastGeneratedUsername;

                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                // if (!status) {
                // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase9");
                // logStatus("End TestCase",
                // "Test Case 8: Verify that product added to cart is available when a new tab is
                // opened",
                // status ? "PASS" : "FAIL");
                // }
                Assert.assertTrue(status, "login failedt");
                Home homePage = new Home(driver);
                homePage.navigateToHome();

                status = homePage.searchForProduct("YONEX");
                Assert.assertTrue(status, "Failed to searchForProduct YONEX");
                status = homePage.addProductToCart("YONEX Smash Badminton Racquet");
                Assert.assertTrue(status, "Failed to addProductToCart YONEX");

                String currentURL = driver.getCurrentUrl();

                driver.findElement(By.linkText("Privacy policy")).click();
                Set<String> handles = driver.getWindowHandles();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

                driver.get(currentURL);
                Thread.sleep(2000);

                List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
                status = homePage.verifyCartContents(expectedResult);
                Assert.assertTrue(status, "failed to verify cart contents ");

                driver.close();

                driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

                logStatus("End TestCase",
                                "Test Case 8: Verify that product added to cart is available when a new tab is opened",
                                status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "EndTestCase", "TestCase08");


        }

        @Test(groups = {"Regression_Test"}, priority = 9,
                        description = "Verify that privacy policy and about us links are working fine")
        public void TestCase09() throws InterruptedException {
                Boolean status = false;

                logStatus("Start TestCase",
                                "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
                                "DONE");
                // takeScreenshot(driver, "StartTestCase", "TestCase09");

                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);
                // if (!status) {
                // logStatus("TestCase 09",
                // "Test Case Failure. Verify that the Privacy Policy, About Us are displayed
                // correctly ",
                // "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase09");
                // }
                Assert.assertTrue(status, "failed to do registeration ");
                lastGeneratedUserName = registration.lastGeneratedUsername;

                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                // if (!status) {
                // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase09");
                // logStatus("End TestCase",
                // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                // status ? "PASS" : "FAIL");
                // }
                Assert.assertTrue(status, "failed to perform login ");

                Home homePage = new Home(driver);
                homePage.navigateToHome();
                SoftAssert softasserobj = new SoftAssert();
                String basePageURL = driver.getCurrentUrl();

                driver.findElement(By.linkText("Privacy policy")).click();
                status = driver.getCurrentUrl().equals(basePageURL);

                // if (!status) {
                // logStatus("Step Failure",
                // "Verifying parent page url didn't change on privacy policy link click failed",
                // status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase09");}
                softasserobj.assertTrue(status,
                                "failed to verify that parent page url didn't change ");
                logStatus("End TestCase",
                                "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                                status ? "PASS" : "FAIL");


                Set<String> handles = driver.getWindowHandles();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
                WebElement PrivacyPolicyHeading =
                                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
                status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
                // if (!status) {
                // logStatus("Step Failure",
                // "Verifying new tab opened has Privacy Policy page heading failed",
                // status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase9");}
                softasserobj.assertTrue(status,
                                "failed to verify that new tab opened has Privacy Policy page heading ");
                logStatus("End TestCase",
                                "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                                status ? "PASS" : "FAIL");


                driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
                driver.findElement(By.linkText("Terms of Service")).click();

                handles = driver.getWindowHandles();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
                WebElement TOSHeading =
                                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
                status = TOSHeading.getText().equals("Terms of Service");
                // if (!status) {
                // logStatus("Step Failure",
                // "Verifying new tab opened has Terms Of Service page heading failed",
                // status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase9");}
                softasserobj.assertTrue(status,
                                "failed to verify that new tab opened has Terms of service page heading ");
                logStatus("End TestCase",
                                "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                                status ? "PASS" : "FAIL");
                softasserobj.assertAll();

                driver.close();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

                logStatus("End TestCase",
                                "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                                "PASS");
                // takeScreenshot( "EndTestCase", "TestCase9");


        }

        @Test(groups = {"Regression_Test"}, priority = 10,
                        description = "Verify that the contact us dialog works fine")
        public void TestCase10() throws InterruptedException {
                Boolean status = false;
                logStatus("Start TestCase",
                                "Test Case 10: Verify that contact us option is working correctly ",
                                "DONE");
                // takeScreenshot(driver, "StartTestCase", "TestCase10");

                Home homePage = new Home(driver);
                homePage.navigateToHome();

                driver.findElement(By.xpath("//*[text()='Contact us']")).click();

                WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
                name.sendKeys("crio user");
                WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
                email.sendKeys("criouser@gmail.com");
                WebElement message =
                                driver.findElement(By.xpath("//input[@placeholder='Message']"));
                message.sendKeys("Testing the contact us page");

                WebElement contactUs = driver.findElement(By.xpath(
                                "/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

                contactUs.click();

                WebDriverWait wait = new WebDriverWait(driver, 30);
                status = wait.until(ExpectedConditions.invisibilityOf(contactUs));
                Assert.assertTrue(status,
                                "failed to verify that contact us option is working correctly ");

                logStatus("End TestCase",
                                "Test Case 10: Verify that contact us option is working correctly ",
                                "PASS");

                // takeScreenshot(driver, "EndTestCase", "TestCase10");


        }

        @Test(groups = {"Sanity_test"}, priority = 11,
                        description = "Ensure that the Advertisement Links on the QKART page are clickable")
        public void TestCase11() throws InterruptedException {
                Boolean status = false;
                logStatus("Start TestCase",
                                "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
                                "DONE");
                // takeScreenshot(driver, "StartTestCase", "TestCase11");

                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);
                // if (!status) {
                // logStatus("TestCase 11",
                // "Test Case Failure. Ensure that the links on the QKART advertisement are
                // clickable",
                // "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase11");
                // }
                Assert.assertTrue(status, "failed to perform registration ");

                lastGeneratedUserName = registration.lastGeneratedUsername;

                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                // if (!status) {
                // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase 11");}
                Assert.assertTrue(status, "failed to perform login ");

                logStatus("End TestCase",
                                "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
                                status ? "PASS" : "FAIL");


                Home homePage = new Home(driver);
                homePage.navigateToHome();

                status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
                Assert.assertTrue(status, "failed to search for the product ");

                status = homePage.addProductToCart("YONEX Smash Badminton Racquet");
                Assert.assertTrue(status, "failed to add product to the cart ");

                status = homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
                Assert.assertTrue(status, "failed to change product quantity in the cart ");

                status = homePage.clickCheckout();
                Assert.assertTrue(status, "failed to successfully checkout ");


                Checkout checkoutPage = new Checkout(driver);
                checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
                checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
                status = checkoutPage.placeOrder();
                Thread.sleep(3000);
                Assert.assertTrue(status, "failed to placeorder ");


                String currentURL = driver.getCurrentUrl();

                List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

                status = Advertisements.size() == 3;
                Assert.assertTrue(status, "failed to verify the total number of advertisements ");

                logStatus("Step ", "Verify that 3 Advertisements are available",
                                status ? "PASS" : "FAIL");

                WebElement Advertisement1 = driver.findElement(By.xpath("(//iframe)[1]"));
                driver.switchTo().frame(Advertisement1);
                driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
                WebDriverWait wait = new WebDriverWait(driver, 5);
                wait.until(ExpectedConditions.urlContains("/checkout"));
                status = driver.getCurrentUrl().contains("/checkout");
                Assert.assertTrue(status, "failed to change the  url to /checkout page");

                driver.switchTo().parentFrame();

                status = !driver.getCurrentUrl().equals(currentURL);
                logStatus("Step ", "Verify that Advertisement 1 is clickable ",
                                status ? "PASS" : "FAIL");

                driver.get(currentURL);
                Thread.sleep(3000);

                WebElement Advertisement2 = driver.findElement(By.xpath("(//iframe)[2]"));
                driver.switchTo().frame(Advertisement2);
                driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
                wait.until(ExpectedConditions.urlContains("/checkout"));
                driver.switchTo().parentFrame();

                status = !driver.getCurrentUrl().equals(currentURL);
                Assert.assertTrue(status, "url did not change ");

                logStatus("Step ", "Verify that Advertisement 2 is clickable ",
                                status ? "PASS" : "FAIL");

                logStatus("End TestCase",
                                "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
                                status ? "PASS" : "FAIL");

        }



        @AfterSuite
        public static void quitDriver() {
                System.out.println("quit()");
                driver.quit();
        }

        public static void logStatus(String type, String message, String status) {

                System.out.println(String.format("%s |  %s  |  %s | %s",
                                String.valueOf(java.time.LocalDateTime.now()), type, message,
                                status));
        }

        public static void takeScreenshot(String screenshotType, String description) {
                try {
                        File theDir = new File("/screenshots");
                        if (!theDir.exists()) {
                                theDir.mkdirs();
                        }
                        String timestamp = String.valueOf(java.time.LocalDateTime.now());
                        String fileName = String.format("screenshot_%s_%s_%s.png", timestamp,
                                        screenshotType, description);
                        TakesScreenshot scrShot = ((TakesScreenshot) driver);
                        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
                        File DestFile = new File("screenshots/" + fileName);
                        FileUtils.copyFile(SrcFile, DestFile);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
}

