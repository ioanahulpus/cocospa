package dws.uni.mannheim.relatedness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import dws.uni.mannheim.semantic_complexity.KanopyDocument;


public class ELDocJson
{

    public String text;
    public String confidence;
    public String support;
    public String types;
    public String spargl;
    public String policy;
    public List<ResourceItem> Resources;
    public KanopyDocument toKanopy()
    {
        KanopyDocument doc = new KanopyDocument();
        doc.topicWordConcept = new HashMap<String, Map<String, String>>();
        doc.docPath = "";
        Map<String, String> top = new HashMap<String, String>();
        if (Resources != null)
        {
            for (ResourceItem it : Resources)
            {
                top.put(it.surfaceForm, it.URI);
            }
        }
        doc.topicWordConcept.put("0", top);

        Map<String, String> iso = new HashMap<String, String>();

        doc.setIsolatedConcepts(iso);
        
        return doc;
    }

    /*
{
  "@text": "Health insurance companies should not cover treatment in complementary medicine unless the promised effect and its medical benefit have been concretely proven. Yet this very proof is lacking in most cases. Patients do often report relief of their complaints after such treatments. But as long as it is unclear as to how this works, the funds should rather be spent on therapies where one knows with certainty.",
  "@confidence": "0.5",
  "@support": "0",
  "@types": "",
  "@sparql": "",
  "@policy": "whitelist",
  "Resources": [
    {
      "@URI": "http://dbpedia.org/resource/Health_insurance",
      "@support": "2481",
      "@types": "",
      "@surfaceForm": "Health insurance",
      "@offset": "0",
      "@similarityScore": "0.9987339927561958",
      "@percentageOfSecondRank": "0.00126761204984631"
    },
    {
      "@URI": "http://dbpedia.org/resource/Alternative_medicine",
      "@support": "2520",
      "@types": "",
      "@surfaceForm": "complementary medicine",
      "@offset": "57",
      "@similarityScore": "1.0",
      "@percentageOfSecondRank": "0.0"
    }
  ]
}
     */
    
}
