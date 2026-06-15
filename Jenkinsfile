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
                sh '''
                    # 1. Create a temporary folder on your Mac to hold the JAR
                    mkdir -p /tmp/jenkins-demo-target
                    
                    # 2. Copy the compiled JAR out of Jenkins onto your Mac filesystem
                    docker cp jenkins-local:/var/jenkins_home/workspace/java-demo/app/target/java-jenkins-demo-1.0-SNAPSHOT.jar /tmp/jenkins-demo-target/
                    
                    # 3. Remove any old app container if it exists
                    docker rm -f java-app-demo || true
                    
                    # 4. Run the container by mounting the Mac path (/tmp/jenkins-demo-target)
                    docker run -d --name java-app-demo \
                      -v /tmp/jenkins-demo-target:/apps \
                      eclipse-temurin:17-jre \
                      java -jar /apps/java-jenkins-demo-1.0-SNAPSHOT.jar
                '''
            }
        }
    }
}
