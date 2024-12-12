@Library('shared-library@1') _

// Import Constants from shared library
import org.demo.Constants

pipeline {
    agent any
    
    environment {        
        // Credentials
        DOCKER_CREDENTIALS = credentials('docker-cred-id')
        OPENSHIFT_CREDENTIALS = credentials('openshift-cred-id')
    }
    
    stages {

        stage('Initialize') {
            steps {
                script {
                    // Set variables from Constants
                    env.DOCKER_IMAGE = Constants.DOCKER_IMAGE
                    env.DOCKER_REGISTRY = Constants.DOCKER_REGISTRY
                    env.OPENSHIFT_PROJECT = Constants.OPENSHIFT_PROJECT
                    env.OPENSHIFT_SERVER = Constants.OPENSHIFT_SERVER
                }
            }
        }

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
