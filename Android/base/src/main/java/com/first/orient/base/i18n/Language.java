package com.first.orient.base.i18n;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class Language {

    private static String TAG = "Language";

    private Context mContext;

    public static class Public {
        public static String Language = "en";
    }

    private static Language instance;


    public static final void initAppLanguage(Context context) {
        if (context == null) {
            throw new NullPointerException("Language init Context Nulll......");
        }
        if (instance == null) {
            synchronized (Language.class) {
                if (instance == null) {
                    instance = new Language(context);
                }
            }
        }
    }

    private Language (Context context) {
        this.mContext = context;
        Public.Language = loadConfigLanguage();
        Log.d(TAG, "Public Language:" + Public.Language);
    }

    private String loadConfigLanguage() {
        SharedPreferences preferences = mContext.getSharedPreferences("language", Context.MODE_PRIVATE);
        String selectedLanguage = preferences.getString("language", "en");
        Log.d(TAG, "Load Language:" + selectedLanguage);
        return selectedLanguage;
    }

    public static void changeLanguage(String language) {
        if (TextUtils.isEmpty(language)) return;
        if (language.equals(Public.Language)) return;
        if (instance == null) {
            throw new NullPointerException("Language is Not Init ......");
        }
        Public.Language = language;
        SharedPreferences.Editor editor = instance.mContext.getSharedPreferences("language", Context.MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.commit();
        Log.d(TAG, "Changed Language:" + instance.loadConfigLanguage());
    }
}
