package dws.uni.mannheim.semantic_complexity;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.rdfhdt.hdt.hdt.HDT;

public class MentionExtractor {

	
	GraphDatabaseService db ;	
	HDT DBpediaHDT; 
	
	final double confidence_threshold =1E-6;
	KanopyDocument doc;
	
	public MentionExtractor(GraphDatabaseService db, HDT  dbpediaHDT, KanopyDocument doc) {
		this.db = db;
		this.DBpediaHDT  = dbpediaHDT;
		this.doc	= doc;
	}
	
	public void removeLikelyWrongLinks(){
		
		for (Entry<String, Map<String, String>> topice : this.doc.topicWordConcept.entrySet()) {
			Map<String, String> topic = topice.getValue();
			String topicId = topice.getKey();
			LinkedEntityConfidence confComputer = new LinkedEntityConfidence(db, this.DBpediaHDT);
			Map<String, Double> conf = confComputer.computeConfidences(topic.values());
		//	System.out.println("CONFIDENCES:  " + conf);
			Set<String> toRemove = new HashSet<>();
			for(Entry<String, Double> e : conf.entrySet()) {
				if (e.getValue()< confidence_threshold){
					toRemove.add(e.getKey());
				}
			}
			Set<String> toRemoveKeys = new HashSet<>();
			for (String tr : toRemove){
				for (Entry<String, String> e : topic.entrySet()){
					if (e.getValue().equals(tr)){
						toRemoveKeys.add(e.getKey());
					}
				}
			}
			
			for (String tr:toRemoveKeys){
				topic.remove(tr);
		//		System.out.println("removing " + tr);
			}
			this.doc.topicWordConcept.put(topicId, topic);			
			
		}
	}
	
	
}
