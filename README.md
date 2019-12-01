# Conceptual Complexity using Spreading Activation ![](https://github.com/ioanahulpus/cocospa/workflows/Java%20CI/badge.svg)

This is the code to run and call an HTTP endpoint that returns a score of conceptual complexity for a text. We are currently running a Swagger API component onto a server at the [University of Mannheim](http://demaq3.informatik.uni-mannheim.de:8080/swagger-ui.html).

Clone the repo using:
```bash
	git clone --recurse-submodules https://github.com/ioanahulpus/cocospa
```

## Contents
- [Calling the API](https://github.com/ioanahulpus/cocospa/blob/master/README.md#api)
	- [Parameter description](https://github.com/ioanahulpus/cocospa/blob/master/README.md#params)
	- [cURL Example](https://github.com/ioanahulpus/cocospa/blob/master/README.md#curl)
	- [python Example](https://github.com/ioanahulpus/cocospa/blob/master/README.md#python)
- [Running the API](https://github.com/ioanahulpus/cocospa/blob/master/README.md#run)
	- [Using docker](https://github.com/ioanahulpus/cocospa/blob/master/README.md#docker)
	- [Run natively](https://github.com/ioanahulpus/cocospa/blob/master/README.md#natively)


<a name="api"></a> 
## Calling the API
The HTTP API runs on port 8080 and the swagger documentation of the endpoints together with the corresponding input types is available on the same port [http://$HOST:8080/swagger-ui.html].
We currently support two endpoints: *compare* and *complexity*. The later expects a payload of the following form:
```json
{
  "firingThreshold": 0.01,
  "graphDecay": 0.25,
  "linkerThreshold": 0.35,
  "paragraphDecay": 0.5,
  "phiTo1": true,
  "sentenceDecay": 0.7,
  "text": "Some towns on the Eyre Highway in the south-east corner of Western Australia, between the South Australian border almost as far as Caiguna, do not follow official Western Australian time. \nEugowra is said to be named after the Indigenous Australian word meaning \"The place where the sand washes down the hill \"",
  "tokenDecay": 0.85,
  "useExclusivity": true,
  "usePopularity": true
}
```
The only required parameter is the **text**, all the other values default to the optimal (as reported in the paper) from the example above. To have an easy method of comparing two texts, we also added an extra endpoint called */compare* that takes exactly the same configuration parameters with **text1** and **text2** instead of text.  

<a name="params"></a> 
### Parameter description
- firingThreshold - *beta*, β in the paper, limits the concepts that fire to those whose activation surpasses the threhold β
- graphDecay - *alpha*, α in the paper, represents a decay value for the activations going out of each node exponentially
- tokenDecay, sentenceDecay and paragraphDecay - *gamma*, γ_w, γ_s, γ_p defined in Section 3.2 in the [ACL paper](https://www.aclweb.org/anthology/P19-1377.pdf), decay values that take into consideration the distance between concepts within a sentence, between sentences and between paragraphs
- phiTo1 - If true, the phi function is sensitive only to changes in the set of activated concepts. If false, the phi function is sensitive to the actual spreading activation scores, and to the popularity of mentioned concepts
- useExclusivity and usePopularity - boolean values that make use or not of the strenght of semantic relations between concepts as defined in [this paper](https://link.springer.com/chapter/10.1007%2F978-3-319-25007-6_26), see Section 3.1 for more details
- linkerThreshold - is a parameter that sets the confidence therhsold for the entity linker. One may use the current [dbpedia-spotlight demo](https://www.dbpedia-spotlight.org/demo/) to test different thesholds on the input texts. 

<a name="curl"></a> 
### cURL Example
The expected header is `"Content-Type": "application/json"`, for the text above you may call the api with:
```
curl -X POST --header 'Content-Type: application/json' -d '
{
  "firingThreshold": 0.01,
  "graphDecay": 0.25,
  "linkerThreshold": 0.35,
  "paragraphDecay": 0.5,
  "phiTo1": true,
  "sentenceDecay": 0.7,
  "text": "Some towns on the Eyre Highway in the south-east corner of Western Australia, between the South Australian border almost as far as Caiguna, do not follow official Western Australian time. \nEugowra is said to be named after the Indigenous Australian word meaning \"The place where the sand washes down the hill \"",
  "tokenDecay": 0.85,
  "useExclusivity": true,
  "usePopularity": true
}' http://demaq3.informatik.uni-mannheim.de:8080/complexity
```

<a name="python"></a> 
### python Example
Using curl from the command line might not be optimal in case we want to automate a process or call the API for multiple texts to get the comparisons. In the directory *scripts*, we provide some python3 scripts that can be helpfull to call the API on arbitrary texts and reproduce our results. Depending on whether the API is run locally or not and whether the redis cache has been instantiated previously, it might take a few seconds to get a response from the API. So please be patient.

#### 1. Install requirements
```bash
	pip3 install -r scripts/requirements.txt
```

#### 2. Run complexity assesment on a file
Calling the API on a single file, simplified (\*.sim) and complex wikipedia (\*.com) can be done by providing the file path to *call_file.py* script.
```bash
python3 scripts/call_file.py data/examples/Gambling.sim http://demaq3.informatik.uni-mannheim.de:8080/complexity

# if no URL is provided, it defaults to the server from Mannheim
python3 scripts/call_file.py data/examples/Gambling.com 
```
which returns:
```json
{
  "complexityScore" : 0.49564969874355497
}
{
  "complexityScore" : 1.6215938510362011
}
```

#### 3. Run complexity assesment on a directory
```bash
# first parameter is the directory, second parametere (optional) is the API endpoint
python3 scripts/call_on_dir.py data/examples/
```
which prints the table separated values:

| Document            | Score               |
| ------------------- | :------------------:|
| Contact network.com | 0.9221964367423306  |
| Contact network.sim | 0.49564969874355497 |
| Gambling.com        | 1.6215938510362011  |
| Gambling.sim        | 0.5680619862642763  |
| Immunology.com      | 0.611732686310203   |
| Immunology.sim      | 1.7271475433638426  |

#### 4. Run complexity assesment on Newsela
```bash
python3 scripts/call_on_newsela.py $LOCATION_OF_NEWSELA $API_ENDPOINT
```
First one needs to obtain access the [Newsela Data](https://newsela.com/data/). The result of the script is available in **results/newsela.csv**. The script by default calls an API running on natively.
The resuls is a .csv file that shows the scores for different newsela levels:

| Document            |   0    |   1    |   2    |   3    |   4    |
| ------------------- | :----: | :----: | :----: | :----: | :----: |
| 10dollarbill-woman  | 31.85  | 32.74  | 17.82  | 13.42  | 11.79  | 
| 17century-selfies   | 26.52  | 14.91  | 14.83  | 10.15  | 9.04   | 
| 20dollarbill-female | 28.96  | 28.06  | 25.89  | 18.25  | 13.12  | 

#### 5. Run complexity assesment on Wikipedia
```bash
# download the simple-complex wikipedia first:
cd data && \
wget https://cs.pomona.edu/~dkauchak/simplification/data.v2/document-aligned.v2.tar.gz && \
tar -xvf document-aligned.v2.tar.gz && \
cd ..

# run the python script
python3 scripts/call_on_wikipedia.py data/document-aligned.v2/simple.txt data/document-aligned.v2/normal.txt $API_ENDPOINT
```
The result of the script is available in **results/wikipedia.csv**. The script by default calls an API running on natively. The resuls is a .csv file that shows the scores for different wikipedia documents:

| article | simple | complex | simple_size | complex_size |
| -------: | :----: | :----: | :----: | :----: |
| April   | 11.89 | 26.50 | 99 | 535 |
| August  | 14.61 | 21.92 | 179 | 271 |
| Art     | 15.73 | 20.07 | 864 | 5916 |


<a name="run"></a> 
## Running the API
In order to run the API locally, one needs to [download the data first](https://github.com/ioanahulpus/cocospa/tree/master/data). The shell script `download_data.sh` will pull 38 GB consisting of dbpedia dumps, dpbedia-spotlight dumps and a redis cache dump. If you do not have enough space in the data directory of the repository, set $DATA environment variable to the path where it can be downloaded:
```bash
	# if there is not enough space in the data directory where the repo is cloned:
	export DATA="/path/where/data/is/stored"

	# the script removes the tar.gz files after it downloads them
	./download_data.sh
```
The following processes make the assumption that the data is already downloaded.

<a name="docker"></a>
### Run using docker
You can run the api if you have *docker* and *docker-compose* installed.
```bash
	# optional (if the data has been downloaded somewhere else than the data dir)
	export DATA="/path/where/data/is/stored"

	# pull and run the containers that we released with compiled code on docker hub
	docker-compose up -d
```
If you wish to build the images of the containers from scracth, use `docker-compose -f docker-compose_build.yaml up -d`.

Check that the containers have started successfully (`docker-compose ps`) and that dbspotlight finished loading the data with:
```bash
docker-compose logs dbspotlight
```

<a name="natively"></a>
### Run natively

#### 1. Compile dbspotlight
To compile the dbspotlight instance that is compatible with our code **please use java 8**, and run:
```bash
	cd dbpedia-spotlight-model 
	mvn package
	cd ..
```
#### 2. Compile cocospa
Our code is compatible with newer java version and it can be compiled using maven with:
```bash
mvn package
```

#### 3. Install Redis
A redis instance must run on localhost first. Follow the steps in this [tutorial to install Redis](https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-redis-on-ubuntu-16-04).


To have the api running faster, we provide a dump of a database in which the activations have been computed for a large number of entities. The dump has been saved with redis version 5.0.6. **If you are running a newer version (e.g., 6.0.1), the dump might not be compatible**. This will not be a problem if you run using docker.

To load the dump that we provide in your redis instance, follow the steps provided here:

```bash
# get current redis dump and make a backup (optional, if you just installed)
redis-cli
127.0.0.1:6379> CONFIG GET dir
	1) "dir"
	2) "/var/lib/redis/"
127.0.0.1:6379> SAVE
	OK
exit
sudo mv /var/lib/redis/dump.rdb /var/lib/redis/dump.rdb.backup

# stop redis
sudo systemctl stop redis

# copy the redis.rdb dump downloaded in the data directory
sudo cp data/redis.rdb /var/lib/redis/
# change the access rigths to redis
sudo chown redis: /var/lib/redis/dump.rdb

# start redis
sudo systemctl start redis
sudo systemctl status redis

```
if things run smoothly, the dump will consume around 5-6 GB of memory:
```
 redis-server.service - Advanced key-value store
   Loaded: loaded (/lib/systemd/system/redis-server.service; enabled; vendor preset: enabled)
   Active: active (running) since Wed 2019-11-27 15:18:44 CET; 3 days ago
     Docs: http://redis.io/documentation,
           man:redis-server(1)
  Process: 434 ExecStart=/usr/bin/redis-server /etc/redis/redis.conf (code=exited, status=0/SUCCESS)
 Main PID: 571 (redis-server)
    Tasks: 4 (limit: 4915)
   Memory: 6.1G
   CGroup: /system.slice/redis-server.service
           └─571 /usr/bin/redis-server 127.0.0.1:6379
```


# First compile and package the two repos:
	# mvn package 
	# cd dbpedia-spotlight-model && mvn package
# Install redis
	





