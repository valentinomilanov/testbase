package com.project.test.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.project.test.constants.ServerURL;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.hssf.converter.ExcelToFoConverter;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.json.Json;

public class ProjectInternalApi extends BaseApi {

	private String cookie;

    public ProjectInternalApi(UsersEnum user) {
        login(user.getUsername(), user.getPassword());
    }

    public ProjectInternalApi(String userName, String password) {
        login(userName, password);
    }

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

    public void logout() {
        executeCall(Method.POST, ServerURL.QA_LOGIN_URL + ApiCall.LOGOUT.getUrl(), null, null);
    }
}
