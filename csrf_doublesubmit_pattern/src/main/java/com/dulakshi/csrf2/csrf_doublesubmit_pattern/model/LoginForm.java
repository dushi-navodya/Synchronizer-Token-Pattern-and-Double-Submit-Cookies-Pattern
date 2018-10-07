package com.dulakshi.csrf2.csrf_doublesubmit_pattern.model;

public class LoginForm{
    private String email;
    private String password;

    public LoginForm(){}


    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return this.email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }

    public String toString(){
        return this.email+":"+this.password;
    }
}