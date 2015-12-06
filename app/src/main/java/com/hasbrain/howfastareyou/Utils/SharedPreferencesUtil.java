package com.hasbrain.howfastareyou.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hasbrain.howfastareyou.MainApplication;

/**
 * Created by Khang on 05/12/2015.
 */
public class SharedPreferencesUtil {
    private static final String FILE_NAME = "SharedPreferencesUtil";


    private static SharedPreferences mPreferences;

    public static SharedPreferences getInstance() {
        if (mPreferences == null)
            mPreferences = MainApplication.sharedContext
                    .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return mPreferences;
    }
}
