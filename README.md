Airbnb Selenium Automation

1. Overview

   This project is a Selenium-based automation framework designed to test the search functionality of the Airbnb website. It integrates Cucumber for behavior-driven development (BDD) and TestNG for managing test execution.

2. Project Structure

   The project includes the following key components:
   - Maven POM file: Configures dependencies, build settings, and plugins.
   - Feature Files: Define test scenarios using Gherkin syntax to describe user interactions.
   - Step Definitions: Contain the logic for the steps defined in the feature files.
   - Test Runner: Executes the Cucumber tests with TestNG.
   - Utilities: Helper classes for handling web driver waits and locator management.
   - Locator Properties: A properties file that defines web element locators.
  
3. Technologies Used
   - Java: The main programming language.
   - Selenium: Automates browser interactions.
   - Cucumber: Supports BDD by connecting Gherkin feature files to step definitions.
   - TestNG: Manages test execution.
   - Lombok: Simplifies code by reducing boilerplate.
   - Log4j: Handles application logging.
   - ExtentReports: Generates detailed HTML reports for test execution.
   - WebDriverManager: Manages browser drivers automatically.
   - Spotless Plugin: Ensures consistent code formatting according to Google Java standards.
  
4. Installation

   To run this project, ensure the following are installed:
   - Java Development Kit (JDK) 21 or later.
     
     download the JDK from Oracle's website or use a package manager like brew on macOS or choco on Windows.
     
   - Apache Maven: Manages dependencies and builds the project.
     
     it can be downloaded from the Apache Maven website, or it can be installed using a package manager like brew (macOS) or choco (Windows).
     
   - IntelliJ IDEA or any Java-compatible IDE.
  
   4.1. Installation Steps
   1. Clone the repository:

         ```
         git clone <repository-url>
         cd Airbnb-selenium-automation
        
   2. Install project dependencies by running (open a terminal or command prompt in the root of the project directory):

        ```
        mvn clean install

  5. Code Formatting
       
     The project uses the Spotless Maven Plugin to ensure consistent code formatting. The configuration applies Google Java Format standards automatically during the build process.
     
     Spotless is also configured to apply formatting automatically as part of the build process, ensuring that all code adheres to the defined standards.

     You can run the formatter manually by executing:

        ```
        mvn spotless:apply
   
  7. Running the Tests
     1. From Command Line
    
        Run the tests from the command line using Maven. In the project directory, run:

        ```
        mvn test

     2. From IDE
    
        Alternatively, run the tests directly from your IDE:

        - Open the project in your IDE.
        - Navigate to the TestRunner.java class in the runners package.
        - Right-click the class and select Run.
       
     3. Test Output
    
        Test results will be displayed in the console, and an HTML report will be generated in target/cucumber-reports.html.
        
   8. Usage

      - The feature files in src/test/resources/features define the test scenarios.
        
      - Step definitions in stepDefinition package implement the test steps that interact with the Airbnb website.
       
      - You can add or modify scenarios in the feature files to extend the coverage as needed. 
        
       
