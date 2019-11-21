package dws.uni.mannheim.relatedness;


import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


/**
 * class that uses the edge exclusivity score to compute pair of nodes exclusivity, 
 * path weights, etc. 
 * @author ioahul
 *
 */
public class Neo4jRelatednessComputer {
	
	
	private Map<Long, Map<Long, Double>> nodeExclusivityCache = new HashMap<Long, Map<Long,Double>>();
	
	public enum ExclusivityType {
		DIVIDED {
		    public String toString() {
		        return "Exclusivity";
		    }
		},
		 
		PROBABILISTIC {
		    public String toString() {
		        return "ExclusivityProb";
		    }
		}
		}
	
	
	public enum RelatednessType{
		ExclusivityRel, ISP, Katz, Schuhmacher
	}

	
	
	
	
	public double getRelPathExclusivityAlpha(Path path, double alpha, ExclusivityType type){
		double pathCost = 0.0;
		int pathlength = 0;
		
		for (Relationship r : path.relationships()){
			if (r.hasProperty("Exclusivity")){
				double excl  = Double.valueOf(r.getProperty(type.toString()).toString());
				pathCost = pathCost+ 1.0/excl;
			}
			else System.out.println("Relation without Exclusivity found!!");
			pathlength++;
		}
		
		return Math.pow(alpha, pathlength-1)/pathCost;
	}
	
	
	
	
	/**
	 * method that uses the exclusivity to compute the relation based relatedness of 2 nodes.
	 * @param p a neo4j Path object
	 * @return
	 */
	
	public double getRelRelatednessof2Nodes(GraphDatabaseService db , Node node1, Node node2, int maxPathLength, int maxNrPaths, double alpha){
	
		
		if (node1.getProperty("uri").toString().equals(node2.getProperty("uri").toString())) return 1.0;
		
		int i=0;
		int nrRelPaths =0;
		Set<Path>[] relPaths = new HashSet[maxPathLength+1];
		
		while (i <= maxPathLength && nrRelPaths < maxNrPaths ){
				PathFinder<org.neo4j.graphdb.Path> finder = GraphAlgoFactory.pathsWithLength(PathExpanders.allTypesAndDirections(), i);
				Iterable<org.neo4j.graphdb.Path> paths = finder.findAllPaths(node1, node2);
				
				int nrRelPathsOfi = 0;
				
				Set<Path> relPathsOfLengthi = new HashSet<Path>();
				
				for(org.neo4j.graphdb.Path p: paths){
						nrRelPathsOfi++ ;
						relPathsOfLengthi.add(p);					
				}
				
				relPaths[i] = relPathsOfLengthi;
				nrRelPaths = nrRelPaths + nrRelPathsOfi;
				i++;
		}
			
		double cumulativeRelPathExclAlpha =0.0;
		
		List<Double> relpathExclAlphaList = new ArrayList<Double>();

		
		for(i=0; i<=maxPathLength; i++){
			Set<Path> pathsofi = relPaths[i];			
			if (pathsofi!=null && pathsofi.size()>0){					
					for(Path p: pathsofi){
						double relpathExclKatz = this.getRelPathExclusivityAlpha(p, alpha, ExclusivityType.DIVIDED);
						relpathExclAlphaList.add(relpathExclKatz);
					}
			}
		}
		Collections.sort(relpathExclAlphaList, Collections.reverseOrder());
					
		for (int k=0;k<maxNrPaths; k++){
			if(relpathExclAlphaList.size()>k){
				cumulativeRelPathExclAlpha += relpathExclAlphaList.get(k);
			}
		}
		
		return cumulativeRelPathExclAlpha;
	}
	
	
	/**
	 * method that uses the exclusivity to compute the relation based relatedness of 2 nodes.
	 * @param p a neo4j Path object
	 * @return
	 */
	
	public double getRelRelatednessof2Nodes(GraphDatabaseService db , String uri1, String uri2, int maxPathLength, int maxNrPaths, double alpha){
	
		if(uri1.equals(uri2))
			return 1.0;
		
		Node node1 = this.getNodeFromNeo4J(uri1, db);
		Node node2 = this.getNodeFromNeo4J(uri2, db);
		
		return this.getRelRelatednessof2Nodes(db, node1, node2, maxPathLength, maxNrPaths, alpha);
		
		
	
	}

	public  Node getNodeFromNeo4J(String nodeURI, GraphDatabaseService db ){
		Node result =null;
			
			  try 	 
			  {
				  ResourceIterable<Node>  nodes = db.findNodesByLabelAndProperty(DynamicLabel.label("Resource"), "uri", nodeURI);
				  
				 for(org.neo4j.graphdb.Node n: nodes){
					System.out.println(n.getProperty("uri")); 
					result = n;
				 }
			  }
			  catch(Exception ex){
				  ex.printStackTrace();
			  }
			 
			 return result;
		}
		
}
	
	

