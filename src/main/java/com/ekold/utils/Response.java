package com.ekold.utils;

import java.io.Serializable;

/**
 * Created by zhangyingdong on 2018/6/8.
 */
public class Response implements Serializable {
    protected Integer returnCode;
    protected String message;

    public Response() {
    }

    public Integer getReturnCode() {
        return this.returnCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Response)) {
            return false;
        } else {
            Response other = (Response)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$returnCode = this.getReturnCode();
                Object other$returnCode = other.getReturnCode();
                if (this$returnCode == null) {
                    if (other$returnCode != null) {
                        return false;
                    }
                } else if (!this$returnCode.equals(other$returnCode)) {
                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Response;
    }



    public String toString() {
        return "Response(returnCode=" + this.getReturnCode() + ", message=" + this.getMessage() + ")";
    }
}
