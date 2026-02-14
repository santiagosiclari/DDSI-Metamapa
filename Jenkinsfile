pipeline {
    agent any

    options {
        // Mantiene limpio el historial de builds
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    stages {
        // 1. LIMPIEZA TOTAL (El Exorcismo)
        // Esto borra la carpeta 'nginx.conf' mal creada para que Git pueda bajar el archivo real.
        stage('Limpieza Inicial') {
            steps {
                cleanWs()
            }
        }

        // 2. CHECKOUT
        // Baja el código fresco de GitHub (incluyendo el nginx.conf real)
        stage('Checkout del Código') {
            steps {
                checkout scm
            }
        }

        // 3. DESPLIEGUE
        // Entra a la carpeta infra y ejecuta el script
        stage('Despliegue Microservicios') {
            steps {
                dir('infra') {
                    script {
                        // Le damos permisos y ejecutamos
                        sh 'chmod +x deploy.sh'
                        sh './deploy.sh'
                    }
                }
            }
        }
    }

    post {
        always {
            echo "🏁 Build finalizado."
        }
        failure {
            echo "❌ Algo falló. Revisá los logs."
        }
    }
}