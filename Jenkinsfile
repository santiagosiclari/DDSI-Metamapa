pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '3'))
        timeout(time: 20, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Despliegue Microservicios') {
            steps {
                dir('infra') {
                    script {
                        sh 'chmod +x deploy.sh'
                        sh './deploy.sh'
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🔥 ¡Metamapa desplegado! Gateway en puerto 80."
        }
        failure {
            echo "❌ Error en el despliegue. Revisá logs de Docker."
        }
    }
}