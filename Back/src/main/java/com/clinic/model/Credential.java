package com.clinic.model;


public class Credential
{
    private String username, password;

    public Credential(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

}
