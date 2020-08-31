package org.perfit.fitness.utilities;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class APIManager {

    public static final String BaseUrl = !AppController.DEBUG_MODE ? "https://perfit.today/fitness_tracker_dev/" : "https://perfit.today/fitness_tracker/";

    public static final String GOOGLE_LOGIN = BaseUrl + "login_google.php";
    public static final String FACEBOOK_LOGIN = BaseUrl + "login_facebook.php";
    public static final String EMAIL_LOGIN = BaseUrl + "login_email.php";
    public static final String EMAIL_SIGNUP = BaseUrl + "signup_email.php";
    public static final String PUSH_SESSION = BaseUrl + "add_score.php";
    public static final String UPDATE_USER = BaseUrl + "update_user_details.php";
    public static final String LOAD_VIDEOS = BaseUrl + "load_video_urls.php";
    public static final String LOAD_RECOMMENDATION_VIDEOS = BaseUrl + "load_recommended_videos.php";
    public static final String DATA_COLLECTION = BaseUrl + "save_debug_logs.php";
    public static final String UPLOAD_IMAGES = BaseUrl + "upload_debug_images.php";

    private static JSONObject jsonParameters;
    private static JSONObject jsonResponse;

    public static void CallAPI(String url, JSONObject inputParameters, final VolleyCallback volleyCallback) {
        jsonParameters = inputParameters;
//        final ProgressDialog pDialog = new ProgressDialog(context);
//        pDialog.setMessage(loadingMsg);
//        pDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonResponse = new JSONObject(response);
//                            pDialog.dismiss();
                            volleyCallback.onSuccessResponse(jsonResponse);
                        }
                        catch (Exception error) {
//                            pDialog.dismiss();
                            Log.i("Error", error.toString());
                            volleyCallback.onFailureResponse(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        pDialog.dismiss();
                        Log.i("Error", error.toString());
                        volleyCallback.onFailureResponse(null);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                try
                {
                    Iterator<?> keys = jsonParameters.keys();
                    while (keys.hasNext())
                    {
                        String key = (String) keys.next();
                        String value = jsonParameters.getString(key);
                        params.put(key, value);
                    }
                }
                catch (Exception ex)
                {
                    ex.toString();
                }
                return params;
            }
        };
        int socketTimeout = 300000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);


        AppController.getInstance().addToRequestQueue(request, url);
    }

}
