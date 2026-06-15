pipeline {
    agent any

    stages {
        stage('Checkout Code') {
            steps {
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
                sh 'cd app && mvn clean test package'
                
                // 1. Tell Jenkins to save the JAR file before leaving this block
                stash name: 'app-jar', includes: 'app/target/*.jar'
            }
            post {
                always {
                    junit 'app/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy to Docker') {
            steps {
                echo 'Deploying application container on host machine...'
                
                // 2. Bring the saved JAR into this stage's current workspace directory
                unstash 'app-jar'
                
                sh '''
                    # 3. Create a clean temporary folder on your Mac
                    mkdir -p /tmp/jenkins-demo-target
                    
                    # 4. Copy the freshly unstashed JAR from the local workspace directory
                    cp app/target/java-jenkins-demo-1.0-SNAPSHOT.jar /tmp/jenkins-demo-target/
                    
                    # 5. Clean up any previous demo container if it exists
                    docker rm -f java-app-demo || true
                    
                    # 6. Spin up your dedicated, Mac-compatible Java runtime container
                    docker run -d --name java-app-demo \
                      -v /tmp/jenkins-demo-target:/apps \
                      eclipse-temurin:17-jre \
                      java -jar /apps/java-jenkins-demo-1.0-SNAPSHOT.jar
                '''
            }
        }
    }
}
