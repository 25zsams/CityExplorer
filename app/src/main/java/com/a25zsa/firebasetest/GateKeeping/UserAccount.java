package com.a25zsa.firebasetest.GateKeeping;

/**
 * Created by 25zsa on 4/11/2018.
 */

/**
 * this class stores user account info
 */
public class UserAccount {
    private String userName;
    private String password;

    /**
     * Instantiates a new User account.
     *
     * @param userName the user name
     * @param password the password
     */
    public UserAccount(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    /**
     * Get user name string.
     *
     * @return the string
     */
    public String getUserName(){
        return userName;
    }

    /**
     * Get password string.
     *
     * @return the string
     */
    public String getPassword(){
        return password;
    }
}
