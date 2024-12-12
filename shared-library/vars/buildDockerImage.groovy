#!/usr/bin/env groovy

def call(Map config = [:]) {
    def imageName = config.imageName ?: env.DOCKER_IMAGE
    def buildNumber = config.buildNumber ?: env.BUILD_NUMBER
    
    sh """
        docker build -t ${imageName}:${buildNumber} .
    """
}
