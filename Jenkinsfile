pipeline {
    agent any
    
    environment {
        // Define variables
        DOCKER_IMAGE = 'flask-memo'
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_REGISTRY = 'docker.io/maelhadyf' // e.g., 'quay.io/username' or 'docker.io/username'
        OPENSHIFT_PROJECT = 'mohamedabdelhady'
        
        // Credentials (configure these in Jenkins)
        DOCKER_CREDENTIALS = credentials('docker-cred-id')
        OPENSHIFT_CREDENTIALS = credentials('openshift-cred-id')
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Get code from repository
                checkout scm
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
        
        stage('Create Docker Repository') {
            steps {
                script {
                    sh """
                        # First ensure we're logged in to Docker Hub
                        echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin
        
                        # Create repository with proper curl syntax
                        curl -X POST \\
                            -H 'Content-Type: application/json' \\
                            -u "${DOCKER_CREDENTIALS_USR}:${DOCKER_CREDENTIALS_PSW}" \\
                            -d '{\\"namespace\\":\\"${DOCKER_CREDENTIALS_USR}\\",\\"name\\":\\"${DOCKER_IMAGE}\\",\\"is_private\\":false}' \\
                            https://hub.docker.com/v2/repositories/ || true
                    """
                }
            }
        }

        stage('Push to Registry') {
            steps {
                script {
                    sh """
                        # Ensure we're logged in
                        echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin
                        
                        # Tag and push the image
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }


    }
    
    post {
        always {
            // Cleanup
            sh 'docker logout ${DOCKER_REGISTRY}'
            cleanWs()
        }
    }
}
