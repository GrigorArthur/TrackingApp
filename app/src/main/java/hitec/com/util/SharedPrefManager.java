package hitec.com.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "OfficeBookPreference";
    private static final String TAG_FIRST_RUN = "tagfirstrun";
    private static final String TAG_TOKEN = "tagtoken";
    private static final String TAG_USERNAME = "tagusername";
    private static final String TAG_CUSTOMER_ID = "tagcustomerid";
    private static final String TAG_USER_TYPE = "tagusertype";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_TOKEN, null);
    }

    public boolean saveUserName(String username) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_USERNAME, username);
        editor.apply();
        return true;
    }

    public String getUsername() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_USERNAME, null);
    }

    public boolean saveCustomerID(String customerID) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_CUSTOMER_ID, customerID);
        editor.apply();
        return true;
    }

    public String getCustomerID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_CUSTOMER_ID, null);
    }

    public boolean saveFirstRun(boolean firstrun) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TAG_FIRST_RUN, firstrun);
        editor.apply();
        return true;
    }

    public boolean getFirstRun() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getBoolean(TAG_FIRST_RUN, true);
    }

    public boolean saveUserType(int usertype) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TAG_USER_TYPE, usertype);
        editor.apply();
        return true;
    }

    public int getUserType() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getInt(TAG_USER_TYPE, 1);
    }
}