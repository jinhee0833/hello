package com.helloants.mm.helloants1.login;

/**
 * Created by park on 2016-01-21.
 */
public class LoginData {
    public static String mEmail = "";
    public static String mName = "";
    public static String mJoinPath = "";

    public static void setLoginData(String email, String name, String joinPath) {
        mEmail = email;
        mName = name;
        mJoinPath = joinPath;
    }

    public static void clear() {
        mEmail = "";
        mName = "";
        mJoinPath = "";
    }
}