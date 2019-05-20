package com.vuanhlevis.cropimage.base;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import androidx.multidex.MultiDexApplication;


public class Application extends MultiDexApplication {
    private static Application mInstance;
    public static final String TAG = Application.class.getSimpleName();
//    private RequestQueue mRequestQueue;

    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }
    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }
    public static synchronized Application getInstance() {
        return mInstance;
    }

//    public RequestQueue getRequestQueue() {
//        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//        }
//
//        return mRequestQueue;
//    }
//
//    public <T> void addToRequestQueue(Request<T> req, String tag) {
//        // set the default tag if tag is empty
//        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//        getRequestQueue().add(req);
//    }
//
//    public <T> void addToRequestQueue(Request<T> req) {
//        req.setTag(TAG);
//        getRequestQueue().add(req);
//    }
//
//    public void cancelPendingRequests(Object tag) {
//        if (mRequestQueue != null) {
//            mRequestQueue.cancelAll(tag);
//        }
//    }

    @Override
    public void onCreate() {
        if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }

        mInstance = this;
//        FirebaseApp.initializeApp(this);

        mContext = this;
        super.onCreate();
    }

}
