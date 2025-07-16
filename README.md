# Selenium Test Automation Framework

This is a Selenium WebDriver test automation framework built with Java, TestNG, and Maven, designed for CI/CD integration with Jenkins.

## Features

- Cross-browser testing support (Chrome, Firefox, Edge)
- Parallel test execution
- Headless mode support
- ExtentReports for rich test reporting
- Screenshot capture on test failure
- Maven-based dependency management
- CI/CD ready with Jenkins pipeline
- Thread-safe WebDriver management

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- Chrome/Firefox/Edge browser installed
- Git
- Jenkins (for CI/CD pipeline)

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/Prince-Automation/SeleniumCICD.git
cd SeleniumCICD
```

### Build the Project
```bash
mvn clean install
```

### Run Tests

#### Run all tests
```bash
mvn test
```

#### Run tests with specific browser
```bash
mvn test -Dbrowser=chrome
```

#### Run tests in headless mode
```bash
mvn test -Dheadless=true
```

#### Run tests in parallel
```bash
mvn test -Dthreads=3
```

#### Run tests for specific environment
```bash
mvn test -Denv=qa
```

### Jenkins Pipeline

1. Create a new Pipeline job in Jenkins
2. Set the pipeline definition to "Pipeline script from SCM"
3. Configure your Git repository URL and credentials
4. Set the script path to `Jenkinsfile`
5. Save and run the pipeline

## Project Structure

```
├── src/
│   ├── main/
│   │   └── java/
│   │       └── rahulshettyacademy/
│   │           ├── pageobjects/
│   │           └── resources/
│   └── test/
│       └── java/
│           └── rahulshettyacademy/
│               ├── tests/
│               └── TestComponents/
├── testSuites/
│   └── testng.xml
├── screenshots/
├── target/
├── .gitignore
├── Jenkinsfile
├── pom.xml
└── README.md
```

## Configuration

### Browser Configuration
Edit the `pom.xml` file to set default browser and other configurations:

```xml
<properties>
    <browser>chrome</browser>
    <headless>false</headless>
    <env>qa</env>
    <threads>3</threads>
</properties>
```

### TestNG Suite
Test suites can be configured in the `testSuites/testng.xml` file.

## Reporting

Test reports are generated in the `target/surefire-reports` directory after test execution. The framework also generates ExtentReports in the `test-output` directory.

## Best Practices

1. Use Page Object Model (POM) design pattern
2. Keep test data separate from test logic
3. Use explicit waits instead of thread.sleep()
4. Add meaningful test descriptions and assertions
5. Handle test data cleanup in @AfterMethod
6. Use soft assertions when appropriate

## Troubleshooting

### Common Issues

1. **ChromeDriver version mismatch**:
   - Ensure Chrome browser and ChromeDriver versions are compatible
   - Update WebDriverManager to the latest version

2. **Element not found**:
   - Check if the element is in an iframe
   - Verify the element locator is correct
   - Add explicit wait for the element

3. **Tests failing in headless mode**:
   - Some websites behave differently in headless mode
   - Add appropriate window size and user-agent if needed

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
