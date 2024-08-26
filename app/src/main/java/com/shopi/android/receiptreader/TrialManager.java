package com.shopi.android.receiptreader;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.runner.manipulation.Ordering;

public class TrialManager {
    private static final String PREFS_NAME = "TrialPRefs";
    private static final String KEY_INSTALL_DATE = "InstallDate";
    private static final long TRIAL_PERIOD_MS = 6*7*24*60*60*1000L;
    private SharedPreferences prefs;
    public TrialManager(Context context){
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isTrialExpired(){
        long installDate = prefs.getLong(KEY_INSTALL_DATE, -1);
        if(installDate == -1){
            installDate = System.currentTimeMillis();
            prefs.edit().putLong(KEY_INSTALL_DATE, installDate).apply();
        }
        return System.currentTimeMillis() > installDate + TRIAL_PERIOD_MS;
    }
    public void resetTrial(){
        prefs.edit().remove(KEY_INSTALL_DATE).apply();
    }
}
