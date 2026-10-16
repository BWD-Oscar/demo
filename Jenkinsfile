
pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-southeast-2'
        AWS_ACCOUNT_ID = '311916835630'
        ECR_REPOSITORY = 'demo'
        ECS_CLUSTER = 'http-echo-cluster'
        ECS_SERVICE = 'demo-service'
        TASK_FAMILY = 'demo-task'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

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
                sh 'docker tag demo:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG'
            }
        }

        stage('Push to ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region "$AWS_REGION" \
                    | docker login --username AWS --password-stdin \
                      "$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"

                    docker push "$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG"
                '''
            }
        }

        stage('Deploy to ECS') {
            steps {
                sh '''
                    IMAGE_URI="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG"

                    aws ecs describe-task-definition \
                      --task-definition "$TASK_FAMILY" \
                      --region "$AWS_REGION" \
                      --query taskDefinition > task-definition.json

                    python3 -c '
import json
with open("task-definition.json") as f:
    task = json.load(f)
for key in ["taskDefinitionArn", "revision", "status", "requiresAttributes", "compatibilities", "registeredAt", "registeredBy"]:
    task.pop(key, None)
for container in task["containerDefinitions"]:
    if container["name"] == "demo-container":
        container["image"] = "'"$IMAGE_URI"'"
with open("task-definition-new.json", "w") as f:
    json.dump(task, f)
'

                    NEW_TASK_DEF=$(aws ecs register-task-definition \
                      --cli-input-json file://task-definition-new.json \
                      --region "$AWS_REGION" \
                      --query 'taskDefinition.taskDefinitionArn' \
                      --output text)

                    aws ecs update-service \
                      --cluster "$ECS_CLUSTER" \
                      --service "$ECS_SERVICE" \
                      --task-definition "$NEW_TASK_DEF" \
                      --region "$AWS_REGION"

                    aws ecs wait services-stable \
                      --cluster "$ECS_CLUSTER" \
                      --services "$ECS_SERVICE" \
                      --region "$AWS_REGION"
                '''
            }
        }
    }

    post {
        success {
            echo 'Build, ECR push, and ECS deployment completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check Console Output for the failing stage.'
        }
    }
}
