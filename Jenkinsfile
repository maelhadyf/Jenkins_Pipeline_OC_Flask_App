pipeline {
    agent any
    
    environment {
        // Define variables
        DOCKER_IMAGE = 'flask-memo'
        DOCKER_REGISTRY = 'docker.io/maelhadyf' // e.g., 'quay.io/username' or 'docker.io/username'
        OPENSHIFT_PROJECT = 'mohamedabdelhady'
        OPENSHIFT_SERVER  = 'https://api.ocp-training.ivolve-test.com:6443'
        
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
                        docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .
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
                        docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${BUILD_NUMBER}
                        docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${BUILD_NUMBER}
                    """
                }
            }
        }

        stage('Deploy to OpenShift') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'openshift-cred-id', variable: 'OPENSHIFT_TOKEN')]) {
                        sh """
                            # Login to OpenShift
                            oc login --token=${OPENSHIFT_TOKEN} --server=${OPENSHIFT_SERVER} --insecure-skip-tls-verify=true
            
                            # Replace variables in deployment.yaml
                            sed 's|\${DOCKER_REGISTRY}|'${DOCKER_REGISTRY}'|g; s|\${DOCKER_IMAGE}|'${DOCKER_IMAGE}'|g; s|\${DOCKER_TAG}|'${BUILD_NUMBER}'|g' app-deployment.yaml > deployment_processed.yaml
            
                            # Apply the configuration
                            oc apply -f deployment_processed.yaml
            
                            # Wait for rollout to complete
                            oc rollout status deployment/${DOCKER_IMAGE}
            
                            # Get the Route URL
                            echo "Application is deployed at: \$(oc get route ${DOCKER_IMAGE} -o jsonpath='{.spec.host}')"
                        """
                    }
                }
            }
        }


    }
    
    post {
        always {
            // Cleanup
            sh 'docker logout ${DOCKER_REGISTRY}'
            // Remove OpenShift login session
            sh 'oc logout'
            cleanWs()
        }
    }
}
