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

        stage('Validar Ambiente') {
            steps {
                script {
                    if (params.DEPLOY_ENV == 'prod' && env.BRANCH_NAME != 'main') {
                        error "Solo la rama 'main' puede desplegar a producción (prod). Estás en la rama '${env.BRANCH_NAME}'."
                    }
                    if (params.DEPLOY_ENV == 'prod') {
                        input message: "¿Confirmas el despliegue a PRODUCCIÓN?", ok: "Sí, desplegar"
                    }
                }
            }
        }

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
                        -n gradle

                    kubectl apply \
                        --context ${TARGET_ENV} \
                        -f manifests/k8s/${TARGET_ENV}/app-java-gradle-service.yaml

                    kubectl annotate deployment/app-java-gradle \
                        --context ${TARGET_ENV} \
                        -n gradle \
                        kubernetes.io/change-cause="Jenkins build #${BUILD_NUMBER} - commit ${IMAGE_TAG}" \
                        --overwrite

                    echo "Para ver la app, corre en tu terminal: minikube service app-java-gradle-service -n gradle -p ${TARGET_ENV} --url"
                '''
            }
        }
    }
}