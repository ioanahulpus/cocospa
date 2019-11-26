# First compile and package the two repos:
	# mvn package 
	# cd dbpedia-spotlight-model && mvn package
# Install redis
	# follow this https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-redis-on-ubuntu-16-04

# Set the right paths:
DBPEDIA_SPOTLIGHT_DATA_EN="/data/en"
DBPEDIA_IN_NEO4J="/data/graph_noduplicates_costs_noliterals_nostopUris_new_copy.db"
DBPEDIA_IN_HDT=/data/DBpedia2014Selected.hdt

# Run dbspotlight
java -Xmx300g -jar dbpedia-spotlight-model/rest/target/rest-1.0-jar-with-dependencies.jar "${DBPEDIA_SPOTLIGHT_DATA_EN}" http://localhost:2222/rest 

# Run the spring service
mvn spring-boot:run -Dspring-boot.run.arguments="${DBPEDIA_IN_NEO4J},${DBPEDIA_IN_HDT},127.0.0.1,6379"