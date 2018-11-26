package com.ekold.utils;

/**
 * Created by zhangyingdong on 2018/6/8.
 */
public class BackResponseUtil {
    public BackResponseUtil() {
    }

    public static BaseResponse getBaseResponse(Integer code) {
        BaseResponse response = new BaseResponse();
        if (ReturnCodeEnum.CODE_1000.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1000.getValue());
        } else if (ReturnCodeEnum.CODE_1001.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1001.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1001.getValue());
        } else if (ReturnCodeEnum.CODE_1002.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1002.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1002.getValue());
        } else if (ReturnCodeEnum.CODE_1003.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1003.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1003.getValue());
        } else if (ReturnCodeEnum.CODE_1004.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1004.getValue());
        } else if (ReturnCodeEnum.CODE_1006.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1006.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1006.getValue());
        } else if (ReturnCodeEnum.CODE_1008.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1008.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1008.getValue());
        } else if (ReturnCodeEnum.CODE_1009.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1009.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1009.getValue());
        } else if (ReturnCodeEnum.CODE_1010.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1010.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1010.getValue());
        } else if (ReturnCodeEnum.CODE_1005.getCode().equals(code)) {
            response.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
            response.setMessage(ReturnCodeEnum.CODE_1005.getValue());
        }

        return response;
    }

}
