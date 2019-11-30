#!/bin/bash 

# Make sure you have downloaded the data from: 
# https://github.com/ioanahulpus/cocospa/releases/download/Data/en.tar.gz
# Set the right paths:
source base.sh
DBPEDIA_SPOTLIGHT_DATA_EN="${DATA_DIR}/en"

# Run dbspotlight
java -Xmx300g -jar dbpedia-spotlight-model/rest/target/rest-1.0-jar-with-dependencies.jar "${DBPEDIA_SPOTLIGHT_DATA_EN}" "${DBSPOTLIGHT_URL}"

