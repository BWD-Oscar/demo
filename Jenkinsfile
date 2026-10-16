
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Jenkins has checked out the source code from GitHub.'
            }
        }

        stage('Check Source') {
            steps {
                sh 'ls -la'
                sh 'test -f pom.xml'
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean verify'
            }
        }
    }

    post {
        success {
            echo 'Build and tests completed successfully!'
        }
        failure {
            echo 'Build failed. Check the console output for details.'
        }
    }
}
