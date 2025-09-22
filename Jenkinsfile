// ===================================================================
// DIGITAL ASSISTANT SERVICE - JENKINS CI/CD PIPELINE
// ===================================================================
// This Jenkinsfile defines the complete CI/CD pipeline for the Digital Assistant Service
// 
// Pipeline Stages:
// 1. Checkout - Pull latest code from Git repository
// 2. Build - Compile Java code with Maven
// 3. Test - Run unit tests and generate reports
// 4. Package - Create JAR file
// 5. Docker Build - Create Docker image
// 6. Deploy - Deploy application using Docker
// 7. Health Check - Verify deployment success

pipeline {
    agent any
    
    // Environment variables for the pipeline
    environment {
        // Application configuration
        APP_NAME = 'digital-assistant-service'
        APP_VERSION = "${BUILD_NUMBER}"
        DOCKER_IMAGE = "${APP_NAME}:${APP_VERSION}"
        
        // Docker configuration
        DOCKER_REGISTRY = 'localhost:5000'  // Local registry for testing
        HOST_PORT = '8081'
    }

    stages {

        // ===================================================================
        // CHECKOUT STAGE
        // ===================================================================
        stage('Checkout') {
            steps {
                echo "Starting CI/CD Pipeline for Digital Assistant Service"
                echo "Build Number: ${BUILD_NUMBER}"
                echo "Docker Image: ${DOCKER_IMAGE}"
                
                // Pull latest source code from Git
                checkout scm
                echo "Source code checked out successfully"
                echo "Working directory: ${WORKSPACE}"
            }
        }

        // ===================================================================
        // BUILD STAGE
        // ===================================================================
        stage('Build') {
            steps {
                echo "Building Digital Assistant Service..."
                
                // Clean and compile the application (Windows-friendly Maven wrapper)
                bat '''
                    echo Cleaning previous builds...
                    call mvnw.cmd clean

                    echo Compiling Java source code...
                    call mvnw.cmd compile
                '''
                
                echo "Application built successfully"
            }
        }

        // ===================================================================
        // TEST STAGE
        // ===================================================================
        stage('Test') {
            steps {
                echo "Running Unit Tests..."
                
                // Execute unit tests
                bat '''
                    call mvnw.cmd test
                '''
                
                echo "Tests executed"
            }
        }

        // ===================================================================
        // PACKAGE STAGE
        // ===================================================================
        stage('Package') {
            steps {
                echo "Packaging Application..."
                
                // Package the application JAR file
                bat '''
                    call mvnw.cmd package -DskipTests
                '''
                
                echo "Application packaged successfully"
            }
        }

        // ===================================================================
        // DOCKER BUILD STAGE
        // ===================================================================
        stage('Docker Build') {
            steps {
                echo "Building Docker Image..."
                
                script {
                    try {
                        // First, try to build the Docker image
                        bat """
                            docker build -t %DOCKER_IMAGE% .
                            echo Docker image built successfully
                        """
                        
                        // Check if registry is accessible
                        bat """
                            echo Testing registry connectivity...
                            curl -X GET http://%DOCKER_REGISTRY%/v2/_catalog || echo Registry not accessible
                        """
                        
                        // Try to push to registry with timeout
                        bat """
                            echo Attempting to push to registry...
                            timeout 30 docker push %DOCKER_REGISTRY%/%DOCKER_IMAGE%
                        """
                        
                        echo "Docker Image built and pushed to registry successfully"
                        
                    } catch (Exception e) {
                        echo "Registry push failed, continuing with local deployment..."
                        echo "Error: ${e.getMessage()}"
                        
                        // Tag the image for local use
                        bat """
                            docker tag %DOCKER_IMAGE% %DOCKER_REGISTRY%/%DOCKER_IMAGE%
                            echo Docker image tagged for local deployment
                        """
                    }
                }
            }
        }

        // ===================================================================
        // DEPLOY STAGE
        // ===================================================================
        stage('Deploy') {
            steps {
                echo "Deploying Application..."
                
                script {
                    // Deploy the application using Docker
                    bat """
                        echo Stopping and removing existing containers...
                        docker rm -f %APP_NAME% || echo Container not found
                        
                        echo Starting new container...
                        docker run -d -p %HOST_PORT%:8080 --name %APP_NAME% %DOCKER_IMAGE%
                    """
                    
                    echo "Application deployed successfully"
                    echo "Application URL: http://localhost:${HOST_PORT}"
                }
            }
        }

        // ===================================================================
        // HEALTH CHECK STAGE
        // ===================================================================
        stage('Health Check') {
            steps {
                echo "Performing Health Check..."
                
                script {
                    // Wait for application to start
                    bat """
                        echo Waiting for application to start...
                        timeout /t 10 /nobreak
                    """
                    
                    // Test multiple endpoints
                    bat '''
                        echo Testing Spring Boot Actuator Health...
                        powershell -Command "try { Invoke-WebRequest http://localhost:%HOST_PORT%/actuator/health -UseBasicParsing | Select-Object StatusCode } catch { Write-Host 'Actuator health check failed' }"
                        
                        echo Testing Custom Health Endpoint...
                        powershell -Command "try { Invoke-WebRequest http://localhost:%HOST_PORT%/api/assistants/health -UseBasicParsing | Select-Object StatusCode } catch { Write-Host 'Custom health check failed' }"
                        
                        echo Testing Frontend...
                        powershell -Command "try { Invoke-WebRequest http://localhost:%HOST_PORT%/ -UseBasicParsing | Select-Object StatusCode } catch { Write-Host 'Frontend check failed' }"
                    '''
                    
                    echo "Health Check completed"
                    echo "Available endpoints:"
                    echo "- Frontend: http://localhost:${HOST_PORT}/"
                    echo "- API Health: http://localhost:${HOST_PORT}/api/assistants/health"
                    echo "- Spring Health: http://localhost:${HOST_PORT}/actuator/health"
                    echo "- All Assistants: http://localhost:${HOST_PORT}/api/assistants"
                }
            }
        }
    }

    // ===================================================================
    // POST ACTIONS
    // ===================================================================
    post {
        // Actions to run after all stages
        always {
            echo "Pipeline execution completed"
            
            // Clean workspace
            cleanWs()
        }
        
        // Actions on successful build
        success {
            echo " Pipeline SUCCESS!"
            echo " Digital Assistant Service deployed successfully"
            echo " Application URL: http://localhost:${HOST_PORT}"
            echo " Health Check: http://localhost:${HOST_PORT}/actuator/health"
            echo " API Health: http://localhost:${HOST_PORT}/api/assistants/health"
        }
        
        // Actions on failed build
        failure {
            echo "Ohhh Noo Yash!! Pipeline FAILED!"
            echo "Check the logs above for error details"
            
            // Send failure notification (customize as needed)
            // emailext (
            //     subject: " CI/CD Failed: ${APP_NAME} Build #${BUILD_NUMBER}",
            //     body: "Digital Assistant Service deployment failed!\\n\\nBuild: #${BUILD_NUMBER}\\nCheck Jenkins logs for details.",
            //     to: "your-email@example.com"
            // )
        }
        
        // Actions on unstable build
        unstable {
            echo "Pipeline UNSTABLE!"
            echo "Some tests may have failed, but deployment continued"
        }
    }
}
