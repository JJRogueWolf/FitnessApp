package org.perfit.fitness.utilities;


import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccessResponse(JSONObject result);
    void onFailureResponse(String result);
}
