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
    public static final int DATA_CONTENT_INSTANCE = 3;
    public static final int DESCRIPTOR_CONTENT_INSTANCE = 4;
    public static final int METHOD_GET = 100;
    public static final int METHOD_POST = 200;


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
    }


    public void createApp() {
        // Send POST request to this URL:
        // http://<host>:<port>/om2m/<sclID>/applications

        makeGETRequest(makeURL(METHOD_GET, APPLICATION), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("GET: app", "success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String reqBody = "<om2m:application xmlns:om2m='http://uri.etsi.org/m2m' appId='" + appName + "' >" +
                        "<om2m:searchStrings>" +
                        "<om2m:searchString>Type/android</om2m:searchString>" +
                        "<om2m:searchString>Category/gps</om2m:searchString>" +
                        "<om2m:searchString>Location/anywhere</om2m:searchString>" +
                        "</om2m:searchStrings>" +
                        "</om2m:application>";

                makePOSTRequest(makeURL(METHOD_POST, APPLICATION), reqBody, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("POST: app", "success");
                        createDescriptorContainer();
                        createDataContainer();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("POST: app", "fail");
                    }
                });
            }
        });


    }



//    public static String getAuthHeader(String username, String password) {
//        String str = username + ":" + password;
//        byte[] authBytes = str.getBytes(Charset.defaultCharset());
//        return "Basic " + Base64.encodeToString(authBytes, Base64.DEFAULT);
//    }

    public void createDescriptorContainer() {
        // Send POST request to this URL:
        // http://<host>:<port>/om2m/<sclID>/applications/<appName>/containers
        // with body of the following content:
        // <om2m:container xmlns:om2m='http://uri.etsi.org/m2m' om2m:id='DATA'></om2m:container>

        makeGETRequest(makeURL(METHOD_GET, DESCRIPTOR_CONTENT_INSTANCE), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("GET: ", "description container success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String reqBody = "<om2m:container xmlns:om2m='http://uri.etsi.org/m2m' om2m:id='DESCRIPTOR'></om2m:container>";
                makePOSTRequest(makeURL(METHOD_POST, CONTAINER), reqBody, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("POST: ", "description container success");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("POST: ", "description container fail");
                    }
                });
            }
        });
    }

    public void createDataContainer() {
        makeGETRequest(makeURL(METHOD_GET, DATA_CONTENT_INSTANCE), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("GET: data container", "success " + String.valueOf(statusCode));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String reqBody = "<om2m:container xmlns:om2m='http://uri.etsi.org/m2m' om2m:id='DATA'></om2m:container>";
                makePOSTRequest(makeURL(METHOD_POST, CONTAINER), reqBody, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("POST: data container", "success");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("POST: data container", "fail");
                    }
                });
            }
        });
    }

    public void createDataInstance(TrackingData data) {
        String reqBody = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" +
                "<obj>" +
                "<str name='appId' val='" + this.appName + "'/>" +
                "<str name='category' val='gps'/>" +
                "<str name='userEmail' val='" + data.getUserEmail() + "'/>" +
                "<str name='timestamp' val='" + data.getTimestamp() + "'/>" +
                "<str name='address' val='" + data.getAddress() + "'/>" +
//                "<str name='latitude' val='" + data.getLatitude() + "'/>" +
//                "<str name='longitude' val='" + data.getLongitude() + "'/>" +
                "<str name='velocity' val='" + data.getSpeed() + "'/>" +
            "</obj>";

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        };
        handler.setCharset("UTF-8");

        makePOSTRequest(makeURL(METHOD_POST, DATA_CONTENT_INSTANCE), reqBody, handler);

    }

    private String makeURL(int method, int type) {
        String defaultURL = "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID;
        if (method == METHOD_GET) {
            if (type == APPLICATION) {
                return "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID + "/applications/" + this.appName;
            } else if (type == DESCRIPTOR_CONTENT_INSTANCE) {
                return "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID + "/applications" + this.appName + "/containers/DESCRIPTOR";
            } else if (type == DATA_CONTENT_INSTANCE) {
                return "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID + "/applications/" + this.appName + "/containers/DATA";
            } else {
                return defaultURL;
            }

        } else if (method == METHOD_POST)  {

            if (type == CONTAINER) {
                return "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID + "/applications/" + this.appName + "/containers";
            } else if (type == APPLICATION) {
                return "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID + "/applications";
            } else if (type == DATA_CONTENT_INSTANCE) {
                return "http://" + this.host + ":" + String.valueOf(this.port) + "/om2m/" + this.sclID + "/applications/" + this.appName + "/containers/DATA/contentInstances";
            } else {
                return defaultURL;
            }

        } else {
            return defaultURL;
        }

    }


    public void makePOSTRequest(String url, String reqBody, AsyncHttpResponseHandler handler) {
        HttpEntity entity;

        try {
            entity = new StringEntity(reqBody);
        } catch (IllegalArgumentException e) {
            Log.d("HTTP", "StringEntity: IllegalArgumentException");
            return;
        }
        catch (UnsupportedEncodingException e) {
            Log.d("HTTP", "StringEntity: UnsupportedEncodingException");
            return;
        }

        String contentType = "text/xml";

        client.post(context, url, entity, contentType, handler);
    }

    public void makeGETRequest(String url, AsyncHttpResponseHandler handler) {
        client.get(context, url, handler);
    }

}