pipeline {
    agent any

    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['dev', 'qa', 'prod'],
            description: 'Ambiente al que se va a desplegar'
        )
    }

    environment {
        TARGET_ENV = "${params.DEPLOY_ENV}"
    }

    stages {
        stage('Test') {
            agent {
                docker { image 'gradle:8-jdk21' }
            }
            steps {
                sh '''
                    ./gradlew test
                '''
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Construyendo imagen Docker...'
                sh '''
                    IMAGE_TAG=$(git rev-parse --short HEAD)

                    docker build \
                        -t localhost:5000/app-java-gradle:${IMAGE_TAG} \
                        .

                    docker push \
                        localhost:5000/app-java-gradle:${IMAGE_TAG}
                '''
            }
        }

        stage('Deploy') {
            steps {
                dir('manifests') {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],
                        userRemoteConfigs: [[
                            url: 'git@github.com:Emmanuel-1919/Devops-cicd.git',
                            credentialsId: 'github-devops-cicd'
                        ]]
                    ])
                }

                echo "Desplegando en el ambiente: ${TARGET_ENV}"

                sh '''
                    IMAGE_TAG=$(git rev-parse --short HEAD)

                    kubectl apply \
                        --context ${TARGET_ENV} \
                        -f manifests/k8s/${TARGET_ENV}/app-java-gradle-deployment.yaml

                    kubectl set image \
                        --context ${TARGET_ENV} \
                        deployment/app-java-gradle \
                        app-java-gradle=host.docker.internal:5000/app-java-gradle:${IMAGE_TAG} \
                        -n ${TARGET_ENV}

                    kubectl apply \
                        --context ${TARGET_ENV} \
                        -f manifests/k8s/${TARGET_ENV}/app-java-gradle-service.yaml
                '''
            }
        }
    }
}