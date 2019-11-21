package dws.uni.mannheim.semantic_complexity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class LinkedDocument implements Serializable{

   
   /**
    * 
   /**
    * 
    */
   private static final long serialVersionUID = -4492730562218838087L;
   String text;
   Set<Mention> mentions = new HashSet<>();
   List<Paragraph> paragraphs = new ArrayList<>();
   List<Sentence> sentences = new ArrayList<>();   
   String path;

   Map<Mention, Integer> mapConceptToSentence = new HashMap<>();
   Map<Mention, Integer> mapConceptToParagraph = new HashMap<>();

   int complexityLevel;
   
   public LinkedDocument(FeaturedDocument fdoc, String text) {
     this.text = text;
     this.mentions = fdoc.mentions;
     this.paragraphs =  fdoc.paragraphs;
     this.sentences = fdoc.sentences;
     this.path = fdoc.ldoc.docPath;
     this.mapConceptToParagraph = fdoc.mapConceptToParagraph;
     this.mapConceptToSentence = fdoc.mapConceptToSentence;
     this.complexityLevel = fdoc.complexityLevel;
   }

   public int getComplexityLevel() {
      return complexityLevel;
   }
   
   public Map<Mention, Integer> getMapConceptToParagraph() {
      return mapConceptToParagraph;
   }
   
   public Map<Mention, Integer> getMapConceptToSentence() {
      return mapConceptToSentence;
   }
   
   public Set<Mention> getMentions() {
      return mentions;
   }
   
   public List<Paragraph> getParagraphs() {
      return paragraphs;
   }
   
   public String getPath() {
      return path;
   }
   
   public List<Sentence> getSentences() {
      return sentences;
   }
   
   public String getText() {
      return text;
   }
   
   
   public String toString() {
      String result = "";
      for (Paragraph p: this.paragraphs) {
         result = result  + "\n*******************************************\n";
         for (Sentence s: p.sentences) {
            result = result + "\n----\n";
            for(Mention m: s.getOrderedMentions()) {
              result = result  + m.toString()+"\n";
            }
         }
      }
      return result;
   }
   
   
   public void mapMentionsToSentencesAndParagraphs (){
      this.mapConceptToParagraph = new HashMap<Mention, Integer>();
      this.mapConceptToSentence = new HashMap<Mention, Integer>();
      
      for (Mention m : this.mentions) {
         for (Sentence s : this.sentences) {
            if (m.getCharOffsetStart() >= s.charOffsetStart && m.getCharOffsetEnd() <= s.charOffsetEnd) {
               this.mapConceptToSentence.put(m, this.sentences.indexOf(s));
               s.mentions.add(m);
               break;
            }
         }
         for (Paragraph p : this.paragraphs) {
            if (m.getCharOffsetStart() >= p.charOffsetStart && m.getCharOffsetEnd() <= p.charOffsetEnd) {
               this.mapConceptToParagraph.put(m, this.paragraphs.indexOf(p));
               p.mentions.add(m);
               break;
            }
         }
      }
   }

   public List<Sentence> extractSentences(StanfordCoreNLP nlpPipeline){
      List<Sentence> ss = new ArrayList<>();
      if (this.paragraphs.size() == 0) {
         this.paragraphs =  this.extractParagraphs();
      }

      for (Paragraph p : this.paragraphs) {
         Annotation doc = new Annotation(p.text);
         nlpPipeline.annotate(doc);

         List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
         int nextSentStartTokenIndex = 0;
         for(CoreMap sentence: sentences) {
            int beginOffset = sentence.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
            int endOffset = sentence.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
            String text = sentence.get(CoreAnnotations.TextAnnotation.class);
            Sentence s = new Sentence();
            s.charOffsetStart = p.charOffsetStart + beginOffset;
            s.charOffsetEnd = p.charOffsetStart + endOffset;
            s.text = text;
              
            int nrTokens = s.text.split("\\W+").length;
            s.tokenOffsetStart =  p.tokenOffsetStart + nextSentStartTokenIndex;
            s.tokenOffsetEnd =  p.tokenOffsetStart + nextSentStartTokenIndex + nrTokens - 1;
            nextSentStartTokenIndex =  nextSentStartTokenIndex + nrTokens;
            
            ss.add(s);
            p.sentences.add(s);

         }

      }
      this.sentences = ss;
      return ss;
   }

   public List<Paragraph> extractParagraphs() {
      List<Paragraph> l = new ArrayList<Paragraph>();
      int prevParEnd = 0;
      int nextParStartTokenIndex = 0;
      text += "\n";
      while (text.indexOf("\n", prevParEnd)!=-1 ) {
         int breakpar = text.indexOf("\n", prevParEnd);
         Paragraph p = new Paragraph();
         p.charOffsetStart = prevParEnd;
         p.charOffsetEnd = breakpar;
         p.text = text.substring(prevParEnd, breakpar);
         
         int nrTokens = p.text.split("\\W+").length;
         p.tokenOffsetStart= nextParStartTokenIndex;
         p.tokenOffsetEnd = nextParStartTokenIndex+nrTokens-1;
         nextParStartTokenIndex = p.tokenOffsetEnd+1;
         
         prevParEnd = breakpar+2;
         l.add(p);
      }

      this.paragraphs = l;
      return l;
   }
   
   
   
}
