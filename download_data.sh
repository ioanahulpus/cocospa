cd data && \

wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpedia2014inHDT.tar.gz" && \
wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpedia2014inHDTindex.tar.gz" && \
wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpediaNeo4J.tar.gz.aa" && \
wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpediaNeo4J.tar.gz.ab" && \
wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/dbpediaNeo4J.tar.gz.ac" && \
wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/en.tar.gz" && \
wget "https://github.com/ioanahulpus/cocospa/releases/download/Data/redisDump.tar.gz" && \

tar -xvf dbpedia2014inHDT.tar.gz && \
tar -xvf dbpedia2014inHDTindex.tar.gz && \
tar -xvf en.tar.gz && \
tar -xvf redisDump.tar.gz && \
cat dbpediaNeo4J.tar.gz.* | tar xzvf -