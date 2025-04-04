package apaosha.DiceAutomation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DiceLoginWithEmail {
    // SLF4J logger for console output
    private static final Logger logger = LoggerFactory.getLogger(DiceLoginWithEmail.class);

    // Default test credentials for login
    private static final String DEFAULT_EMAIL = "soyoxok649@avulos.com";
    private static final String DEFAULT_PASSWORD = "!2q3wa4esZ";
    // File to store credentials (first line: email, second line: password)
    private static final String CREDENTIALS_FILE = "credentials.txt";

    public static void main(String[] args) {
        logger.info("Starting Dice login automation.");
        Scanner scanner = new Scanner(System.in);
        String email;
        String password;
        String jobTitle;
        String location;

        // --- Prompt for login credentials ---
        File credFile = new File(CREDENTIALS_FILE);
        if (credFile.exists()) {
            logger.info("Stored credentials file found.");
            System.out.print("Stored credentials found. Use stored credentials? (Y/N): ");
            String useStored = scanner.nextLine();
            if (useStored.equalsIgnoreCase("Y")) {
                try (BufferedReader br = new BufferedReader(new FileReader(credFile))) {
                    email = br.readLine();
                    password = br.readLine();
                    logger.info("Using stored credentials for email: {}", email);
                } catch (IOException e) {
                    logger.error("Error reading credentials file. Using default credentials.", e);
                    email = DEFAULT_EMAIL;
                    password = DEFAULT_PASSWORD;
                }
            } else {
                logger.info("User opted not to use stored credentials.");
                System.out.print("Use test credentials (" + DEFAULT_EMAIL + ", " + DEFAULT_PASSWORD + ")? (Y/N): ");
                String useTest = scanner.nextLine();
                if (useTest.equalsIgnoreCase("Y")) {
                    email = DEFAULT_EMAIL;
                    password = DEFAULT_PASSWORD;
                    saveCredentials(email, password);
                    logger.info("Using test credentials.");
                } else {
                    System.out.print("Enter your email: ");
                    email = scanner.nextLine();
                    System.out.print("Enter your password: ");
                    password = scanner.nextLine();
                    saveCredentials(email, password);
                    logger.info("Using user-provided credentials for email: {}", email);
                }
            }
        } else {
            logger.info("No stored credentials found.");
            System.out.print("No stored credentials found. Use test credentials (" + DEFAULT_EMAIL + ", " + DEFAULT_PASSWORD + ")? (Y/N): ");
            String useTest = scanner.nextLine();
            if (useTest.equalsIgnoreCase("Y")) {
                email = DEFAULT_EMAIL;
                password = DEFAULT_PASSWORD;
                saveCredentials(email, password);
                logger.info("Using test credentials.");
            } else {
                System.out.print("Enter your email: ");
                email = scanner.nextLine();
                System.out.print("Enter your password: ");
                password = scanner.nextLine();
                saveCredentials(email, password);
                logger.info("Using user-provided credentials for email: {}", email);
            }
        }

        // --- Prompt for job search details ---
        System.out.print("Enter job title/skill (default 'QA tester'): ");
        jobTitle = scanner.nextLine().trim();
        if (jobTitle.isEmpty()) {
            jobTitle = "QA tester";
        }
        System.out.print("Enter location (default 'United States'): ");
        location = scanner.nextLine().trim();
        if (location.isEmpty()) {
            location = "United States";
        }
        logger.info("Job title/skill set to: {}", jobTitle);
        logger.info("Location set to: {}", location);

        // --- Prompt for Posted Date filter ---
        System.out.println("Select Posted Date filter (default is 0 - Any Date):");
        System.out.println("[0] Any Date");
        System.out.println("[1] Today");
        System.out.println("[2] Last 3 Days");
        System.out.println("[3] Last 7 Days");
        String postedDateChoice = scanner.nextLine().trim();
        int postedDateOption = 0;
        try {
            if (!postedDateChoice.isEmpty()) {
                postedDateOption = Integer.parseInt(postedDateChoice);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid input for Posted Date filter, defaulting to Any Date.");
        }
        logger.info("Posted Date filter selected: {}", postedDateOption);

        // --- Prompt for Employment Type filter using numbers ---
        System.out.println("Select Employment Type filter(s) by numbers (comma separated). Options:");
        System.out.println("[1] FULLTIME");
        System.out.println("[2] PARTTIME");
        System.out.println("[3] CONTRACTS");
        System.out.println("[4] THIRD_PARTY");
        System.out.println("Press ENTER to select all (default).");
        String empTypeInput = scanner.nextLine().trim();

        // --- Prompt for Employer Type filter using numbers ---
        System.out.println("Select Employer Type filter(s) by numbers (comma separated). Options:");
        System.out.println("[1] Direct Hire");
        System.out.println("[2] Recruiter");
        System.out.println("[3] Other");
        System.out.println("Press ENTER to select all (default).");
        String employerTypeInput = scanner.nextLine().trim();

        // --- Prompt for Easy Apply filter (default Y) ---
        System.out.print("Filter by Easy Apply? (Y/N, default Y): ");
        String easyApplyChoice = scanner.nextLine().trim();

        // --- Prompt for Work Authorization filter (default N) ---
        System.out.print("Filter by Work Authorization? (Y/N, default N): ");
        String workAuthChoice = scanner.nextLine().trim();

        // --- Setup WebDriver and login ---
        System.setProperty("webdriver.chrome.silentOutput", "true");
        logger.info("Setting up ChromeDriver.");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        logger.info("Maximizing the browser window.");
        driver.manage().window().maximize();

        logger.info("Navigating to https://www.dice.com/dashboard/login");
        driver.get("https://www.dice.com/dashboard/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            // --- Login Process ---
            logger.info("Waiting for the login email input field to become visible.");
            By loginEmailXPath = By.xpath("//input[@placeholder='Please enter your email' and @type='email']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginEmailXPath));
            logger.info("Entering email: {}", email);
            driver.findElement(loginEmailXPath).sendKeys(email);

            logger.info("Waiting for the Continue button to be clickable.");
            By continueButtonXPath = By.xpath("//button[@data-testid='sign-in-button']");
            wait.until(ExpectedConditions.elementToBeClickable(continueButtonXPath));
            logger.info("Clicking the Continue button.");
            driver.findElement(continueButtonXPath).click();

            logger.info("Waiting for the password input field to become visible.");
            By passwordFieldXPath = By.xpath("//input[@placeholder='Enter Password' and @type='password']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordFieldXPath));
            logger.info("Entering password.");
            driver.findElement(passwordFieldXPath).sendKeys(password);

            logger.info("Waiting for the Sign In button to be clickable.");
            By signInButtonXPath = By.xpath("//button[@data-testid='submit-password']");
            wait.until(ExpectedConditions.elementToBeClickable(signInButtonXPath));
            logger.info("Clicking the Sign In button.");
            driver.findElement(signInButtonXPath).click();
        } catch (Exception e) {
            logger.error("Error during login interaction: {}", e.getMessage(), e);
        }

        logger.info("Login submitted. Waiting for job search page to load.");
        try {
            Thread.sleep(5000); // Adjust as needed for the page to load
        } catch (InterruptedException ie) {
            logger.error("Interrupted while waiting for job search page.", ie);
        }

        // --- Fill in the job search fields ---
        try {
            logger.info("Waiting for the job title input field to become visible.");
            By jobTitleXPath = By.xpath("//input[@aria-label='Job title, skill, company, keyword' and @placeholder='Job title, skill, company, keyword']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(jobTitleXPath));
            logger.info("Entering job title/skill: {}", jobTitle);
            driver.findElement(jobTitleXPath).clear();
            driver.findElement(jobTitleXPath).sendKeys(jobTitle);

            logger.info("Waiting for the location input field to become visible.");
            By locationXPath = By.xpath("//input[@aria-label='Location Field' and @placeholder='Location (ex. Denver, remote)']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(locationXPath));
            logger.info("Entering location: {}", location);
            WebElement locationField = driver.findElement(locationXPath);
            locationField.clear();
            locationField.sendKeys(location);
            // Trigger search by sending ENTER key
            logger.info("Sending ENTER key to location field to trigger search.");
            locationField.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            logger.error("Error during job search fields interaction: {}", e.getMessage(), e);
        }

        // --- Apply Filters ---
        try {
            // Posted Date filter (if not default)
            if (postedDateOption != 0) {
                String postedDateText = "";
                switch (postedDateOption) {
                    case 1: postedDateText = "Today"; break;
                    case 2: postedDateText = "Last 3 Days"; break;
                    case 3: postedDateText = "Last 7 Days"; break;
                    default: postedDateText = "Any Date"; break;
                }
                logger.info("Applying Posted Date filter: {}", postedDateText);
                By postedDateButtonXPath = By.xpath("//js-single-select-filter//button[contains(text(),'" + postedDateText + "')]");
                wait.until(ExpectedConditions.elementToBeClickable(postedDateButtonXPath));
                driver.findElement(postedDateButtonXPath).click();
            } else {
                logger.info("No Posted Date filter applied (default: Any Date).");
            }

            // Employment Type filter
            if (!empTypeInput.isEmpty()) {
                String[] empTypeNumbers = empTypeInput.split(",");
                // Map numbers to filter values
                List<String> empTypesToApply = new ArrayList<>();
                for (String num : empTypeNumbers) {
                    switch (num.trim()) {
                        case "1":
                            empTypesToApply.add("FULLTIME");
                            break;
                        case "2":
                            empTypesToApply.add("PARTTIME");
                            break;
                        case "3":
                            empTypesToApply.add("CONTRACTS");
                            break;
                        case "4":
                            empTypesToApply.add("THIRD_PARTY");
                            break;
                        default:
                            logger.warn("Invalid Employment Type option: {}", num);
                    }
                }
                for (String type : empTypesToApply) {
                    logger.info("Applying Employment Type filter: {}", type);
                    By empTypeButtonXPath = By.xpath("//li[@data-cy-value='" + type + "']//button");
                    wait.until(ExpectedConditions.elementToBeClickable(empTypeButtonXPath));
                    driver.findElement(empTypeButtonXPath).click();
                }
            } else {
                logger.info("No Employment Type filter applied (default: all).");
            }

            // Employer Type filter
            if (!employerTypeInput.isEmpty()) {
                String[] employerTypeNumbers = employerTypeInput.split(",");
                // Map numbers to filter values
                List<String> employerTypesToApply = new ArrayList<>();
                for (String num : employerTypeNumbers) {
                    switch (num.trim()) {
                        case "1":
                            employerTypesToApply.add("Direct Hire");
                            break;
                        case "2":
                            employerTypesToApply.add("Recruiter");
                            break;
                        case "3":
                            employerTypesToApply.add("Other");
                            break;
                        default:
                            logger.warn("Invalid Employer Type option: {}", num);
                    }
                }
                for (String type : employerTypesToApply) {
                    logger.info("Applying Employer Type filter: {}", type);
                    // Note: The XPath here uses the text value exactly as displayed.
                    By employerTypeButtonXPath = By.xpath("//li[@data-cy-value='" + type + "']//button");
                    wait.until(ExpectedConditions.elementToBeClickable(employerTypeButtonXPath));
                    driver.findElement(employerTypeButtonXPath).click();
                }
            } else {
                logger.info("No Employer Type filter applied (default: all).");
            }

            // Easy Apply filter (default Y)
            if (easyApplyChoice.equalsIgnoreCase("Y") || easyApplyChoice.isEmpty()) {
                logger.info("Applying Easy Apply filter.");
                By easyApplyButtonXPath = By.xpath("//button[@aria-label='Filter Search Results by Easy Apply']");
                wait.until(ExpectedConditions.elementToBeClickable(easyApplyButtonXPath));
                driver.findElement(easyApplyButtonXPath).click();
            } else {
                logger.info("Easy Apply filter not applied.");
            }

            // Work Authorization filter
            if (workAuthChoice.equalsIgnoreCase("Y")) {
                logger.info("Applying Work Authorization filter.");
                By workAuthButtonXPath = By.xpath("//button[@aria-label='Filter Search Results by Work Authorization']");
                wait.until(ExpectedConditions.elementToBeClickable(workAuthButtonXPath));
                driver.findElement(workAuthButtonXPath).click();
            } else {
                logger.info("Work Authorization filter not applied.");
            }
        } catch (Exception e) {
            logger.error("Error during filter interaction: {}", e.getMessage(), e);
        }

        // --- Extract and log the job count ---
        try {
            logger.info("Waiting for the job count element to be visible.");
            By jobCountXPath = By.xpath("//span[@id='totalJobCount' and @data-cy='search-count']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(jobCountXPath));
            String jobsCount = driver.findElement(jobCountXPath).getText();
            logger.info("Job count for '{}' in '{}': {}", jobTitle, location, jobsCount);
        } catch (Exception e) {
            logger.error("Error retrieving job count: {}", e.getMessage(), e);
        }

        logger.info("Job search submitted. Press ENTER to exit and close the browser.");
        scanner.nextLine();
        logger.info("Closing the browser.");
        driver.quit();
        logger.info("Dice login and job search automation finished.");
    }

    // Save credentials (email and password) to a file for future runs.
    private static void saveCredentials(String email, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CREDENTIALS_FILE))) {
            bw.write(email);
            bw.newLine();
            bw.write(password);
        } catch (IOException e) {
            logger.error("Error saving credentials: {}", e.getMessage(), e);
        }
    }
}
