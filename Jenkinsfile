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
                
                // Safely stashes the compiled JAR file before leaving the isolated Maven container
                stash name: 'app-jar', includes: 'app/target/*.jar'
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
                
                // Unstashes the JAR into the base workspace directory (/var/jenkins_home/workspace/java-demo)
                unstash 'app-jar'
                
                sh '''
                    # 1. Clean up any previous demo container if it exists
                    docker rm -f java-app-demo || true
                    
                    # 2. Spin up the container by mounting the global jenkins_home volume directly.
                    # This maps the shared storage so both containers can access the exact same workspace files.
                    docker run -d --name java-app-demo \
                      -v jenkins_home:/var/jenkins_home \
                      eclipse-temurin:17-jre \
                      java -jar /var/jenkins_home/workspace/java-demo/app/target/java-jenkins-demo-1.0-SNAPSHOT.jar
                '''
            }
        }
    }
}
