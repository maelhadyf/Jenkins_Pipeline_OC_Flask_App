#!/usr/bin/env groovy

def call(Map config = [:]) {
    def imageName = config.imageName ?: env.DOCKER_IMAGE
    def registry = config.registry ?: env.DOCKER_REGISTRY
    def server = config.server ?: env.OPENSHIFT_SERVER
    def buildNumber = config.buildNumber ?: env.BUILD_NUMBER
    
    withCredentials([string(credentialsId: 'openshift-cred-id', variable: 'OPENSHIFT_TOKEN')]) {
        sh """
            # Login to OpenShift
            oc login --token=${OPENSHIFT_TOKEN} --server=${server} --insecure-skip-tls-verify=true
            
            # Replace variables in deployment.yaml
            sed 's|\${DOCKER_REGISTRY}|'${registry}'|g; s|\${DOCKER_IMAGE}|'${imageName}'|g; s|\${BUILD_NUMBER}|'${buildNumber}'|g' app-deployment.yaml > deployment_processed.yaml
            
            # Apply the configuration
            oc apply -f deployment_processed.yaml
            
            # Wait for rollout to complete
            oc rollout status deployment/${imageName}
            
            # Get the Route URL
            echo "Application is deployed at: \$(oc get route ${imageName} -o jsonpath='{.spec.host}')"
        """
    }
}
