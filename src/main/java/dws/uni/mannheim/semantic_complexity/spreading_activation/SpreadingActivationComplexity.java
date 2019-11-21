package dws.uni.mannheim.semantic_complexity.spreading_activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import dws.uni.mannheim.semantic_complexity.KanopyDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocumentJsonReader;
import dws.uni.mannheim.semantic_complexity.Mention;

public class SpreadingActivationComplexity
{

    GraphDatabaseService db;
    final static double FIRING_THRESHOLD = 0.001;
    final static double ACTIVATION_GRAPH_DECAY = 0.5;
    final static double ACTIVATION_READING_DECAY = 0.998;
    final static double FORGET_THRESHOLD = 10E-6;
    static double DEGREE_LOG_NORMALIZATION;

    public SpreadingActivationComplexity(GraphDatabaseService db)
    {
        this.db = db;
        GlobalGraphOperations op = GlobalGraphOperations.at(this.db);
        int count = 0;
        ResourceIterator<Node> it = op.getAllNodes().iterator();
        while (it.hasNext())
        {
            count++;
            it.next();
        }
        DEGREE_LOG_NORMALIZATION = log2(count - 1);

    }

    private double log2(int x)
    {
        return 1.0 * Math.log(x) / (1.0 * Math.log(2));
    }

    public void runSpreadingActivation(Node seed, Map<Node, Double> activation,
            Set<Node> idle)
    {
        activation.put(seed, 1.0);
        Set<Node> fired = new HashSet<>();
        Set<Node> burned = new HashSet<>();
        fired.add(seed);

        updateActivation(activation, fired, burned, idle, true);
        burned.addAll(fired);
        fired.clear();
        for (Entry<Node, Double> e : activation.entrySet())
        {
            if (e.getValue() > FIRING_THRESHOLD && !burned.contains(e.getKey())
                    && !idle.contains(e.getKey()))
            {
                fired.add(e.getKey());
            }
        }

        while (fired.size() > 0)
        {

            System.out
                    .println("Fired.size> 0, so Im spreading the activations....");
            System.out.println("Fired " + fired.size() + ":");
            for (Node n : fired)
            {
                System.out.println(n.getProperty("uri") + ": "
                        + activation.get(n));
            }
            updateActivation(activation, fired, burned, idle, false);
            /*
             * System.out.println("new activation : " ); for (Node n :
             * activation.keySet()){ System.out.println(n.getProperty("uri")
             * +": " + activation.get(n) +";  "); }
             */
            burned.addAll(fired);
            fired.clear();
            for (Entry<Node, Double> e : activation.entrySet())
            {
                if (e.getValue() > FIRING_THRESHOLD
                        && !burned.contains(e.getKey())
                        && !idle.contains(e.getKey()))
                {
                    fired.add(e.getKey());
                }
            }
        }
    }

    private void updateActivation(Map<Node, Double> activation,
            Set<Node> fired, Set<Node> burned, Set<Node> idle, boolean first)
    {

        for (Node f : fired)
        {
            for (Relationship r : f.getRelationships())
            {
                Node neighbour = r.getOtherNode(f);
                double exclusivity = 0.0;
                if (checkResourceIsEquivalentToCateg(f.getProperty("uri")
                        .toString(), neighbour.getProperty("uri").toString()))
                    exclusivity = 1.0;
                else if (r.hasProperty("Exclusivity"))
                    exclusivity = Double.valueOf(r.getProperty("Exclusivity")
                            .toString());

                if (exclusivity > 0.0)
                {
                    double newActivation = 0.0;
                    double importance = log2(neighbour.getDegree() + 1)
                            / DEGREE_LOG_NORMALIZATION;
                    if (activation.containsKey(neighbour))
                    {
                        if (!burned.contains(neighbour))
                        {
                            if (first)
                                newActivation = activation.get(neighbour)
                                        + exclusivity * importance
                                        * activation.get(f);
                            else
                                newActivation = activation.get(neighbour)
                                        + exclusivity * importance
                                        * activation.get(f)
                                        * ACTIVATION_GRAPH_DECAY;
                        }
                        if (idle.contains(neighbour))
                            idle.remove(neighbour);
                    } else if (first)
                        newActivation = exclusivity * importance
                                * activation.get(f);
                    else
                        newActivation = exclusivity * importance
                                * activation.get(f) * ACTIVATION_GRAPH_DECAY;

                    if (newActivation > 1.0)
                        newActivation = 1.0;

                    activation.put(neighbour, newActivation);
                }
            }
        }
    }

