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
        DOCKER_REGISTRY = 'localhost:5000'  // Local registry for demo
        CONTAINER_NAME = 'digital-assistant-app'
        
        // Application ports
        APP_PORT = '8080'
        HOST_PORT = '8080'
        
        // Maven configuration
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    // Pipeline stages
    stages {
        
        // ===================================================================
        // STAGE 1: CHECKOUT SOURCE CODE
        // ===================================================================
        stage('Checkout') {
            steps {
                echo "Starting CI/CD Pipeline for Digital Assistant Service"
                echo "Build Number: ${BUILD_NUMBER}"
                echo " Docker Image: ${DOCKER_IMAGE}"
                
                // Checkout code from Git repository
                checkout scm
            
                echo "Source code checked out successfully"
                echo "Working directory: ${WORKSPACE}"
            }
        }
        
        // ===================================================================
        // STAGE 2: BUILD APPLICATION
        // ===================================================================
        stage('Build') {
            steps {
                echo "Building Digital Assistant Service..."
                
                // Clean and compile the application
                sh '''
                    echo "Setting up Maven wrapper permissions..."
                    chmod +x mvnw
                    
                    echo "Cleaning previous builds..."
                    ./mvnw clean
                    
                    echo "Compiling Java source code..."
                    ./mvnw compile
                '''
                
                echo "Application built successfully"
            }
            
            // Archive build artifacts
            post {
                success {
                    echo "Build artifacts archived"
                }
            }
        }
        
        // ===================================================================
        // STAGE 3: RUN TESTS
        // ===================================================================
        stage('Test') {
            steps {
                echo "Running unit tests..."
                
                // Run tests and generate reports
                sh '''
                    echo "Executing unit tests..."
                    ./mvnw test
                    
                    echo "Generating test reports..."
                    ./mvnw surefire-report:report
                '''
                
                echo " Tests completed successfully"
            }
            
            // Publish test results
            post {
                always {
                    echo "Publishing test results..."
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site',
                        reportFiles: 'surefire-report.html',
                        reportName: 'Test Report'
                    ])
                }
            }
        }
        
        // ===================================================================
        // STAGE 4: PACKAGE APPLICATION
        // ===================================================================
        stage('Package') {
            steps {
                echo " Packaging application..."
                
                // Create JAR file
                sh '''
                    echo "Creating JAR package..."
                    ./mvnw package -DskipTests
                    
                    echo "JAR file created successfully"
                    ls -la target/*.jar
                '''
                
                echo "Application packaged successfully"
            }
            
            // Archive JAR file
            post {
                success {
                    echo "Archiving JAR file..."
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
        
        // ===================================================================
        // STAGE 5: BUILD DOCKER IMAGE
        // ===================================================================
        stage('Docker Build') {
            steps {
                echo "Building Docker image..."
                
                // Build Docker image
                script {
                    def dockerImage = docker.build("${DOCKER_IMAGE}")
                    echo "Docker image built: ${DOCKER_IMAGE}"
                }
                
                echo "Docker image created successfully"
            }
            
            // Clean up old images
            post {
                success {
                    echo "Cleaning up old Docker images..."
                    sh '''
                        # Remove dangling images
                        docker image prune -f
                        
                        # Keep only last 3 versions
                        docker images ${APP_NAME} --format "table {{.Tag}}\t{{.CreatedAt}}" | tail -n +4 | awk '{print $1}' | xargs -r docker rmi ${APP_NAME}:
                    '''
                }
            }
        }
        
        // ===================================================================
        // STAGE 6: DEPLOY APPLICATION
        // ===================================================================
        stage('Deploy') {
            steps {
                echo "Deploying Digital Assistant Service..."
                
                // Stop existing container
                sh '''
                    echo "Stopping existing container if running..."
                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true
                '''
                
                // Deploy new container
                sh '''
                    echo "Starting new container..."
                    docker run -d \
                        --name ${CONTAINER_NAME} \
                        -p ${HOST_PORT}:${APP_PORT} \
                        --restart unless-stopped \
                        ${DOCKER_IMAGE}
                    
                    echo "Container started successfully"
                    docker ps | grep ${CONTAINER_NAME}
                '''
                
                echo "Application deployed successfully"
            }
        }
        
        // ===================================================================
        // STAGE 7: HEALTH CHECK
        // ===================================================================
        stage('Health Check') {
            steps {
                echo "Performing health check..."
                
                // Wait for application to start
                sh '''
                    echo "Waiting for application to start..."
                    sleep 30
                    
                    echo "Checking application health..."
                    curl -f http://localhost:${APP_PORT}/actuator/health || exit 1
                    
                    echo "Testing API endpoints..."
                    curl -f http://localhost:${APP_PORT}/api/assistants/health || exit 1
                '''
                
                echo "Health check passed - Application is running correctly"
            }
        }
    }
    
    // ===================================================================
    // POST-BUILD ACTIONS
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
            

        
        // Actions on failed build
        failure {
            echo "Ohhh Noo Yash!! Pipeline FAILED!"
            echo " Check the logs above for error details"
            
            // Send failure notification (customize as needed)
            // emailext (
            //     subject: " CI/CD Failed: ${APP_NAME} Build #${BUILD_NUMBER}",
            //     body: "Digital Assistant Service deployment failed!\n\nBuild: #${BUILD_NUMBER}\nCheck Jenkins logs for details.",
            //     to: "your-email@example.com"
            // )
        }
        
        // Actions on unstable build
        unstable {
            echo " Pipeline UNSTABLE!"
            echo " Some tests may have failed, but deployment continued"
        }
    }
}
}
