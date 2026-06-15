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
                
                // Securely stash the compiled JAR file for environment promotion
                // test
                stash name: 'app-jar', includes: 'app/target/*.jar'
            }
            post {
                always {
                    junit 'app/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy to DEV') {
            steps {
                echo 'Deploying application container to DEV environment...'
                unstash 'app-jar'
                sh '''
                    # Clean up old DEV container if it exists
                    docker rm -f java-app-dev || true
                    
                    # Spin up DEV container on a unique port if needed, or just unique name
                    docker run -d --name java-app-dev \
                      -v jenkins_home:/var/jenkins_home \
                      eclipse-temurin:17-jre \
                      java -jar /var/jenkins_home/workspace/java-demo/app/target/java-jenkins-demo-1.0-SNAPSHOT.jar
                '''
            }
        }

        stage('Promote Gateway (Approval)') {
            steps {
                // This completely pauses the pipeline UI and waits for user interaction
                input message: 'Does the DEV deployment look healthy? Approve to promote to PROD.', ok: 'Release to Prod'
            }
        }

        stage('Deploy to PROD') {
            steps {
                echo 'Deploying application container to PROD environment...'
                unstash 'app-jar'
                sh '''
                    # Clean up old PROD container if it exists
                    docker rm -f java-app-prod || true
                    
                    # Spin up the official PROD container using the exact same verified artifact
                    docker run -d --name java-app-prod \
                      -v jenkins_home:/var/jenkins_home \
                      eclipse-temurin:17-jre \
                      java -jar /var/jenkins_home/workspace/java-demo/app/target/java-jenkins-demo-1.0-SNAPSHOT.jar
                '''
            }
        }
    }
}