    private Map<Mention, Double> computeActivationAtEncounter(
            List<Mention> mentions)
    {
        Map<Mention, Double> result = new HashMap<>();
        Map<Node, Double> activation = new HashMap<>();
        Set<Node> idle = new HashSet<>();

        for (int i = 0; i < mentions.size(); i++)
        {
            Mention m = mentions.get(i);
            Label res = DynamicLabel.label("Resource");
            Node seed = this.db.findNode(res, "uri", m.getMentionedConcept());
            if (i > 0)
            {
                int deltaToken = mentions.get(i).getTokenOffsetStart()
                        - mentions.get(i - 1).getTokenOffsetEnd();
                System.out.println("applying reading decay...");
                for (Node n : activation.keySet())
                {
                    activation.put(
                            n,
                            activation.get(n)
                                    * Math.pow(ACTIVATION_READING_DECAY,
                                            deltaToken));
                }

                if (activation.containsKey(seed))
                {
                    result.put(m, activation.get(seed));
                } else
                    result.put(m, 0.0);
            }
            if (i < mentions.size() - 1)
            {
                System.out
                        .println("Spreading from mention: " + mentions.get(i));
                runSpreadingActivation(seed, activation, idle);
                // filterOutLowActivation(activation);

                idle.addAll(activation.keySet());
            }
        }

        return result;
    }

    private void filterOutLowActivation(Map<Node, Double> activation)
    {
        Set<Node> toremove = new HashSet<>();
        for (Entry<Node, Double> act : activation.entrySet())
        {
            if (act.getValue() < FORGET_THRESHOLD)
                toremove.add(act.getKey());
        }

        for (Node n : toremove)
        {
            activation.remove(n);
        }
    }

    private boolean checkResourceIsEquivalentToCateg(String uri1, String uri2)
    {
        if (!uri1.contains("Category:") && uri2.contains("Category:"))
        {
            String name1 = getResourceName(uri1);
            String name2 = getCategoryName(uri2);
            if (this.levenshteinDistance(name1, name2) <= 1)
                return true;
        } else if (uri1.contains("Category:") && !uri2.contains("Category:"))
        {
            String name1 = getCategoryName(uri1);
            String name2 = getResourceName(uri2);
            if (this.levenshteinDistance(name1, name2) <= 1)
                return true;
        }

        return false;
    }

    public String getResourceName(String uri)
    {
        return uri.replaceAll("http://dbpedia.org/resource/", "");

    }

    public String getCategoryName(String uri)
    {
        return uri.replaceAll("http://dbpedia.org/resource/Category:", "");

    }

