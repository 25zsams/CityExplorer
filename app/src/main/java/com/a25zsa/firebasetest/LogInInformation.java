package com.a25zsa.firebasetest;

/**
 * Created by 25zsa on 4/8/2018.
 */

public class LogInInformation {
    private String userName;
    private String passWord;
    public LogInInformation(String userName, String passWord){
        this.userName = userName;
        this.passWord = passWord;
    }

    public String get1User1(){
        return userName;
    }

    public String get1Pass1(){
        return passWord;
    }
}
