#!/bin/sh
# Maven Wrapper Script

set -e

# Find maven home if MAVEN_HOME is not set
if [ -z "$MAVEN_HOME" ]; then
    MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6-bin"
fi

WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

# Download Maven if not present
download_maven() {
    echo "Downloading Apache Maven 3.9.6..."
    mkdir -p "$HOME/.m2/wrapper/dists"
    curl -sL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.tar.gz" | tar xz -C "$HOME/.m2/wrapper/dists"
    MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6"
}

# Check if Maven is installed
if [ ! -d "$HOME/.m2/wrapper/dists/apache-maven-3.9.6" ]; then
    download_maven
fi

MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6"
export PATH="$MAVEN_HOME/bin:$PATH"

# Run Maven
exec mvn "$@"
