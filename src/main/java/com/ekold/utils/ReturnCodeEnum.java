package com.ekold.utils;

/**
 * Created by zhangyingdong on 2018/6/8.
 */
public enum ReturnCodeEnum {
    CODE_1000(1000, "message.common.success"),
    CODE_1001(1001, "message.security.unauthorized"),
    CODE_1002(1002, "message.common.information.nonexistence"),
    CODE_1003(1003, "message.payment.type.error"),
    CODE_1004(1004, "message.common.server.error"),
    CODE_1005(1005, "message.common.failed"),
    CODE_1006(1006, "message.common.parameters.missing"),
    CODE_1007(1007, "message.common.timeout"),
    CODE_1008(1008, "message.common.information.already.exists"),
    CODE_1009(1009, "message.common.session.expired"),
    CODE_1010(1010, "message.security.authfailed");

    private Integer code;
    private String value;

    private ReturnCodeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public final Integer getCode() {
        return this.code;
    }

    public final String getValue() {
        return this.value;
    }

    public String toString() {
        return this.code + "=" + this.value;
    }
}
