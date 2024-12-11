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
        
        stage('Push to Registry') {
            steps {
                script {
                    // Login to Docker registry and push
                    sh """
                        echo ${DOCKER_CREDENTIALS_PSW} | docker login ${DOCKER_REGISTRY} -u ${DOCKER_CREDENTIALS_USR} --password-stdin
                        docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
        
        stage('Deploy to OpenShift') {
            steps {
                script {
                    // Login to OpenShift
                    sh """
                        oc login --token=${OPENSHIFT_CREDENTIALS_PSW} --server=your-openshift-server
                        oc project ${OPENSHIFT_PROJECT}
                    """
                    
                    // Create/Update deployment
                    sh """
                        oc set image deployment/${DOCKER_IMAGE} ${DOCKER_IMAGE}=${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG} --record
                        oc rollout status deployment/${DOCKER_IMAGE}
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
