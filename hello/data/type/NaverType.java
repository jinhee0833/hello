package com.helloants.mm.helloants1.data.type;

/**
 * Created by park on 2016-05-06.
 */
public class NaverType {
    public String mEmail;
    public String mName;
    public String mBirth;
    public String mGender;
    public String mJoinPath;

    public NaverType() {}
    public NaverType(String email, String name, String birth, String gender) {
        mEmail = email;
        mName = name;
        mBirth = birthModify(birth);
        mGender = gender;
        mJoinPath = "naver";
    }

    public String birthModify(String birth) {
        birth = birth.replaceAll("-", "");
        return birth;
    }
}