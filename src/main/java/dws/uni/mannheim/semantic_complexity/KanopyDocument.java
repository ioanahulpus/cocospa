package dws.uni.mannheim.semantic_complexity;

import java.util.Map;

public class KanopyDocument {
	
	public String docPath;
	public Map<String, Map<String, String>> topicWordConcept;
	private Map<String, String> isolatedConcepts;
	public Map<String, String> getIsolatedConcepts() {
		return isolatedConcepts;
	}
	public void setIsolatedConcepts(Map<String, String> isolatedConcepts) {
		this.isolatedConcepts = isolatedConcepts;
	}
	
	
	
	
}
