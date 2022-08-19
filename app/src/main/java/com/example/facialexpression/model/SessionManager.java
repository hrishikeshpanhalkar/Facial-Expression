package com.example.facialexpression.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    public static final String SESSION_REMEMBERME="rememberme";

    private static final String IS_REMEMBERME="IsRememberMe";
    public static final String KEY_SESSIONEMAIL="email";
    public static final String KEY_SESSIONPASSWORD="password";

    public SessionManager(Context context, String sessionName){
        context=context;
        userSession=context.getSharedPreferences(sessionName,Context.MODE_PRIVATE);
        editor=userSession.edit();
    }

    public void createRememberMeSession(String email,String password){
        editor.putBoolean(IS_REMEMBERME,true);
        editor.putString(KEY_SESSIONEMAIL,email);
        editor.putString(KEY_SESSIONPASSWORD,password);
        editor.commit();
    }

    public HashMap<String,String> getRememberMeDetailsFromSession(){
        HashMap<String,String> userData=new HashMap<>();
        userData.put(KEY_SESSIONEMAIL,userSession.getString(KEY_SESSIONEMAIL,null));
        userData.put(KEY_SESSIONPASSWORD,userSession.getString(KEY_SESSIONPASSWORD,null));

        return userData;
    }

    public boolean checkRememberMe() {
        if(userSession.getBoolean(IS_REMEMBERME, false)) {
            return true;
        }else {
            return false;
        }
    }
}

