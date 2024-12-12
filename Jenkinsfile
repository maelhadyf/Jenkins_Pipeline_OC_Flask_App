@Library('shared-library') _

// Import Constants from shared library
import org.demo.Constants

pipeline {
    agent any
    
    environment {
        // Use Constants from shared library
        DOCKER_IMAGE = Constants.DOCKER_IMAGE
        DOCKER_REGISTRY = Constants.DOCKER_REGISTRY
        OPENSHIFT_PROJECT = Constants.OPENSHIFT_PROJECT
        OPENSHIFT_SERVER = Constants.OPENSHIFT_SERVER
        
        // Credentials
        DOCKER_CREDENTIALS = credentials('docker-cred-id')
        OPENSHIFT_CREDENTIALS = credentials('openshift-cred-id')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    // Use shared library function
                    buildDockerImage([
                        imageName: env.DOCKER_IMAGE,
                        buildNumber: env.BUILD_NUMBER
                    ])
                }
            }
        }

        stage('Push to Registry') {
            steps {
                script {
                    // Use shared library function
                    pushToRegistry([
                        imageName: env.DOCKER_IMAGE,
                        registry: env.DOCKER_REGISTRY,
                        buildNumber: env.BUILD_NUMBER
                    ])
                }
            }
        }

        stage('Deploy to OpenShift') {
            steps {
                script {
                    // Use shared library function
                    deployToOpenShift([
                        imageName: env.DOCKER_IMAGE,
                        registry: env.DOCKER_REGISTRY,
                        server: env.OPENSHIFT_SERVER,
                        buildNumber: env.BUILD_NUMBER
                    ])
                }
            }
        }
    }
    
    post {
        always {
            script {
                // Cleanup
                sh """
                    docker logout ${env.DOCKER_REGISTRY}
                    oc logout
                """
                cleanWs()
            }
        }

    }

}
