package dws.uni.mannheim.semantic_complexity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sentence implements TextSegment, Serializable{

	/**
    * 
    */
   private static final long serialVersionUID = 5996378906163303462L;
   String text;
	int charOffsetStart;
	int charOffsetEnd;
	
	int tokenOffsetStart;
	int tokenOffsetEnd;
	
	Set<Mention> mentions = new HashSet<Mention>();
	
	
	public String toString(){
		return "("+charOffsetStart +" ->" + charOffsetEnd+ " ) " +text;
	}


	@Override
	public Set<Mention> getMentions() {
		return this.mentions;
	}
	
	public List<Mention> getOrderedMentions(){
      List<Mention> mentions = new ArrayList<>(this.mentions);
      Collections.sort(mentions, new Comparator<Mention> () {
            @Override
            public int compare(final Mention m1, Mention m2){
               if (m1.tokenOffsetStart < m2.tokenOffsetStart && m1.tokenOffsetEnd < m2.tokenOffsetEnd) return -1;
          else if (m1.tokenOffsetStart < m2.tokenOffsetStart && m1.tokenOffsetEnd > m2.tokenOffsetEnd) return 1;
          else if (m1.tokenOffsetStart < m2.tokenOffsetStart && m1.tokenOffsetEnd == m2.tokenOffsetEnd ) return -1;
          else if (m1.tokenOffsetStart > m2.tokenOffsetStart && m1.tokenOffsetEnd < m2.tokenOffsetEnd) return -1;
          else if (m1.tokenOffsetStart > m2.tokenOffsetStart && m1.tokenOffsetEnd > m2.tokenOffsetEnd) return 1;
          else if (m1.tokenOffsetStart > m2.tokenOffsetStart && m1.tokenOffsetEnd == m2.tokenOffsetEnd) return 1; 
          else if (m1.tokenOffsetStart == m2.tokenOffsetStart && m1.tokenOffsetEnd < m2.tokenOffsetEnd) return -1;
          else if (m1.tokenOffsetStart == m2.tokenOffsetStart && m1.tokenOffsetEnd > m2.tokenOffsetEnd) return 1;
          else return 0;
               
             /*  if (m1.tokenOffsetStart< m2.tokenOffsetStart) return -1;
               else if (m1.tokenOffsetStart > m2.tokenOffsetStart) return 1;
               else if (m1.tokenOffsetEnd < m2.tokenOffsetEnd) return -1;
               else if (m1.tokenOffsetEnd > m2.tokenOffsetEnd) return 1; 
               else return 0; */
            }
      });
      return mentions;
   }


   public int getTokenOffsetEnd() {
      return this.tokenOffsetEnd;
   }
	
}
