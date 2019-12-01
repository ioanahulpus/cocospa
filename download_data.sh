#!/bin/bash 

source base.sh

while true; do
    read -p "This script will download ~38 GB of data. Are you sure?" yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

cd ${DATA_DIR} && \

wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpedia2014inHDT.tar.gz" && \
tar -xvf dbpedia2014inHDT.tar.gz && \
\rm dbpedia2014inHDT.tar.gz && \

wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpedia2014inHDTindex.tar.gz" && \
tar -xvf dbpedia2014inHDTindex.tar.gz && \
\rm dbpedia2014inHDTindex.tar.gz && \

wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/en.tar.gz" && \
tar -xvf en.tar.gz && \
\rm en.tar.gz && \

wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpediaNeo4J.tar.gz.aa" && \
wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpediaNeo4J.tar.gz.ab" && \
wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpediaNeo4J.tar.gz.ac" && \
cat dbpediaNeo4J.tar.gz.* | tar xzvf - && \
\rm dbpediaNeo4J.tar.gz.* && \

wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/redisDump.tar.gz" && \
tar -xvf redisDump.tar.gz && \
\rm redisDump.tar.gz
