package dws.uni.mannheim.semantic_complexity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class Paragraph implements TextSegment, Serializable {

	/**
    * 
    */
   private static final long serialVersionUID = 1L;
   String text;
	int charOffsetStart ;
	int charOffsetEnd;
	
	int tokenOffsetStart;
	int tokenOffsetEnd;
	
	List<Sentence> sentences = new ArrayList<>();
	Set<Mention> mentions = new HashSet<>(); 
	
	public String toString() {
		return "("+ charOffsetStart +" ->" + charOffsetEnd +") "+text;
	}

	@Override
	public Set<Mention> getMentions() {
		return this.mentions;
	}

   public List<Sentence> getSentences() {
      // TODO Auto-generated method stub
      return this.sentences;
   }
}
