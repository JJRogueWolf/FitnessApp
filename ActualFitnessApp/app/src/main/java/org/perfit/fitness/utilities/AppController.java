package org.perfit.fitness.utilities;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by jerin on 9/10/16.
 */

public class AppController extends Application {
    private static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;
    private RequestQueue mRequestQueue;

    public static final boolean DEBUG_MODE = true;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
        mInstance = this;

        //    public static final List<Pojoclass> list = new ArrayList<>();
        Context context = getApplicationContext();
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

}
