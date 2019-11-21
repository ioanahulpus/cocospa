package dws.uni.mannheim.semantic_complexity;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "text1ComplexityScore", "text2ComplexityScore", "comparison" })
public class TextComplexityComparisonResponseObject
{

    @JsonProperty("text1ComplexityScore")
    private Double text1ComplexityScore;
    @JsonProperty("text2ComplexityScore")
    private Double text2ComplexityScore;
    @JsonProperty("comparison")
    private String comparison;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("text1ComplexityScore")
    public Double getText1ComplexityScore()
    {
        return text1ComplexityScore;
    }

    @JsonProperty("text1ComplexityScore")
    public void setText1ComplexityScore(Double text1ComplexityScore)
    {
        this.text1ComplexityScore = text1ComplexityScore;
    }

    @JsonProperty("text2ComplexityScore")
    public Double getText2ComplexityScore()
    {
        return text2ComplexityScore;
    }

    @JsonProperty("text2ComplexityScore")
    public void setText2ComplexityScore(Double text2ComplexityScore)
    {
        this.text2ComplexityScore = text2ComplexityScore;
    }

    @JsonProperty("comparison")
    public String getComparison()
    {
        return comparison;
    }

    @JsonProperty("comparison")
    public void setComparison(String comparison)
    {
        this.comparison = comparison;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties()
    {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value)
    {
        this.additionalProperties.put(name, value);
    }

}