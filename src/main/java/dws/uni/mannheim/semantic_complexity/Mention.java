package dws.uni.mannheim.semantic_complexity;

import java.io.Serializable;

public class Mention implements Serializable {
	
	/**
    * 
    */
   private static final long serialVersionUID = 1L;
   String mentionText;
	String mentionedConcept;
	int charOffsetStart;
	int charOffsetEnd;
	
	int tokenOffsetStart;
	int tokenOffsetEnd;
	
	public String toString(){
		return mentionText + "(" +tokenOffsetStart + " -> " + tokenOffsetEnd +") => "+ mentionedConcept;
	}
	
	public String getMentionedConcept() {
		return mentionedConcept;
	}

	public int getTokenOffsetEnd() {
		return tokenOffsetEnd;
	}
	
	public int getTokenOffsetStart() {
		return tokenOffsetStart;
	}
	
	public void setMentionedConcept(String mentionedConcept) {
		this.mentionedConcept = mentionedConcept;
	}
	
	public void setTokenOffsetEnd(int tokenOffsetEnd) {
		this.tokenOffsetEnd = tokenOffsetEnd;
	}
	
	public void setTokenOffsetStart(int tokenOffsetStart) {
		this.tokenOffsetStart = tokenOffsetStart;
	}

	public void setCharOffsetEnd(int charOffsetEnd) {
		this.charOffsetEnd = charOffsetEnd;
		
	}

	public void setCharOffsetStart(int charOffsetBegin) {
		this.charOffsetStart = charOffsetBegin;
		
	}

	public void setMentionText(String mentionText) {
		this.mentionText = mentionText;
		
	}

	public int getCharOffsetStart() {
	return this.charOffsetStart;
	}
	
	public int getCharOffsetEnd() {
		return charOffsetEnd;
	}
	public String getMentionText() {
		return mentionText;
	}
	
	
	
}
