package com.project.test.api;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.project.test.api.objects.WorkflowProcess;
import com.project.test.api.pojo.ProjectsStudyPojo;
import com.project.test.constants.ServerURL;
import com.project.test.enums.UsersEnum;

public class ProjectInternalApi extends BaseApi {

	private String cookie;

	/**
	 * Login with UsersEnum
	 * @param user
	 */
    public ProjectInternalApi(UsersEnum user) {
        login(user.getUsername(), user.getPassword());
    }

    /**
     * Looging with username and passwprd
     * @param userName
     * @param password
     */
    public ProjectInternalApi(String userName, String password) {
        login(userName, password);
    }

    /**
     * Loging method
     * @param userName
     * @param password
     */
    private void login(String userName, String password) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("user_name", userName));
        params.add(new BasicNameValuePair("user_password", password));
        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8"));
        ClosedResponse response = executeCall(Method.POST, ServerURL.QA_LOGIN_URL + ApiCall.LOGIN.getUrl(), entity, headers);
        cookie = response.getResponse().getHeaders("set-cookie")[0].getValue().split(";")[0];
    }

    @Override
    protected ClosedResponse executeCall(Method method, String apiCall, HttpEntity entity, List<Header> headers,
                                         int expectedCode) {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        headers.add(new BasicHeader("cookie", cookie));
        return super.executeCall(method, apiCall, entity, headers, expectedCode);
    }

    /**
     * Logout method
     */
    public void logout() {
        executeCall(Method.POST, ServerURL.QA_LOGIN_URL + ApiCall.LOGOUT.getUrl(), null, null);
    }
    

    /**
     * A couple examples of the API methods
     */
    public void enableModule(Modules module, Plan plan) {
        String entityStr;
        if (plan != null) {
            entityStr = String.format("{\"params\":{\"moduleId\":\"%s\",\"enabled\":true, \"plan\":\"%s\"}}", module.getModuleId(), plan.getName());
        } else {
            entityStr = String.format("{\"params\":{\"moduleId\":\"%s\",\"enabled\":true}}", module.getModuleId());
        }
        try {
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("content-type", "application/json+nxrequest"));
            HttpEntity entity = new StringEntity(entityStr);
            ClosedResponse response = executeCall(Method.POST, ServerURL.QA_LOGIN_URL + ApiCall.UPDATE_MODULE.getUrl(), entity, headers, 200);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Error("An exception thrown while executing the API call");
        }        
    }
    
    public void disableModule(Modules module) {
        String entityStr = String.format("{\"params\":{\"moduleId\":\"%s\",\"enabled\":false}}", module.getModuleId());
        try {
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("content-type", "application/json+nxrequest"));
            HttpEntity entity = new StringEntity(entityStr);
            ClosedResponse response = executeCall(Method.POST, ServerURL.QA_LOGIN_URL + ApiCall.UPDATE_MODULE.getUrl(), entity, headers, 200);
            apiLog(ApiCall.UPDATE_MODULE, entity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Error("An exception thrown while executing the API call");
        }
    }
    
    public void createBookmarks(String documentId, String subject, String message, String userUsername) {
        String entityStr = String.format("{\"input\":\"docs:%s\",\"params\":{\"subject\":\"%s\",\"message\":\"%s\",\"users\":\"%s\"}}",
                documentId,
                subject,
                message,
                userUsername
        );
        try {
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("content-type", "application/json+nxrequest"));
            HttpEntity entity = new StringEntity(entityStr);
            ClosedResponse response = executeCall(Method.POST, ServerURL.QA_LOGIN_URL + ApiCall.CREATE_BOOKMARK.getUrl(), entity, headers, 204);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Error("An exception thrown while executing the API call");
        }
    }
    
    public WorkflowProcess getSureNetworkTrustWorkflows() {
        try {
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("accept", "application/json, text/plain, */*"));
            ClosedResponse response = executeCall(Method.GET, ServerURL.QA_LOGIN_URL + ApiCall.GET_WORKFLOWS.getUrl(), null, headers, 200);
            WorkflowProcess workflows = gson.fromJson(response.getMessage(), WorkflowProcess.class);
            return workflows;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Error("An exception thrown while executing the API call");
        }
    }
    
    public int createCompleteAndVerifyContactWorkflow(String orgName,
    		String orgId,
    		String contactEmail,
    		String firstName,
    		String lastName,
    		String contactId) {
        try {
            String entryStr = String.format(
                    "{\"model\":{\"qualifyContactForSigning\":" +
                            "{\"initiator\":\"admin\"," +
                            "\"notifyUsers\":true," +
                            "\"notifyInitiator\":false," +
                            "\"message\":\"\"" +
                            ",\"completeAndVerifyContactAction\":{\"taskType\":\"COMPLETE_AND_VERIFY_CONTACT\"," +
                            "\"user\":\"%s\"," +
                            "\"dueDate\":\"%s\"," +
                            "\"reminderTimeCycle\":\"%s\"," +
                            "\"document\":{\"id\":\"%s\"," +
                            "\"name\":\"%s\"," +
                            "\"source\":\"NETWORK_CONTACT\"}," +
                            "\"externalPerson\":" +
                            "{\"id\":%s," +
                            "\"email\":\"%s\"," +
                            "\"firstName\":\"%s\"," +
                            "\"lastName\":\"%s\"," +
                            "\"organizations\":[{\"id\":%s,\"name\":\"%s\"}]," +
                            "\"qualificationRequirement\":\"TRUSTED_SIGNER_BASIC_TRUST\"}}," +
                            "\"subjectId\":\"%s\"," +
                            "\"person\":{\"id\":%s," +
                            "\"email\":\"%s\"," +
                            "\"firstName\":\"%s\"," +
                            "\"lastName\":\"%s\"," +
                            "\"organizations\":[{\"id\":%s,\"name\":\"%s\"}]," +
                            "\"qualificationRequirement\":\"TRUSTED_SIGNER_BASIC_TRUST\"}}}," +
                            "\"name\":\"%s\"," +
                            "\"processType\":\"QUALIFY_CONTACT_FOR_SIGNING\"}",
                    contactEmail,
                    DateTime.now().plusDays(1).toString(DateTimeFormat.forPattern("yyyy-MM-dd hh:mm")),
                    "0 0 0 1 1 ? 1970 * MON-FRI",
                    contactEmail,
                    firstName + " " + lastName,
                    contactId,
                    contactEmail,
                    firstName,
                    lastName,
                    orgId,
                    orgName,
                    contactEmail,
                    contactId,
                    contactEmail,
                    firstName,
                    lastName,
                    orgId,
                    orgName,
                    firstName + " " + lastName
            );

            HttpEntity entity = new StringEntity(entryStr);
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("content-type", "application/json; charset=UTF-8"));
            ClosedResponse response = executeCall(Method.POST, ServerURL.QA_LOGIN_URL + ApiCall.CREATE_WORKFLOW.getUrl(), entity, headers, 201);
            return Integer.parseInt(response.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Error("An exception thrown while executing the API call");
        }
    }
    
    public String createETMFProject(ProjectsStudyPojo projectsProjectPojo) {
        try {
            JsonArray archiveCreatedMessages = new JsonArray();
            projectsProjectPojo.getArchiveCreatedMessage()
                    .forEach(a -> archiveCreatedMessages.add(a));

            JsonArray models = new JsonArray();
            projectsProjectPojo.getModel()
                    .forEach(m -> models.add(m));

            JsonArray successHandlers = new JsonArray();
            projectsProjectPojo.getSuccessHandler()
                    .forEach(s -> successHandlers.add(s));

            JsonArray failureHandlers = new JsonArray();
            projectsProjectPojo.getFailureHandler()
                    .forEach(f -> failureHandlers.add(f));

            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("name", projectsProjectPojo.getName());
            requestJson.addProperty("id", projectsProjectPojo.getId());
            requestJson.addProperty("description", projectsProjectPojo.getDescription());
            requestJson.addProperty("startDate", projectsProjectPojo.getStartDate());
            requestJson.addProperty("duration", projectsProjectPojo.getDuration());
            requestJson.addProperty("targetSites", projectsProjectPojo.getTargetSites());
            requestJson.addProperty("duplicationPolicy", projectsProjectPojo.getDuplicationPolicy());
            requestJson.addProperty("contentModelName", projectsProjectPojo.getContentModelName());
            requestJson.addProperty("contentModelDate",projectsProjectPojo.getContentModelDate());
            requestJson.addProperty("contentModelVersion", projectsProjectPojo.getContentModelVersion());
            requestJson.add("model", models);
            requestJson.addProperty("buildInChain", projectsProjectPojo.getBuildInChain());
            requestJson.addProperty("studyBased", projectsProjectPojo.getStudyBased());
            requestJson.addProperty("sourceStudyId", projectsProjectPojo.getSourceStudyId());
            requestJson.addProperty("cloneDocumentNaming", projectsProjectPojo.getCloneDocumentNaming());
            requestJson.addProperty("cloneMetrics", projectsProjectPojo.getCloneMetrics());
            requestJson.addProperty("cloneDocumentMilestones", projectsProjectPojo.getCloneDocumentMilestones());
            requestJson.add("successHandler", successHandlers);
            requestJson.add("failureHandler", failureHandlers);
            requestJson.addProperty("archiveType", projectsProjectPojo.getArchiveType());
            requestJson.addProperty("archiveCreatedHeader", projectsProjectPojo.getArchiveCreatedHeader());
            requestJson.add("archiveCreatedMessage", archiveCreatedMessages);
            requestJson.addProperty("archiveListLink", projectsProjectPojo.getArchiveListLink());


            JsonObject parametersJson = new JsonObject();
            parametersJson.add("parameters", requestJson);
            JsonObject paramsJson = new JsonObject();
            paramsJson.add("params", parametersJson);
            Gson gsonP = new GsonBuilder().setPrettyPrinting().create();
            String json = gsonP.toJson(paramsJson);
            json = json.replaceAll("\\[\\]","{}");
            System.out.println(json);

            HttpEntity entity = new StringEntity(json);
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("content-type", "application/json"));
            System.out.println(entity.getContent());
            ClosedResponse response = executeCall(Method.POST, ServerURL.QA_LOGIN_URL + ApiCall.CREATE_STUDY.getUrl(), entity, headers);
            System.out.println(response);
            String id = response.getMessage().substring(
                    response.getMessage().indexOf( "\"archiveId\":"),
                    response.getMessage().indexOf( ",\"archiveName\"")
            );
           String parsedId = id.replace("\"archiveId\":", "");
           return parsedId.replaceAll("\"", "");


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Error("An exception thrown while executing the API call");
        }
    }
}
