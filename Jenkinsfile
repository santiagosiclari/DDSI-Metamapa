pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        skipDefaultCheckout(true)  // We'll do checkout manually
    }

    stages {
        stage('Limpieza Total') {
            steps {
                echo "🧹 Limpieza completa del workspace..."
                deleteDir()  // Nuclear option - deletes EVERYTHING
            }
        }

        stage('Checkout Fresco') {
            steps {
                echo "📦 Clonando repositorio..."
                checkout scm

                // Verify files exist
                sh '''
                    echo "=== Archivos en workspace ==="
                    ls -la

                    echo "=== Archivos en infra ==="
                    ls -la infra/

                    echo "=== Verificando archivos críticos ==="
                    [ -f "Jenkinsfile" ] && echo "✅ Jenkinsfile" || echo "❌ Jenkinsfile"
                    [ -f "infra/docker-compose.yml" ] && echo "✅ docker-compose.yml" || echo "❌ docker-compose.yml"
                    [ -f "infra/nginx.conf" ] && echo "✅ nginx.conf" || echo "❌ nginx.conf"
                    [ -f "infra/deploy.sh" ] && echo "✅ deploy.sh" || echo "❌ deploy.sh"
                '''
            }
        }

        stage('Limpieza de Zombies') {
            steps {
                dir('infra') {
                    sh '''
                        # Solo remover si es directorio
                        if [ -d "nginx.conf" ]; then
                            echo "⚠️ nginx.conf es directorio, eliminando..."
                            sudo rm -rf nginx.conf
                        fi

                        # Verificar que ahora sea archivo
                        if [ ! -f "nginx.conf" ]; then
                            echo "❌ nginx.conf no existe después de limpieza"
                            exit 1
                        fi

                        echo "✅ nginx.conf es archivo válido"
                    '''
                }
            }
        }

        stage('Despliegue') {
            steps {
                dir('infra') {
                    sh '''
                        chmod +x deploy.sh
                        ./deploy.sh
                    '''
                }
            }
        }

        stage('Verificación') {
            steps {
                dir('infra') {
                    sh '''
                        sleep 5
                        echo "=== Estado de contenedores ==="
                        docker compose ps

                        echo "=== Gateway logs ==="
                        docker logs metamapa-gateway --tail=20 || true
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Despliegue exitoso!"
        }
        failure {
            echo "❌ Build falló"
            sh '''
                echo "=== Workspace content ==="
                ls -la || true
                ls -la infra/ || true

                echo "=== Docker logs ==="
                cd infra && docker compose logs --tail=30 || true
            '''
        }
    }
}