package dws.uni.mannheim.semantic_complexity;

import io.swagger.annotations.ApiModelProperty;

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
{ "linkerThreshold", "graphDecay", "firingThreshold", "tokenDecay",
        "sentenceDecay", "paragraphDecay", "usePopularity", "useExclusivity",
        "phiTo1", "text1", "text2" })
public class TextComplexityComparisonRequestObject
{

    @JsonProperty("linkerThreshold")
    @ApiModelProperty(example = "0.35")
    private Double linkerThreshold = 0.35;

    @JsonProperty("graphDecay")
    @ApiModelProperty(example = "0.25")
    private Double graphDecay = 0.25;

    @JsonProperty("firingThreshold")
    @ApiModelProperty(example = "0.01")
    private Double firingThreshold = 0.01;

    @JsonProperty("tokenDecay")
    @ApiModelProperty(example = "0.85")
    private Double tokenDecay = 0.85;

    @JsonProperty("sentenceDecay")
    @ApiModelProperty(example = "0.7")
    private Double sentenceDecay = 0.7;

    @JsonProperty("paragraphDecay")
    @ApiModelProperty(example = "0.5")
    private Double paragraphDecay = 0.5;

    @JsonProperty("usePopularity")
    @ApiModelProperty(example = "true")
    private Boolean usePopularity = true;

    @JsonProperty("useExclusivity")
    @ApiModelProperty(example = "true")
    private Boolean useExclusivity = true;

    @JsonProperty("phiTo1")
    @ApiModelProperty(example = "true")
    private Boolean phiTo1 = true;

    @JsonProperty("text1")
    @ApiModelProperty(example = "Some towns on the Eyre Highway in the south-east corner of Western Australia, between the South Australian border almost as far as Caiguna, do not follow official Western Australian time. \nEugowra is said to be named after the Indigenous Australian word meaning \"The place where the sand washes down the hill \"")
    private String text1;

    @JsonProperty("text2")
    @ApiModelProperty(example = "Some towns in the south-east corner of Australia, use central western standard time. \nThe word \"Eugowra\" comes from an aboriginal word meaning \"the place where the sand washes down the hill\".")
    private String text2;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("linkerThreshold")
    public Double getLinkerThreshold()
    {
        return linkerThreshold;
    }

    @JsonProperty("linkerThreshold")
    public void setLinkerThreshold(Double linkerThreshold)
    {
        this.linkerThreshold = linkerThreshold;
    }

    @JsonProperty("graphDecay")
    public Double getGraphDecay()
    {
        return graphDecay;
    }

    @JsonProperty("graphDecay")
    public void setGraphDecay(Double graphDecay)
    {
        this.graphDecay = graphDecay;
    }

    @JsonProperty("firingThreshold")
    public Double getFiringThreshold()
    {
        return firingThreshold;
    }

    @JsonProperty("firingThreshold")
    public void setFiringThreshold(Double firingThreshold)
    {
        this.firingThreshold = firingThreshold;
    }

    @JsonProperty("tokenDecay")
    public Double getTokenDecay()
    {
        return tokenDecay;
    }

    @JsonProperty("tokenDecay")
    public void setTokenDecay(Double tokenDecay)
    {
        this.tokenDecay = tokenDecay;
    }

    @JsonProperty("sentenceDecay")
    public Double getSentenceDecay()
    {
        return sentenceDecay;
    }

    @JsonProperty("sentenceDecay")
    public void setSentenceDecay(Double sentenceDecay)
    {
        this.sentenceDecay = sentenceDecay;
    }

    @JsonProperty("paragraphDecay")
    public Double getParagraphDecay()
    {
        return paragraphDecay;
    }

    @JsonProperty("paragraphDecay")
    public void setParagraphDecay(Double paragraphDecay)
    {
        this.paragraphDecay = paragraphDecay;
    }

    @JsonProperty("usePopularity")
    public Boolean getUsePopularity()
    {
        return usePopularity;
    }

    @JsonProperty("usePopularity")
    public void setUsePopularity(Boolean usePopularity)
    {
        this.usePopularity = usePopularity;
    }

    @JsonProperty("useExclusivity")
    public Boolean getUseExclusivity()
    {
        return useExclusivity;
    }

    @JsonProperty("useExclusivity")
    public void setUseExclusivity(Boolean useExclusivity)
    {
        this.useExclusivity = useExclusivity;
    }

    @JsonProperty("phiTo1")
    public Boolean getPhiTo1()
    {
        return phiTo1;
    }

    @JsonProperty("phiTo1")
    public void setPhiTo1(Boolean phiTo1)
    {
        this.phiTo1 = phiTo1;
    }
    
    @JsonProperty("text1")
    public String getText1()
    {
        return text1;
    }

    @JsonProperty("text1")
    public void setText1(String text1)
    {
        this.text1 = text1;
    }

    @JsonProperty("text2")
    public String getText2()
    {
        return text2;
    }

    @JsonProperty("text2")
    public void setText2(String text2)
    {
        this.text2 = text2;
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