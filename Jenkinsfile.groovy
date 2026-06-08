pipeline {
    agent any

    stages {
        stage('Checkout Code') {
            steps {
                // This pulls the code down from your GitHub repository into the Jenkins workspace
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
                    // This tells Jenkins to look for your JUnit XML test results and visualize them
                    junit 'app/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy to Docker') {
            steps {
                echo 'Deploying application container on host machine...'
                // This launches your compiled Java application in a brand new container on your Mac
                sh '''
                    docker rm -f java-app-demo || true
                    docker run -d --name java-app-demo eclipse-temurin:17-jre-alpine java -jar /var/jenkins_home/workspace/java-demo/app/target/java-jenkins-demo-1.0-SNAPSHOT.jar
                '''
            }
        }
    }
}
