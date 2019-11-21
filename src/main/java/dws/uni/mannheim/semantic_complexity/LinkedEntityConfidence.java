package dws.uni.mannheim.semantic_complexity;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import dws.uni.mannheim.relatedness.Neo4jRelatednessComputer;

public class LinkedEntityConfidence {
	
	final static String WIKI_DISAMB = "http://dbpedia.org/ontology/wikiPageDisambiguates";
	
	GraphDatabaseService db ;	
	HDT DBpediaHDT; 
	
	public LinkedEntityConfidence(GraphDatabaseService db, HDT  dbpediaHDT) {
		this.db = db;
		this.DBpediaHDT  = dbpediaHDT;
	}
	
	Map<String, Double> computeConfidences(Collection<String> linkedTopic){
		
		Map<String, Double> result = new HashMap<>();
		Set<Node> topic = new HashSet<>();
		for (String uri: linkedTopic) {
			if (uri.length() > 1){
			Node n =  db.findNode(DynamicLabel.label("Resource"), "uri", uri);
			if(n!=null)
				topic.add(n);
			}
		} 
		
		for (Node n: topic) {
			if (n.hasProperty("uri")&& n.getProperty("uri")!=null){
			double prior = disambiguationPrior(n);
			double topicFitness = fitnessToTopic(n, topic);
			if (prior == 1)
				result.put(n.getProperty("uri").toString(), prior);
			
			else 
				result.put(n.getProperty("uri").toString(), prior*topicFitness);
			}
		}
		
		return result;
	}
	
	
	double disambiguationPrior(Node target){
		int nrOptions = 0 ;
		
		try{
	//	System.out.println("searching disamb count for " + target.getProperty("uri").toString());
		
		IteratorTripleString  tripleString = this.DBpediaHDT.search("", WIKI_DISAMB, target.getProperty("uri").toString());
		
		while (tripleString.hasNext()){
			TripleString ts = tripleString.next();
			String ambig = ts.getSubject().toString();
			IteratorTripleString  ambigTripleString = this.DBpediaHDT.search(ambig, WIKI_DISAMB, "");
			while (ambigTripleString.hasNext()) {
				TripleString ats  = ambigTripleString.next();
				nrOptions ++;
			}
		}
		}catch(NotFoundException nfe){
			//nfe.printStackTrace();
		}
		catch(Exception e){
			System.out.println("Exception querying the disamb of " + target);
			e.printStackTrace();
			
		}
	//	System.out.println(nrOptions);
		if (nrOptions == 0) return 1.0;
		else return 1.0/nrOptions;		
	}
	
	double fitnessToTopic(Node target, Set<Node> topic) {
		double result = 0.0;
		for (Node n: topic) {
			if (n.getId()!= target.getId()) {
				Neo4jRelatednessComputer comp = new Neo4jRelatednessComputer();
				double  rel = comp.getRelRelatednessof2Nodes(db, target, n, 4, 3, 0.2);
				result+=rel;
		//		System.out.println(n.getProperty("uri").toString() + " --- " + target.getProperty("uri").toString() + "====> " + rel);
				
			}
		}
		return result / (topic.size()-1);
	}
	
	Set<String> arrayToString(String[] array) {
		Set<String> res = new HashSet<>();
		for (String a: array){
			res.add(a);
		}
		return res;
	}
	
	
	

}
