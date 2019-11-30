#!/bin/bash 

source base.sh

# Make sure you have downloaded the data from: 
# https://github.com/ioanahulpus/cocospa/releases/tag/Data


DBPEDIA_IN_NEO4J="${DATA_DIR}/dbpediaNeo4J"
DBPEDIA_IN_HDT=${DATA_DIR}/DBpedia2014Selected.hdt

# Run the spring service
mvn spring-boot:run -Dspring-boot.run.arguments="${DBPEDIA_IN_NEO4J},${DBPEDIA_IN_HDT},127.0.0.1,6379,${DBSPOTLIGHT_URL}"