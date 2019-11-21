package dws.uni.mannheim.semantic_complexity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KanopyDocumentJsonReader {

	
	public KanopyDocument readDocument(String pathToJson) throws FileNotFoundException{		
		
		Gson gson = new GsonBuilder().create();
		KanopyDocument doc = gson.fromJson(new InputStreamReader(new FileInputStream(new File(pathToJson))), KanopyDocument.class);
		
		return doc;
	}
	
	       public KanopyDocument readDocumentFromString(String docContent) {	                
	                Gson gson = new GsonBuilder().create();
	                KanopyDocument doc = gson.fromJson(docContent, KanopyDocument.class);
	                
	                return doc;
	        }
	
	
}
