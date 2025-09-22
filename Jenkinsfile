pipeline {
    agent any
    
    environment {
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        IMAGE_NAME = "digital-assistant-service"
        DOCKER_IMAGE = "${IMAGE_NAME}:${BUILD_NUMBER}"
        HOST_PORT = "8080"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo "Starting CI/CD Pipeline for Digital Assistant Service"
                echo "Build Number: ${BUILD_NUMBER}"
                echo " Docker Image: ${DOCKER_IMAGE}"
                
                // Checkout code from Git
                checkout scm
                
                echo "Source code checked out successfully"
                echo "Working directory: ${pwd()}"
            }
        }
        
        stage('Build') {
            steps {
                echo "Building Digital Assistant Service..."
                
                // Clean and compile the application
                bat '''
                    echo Cleaning previous builds...
                    call mvnw.cmd clean
                    
                    echo Compiling Java source code...
                    call mvnw.cmd compile
                '''
                
                echo "Application built successfully"
            }
        }
        
        stage('Test') {
            steps {
                echo "Running tests..."
                
                // Run unit tests
                bat 'call mvnw.cmd test'
                
                echo "Tests executed successfully"
            }
        }
        
        stage('Package') {
            steps {
                echo "Packaging the application..."
                
                // Package the application without running tests
                bat 'call mvnw.cmd package -DskipTests'
                
                echo "Application packaged successfully"
            }
        }
        
        stage('Docker Build') {
            steps {
                echo "Building Docker image: ${DOCKER_IMAGE}"
                
                // Build Docker image
                bat "docker build -t ${DOCKER_IMAGE} ."
                
                echo "Docker image built successfully"
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Deploying Digital Assistant Service..."
                
                // Stop and remove old container if it exists
                bat '''
                    docker stop digital-assistant-service || echo "No container to stop"
                    docker rm digital-assistant-service || echo "No container to remove"
                '''
                
                // Run new container from the built image
                bat """
                    docker run -d --name digital-assistant-service -p ${HOST_PORT}:8080 ${DOCKER_IMAGE}
                """
                
                echo "Application deployed successfully"
            }
        }
        
        stage('Health Check') {
            steps {
                echo "Performing Health Check..."
                
                script {
                    // Wait for application to start (using ping instead of timeout for Windows compatibility)
                    bat """
                        echo Waiting for application to start...
                        ping -n 11 127.0.0.1 > nul
                    """
                    
                    // Test application endpoints
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
                }
            }
        }
    }
    
    post {
        always {
            echo "Pipeline execution completed"
            
            // Clean workspace after pipeline run
            cleanWs()
        }
    }
}
