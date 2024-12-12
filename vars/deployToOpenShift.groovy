def call(Map config) {
    withCredentials([string(credentialsId: 'openshift-cred-id', variable: 'OPENSHIFT_TOKEN')]) {
        sh """
            oc login --token=\$OPENSHIFT_TOKEN --server=${config.server} --insecure-skip-tls-verify=true
            sed 's|\${DOCKER_REGISTRY}|'${config.registry}'|g; s|\${DOCKER_IMAGE}|'${config.imageName}'|g; s|\${DOCKER_TAG}|'${config.buildNumber}'|g' app-deployment.yaml > deployment_processed.yaml
            oc apply -f deployment_processed.yaml
            oc rollout status deployment/${config.imageName}
            echo "Application is deployed at: \$(oc get route ${config.imageName} -o jsonpath='{.spec.host}')"
        """
    }
}
