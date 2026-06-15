pipeline {
    agent any

    stages {
        stage('Checkout Code') {
            steps {
                // Pulls the code down from your GitHub repository into the active Jenkins workspace
                checkout scm
            }
        }

        stage('Build & JUnit Test') {
            agent {
                docker { 
                    image 'maven:3.9.5-eclipse-temurin-17'
                }
            }
            steps {
                echo 'Compiling Java Application and running JUnit tests...'
                // Navigates into your app folder and runs the Maven build/test suite
                sh 'cd app && mvn clean test package'
            }
            post {
                always {
                    // Captures your JUnit XML test results and visualizes them in Jenkins
                    junit 'app/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy to Docker') {
            steps {
                echo 'Deploying application container on host machine...'
                // Double quotes let Jenkins parse the dynamic ${WORKSPACE} variable flawlessly
                sh """
                    # 1. Create a clean temporary folder on your Mac filesystem
                    mkdir -p /tmp/jenkins-demo-target
                    
                    # 2. Extract the freshly compiled JAR from Jenkins to your Mac using the exact workspace path
                    docker cp jenkins-local:${WORKSPACE}/app/target/java-jenkins-demo-1.0-SNAPSHOT.jar /tmp/jenkins-demo-target/
                    
                    # 3. Clean up any previous demo container if it exists
                    docker rm -f java-app-demo || true
                    
                    # 4. Spin up your dedicated, Mac-compatible Java runtime container
                    docker run -d --name java-app-demo \
                      -v /tmp/jenkins-demo-target:/apps \
                      eclipse-temurin:17-jre \
                      java -jar /apps/java-jenkins-demo-1.0-SNAPSHOT.jar
                """
            }
        }
    }
}
