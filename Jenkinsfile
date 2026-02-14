pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    stages {
        // 1. LIMPIEZA TOTAL DEL WORKSPACE
        stage('Limpieza Inicial') {
            steps {
                echo "🧹 Limpiando workspace..."
                cleanWs()
            }
        }

        // 2. CHECKOUT DEL CÓDIGO
        stage('Checkout del Código') {
            steps {
                echo "📦 Descargando código desde Git..."
                checkout scm
            }
        }

        // 3. VERIFICACIÓN PRE-DEPLOY
        stage('Verificación Pre-Deploy') {
            steps {
                dir('infra') {
                    script {
                        echo "🔍 Verificando archivos críticos..."

                        // Verificar que nginx.conf existe y es un archivo
                        sh '''
                            if [ -d "nginx.conf" ]; then
                                echo "❌ ERROR: nginx.conf es un directorio (zombie)"
                                sudo rm -rf nginx.conf
                                echo "✅ Zombie eliminado"
                            fi

                            if [ ! -f "nginx.conf" ]; then
                                echo "❌ ERROR CRÍTICO: nginx.conf no existe"
                                exit 1
                            fi

                            echo "✅ nginx.conf es un archivo válido"
                            ls -lh nginx.conf
                        '''
                    }
                }
            }
        }

        // 4. DESPLIEGUE
        stage('Despliegue Microservicios') {
            steps {
                dir('infra') {
                    script {
                        echo "🚀 Ejecutando deploy.sh..."
                        sh 'chmod +x deploy.sh'
                        sh './deploy.sh'
                    }
                }
            }
        }

        // 5. HEALTH CHECK (opcional pero recomendado)
        stage('Health Check') {
            steps {
                dir('infra') {
                    script {
                        echo "🏥 Verificando salud de los servicios..."
                        sh '''
                            sleep 10
                            docker compose ps

                            # Verificar que el gateway responde
                            if docker compose ps gateway | grep -q "Up"; then
                                echo "✅ Gateway está corriendo"
                            else
                                echo "⚠️ Gateway no está activo"
                            fi
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            echo "🏁 Build finalizado."
        }
        success {
            echo "✅ Despliegue exitoso!"
        }
        failure {
            echo "❌ Algo falló. Revisá los logs."
            dir('infra') {
                sh 'docker compose logs --tail=50 || true'
            }
        }
    }
}