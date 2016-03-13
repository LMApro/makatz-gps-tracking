package example.makatz.gpstracking1;

import android.util.Base64;
import com.loopj.android.http.*;
import java.nio.charset.Charset;

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

    public OM2MConnector(String host, int port, String sclID, String appName) {
        this.host = host;
        this.port = port;
        this.sclID = sclID;
        this.appName = appName;
    }

    public void init() {
        createApp();
        createContainer(DESCRIPTOR_CONTAINER);
        createContainer(DATA_CONTAINER);
    }

    public void createApp() {
        // Send POST request to this URL:
        // http://<host>:<port>/om2m/<sclID>/applications
        // with body of the following content:
        //
    }



    public static String getAuthHeader(String username, String password) {
        String str = username + ":" + password;
        byte[] authBytes = str.getBytes(Charset.defaultCharset());
        return "Basic " + Base64.encodeToString(authBytes, Base64.DEFAULT);
    }

    public void createContainer(String type) {
        // Send POST request to this URL:
        // http://<host>:<port>/om2m/<sclID>/applications/<appName>/containers
        // with body of the following content:
        // <om2m:container xmlns:om2m='http://uri.etsi.org/m2m' om2m:id='DATA'></om2m:container>
        type = type.toUpperCase();
        String container = "<om2m:container xmlns:om2m='http://uri.etsi.org/m2m' om2m:id='" + type + "'></om2m:container>";
        makePOSTRequest(makeURL(CONTAINER), getAuthHeader("admin", "admin"), container);
    }

    public void createInstance(String type) {
        type = type.toUpperCase();

    }

    private String makeURL(int type) {
        if (type == CONTAINER) {
            return "http://" + this.host + ":" + this.port + "/om2m/" + this.sclID + "/applications/" + this.appName + "/containers";
        } else if (type == APPLICATION) {
            return "http://" + this.host + ":" + this.port + "/om2m/" + this.sclID + "/applications";
        } else {
            return "";
        }

    }

    public String makePOSTRequest(String url, String authHeader, String body) {
        return "";
    }

    public String makeGETRequest(String url, String authHeader) {
        return "";
    }
}