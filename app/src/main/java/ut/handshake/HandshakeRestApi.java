package ut.handshake;

/**
 * Created by Aurelius on 12/13/14.
 */
import com.loopj.android.http.*;

public class HandshakeRestApi {
    private static final String BASE_URL = "http://handshake-app.appspot.com/v1/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (params == null) {
            client.get(getAbsoluteUrl(url), responseHandler);
        } else {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
