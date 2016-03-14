package example.makatz.gpstracking1;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class OM2MConnector {
    public static final int APPLICATION = 1;
    public static final int CONTAINER = 2;
    public static final int CONTENT_INSTANCE = 3;

    public static final String DESCRIPTOR_CONTAINER = "DESCRIPTOR";
    public static final String DATA_CONTAINER = "DATA";

    private String host;
    private int port;
    private String sclID;
    private String appName;
    private Context context;

    private static AsyncHttpClient client = new AsyncHttpClient();


    public OM2MConnector(Context context, String host, int port, String sclID, String appName) {
        this.host = host;
        this.port = port;
        this.sclID = sclID;
        this.appName = appName;
        this.context = context;
    }

    public void init() {
        client.setBasicAuth("admin", "admin");
        createApp();
        createContainer(DESCRIPTOR_CONTAINER);
        createContainer(DATA_CONTAINER);
    }

    public void setBasicAuth(String username, String password) {
        client.setBasicAuth(username, password);
    }

    public void createApp() {
        // Send POST request to this URL:
        // http://<host>:<port>/om2m/<sclID>/applications

        String reqBody = "<om2m:application xmlns:om2m='http://uri.etsi.org/m2m' appId='" + appName + "' >" +
            "<om2m:searchStrings>" +
                "<om2m:searchString>Type/android</om2m:searchString>" +
                "<om2m:searchString>Category/gps</om2m:searchString>" +
                "<om2m:searchString>Location/anywhere</om2m:searchString>" +
            "</om2m:searchStrings>" +
        "</om2m:application>";

        makePOSTRequest(makeURL(APPLICATION), reqBody);
    }



//    public static String getAuthHeader(String username, String password) {
//        String str = username + ":" + password;
//        byte[] authBytes = str.getBytes(Charset.defaultCharset());
//        return "Basic " + Base64.encodeToString(authBytes, Base64.DEFAULT);
//    }

    public void createContainer(String type) {
        // Send POST request to this URL:
        // http://<host>:<port>/om2m/<sclID>/applications/<appName>/containers
        // with body of the following content:
        // <om2m:container xmlns:om2m='http://uri.etsi.org/m2m' om2m:id='DATA'></om2m:container>
        type = type.toUpperCase();
        String reqBody = "<om2m:container xmlns:om2m='http://uri.etsi.org/m2m' om2m:id='" + type + "'></om2m:container>";
        makePOSTRequest(makeURL(CONTAINER), reqBody);
    }

    public void createInstance(String type) {
        type = type.toUpperCase();

    }

    private String makeURL(int type) {
        if (type == CONTAINER) {
            return "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID + "/applications/" + this.appName + "/containers";
        } else if (type == APPLICATION) {
            return "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID + "/applications";
        } else {
            return "";
        }

    }

    public void makePOSTRequest(String url, String reqBody) {
        HttpEntity entity;

        try {
            entity = new StringEntity(reqBody);
        } catch (IllegalArgumentException e) {
            Log.d("HTTP", "StringEntity: IllegalArgumentException");
            return;
        } catch (UnsupportedEncodingException e) {
            Log.d("HTTP", "StringEntity: UnsupportedEncodingException");
            return;
        }

        String contentType = "string/xml;UTF-8";
        client.post(context, url, entity, contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("HTTP", "onSuccess: " + responseBody.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("HTTP", "onFailure: " + responseBody.toString());
            }
        });
    }

}