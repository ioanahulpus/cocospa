package dws.uni.mannheim.semantic_complexity;

import io.swagger.annotations.ApiParam;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.graphdb.Transaction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dws.uni.mannheim.semantic_complexity.spreading_activation.DecaySetting;
import dws.uni.mannheim.semantic_complexity.spreading_activation.Mode;


@RestController
public class TextComplexityAssesmentController
{
    @RequestMapping(method = RequestMethod.POST, value = "/complexity")
    @ResponseBody
    TextComplexityAssesmentResponseObject complexityAssesment(
            @RequestBody TextComplexityAssesmentRequestObject complexityRequestObject)
    {
        
        TextComplexityAssesmentResponseObject response = new TextComplexityAssesmentResponseObject();
        response.setComplexityScore(-1.0);

        Set<Mode> modes = new HashSet<>();
        modes.add(new Mode(complexityRequestObject.getGraphDecay(),
                           complexityRequestObject.getFiringThreshold(), 
                               new DecaySetting(complexityRequestObject.getTokenDecay(),
                                                complexityRequestObject.getSentenceDecay(),
                                                complexityRequestObject.getParagraphDecay()),
                           complexityRequestObject.getUsePopularity(),
                           complexityRequestObject.getUseExclusivity()));
        Map<String, Mode> modesIndex = new HashMap<>();
        for (Mode m : modes)
        {
            modesIndex.put(m.getModeName(), m);
        }

        double complexityScore = TextComplexityAssesment.assess(modesIndex,
                complexityRequestObject.getText(),
                complexityRequestObject.getPhiTo1(),
                complexityRequestObject.getLinkerThreshold(),
                Application.dbspotlightURL);
        response.setComplexityScore(complexityScore);
        
        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/compare")
    @ResponseBody
    TextComplexityComparisonResponseObject complexityAssesmentComparison(
            @RequestBody TextComplexityComparisonRequestObject comparisonRequestObject)
    {
        
        TextComplexityComparisonResponseObject response = new TextComplexityComparisonResponseObject();
        response.setText1ComplexityScore(-1.0);
        response.setText2ComplexityScore(-1.0);
        
        Set<Mode> modes = new HashSet<>();
        modes.add(new Mode(comparisonRequestObject.getGraphDecay(),
                           comparisonRequestObject.getFiringThreshold(), 
                               new DecaySetting(comparisonRequestObject.getTokenDecay(),
                                                comparisonRequestObject.getSentenceDecay(),
                                                comparisonRequestObject.getParagraphDecay()),
                           comparisonRequestObject.getUsePopularity(),
                           comparisonRequestObject.getUseExclusivity()));
        Map<String, Mode> modesIndex = new HashMap<>();
        for (Mode m : modes)
        {
            modesIndex.put(m.getModeName(), m);
        }

        double complexityScoreT1 = TextComplexityAssesment.assess(modesIndex,
                comparisonRequestObject.getText1(),
                comparisonRequestObject.getPhiTo1(),
                comparisonRequestObject.getLinkerThreshold(),
                Application.dbspotlightURL);
        double complexityScoreT2 = TextComplexityAssesment.assess(modesIndex,
                comparisonRequestObject.getText2(),
                comparisonRequestObject.getPhiTo1(),
                comparisonRequestObject.getLinkerThreshold(),
                Application.dbspotlightURL);
        response.setText1ComplexityScore(complexityScoreT1);
        response.setText2ComplexityScore(complexityScoreT2);
        if (response.getText1ComplexityScore() == -1 && response.getText2ComplexityScore() == -1)
        {
            response.setComparison("Could not find relevant linked entities or the activations didn't reach the threshold. Can't compare the complexity of text1 and text2.");
        }
        else if (response.getText1ComplexityScore() == -1)
        {
            response.setComparison("Could not find relevant linked entities or the activations didn't reach the threshold. Can't compare the complexity of text1 and text2.");
        }
        else if (response.getText2ComplexityScore() == -1)
        {
            response.setComparison("Could not find relevant linked entities or the activations didn't reach the threshold. Can't compare the complexity of text1 and text2.");
        }
        else if (Math.abs(complexityScoreT1 - complexityScoreT2) <= 0.001)
        {
            response.setComparison("text1 is similar in complexity to text2");
        }
        else if(complexityScoreT1 - complexityScoreT2 < 0)
        {
            response.setComparison("text2 is more complex than text1");
        }
        else
        {
            response.setComparison("text1 is more complex than text2");
        }
        return response;
    }


}