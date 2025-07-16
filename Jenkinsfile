pipeline {
    agent any
    
    environment {
        // Browser configuration (can be overridden in Jenkins job parameters)
        BROWSER = 'chrome'
        HEADLESS = 'true'
        ENV = 'qa' // Default environment
        // Thread count for parallel test execution
        THREADS = '3'
        // Maven settings
        MAVEN_HOME = tool name: 'Maven', type: 'maven'
        // Report directories
        TEST_REPORTS = "${WORKSPACE}/target/surefire-reports"
        SCREENSHOTS_DIR = "${WORKSPACE}/screenshots"
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        skipStagesAfterUnstable()
    }
    
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser to run tests against'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run tests in headless mode'
        )
        choice(
            name: 'ENV',
            choices: ['dev', 'qa', 'staging', 'prod'],
            description: 'Environment to run tests against'
        )
        string(
            name: 'THREADS',
            defaultValue: '3',
            description: 'Number of parallel threads for test execution'
        )
    }
    
    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
                dir("${env.WORKSPACE}") {
                    deleteDir()
                }
            }
        }
        
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[url: 'https://github.com/Prince-Automation/SeleniumCICD.git']],
                    extensions: [[$class: 'LocalBranch']]
                ])
            }
        }
        
        stage('Set Up Environment') {
            steps {
                script {
                    // Create necessary directories
                    sh 'mkdir -p screenshots'
                    sh 'mkdir -p target/surefire-reports'
                    
                    // Display environment information
                    echo """
                    ========================================
                    BUILD ENVIRONMENT
                    ----------------------------------------
                    Job: ${env.JOB_NAME}
                    Build: ${env.BUILD_NUMBER}
                    Workspace: ${env.WORKSPACE}
                    Node: ${env.NODE_NAME}
                    ========================================
                    """
                    
                    // Display test configuration
                    echo """
                    ========================================
                    TEST CONFIGURATION
                    ----------------------------------------
                    Browser: ${params.BROWSER}
                    Headless: ${params.HEADLESS}
                    Environment: ${params.ENV}
                    Threads: ${params.THREADS}
                    ========================================
                    """
                }
            }
        }
        
        stage('Dependency Check') {
            steps {
                script {
                    // Check for dependency updates
                    sh '${MAVEN_HOME}/bin/mvn versions:display-dependency-updates'
                    
                    // Check for plugin updates
                    sh '${MAVEN_HOME}/bin/mvn versions:display-plugin-updates'
                }
            }
        }
        
        stage('Build') {
            steps {
                script {
                    // Build the project and run tests
                    sh """
                    ${MAVEN_HOME}/bin/mvn clean install \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -Denv=${params.ENV} \
                        -Dthreads=${params.THREADS} \
                        -Dmaven.test.failure.ignore=true \
                        -Dmaven.javadoc.skip=true \
                        -DskipITs \
                        -Dcheckstyle.skip \
                        -Dfindbugs.skip \
                        -Dpmd.skip \
                        -DskipTests
                    """
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    // Run tests with parallel execution
                    try {
                        sh """
                        ${MAVEN_HOME}/bin/mvn test \
                            -Dbrowser=${params.BROWSER} \
                            -Dheadless=${params.HEADLESS} \
                            -Denv=${params.ENV} \
                            -Dthreads=${params.THREADS} \
                            -Dmaven.test.failure.ignore=false \
                            -Dmaven.test.error.ignore=false \
                            -Dmaven.test.redirectTestOutputToFile=true \
                            -Dmaven.test.reportsDirectory=${TEST_REPORTS}
                        """
                    } catch (err) {
                        // Continue even if tests fail to generate reports
                        echo "Tests failed: ${err.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            
            post {
                always {
                    // Archive test results and screenshots
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: '**/screenshots/*.png', allowEmptyArchive: true
                    
                    // Publish HTML reports
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'emailable-report.html',
                        reportName: 'HTML Report'
                    ])
                    
                    // Publish JUnit test results
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                    
                    // Publish test results to Jenkins
                    step([
                        $class: 'JUnitResultArchiver',
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    ])
                }
            }
        }
        
        stage('Integration Tests') {
            when {
                expression { params.ENV in ['qa', 'staging'] }
            }
            steps {
                script {
                    // Run integration tests if needed
                    sh """
                    ${MAVEN_HOME}/bin/mvn verify \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -Denv=${params.ENV} \
                        -Dthreads=${params.THREADS} \
                        -DskipTests \
                        -Dmaven.failsafe.failIfNoSpecifiedTests=false
                    """
                }
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
            cleanWs()
            
            // Send notifications
            script {
                if (currentBuild.currentResult == 'SUCCESS') {
                    // Success notification
                    echo 'Build and tests completed successfully!'
                } else if (currentBuild.currentResult == 'UNSTABLE') {
                    // Unstable notification (test failures)
                    echo 'Tests completed with failures!'
                } else {
                    // Failed notification
                    echo 'Build or tests failed!'
                }
            }
            
            // Archive artifacts
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            
            // Clean up workspace
            cleanWs()
        }
    }
}
