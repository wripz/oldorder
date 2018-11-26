package com.ekold.utils;


public class BusTokenBean {

    private static BusTokenBean singleton = null;

    private String token;

    private BusTokenBean() {

    }

    public static synchronized BusTokenBean getInstance() {
        if (null == singleton) {
            singleton = new BusTokenBean();
        }
        return singleton;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
