package ru.dmdevelopment.Calconvertor.core.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by blitz on 14.05.14.
 */
public class App extends Application {

    private static Context mContext;
    private static Activity mActivity;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context сontext) {
        mContext = сontext;
    }

    public static Activity getActivity() {
        return mActivity;
    }

    public static void setActivity(Activity activity) {
        mActivity = activity;
    }
}