package com.ekold.utils;

import java.io.Serializable;

/**
 * Created by zhangyingdong on 2018/6/8.
 */
public class BaseResponse <T> extends Response implements Serializable {
    protected T dataInfo;

    public BaseResponse() {
    }

    public T getDataInfo() {
        return this.dataInfo;
    }

    public void setDataInfo(T dataInfo) {
        this.dataInfo = dataInfo;
    }

    public String toString() {
        return "BaseResponse(super=" + super.toString() + ", dataInfo=" + this.getDataInfo() + ")";
    }

}
