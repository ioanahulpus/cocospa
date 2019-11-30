# Data directory

Use the shell script `download_data.sh` or download the data from [this url](https://github.com/ioanahulpus/cocospa/releases).

Make sure you have at least 50GB if you want to store both the archives and the untarred data. The unarchived data is 36G, but if you're short on storage, the *redisDump.tar.gz* (2GB) is optional and can be skipped.


## About the data
- *en.tar.gz* is the English dataset used to start dbspotlight
- *dbpedia2014inHDT* and *dbpedia2014inHDTindex* is a dump of [dbpedia 2014](https://wiki.dbpedia.org/) in HDT format
- *dbpediaNeo4J* is a dump of dbpedia 2014 in Neo4J format
- *redisDump.tar.gz* the redis dump that contains the cached activations of entities after it has been run on most of wikipedia and newsela with the default parameters. If you have enough memory (at least 16GB), you [could use the dump](https://stackoverflow.com/a/22024286) in your redis instance. This will make the API run faster at the cost of using more storage and memory space. 