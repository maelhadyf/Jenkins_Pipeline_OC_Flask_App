def call(Map config) {
    withCredentials([usernamePassword(
        credentialsId: 'docker-cred-id',
        usernameVariable: 'DOCKER_USER',
        passwordVariable: 'DOCKER_PASS'
    )]) {
        sh """
            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
            docker tag ${config.imageName}:${config.buildNumber} ${config.registry}/${config.imageName}:${config.buildNumber}
            docker push ${config.registry}/${config.imageName}:${config.buildNumber}
        """
    }
}
