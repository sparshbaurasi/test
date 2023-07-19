pipeline {
    agent any
    parameters {
        choice(
            name: 'Envr_Name',
            choices: ['dev', 'prod','uat'],
            description: 'Select from which branch do you want to Deploy'
        )
    }
    environment {
        AWS_ACCOUNT_ID="382904467012"
        AWS_DEFAULT_REGION="us-east-1"
        IMAGE_REPO_NAME="mavenregistry"
        IMAGE_TAG="${GIT_COMMIT}"
        CLUSTER_NAME = "MavenCluster-${Envr_Name}"
        SERVICE_NAME = "test-service-${Envr_Name}"
        TASKDEF_NAME = "tdf-maven-${Envr_Name}"
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}"
        
    }
    

    stages {
        
         stage('Logging into AWS ECR') {
            steps {
                script {
                sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
                }
                 
            }
        }
        
        stage('Cloning Git') {
            steps {
                checkout scmGit(branches: [[name: '*/${branchName}']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/sarvesh5012/springboot-workflow-ecs.git']])
                sh "env"        
            }
        }
  
    // Building Docker images
    stage('Building image') {
      steps{
        script {
          sh "docker build -t ${IMAGE_REPO_NAME}:${IMAGE_TAG} ."
        }
      }
    }
   
    // Uploading Docker images into AWS ECR
    stage('Pushing to ECR') {
     steps{  
         script {
                sh "docker tag ${IMAGE_REPO_NAME}:${IMAGE_TAG} ${REPOSITORY_URI}:$IMAGE_TAG"
                sh "aws sts get-caller-identity"
                sh "docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGE_TAG}"
         }
        }
      }
    
    stage('ECS Deployment'){
      steps{
        script{
          sh "aws ecs describe-task-definition --task-definition ${TASKDEF_NAME} > task-def.json"
          sh "jq .taskDefinition task-def.json > taskdefinition.json"
          sh "jq 'del(.taskDefinitionArn)' taskdefinition.json | jq 'del(.revision)' | jq 'del(.status)' | jq 'del(.requiresAttributes)' | jq 'del(.compatibilities)' | jq 'del(.registeredAt)'| jq 'del(.registeredBy)' > container-definition.json"
          sh "jq '.containerDefinitions[0].image = \"${REPOSITORY_URI}:${IMAGE_TAG}\"' container-definition.json > temp-taskdef.json"
          sh "ls"
          sh "cat temp-taskdef.json"
          sh "aws ecs register-task-definition --cli-input-json file://temp-taskdef.json"
          sh "aws ecs update-service --cluster  ${CLUSTER_NAME} --service  ${SERVICE_NAME} --task-definition  ${TASKDEF_NAME}"
        }
      }
    }

    }
}











// pipeline {
//     agent any

//     environment {
//         AWS_DEFAULT_REGION = 'us-east-1'
//         AWS_ACCESS_KEY_ID = credentials('AKIAVSJXCPZCN7KEPLUC')
//         AWS_SECRET_ACCESS_KEY = credentials('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')
//         ECR_REGISTRY_URL = '382904467012.dkr.ecr.us-east-1.amazonaws.com/mavenregistry'
//         ECS_CLUSTER = 'MavenCluster'
//         ECS_SERVICE = 'test-service'
//         ECS_TASK_DEFINITION = 'tdf-maven'
//     }

//     stages {
//         stage('Checkout') {
//             steps {
//                 // Checkout your source code repository
//                 sh "git clone https://github.com/sarvesh5012/springboot-workflow-ecs.git"
//                 sh "git checkout main"
//             }
//         }

//         // stage('Build') {
//         //     steps {
//         //         // Build your application
//         //         // e.g., compiling code, running tests, etc.
//         //     }
//         // }

//         stage('Docker Build & Push') {
//             steps {
//                 script {
//                     def imageName = "${ECR_REGISTRY_URL}:${env.BUILD_NUMBER}"


//                     // Push Docker image to registry
//                     sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 382904467012.dkr.ecr.us-east-1.amazonaws.com"
//                     // Build Docker image
//                     sh "docker build -t ${imageName} ."
//                     sh "docker tag mavenregistry:latest 382904467012.dkr.ecr.us-east-1.amazonaws.com/mavenregistry:latest"
//                     sh "docker push 382904467012.dkr.ecr.us-east-1.amazonaws.com/mavenregistry:latest"
//                     }
//                 }
//             }
        

//         stage('ECS Deployment') {
//             steps {
//                 script {
//                     def ecsParams = [
//                         cluster: env.ECS_CLUSTER,
//                         service: env.ECS_SERVICE,
//                         taskDefinition: env.ECS_TASK_DEFINITION,
//                         tag: "${env.JOB_NAME}:${env.BUILD_NUMBER}",
//                         imageName: "${ECR_REGISTRY_URL}:${env.BUILD_NUMBER}"
//                     ]

//                     ecsDeploy(params: ecsParams)
//                 }
//             }
//         }
//     }
// }


// def ecsDeploy(params) {
//     def awsCommand = "aws ecs update-service --region ${params.cluster.region} --cluster ${params.cluster.name} --service ${params.service.name} --force-new-deployment"

//     sh awsCommand
// }
















// def COLOR_MAP = [
//     'SUCCESS': 'good', 
//     'FAILURE': 'danger',
// ]
// pipeline {
//     agent any
    
//     environment {
//         registryCredential = 'ecr:us-east-1:awscreds'
//         appRegistry = '382904467012.dkr.ecr.us-east-1.amazonaws.com/mavenregistry'
//         awsRegistry = "https://382904467012.dkr.ecr.us-east-1.amazonaws.com"
//         cluster = "MavenCluster"
//         service = "test-service"
//     }

//     stages {
//         stage('Build App Image') {
//             steps {
//                 script {
//                     dockerImage = docker.build( appRegistry + ":$BUILD_NUMBER", "Dockerfile")
//                 }
//             }
//         }
        
//         stage('Upload App Image') {
//           steps{
//             script {
//               docker.withRegistry( awsRegistry, registryCredential ) {
//                 dockerImage.push("$BUILD_NUMBER")
//                 dockerImage.push('latest')
//               }
//             }
//           }
//         }
//         stage('Deploy to ECS staging') {
//             steps {
//                 withAWS(credentials: 'awscreds', region: 'us-east-1') {
//                     sh 'aws ecs update-service --cluster ${cluster} --service ${service} --force-new-deployment'
//                 }
//             }
//         } 
//     }
// }
