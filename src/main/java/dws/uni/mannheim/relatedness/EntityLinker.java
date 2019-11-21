package dws.uni.mannheim.relatedness;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import dws.uni.mannheim.semantic_complexity.KanopyDocument;

public class EntityLinker
{
    
    public static KanopyDocument Link(String text, String url, double threshold) throws ParseException, IOException{

        HttpPost request = new HttpPost ("http://localhost:2222/rest/annotate");
        JsonArray spans = new JsonArray();
        
        KanopyDocument d = new KanopyDocument();
        
        //String tx = "Health insurance companies should not cover treatment in complementary medicine unless the promised effect and its medical benefit have been concretely proven. Yet this very proof is lacking in most cases. Patients do often report relief of their complaints after such treatments. But as long as it is unclear as to how this works, the funds should rather be spent on therapies where one knows with certainty.";
        String payload = "text=" + text ;
        

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("confidence", Double.toString(threshold)));
        request.setEntity(new UrlEncodedFormEntity(params));
        
        //StringEntity sentity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);
        //request.setEntity(sentity);

        //request.addHeader(HttpHeaders.CONTENT_TYPE,  "application/json");
        request.addHeader(HttpHeaders.ACCEPT, "application/json");
        

        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            
            // Get HttpResponse Status
            System.out.println(response.getProtocolVersion());              // HTTP/1.1
            System.out.println(response.getStatusLine().getStatusCode());   // 200
            System.out.println(response.getStatusLine().getReasonPhrase()); // OK
            System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
            
            
            HttpEntity entity = (HttpEntity) response.getEntity();
            
            //if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString((HttpEntity) entity);
                System.out.println(result);
                Gson gson = new GsonBuilder().create();
                ELDocJson doc = gson.fromJson(result, ELDocJson.class);
                d = doc.toKanopy();
            //}

        }
        return d;
    }

}
