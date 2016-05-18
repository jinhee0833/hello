package com.helloants.mm.helloants1.data.type;

/**
 * Created by park on 2016-02-16.
 */
public class FBType {
    public String mID;
    public String mName;
    public String mEmail;
    public String mBirth;
    public String mGender;
    public String mJoinPath;

    public FBType() {}
    public FBType(String id, String name, String email, String birth, String gender) {
        mID = id;
        mName = name;
        mEmail = email;
        mBirth = birthModify(birth);
        mGender = gender;
        mJoinPath = "facebook";
    }

    public String birthModify(String birth) {
        StringBuilder temp = new StringBuilder();
        birth = birth.replaceAll("/", "");
        temp.append(birth.substring(birth.length() - 2, birth.length()));
        temp.append(birth.substring(0, 4));

        return temp.toString();
    }
}
