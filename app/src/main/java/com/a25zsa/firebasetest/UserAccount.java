package com.a25zsa.firebasetest;

/**
 * Created by 25zsa on 4/11/2018.
 */

public class UserAccount {
    private String userName;
    private String password;

    public UserAccount(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }
}
