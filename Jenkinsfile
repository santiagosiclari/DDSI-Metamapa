pipeline {
    agent any

    stages {
        // --- AGREGAR ESTO AL PRINCIPIO ---
        stage('Limpieza Exorcista') {
            steps {
                // Esto borra TODO lo que haya en la carpeta antes de empezar.
                // Mata cualquier carpeta zombie que haya dejado Docker.
                cleanWs()
            }
        }
        // ---------------------------------

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