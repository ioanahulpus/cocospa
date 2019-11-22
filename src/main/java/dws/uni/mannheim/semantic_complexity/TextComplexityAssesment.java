package dws.uni.mannheim.semantic_complexity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.Transaction;

import redis.clients.jedis.Jedis;
import dws.uni.mannheim.relatedness.EntityLinker;
import dws.uni.mannheim.semantic_complexity.spreading_activation.Mode;
import dws.uni.mannheim.semantic_complexity.spreading_activation.SAComplexityModes;

public class TextComplexityAssesment
{
    public static double assess(Map<String, Mode> modesIndex, String text,
            boolean phiTo1, double linkerThreshold)
    {
        
        try (Transaction tx = Application.db.beginTx())
        {

            KanopyDocument kdoc = EntityLinker.Link(text, "", linkerThreshold);

            SAComplexityModes samodes = new SAComplexityModes(Application.db);
            MentionExtractor extr = new MentionExtractor(Application.db,
                    Application.dbpedia, kdoc);
            //System.out.println("removing outliers");
            //extr.removeLikelyWrongLinks();
            //System.out.println("removed");

            FeaturedDocument fdoc = new FeaturedDocument(kdoc,
                    text, Application.nlpPipeline,
                    Application.db, Application.dbpedia, null, null, null);

            LinkedDocument ldoc = new LinkedDocument(fdoc,
                    text);

            Map<String, Double> activationsAtEncounter = new HashMap<>();
            Map<String, Double> activationAtEOS = new HashMap<>();
            Map<String, Double> activationAtEOP = new HashMap<>();
            Map<String, Double> activationAtEODoc = new HashMap<>();
            try (Jedis jedis = Application.jedisPool.getResource())
            {
                samodes.computeComplexityWithSpreadingActivationOncePerEntity(
                        ldoc, modesIndex, phiTo1,
                        activationsAtEncounter, activationAtEOS,
                        activationAtEOP, activationAtEODoc, jedis);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
            
            for (Entry<String, Mode> me : modesIndex.entrySet())
            {
                System.out.println(String.valueOf(activationsAtEncounter.get(me.getKey())));
                System.out.println(String.valueOf(activationAtEOS.get(me.getKey())));
                System.out.println(String.valueOf(activationAtEOP.get(me.getKey())));
                System.out.println(String.valueOf(activationAtEODoc.get(me.getKey())));
                double simplicityValue = activationAtEOS.get(me.getKey());
                if (simplicityValue == 0.0 || Double.isNaN(simplicityValue))
                    return -1;
                else
                {
                    return 10 / simplicityValue;
                }
            }
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        return -1;
    }
}
