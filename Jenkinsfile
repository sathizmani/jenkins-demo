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
                sh '''
                    # 1. Get the absolute path of where we are standing right now
                    CURRENT_DIR=$(pwd)
                    
                    # 2. Create a clean temporary folder on your Mac
                    mkdir -p /tmp/jenkins-demo-target
                    
                    # 3. Copy the compiled JAR using the exact directory we just discovered
                    docker cp jenkins-local:${CURRENT_DIR}/app/target/java-jenkins-demo-1.0-SNAPSHOT.jar /tmp/jenkins-demo-target/
                    
                    # 4. Remove any old app container if it exists
                    docker rm -f java-app-demo || true
                    
                    # 5. Spin up your dedicated, Mac-compatible Java runtime container
                    docker run -d --name java-app-demo \
                      -v /tmp/jenkins-demo-target:/apps \
                      eclipse-temurin:17-jre \
                      java -jar /apps/java-jenkins-demo-1.0-SNAPSHOT.jar
                '''
            }
        }
    }
}
