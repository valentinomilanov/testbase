package com.project.test.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Valentino Milanov
 * 
 * Base class with all methods and enums for executing API calls
 *
 */
public class BaseApi {

public static final Logger logger = LoggerFactory.getLogger(BaseApi.class);
    
    protected HttpClient httpClient = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy()).build();
    protected Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(json.getAsString());
        }
    }).registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseLocalDateTime(json.getAsString());
        }
    }).create();
    
    /**
     * Method for executing API call without expeted COde
     * @param method
     * @param apiCall
     * @param entity
     * @param haeders
     * @return
     */
    protected ClosedResponse executeCall(Method method, String apiCall, HttpEntity entity, List<Header> haeders) {
        return executeCall(method, apiCall, entity, haeders, method.getExpectedReturnValue());
    }
    
    /**
     * Method for executing API call
     * @param method
     * @param apiCall
     * @param entity
     * @param headers
     * @param expectedCode
     * @return
     */
    protected ClosedResponse executeCall(Method method, String apiCall, HttpEntity entity, List<Header> headers, int expectedCode) {
        HttpRequestBase request = getRequest(method);
        request.setURI(URI.create(apiCall));
        
        if (entity != null) {
            ((HttpEntityEnclosingRequest) request).setEntity(entity);
        }
        for (Header header: headers) {
            request.addHeader(header);
        }
        HttpResponse response = null;
        try {
            System.out.println(request);
            response = httpClient.execute(request);
            String message = "";
            if (response.getEntity() != null) {
                BufferedReader input = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                for (String line = input.readLine(); line!=null; line = input.readLine()) {
                    message += line;
                }
                //logger.trace("return message: " + message);
            }
            int returnCode = response.getStatusLine().getStatusCode(); 
            if (returnCode != expectedCode) {
                logger.error(message);
                throw new Error("The return code is not the expected, expected: " + expectedCode + ", return code: " + returnCode + "\nExecuted call was: " + apiCall);
            }
            return new ClosedResponse(response, message);
        } catch (Exception|Error e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Error("An expcetion thrown while executing the API call");
        } finally {
            request.releaseConnection();
        }
        
    }
    
    /**
     * 
     * @author Valentino Milanov
     * 
     * Handling received response from API call
     */
    public static class ClosedResponse {
        
        private HttpResponse response;
        private String message;
        
        public ClosedResponse(HttpResponse response, String message) {
            super();
            this.response = response;
            this.message = message;
        }
        
        public HttpResponse getResponse() {
            return response;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return "ClosedResponse [response=" + response + ", message=" + message + "]";
        }
        
    }
    
    private HttpRequestBase getRequest(Method method) {
        switch (method) {
        case DELETE:
            return new HttpDelete();
        case GET:
            return new HttpGet();
        case POST:
            return new HttpPost();
        case PUT:
            return new HttpPut();
        default:
            throw new Error("The method is not suppoerted: " + method);
        }
    }
    
    //FIXME Add all api calls you need, here are some exaples from other projects
    /**
     * 
     * @author Valentino milanov
     * 
     * Enum with all API calls that are used on the project
     */
    public enum ApiCall {
        LOGIN("/nuxeo/site/automation-ext/login"),
        LOGOUT("/nuxeo/site/automation-ext/logout"),

        GET_USERS("/nuxeo/site/automation-ext/Mobile.GetUsers"),
        UPDATE_USER("/nuxeo/site/automation-ext/Mobile.UpdateUser"),
        DELETE_USER("/nuxeo/site/automation-ext/User.Delete"),
        
        SN_DELETE_PERSON("/network/persons/%d"),
        
        DELETE_PORTAL_TEMPLATE("/network/studies/%s/portals/%s"),
        
        CREATE_WORKFLOW("/nuxeo/site/project/workflow?start=true"),
        
        CREATE_BOOKMARK("/nuxeo/site/automation-ext/Mobile.CreateBookmark"),
        
        WORKFLOW_SUMMARY("/nuxeo/site/project/workflow/summary?archiveId=%s&excludeActivities=true&includeHistory=true"),
        
        UPDATE_MODULE("/nuxeo/site/automation-ext/Mobile.UpdateModule"),
        
        CREATE_STUDY("/nuxeo/site/automation-ext/Mobile.CreateStudy"),
        
        GET_WORKFLOWS("/nuxeo/site/dms/workflow?excludeActivities=true&historyOnly=false&includeHistory=true&processType=REVIEW_NETWORK_PROFILE,QUALIFY_CONTACT_FOR_SIGNING"),
        
        ;
        
        private String url;
        
        private ApiCall(String url) {
            this.url = url;
        }
        
        public String getUrl() {
            return url;
        }
        
    }
    
 
    /**
     * 
     * @author Valentino Milanov
     *
     * This enum is for HTTP request methods that are used in the project
     */
    public enum Method {
        
        GET("GET", 200),
        POST("POST", 200),
        PUT("PUT", 200),
        DELETE("DELETE", 200),
        ;
        
        private String methodName;
        private int expectedReturnValue;
        
        private Method(String methodName, int expectedReturnValue) {
            this.methodName = methodName;
            this.expectedReturnValue = expectedReturnValue;
        }
        
        public String getMethodName() {
            return methodName;
        }
        
        public int getExpectedReturnValue() {
            return expectedReturnValue;
        }
        
    }

    /**
     * This method is showing the API call that was executed
     * @param apiCall	API call URL
     * @param entity	HTTP request or response entity, consisting of headers and body.
     */
    protected void apiLog(ApiCall apiCall, HttpEntity entity) {
        String responseString = null;
        try {
            responseString = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception er) {

        }
        logger.trace(String.format("Executed API call with URL: \"%s\" and with entity: \"%s\"", apiCall.getUrl(), responseString));
    }
}
