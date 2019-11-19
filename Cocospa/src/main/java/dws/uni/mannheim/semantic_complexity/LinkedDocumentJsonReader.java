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

public class LinkedDocumentJsonReader {

	
	public LinkedDocument readDocument(String pathToJson) throws FileNotFoundException{		
		
		Gson gson = new GsonBuilder().create();
		LinkedDocument doc = gson.fromJson(new InputStreamReader(new FileInputStream(new File(pathToJson))), LinkedDocument.class);
		
		return doc;
	}
	
	
}
