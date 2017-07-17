package com.sow.inglesparaviagem.classes;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by renato.rezende on 16/07/2017.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static void hideSoftKeyboard(Context context, View view) {
        Log.w(TAG, "ApkTools.hideSoftKeyboard()");
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}
