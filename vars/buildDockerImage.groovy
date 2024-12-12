def call(Map config) {
    sh """
        docker build -t ${config.imageName}:${config.buildNumber} .
    """
}
