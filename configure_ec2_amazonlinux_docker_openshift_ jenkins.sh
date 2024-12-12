#!/bin/bash

# Update the system
yum update -y

# Install required packages
yum install -y docker git wget

# Start and enable Docker service
systemctl start docker
systemctl enable docker

# Add ec2-user to docker group
usermod -aG docker ec2-user

####

# Install OpenShift CLI (oc)
wget https://mirror.openshift.com/pub/openshift-v4/clients/oc/latest/linux/oc.tar.gz
tar xvf oc.tar.gz
mv oc /usr/local/bin/
rm -f oc.tar.gz README.md

####

# Create directory for Jenkins data
mkdir -p /var/jenkins_home
chown 1000:1000 /var/jenkins_home

# Run Jenkins container
docker run -d \
  --name jenkins \
  --restart=unless-stopped \
  -p 8080:8080 \
  -p 50000:50000 \
  -v /var/jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v $(which docker):/usr/bin/docker \
  -v $(which oc):/usr/local/bin/oc \
  jenkins/jenkins:lts

# Wait for Jenkins to start and get the initial admin password
echo "Waiting for Jenkins to start..."
sleep 30

# Get into the container as root
docker exec -u root -it jenkins bash
# Inside the container, install Docker CLI
apt-get update && apt-get install -y docker.io
# Get the GID of the docker group from the host
DOCKER_GID=$(stat -c '%g' /var/run/docker.sock)
# Create the docker group with the same GID
groupadd -g ${DOCKER_GID} docker
# Add jenkins user to the docker group
usermod -aG docker jenkins
# Exit the container
exit

# Restart the Jenkins container
docker restart jenkins


echo "Jenkins initial admin password:"
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword



# Print completion message
echo "Installation completed!"
echo "Jenkins is available on port 8080"
echo "Please make sure to configure your security group to allow inbound traffic on port 8080"
