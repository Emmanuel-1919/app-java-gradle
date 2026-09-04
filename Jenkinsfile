pipeline {
    agent any
    stages {
        stage('Test') {
            agent {
                docker { image 'gradle:jdk21-alpine' }
            }
            steps {
                dir('app-java-gradle') {
                    sh '''
                    ./gradlew --no-daemon test
                    '''
                }
            }
        }
        stage('Docker Build') {
            steps {
                echo 'Construyendo imagen Docker...'
                dir('app-java-gradle') {
                    sh "docker build -t localhost:5000/app-java-gradle:\$(git rev-parse --short HEAD) ."
                    sh "docker push localhost:5000/app-java-gradle:\$(git rev-parse --short HEAD)"
                }
            }
        }
        stage('Deploy') {
            steps {
                echo 'Desplegando en el clúster de Kubernetes...'
                sh '''
                    kubectl apply -f k8s/dev/app-java-gradle-deployment.yaml
                    kubectl set image deployment/app-java-gradle app-java-gradle=local-registry:5000/app-java-gradle:$(git rev-parse --short HEAD) -n dev
                '''
            }
        }
    }
}