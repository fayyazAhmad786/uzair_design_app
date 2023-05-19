package com.uzair.landusesurvey.send_request_to_server;

import android.net.ParseException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Http_Data_sender {


    public HttpResponse sendSimpleHTTPRequest(String url) throws TimeoutException, Exception {
        HttpResponse response = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 12000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 12000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        response = httpClient.execute(httpPost);
        return response;
    }

    public HttpResponse sendMultipartHttpRequestWithURLAndValue(String url, MultipartEntity mEntity) throws ConnectTimeoutException, Exception {

        HttpResponse response = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 12000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 12000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(mEntity);
        response = httpClient.execute(httpPost);
        return response;
    }

    public String convertHTTPResponseToStringUsingEntityUtils(HttpResponse response) {
        String strResponse = "";
        try {
            strResponse = EntityUtils.toString(response.getEntity());

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return strResponse;
    }
}
