#!/usr/bin/env groovy

def call(Map config = [:]) {
    def imageName = config.imageName ?: env.DOCKER_IMAGE
    def registry = config.registry ?: env.DOCKER_REGISTRY
    def buildNumber = config.buildNumber ?: env.BUILD_NUMBER
    
    withCredentials([usernamePassword(
        credentialsId: 'docker-cred-id',
        usernameVariable: 'DOCKER_CREDENTIALS_USR',
        passwordVariable: 'DOCKER_CREDENTIALS_PSW'
    )]) {
        sh """
            echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin
            docker tag ${imageName}:${buildNumber} ${registry}/${imageName}:${buildNumber}
            docker push ${registry}/${imageName}:${buildNumber}
        """
    }
}