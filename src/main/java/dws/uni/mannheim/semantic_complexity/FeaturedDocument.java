package dws.uni.mannheim.semantic_complexity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;

import com.beust.jcommander.internal.Lists;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class FeaturedDocument
{

    String text;
    Set<Mention> mentions = new HashSet<>();
    List<Paragraph> paragraphs = new ArrayList<>();
    List<Sentence> sentences = new ArrayList<>();
    int complexityLevel;

    Map<Mention, Integer> mapConceptToSentence = new HashMap<>();
    Map<Mention, Integer> mapConceptToParagraph = new HashMap<>();

    StanfordCoreNLP nlpPipeline;
    GraphDatabaseService db;
    HDT dbpedia;
    Map<String, Double> DBpediaPageRanks;
    Map<String, Set<String>> DBpediaClusters;
    Map<String, Integer> dBpediaClusterSizes;
    KanopyDocument ldoc;

    public FeaturedDocument(KanopyDocument ldoc, String text, StanfordCoreNLP nlpPipeline,
            GraphDatabaseService db, HDT dbpedia,
            Map<String, Double> DBpediaPageRanks,
            Map<String, Set<String>> dbpediaClusters,
            Map<String, Integer> dBpediaClusterSizes)
            throws FileNotFoundException
    {
        /// here

        //FileInputStream in = new FileInputStream(new File(ldoc.docPath));
        //String text = readStream(in);

        this.ldoc = ldoc;
        this.text = text;
        this.db = db;
        this.dbpedia = dbpedia;
        this.DBpediaPageRanks = DBpediaPageRanks;
        this.DBpediaClusters = dbpediaClusters;
        this.dBpediaClusterSizes = dBpediaClusterSizes;
        this.nlpPipeline = nlpPipeline;
        try
        {
            //this.setComplexityOfNewsellaDoc(ldoc.docPath);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        text = text.toLowerCase();

        for (Map<String, String> topic : ldoc.topicWordConcept.values())
        {
            for (Entry<String, String> topicWord : topic.entrySet())
            {
                String word = topicWord.getKey();
                Set<String> forms = new HashSet<>();
                forms.add(word);
                forms.addAll(this.formPluralVariations(word));
                forms.add(topicWord.getValue()
                        .replace("http://dbpedia.org/resource/", "")
                        .replace("_", " ").toLowerCase());
                for (String form : forms)
                {
                    Pattern p = Pattern.compile("\\b" + form + "\\b");
                    Matcher m = p.matcher(text);
                    while (m.find())
                    {
                        int charOffsetBegin = m.start() + 1;
                        int charOffsetEnd = m.end() - 1;
                        String matchedText = m.group();
                        Mention mention = new Mention();

                        String pretext = text.substring(0, charOffsetBegin);
                        int countPretextTokens = pretext.split("\\W+").length; // splits
                                                                               // by
                                                                               // any
                                                                               // non-alphanumeric.
                        int countMatchedTokens = matchedText.split("\\W+").length;
                        mention.tokenOffsetStart = countPretextTokens + 1;
                        mention.tokenOffsetEnd = countPretextTokens
                                + countMatchedTokens;

                        mention.setCharOffsetEnd(charOffsetEnd);
                        mention.setCharOffsetStart(charOffsetBegin);
                        mention.setMentionText(matchedText);
                        mention.mentionedConcept = topicWord.getValue();

                        Node n = db.findNode(DynamicLabel.label("Resource"),
                                "uri", mention.mentionedConcept);
                        if (n != null)
                            this.mentions.add(mention);
                    }
                }
            }
        }

        for (Entry<String, String> isole : ldoc.getIsolatedConcepts()
                .entrySet())
        {
            String word = isole.getKey();
            Set<String> forms = new HashSet<>();
            forms.add(word);
            forms.addAll(this.formPluralVariations(word));
            forms.add(isole.getValue()
                    .replace("http://dbpedia.org/resource/", "")
                    .replace("_", " ").toLowerCase());
            for (String form : forms)
            {
                Pattern p = Pattern.compile("\\b" + form + "\\b");
                Matcher m = p.matcher(text);
                while (m.find())
                {
                    int charOffsetBegin = m.start() + 1;
                    int charOffsetEnd = m.end() - 1;
                    String matchedText = m.group();
                    Mention mention = new Mention();
                    String pretext = text.substring(0, charOffsetBegin);
                    int countPretextTokens = pretext.split("\\W+").length; // splits
                                                                           // by
                                                                           // any
                                                                           // non-alphanumeric.
                    int countMatchedTokens = matchedText.split("\\W+").length;
                    mention.tokenOffsetStart = countPretextTokens + 1;
                    mention.tokenOffsetEnd = countPretextTokens
                            + countMatchedTokens;
                    mention.setCharOffsetEnd(charOffsetEnd);
                    mention.setCharOffsetStart(charOffsetBegin);
                    mention.setMentionText(matchedText);
                    mention.mentionedConcept = isole.getValue();
                    Node n = db.findNode(DynamicLabel.label("Resource"), "uri",
                            mention.mentionedConcept);
                    if (n != null)
                        this.mentions.add(mention);

                }
            }
        }

        this.paragraphs = this.extractParagraphs();
        this.sentences = this.extractSentences();
        this.mapMentionsToSentencesAndParagraphs();
    }

    private void setComplexityOfNewsellaDoc(String filename) throws Exception
    {
        Pattern p = Pattern.compile("\\.en\\.\\d\\.txt");
        Matcher m = p.matcher(filename);
        if (m.find())
        {
            String pattern = m.group();
            String nb = pattern.substring(4, 5);
            int complexity = Integer.valueOf(nb);
            this.complexityLevel = complexity;
        } else
        {
            System.out
                    .println("Could not find document complexity level in its filename");
        }
    }

    public int getComplexityLevel()
    {
        return complexityLevel;
    }

    Set<String> formPluralVariations(String word)
    {
        Set<String> res = new HashSet<>();
        res.add(word + "s");
        if (word.endsWith("y"))
        {
            res.add(word.substring(0, word.length() - 1) + "ie");
            res.add(word.substring(0, word.length() - 1) + "ies");
        }
        if (word.endsWith("f"))
        {
            res.add(word.substring(0, word.length() - 1) + "ves");
            res.add(word.substring(0, word.length() - 1) + "vs");
        }
        if (word.endsWith("s") || word.endsWith("x") || word.endsWith("z")
                || word.endsWith("ch") || word.endsWith("sh"))
        {
            res.add(word + "es");
        }
        return res;
    }

    public List<Mention> getOrderedMentions()
    {
        List<Mention> mentions = new ArrayList<>(this.mentions);
        Collections.sort(mentions, new Comparator<Mention>()
        {
            @Override
            public int compare(final Mention m1, Mention m2)
            {
                if (m1.tokenOffsetStart < m2.tokenOffsetStart)
                    return 1;
                else if (m1.tokenOffsetStart > m2.tokenOffsetStart)
                    return -1;
                else if (m1.tokenOffsetEnd < m2.tokenOffsetEnd)
                    return 1;
                else if (m1.tokenOffsetEnd > m2.tokenOffsetEnd)
                    return -1;
                else
                    return 0;
            }
        });
        return mentions;
    }

    public KanopyDocument getLdoc()
    {
        return ldoc;
    }

    public void mapMentionsToSentencesAndParagraphs()
    {
        for (Mention m : this.mentions)
        {
            for (Sentence s : this.sentences)
            {
                if (m.getCharOffsetStart() >= s.charOffsetStart
                        && m.getCharOffsetEnd() <= s.charOffsetEnd)
                {
                    this.mapConceptToSentence.put(m, this.sentences.indexOf(s));
                    s.mentions.add(m);
                    break;
                }
            }
            for (Paragraph p : this.paragraphs)
            {
                if (m.getCharOffsetStart() >= p.charOffsetStart
                        && m.getCharOffsetEnd() <= p.charOffsetEnd)
                {
                    this.mapConceptToParagraph.put(m,
                            this.paragraphs.indexOf(p));
                    p.mentions.add(m);
                    break;
                }
            }
        }
    }

    private List<Sentence> extractSentences()
    {
        List<Sentence> ss = new ArrayList<>();
        if (this.paragraphs.size() == 0)
        {
            this.paragraphs = this.extractParagraphs();
        }

        for (Paragraph p : this.paragraphs)
        {
            Annotation doc = new Annotation(p.text);
            nlpPipeline.annotate(doc);

            List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
            int nextSentStartTokenIndex = 0;
            for (CoreMap sentence : sentences)
            {
                int beginOffset = sentence
                        .get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
                int endOffset = sentence
                        .get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
                String text = sentence
                        .get(CoreAnnotations.TextAnnotation.class);
                Sentence s = new Sentence();
                s.charOffsetStart = p.charOffsetStart + beginOffset;
                s.charOffsetEnd = p.charOffsetStart + endOffset;
                s.text = text;

                int nrTokens = s.text.split("\\W+").length;
                s.tokenOffsetStart = p.tokenOffsetStart
                        + nextSentStartTokenIndex;
                s.tokenOffsetEnd = p.tokenOffsetStart + nextSentStartTokenIndex
                        + nrTokens - 1;
                nextSentStartTokenIndex = nextSentStartTokenIndex + nrTokens;

                ss.add(s);
                p.sentences.add(s);

            }

        }

        return ss;
    }

    private List<Paragraph> extractParagraphs()
    {
        List<Paragraph> l = new ArrayList<Paragraph>();
        int prevParEnd = 0;
        int nextParStartTokenIndex = 0;
        text += "\n";
        while (text.indexOf("\n", prevParEnd) != -1)
        {
            int breakpar = text.indexOf("\n", prevParEnd);
            Paragraph p = new Paragraph();
            p.charOffsetStart = prevParEnd;
            p.charOffsetEnd = breakpar;
            p.text = text.substring(prevParEnd, breakpar);

            int nrTokens = p.text.split("\\W+").length;
            p.tokenOffsetStart = nextParStartTokenIndex;
            p.tokenOffsetEnd = nextParStartTokenIndex + nrTokens - 1;
            nextParStartTokenIndex = p.tokenOffsetEnd + 1;

            prevParEnd = breakpar + 2;
            l.add(p);
        }

        return l;
    }

    public String readStream(InputStream is)
    {
        StringBuilder sb = new StringBuilder(512);
        try
        {
            Reader r = new InputStreamReader(is, "UTF-8");
            int c = 0;
            while ((c = r.read()) != -1)
            {
                sb.append((char) c);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public List<Paragraph> getParagraphs()
    {
        return paragraphs;
    }

    public List<Sentence> getSentences()
    {
        return sentences;
    }

    public Set<Mention> getMentions()
    {
        return mentions;
    }

}
