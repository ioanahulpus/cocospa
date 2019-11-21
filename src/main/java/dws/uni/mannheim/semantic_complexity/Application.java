package dws.uni.mannheim.semantic_complexity;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.google.common.base.Predicates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import dws.uni.mannheim.semantic_complexity.Greeting;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackageClasses = {
    Greeting.class
})
public class Application {
   
    static GraphDatabaseService db;
    static HDT dbpedia;
    static Properties props; 
    static StanfordCoreNLP nlpPipeline;
    static JedisPool jedisPool;
    static boolean allOnes;
    
    
    public static void main(String[] args) throws IOException {

        System.out.println("Here we go...");
        for(String arg:args) {
            System.out.println(arg);
        }
        
        int arg_idx = 0;
        String neo = args[arg_idx++];
        String hdtWikipedia = args[arg_idx++];
        String redis = args[arg_idx++];
        int redis_port = Integer.valueOf(args[arg_idx++]);
        
        db = new GraphDatabaseFactory().newEmbeddedDatabase(new File(neo));

        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        nlpPipeline = new StanfordCoreNLP(props);

        
        jedisPool = new JedisPool(new JedisPoolConfig(),
                redis, redis_port, 3600000);
        
        dbpedia = HDTManager.mapIndexedHDT(hdtWikipedia, null);
        
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            .select()
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .build();
    }

    @Component
    @Primary
    public class CustomObjectMapper extends ObjectMapper {
        public CustomObjectMapper() {
            setSerializationInclusion(JsonInclude.Include.NON_NULL);
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            enable(SerializationFeature.INDENT_OUTPUT);
        }
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Spreading Activation Text Complexity Assesment API")
            .description("This API uses Spreading Activation and dbpedia linked entities to estimate the complexity of a given text.")
            .contact(new Contact("Sergiu Nisioi", "http://nlp.unibuc.ro/people/snisioi.html", "sergiu.nisioi@gmail.com"))
            //.contact(new Contact("Ioana Hulpus", "https://www.uni-mannheim.de/dws/people/researchers/postdoctoral-research-fellows/dr-ioana-hulpus/", ""))
            //.contact(new Contact("Sanja Štajner", "https://stajnersanja.wixsite.com/sanja", ""))
            .version("0.7")
            .build();
    }


}