    public int levenshteinDistance(CharSequence lhs, CharSequence rhs)
    {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++)
            cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++)
        {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for (int i = 1; i < len0; i++)
            {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete),
                        cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
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

    private List<Mention> extractMentions(KanopyDocument ldoc) throws Exception
    {

        List<Mention> result = new ArrayList<Mention>();

        FileInputStream in = new FileInputStream(new File(ldoc.docPath));
        String text = readStream(in);

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
                        mention.setTokenOffsetStart(countPretextTokens + 1);
                        mention.setTokenOffsetEnd(countPretextTokens
                                + countMatchedTokens);
                        mention.setCharOffsetEnd(charOffsetEnd);
                        mention.setCharOffsetStart(charOffsetBegin);
                        mention.setMentionText(matchedText);
                        mention.setMentionedConcept(topicWord.getValue());

                        Node n = db.findNode(DynamicLabel.label("Resource"),
                                "uri", mention.getMentionedConcept());
                        if (n != null)
                            result.add(mention);
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
                    mention.setTokenOffsetStart(countPretextTokens + 1);
                    mention.setTokenOffsetEnd(countPretextTokens
                            + countMatchedTokens);
                    mention.setCharOffsetEnd(charOffsetEnd);
                    mention.setCharOffsetStart(charOffsetBegin);
                    mention.setMentionText(matchedText);
                    mention.setMentionedConcept(isole.getValue());
                    Node n = db.findNode(DynamicLabel.label("Resource"), "uri",
                            mention.getMentionedConcept());
                    if (n != null)
                        result.add(mention);

                }
            }
        }

        return result;

    }

    public static void main(String[] args)
    {
        GraphDatabaseService db = new GraphDatabaseFactory()
                .newEmbeddedDatabase(new File(
                        "./graph_noduplicates_costs_noliterals_nostopUris.db"));

        try (Transaction tx = db.beginTx())
        {

            SpreadingActivationComplexity sac = new SpreadingActivationComplexity(
                    db);

            // List<Mention> mentions = new ArrayList<>();
            KanopyDocumentJsonReader reader = new KanopyDocumentJsonReader();
            try
            {
                KanopyDocument doc = reader
                        .readDocument("/media/er/Data/simplification/java_proj/src/micro_b003.txt.kanopy.json");

                doc.docPath = "/media/er/Data/simplification/java_proj/src/micro_b003.txt.kanopy.json";
                List<Mention> mentions = sac.extractMentions(doc);
                Comparator<Mention> cmp = new Comparator<Mention>()
                {

                    @Override
                    public int compare(Mention o1, Mention o2)
                    {
                        if (o1.getCharOffsetStart() < o2.getCharOffsetStart())
                            return -1;
                        else if (o1.getCharOffsetStart() == o2
                                .getCharOffsetStart())
                        {
                            if (o1.getCharOffsetEnd() < o2.getCharOffsetEnd())
                                return -1;
                            else if (o1.getCharOffsetEnd() > o2
                                    .getCharOffsetEnd())
                                return 1;
                            else
                                return 0;
                        } else
                            return 1;
                    }
                };

                mentions.sort(cmp);

                /*
                 * Mention m1 = new Mention(); m1.setMentionedConcept(
                 * "http://dbpedia.org/resource/Fatigue_(medical)");
                 * m1.setTokenOffsetStart(11); m1.setTokenOffsetEnd(12);
                 * 
                 * 
                 * 
                 * Mention m2 = new Mention(); m2.setMentionedConcept(
                 * "http://dbpedia.org/resource/Attention_deficit_hyperactivity_disorder"
                 * ); m2.setTokenOffsetStart(31); m2.setTokenOffsetEnd(32);
                 * 
                 * 
                 * Mention m3 = new Mention();
                 * m3.setMentionedConcept("http://dbpedia.org/resource/Attention"
                 * ); m3.setTokenOffsetStart(33); m3.setTokenOffsetEnd(34);
                 * 
                 * Mention m7 = new Mention();
                 * m7.setMentionedConcept("http://dbpedia.org/resource/Attention"
                 * ); m7.setTokenOffsetStart(43); m7.setTokenOffsetEnd(44);
                 * 
                 * 
                 * Mention m4 = new Mention();
                 * m4.setMentionedConcept("http://dbpedia.org/resource/School");
                 * m4.setTokenOffsetStart(53); m4.setTokenOffsetEnd(54);
                 * 
                 * Mention m8 = new Mention(); m8.setMentionedConcept(
                 * "http://dbpedia.org/resource/United_States");
                 * m8.setTokenOffsetStart(57); m8.setTokenOffsetEnd(59);
                 * 
                 * 
                 * 
                 * Mention m5 = new Mention();
                 * m5.setMentionedConcept("http://dbpedia.org/resource/Teacher"
                 * ); m5.setTokenOffsetStart(63); m5.setTokenOffsetEnd(64);
                 * 
                 * 
                 * Mention m6 = new Mention();
                 * m6.setMentionedConcept("http://dbpedia.org/resource/Education"
                 * ); m6.setTokenOffsetStart(73); m6.setTokenOffsetEnd(74);
                 * 
                 * 
                 * 
                 * // mentions.add(m8); mentions.add(m1); mentions.add(m2);
                 * mentions.add(m3); // mentions.add(m7); // mentions.add(m4);
                 * 
                 * mentions.add(m5); mentions.add(m6);
                 */

                Map<Mention, Double> res = sac
                        .computeActivationAtEncounter(mentions);
                for (Entry<Mention, Double> re : res.entrySet())
                {
                    System.out.println(re.getKey().getMentionedConcept() + "->"
                            + re.getValue());
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

        } catch (Exception x)
        {
            x.printStackTrace();
        }
    }
}
