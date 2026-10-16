
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Source code checked out from GitHub.'
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t demo:latest .'
            }
        }

        stage('Verify Docker Image') {
            steps {
                sh 'docker images demo:latest'
            }
        }
    }

    post {
        success {
            echo 'Maven build and Docker image build completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
