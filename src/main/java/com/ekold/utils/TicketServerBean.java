package com.ekold.utils;


import org.apache.commons.lang3.StringUtils;

public class TicketServerBean {

    private static TicketServerBean singleton = null;

    private int ret;
    private String service_ticket;
    private long expire_time;
    private String key;

    private TicketServerBean() {

    }

    public static synchronized TicketServerBean getInstance() {
        if (null == singleton) {
            singleton = new TicketServerBean();
        }
        return singleton;
    }

    public boolean needRefreshST() {
        return System.currentTimeMillis() - expire_time >= 0 || StringUtils.isBlank(service_ticket);
    }


    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getService_ticket() {
        return service_ticket;
    }

    public void setService_ticket(String service_ticket) {
        this.service_ticket = service_ticket;
    }

    public long getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(long expire_time) {
        this.expire_time = expire_time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "TicketServerBean [ret=" + ret + ", service_ticket="
                + service_ticket + ", expire_time=" + expire_time + ", key="
                + key + "]";
    }

}
